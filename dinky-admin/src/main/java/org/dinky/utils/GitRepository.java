/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.utils;

import org.dinky.data.dto.GitProjectDTO;
import org.dinky.data.exception.DinkyException;
import org.dinky.function.constant.PathConstant;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
@AllArgsConstructor
@Slf4j
public class GitRepository {
    private static final String BRANCH_PREFIX = "refs/heads/";
    /** 匹配 ssh格式 git 地址 */
    private static final String SSH_REGEX = "^git@[\\w\\.]+:([\\w_-]+)/(.*?)\\.git$";

    private String url;
    private String username;
    private String password;
    private String privateKey;

    public GitRepository(GitProjectDTO gitProjectDTO) {
        this.url = gitProjectDTO.getUrl();
        this.username = gitProjectDTO.getUsername();
        this.password = gitProjectDTO.getPassword();
        this.privateKey = gitProjectDTO.getPrivateKey();
    }

    public File cloneAndPull(String projectName, String branch) {
        return cloneAndPull(projectName, branch, null, null);
    }

    public File cloneAndPull(String projectName, String branch, File logFile, Consumer<String> consumer) {
        List<String> branchList = getBranchList();
        if (!branchList.contains(branch)) {
            throw new DinkyException(
                    StrUtil.format("Branch: {} does not exist, optional branch is: {}", branch, branchList));
        }

        File targetFile = getProjectDir(projectName);
        FileUtil.mkdir(targetFile);
        File writeFile = new File(targetFile, branch);

        Git git;
        try {

            CloneCommand cloneCommand =
                    Git.cloneRepository().setURI(url).setBranch(branch).setDirectory(writeFile);
            initCommand(cloneCommand);

            if (writeFile.exists()) {
                FileRepositoryBuilder builder = new FileRepositoryBuilder();
                Repository repository = builder.setGitDir(
                                new File(writeFile.getAbsolutePath() + File.separator + ".git"))
                        .readEnvironment()
                        .findGitDir()
                        .build();
                git = new Git(repository);
                git.pull()
                        .setProgressMonitor(new TextProgressMonitor(new StringWriter() {
                            @Override
                            public void write(String str) {
                                if (logFile != null) {
                                    FileUtil.appendUtf8String(str, logFile);
                                }
                                if (consumer != null) {
                                    consumer.accept(str);
                                }
                                System.out.println(str);
                                super.write(str);
                            }
                        }))
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                        .call();
                git.close();
            } else {
                cloneCommand.call().close();
            }
            return writeFile;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DinkyException(e);
        }
    }

    public static File getProjectDir(String projectName) {
        return FileUtil.file(PathConstant.TMP_PATH, "git", projectName);
    }

    public static File getProjectBuildDir(String projectName) {
        return FileUtil.file(PathConstant.TMP_PATH, "git_build", projectName);
    }

    /**
     * 获取分支列表（提供拼音排序）
     *
     * @return {@link List}<{@link String}>
     */
    public List<String> getBranchList() {
        List<String> branchList = new ArrayList<>();

        LsRemoteCommand lsRemoteCommand = initCommand(Git.lsRemoteRepository().setRemote(url));
        try {
            lsRemoteCommand.call().forEach(x -> {
                if (StrUtil.contains(x.getName(), BRANCH_PREFIX)) {
                    branchList.add(StrUtil.removePrefix(x.getName(), BRANCH_PREFIX));
                }
            });
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
        return CollUtil.reverse(CollUtil.sortByPinyin(branchList));
    }

    private static TransportConfigCallback getTransportConfigCallback(String sshKey, String password) {
        JschConfigSessionFactory jschConfigSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                JSch defaultJSch = super.createDefaultJSch(fs);
                // 配置 ssh key
                defaultJSch.addIdentity(sshKey, sshKey + ".pub", StrUtil.bytes(password));
                return defaultJSch;
            }
        };
        return transport -> {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(jschConfigSessionFactory);
        };
    }

    /**
     * 初始化git指令，设置账密信息
     *
     * @param command 命令
     * @return command
     */
    private <T extends TransportCommand<T, ?>> T initCommand(T command) {
        if (isSshGitUrl(url)) {
            Assert.notBlank(privateKey, "The private key address cannot be empty.");
            Assert.isTrue(FileUtil.exist(privateKey), "The private key address does not exist.");

            TransportConfigCallback sshSessionFactory = getTransportConfigCallback(privateKey, password);
            command.setTransportConfigCallback(sshSessionFactory);
            return command;
        } else if (HttpUtil.isHttp(url) || HttpUtil.isHttps(url)) {
            if (StrUtil.isAllNotBlank(username, password)) {
                command.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
                return command;
            }
        } else {
            throw new UnsupportedOperationException(
                    "This format is not supported. Specify as http, git. your url is -> " + url);
        }
        return command;
    }

    /**
     * 判断是否为ssh格式的git地址
     *
     * @param url git地址
     * @return true表示是ssh格式的git地址，false表示不是ssh格式的git地址
     */
    public static boolean isSshGitUrl(String url) {
        return ReUtil.isMatch(SSH_REGEX, url);
    }
}
