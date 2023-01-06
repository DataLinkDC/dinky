package org.dinky.utils;

import org.dinky.assertion.Asserts;
import org.dinky.common.result.Result;
import org.dinky.constant.UploadFileConstant;
import org.dinky.model.CodeEnum;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Hdfs Handle
 **/
@Slf4j
public class HdfsUtil {

    private static FileSystem hdfs = null;

    /**
     * Init internal hdfs client
     *
     * @param hadoopConfigPath HDFS config path
     */
    private static Result init(String hadoopConfigPath) {
        if (hdfs == null) {
            if (Asserts.isNullString(hadoopConfigPath)) {
                hadoopConfigPath = FilePathUtil.removeFileSeparator(UploadFileConstant.HADOOP_CONF_DIR);
            }
            String coreSiteFilePath = hadoopConfigPath + "/core-site.xml";
            String hdfsSiteFilePath = hadoopConfigPath + "/hdfs-site.xml";
            if (!new File(coreSiteFilePath).exists() || !new File(hdfsSiteFilePath).exists()) {
                return Result.failed("在项目根目录下没有找到 core-site.xml/hdfs-site.xml/yarn-site.xml 文件，请先上传这些文件");
            }
            try {
                final Configuration configuration = new Configuration();
                configuration.addResource(new Path(coreSiteFilePath));
                configuration.addResource(new Path(hdfsSiteFilePath));
                hdfs = FileSystem.get(configuration);
            } catch (IOException e) {
                log.error(ExceptionUtil.stacktraceToString(e));
                return Result.failed("内部 hdfs 客户端初始化错误");
            }
            return Result.succeed("hdfs 客户端初始化成功");
        }
        return Result.succeed("");
    }

    /**
     * Upload file byte content to HDFS
     *
     * @param path  HDFS path
     * @param bytes File byte content
     * @return {@link Result}
     */
    public static Result uploadFile(String path, byte[] bytes) {
        return uploadFile(path, bytes, null);
    }

    /**
     * Upload file byte content to HDFS
     *
     * @param path             HDFS path
     * @param bytes            File byte content
     * @param hadoopConfigPath hdfs config path
     * @return {@link Result}
     */
    public static Result uploadFile(String path, byte[] bytes, String hadoopConfigPath) {
        Result initResult = init(hadoopConfigPath);
        if (Objects.equals(initResult.getCode(), CodeEnum.SUCCESS.getCode())) {
            try (FSDataOutputStream stream = hdfs.create(new Path(path), true)) {
                stream.write(bytes);
                stream.flush();
                return Result.succeed("");
            } catch (IOException e) {
                log.error(ExceptionUtil.stacktraceToString(e));
                return Result.failed("文件上传失败");
            }
        } else {
            return initResult;
        }
    }

    /**
     * Upload file byte content to HDFS
     *
     * @param path HDFS path
     * @param file MultipartFile instance
     * @return {@link Result}
     */
    public static Result uploadFile(String path, MultipartFile file) {
        try {
            return uploadFile(path, file.getBytes());
        } catch (IOException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            return Result.failed("文件上传失败");
        }
    }

    /**
     * Upload file byte content to HDFS
     *
     * @param path             HDFS path
     * @param file             MultipartFile instance
     * @param hadoopConfigPath hdfs config path
     * @return {@link Result}
     */
    public static Result uploadFile(String path, MultipartFile file, String hadoopConfigPath) {
        try {
            return uploadFile(path, file.getBytes(), hadoopConfigPath);
        } catch (IOException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            return Result.failed("文件上传失败");
        }
    }

}
