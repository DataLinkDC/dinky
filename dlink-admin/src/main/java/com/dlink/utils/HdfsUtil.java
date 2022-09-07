package com.dlink.utils;

import com.dlink.common.result.Result;
import com.dlink.constant.UploadFileConstant;
import com.dlink.model.CodeEnum;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Hdfs Handle
 **/
@Slf4j
@Component
public class HdfsUtil {

    private final Configuration configuration = new Configuration();
    private FileSystem hdfs = null;

    /**
     * Init internal hdfs client
     */
    @PostConstruct
    private Result init() {
        if (hdfs == null) {
            String coreSiteFilePath = FilePathUtil.addFileSeparator(UploadFileConstant.HADOOP_CONF_DIR) + "core-site.xml";
            String hdfsSiteFilePath = FilePathUtil.addFileSeparator(UploadFileConstant.HADOOP_CONF_DIR) + "hdfs-site.xml";
            if (!new File(coreSiteFilePath).exists() || !new File(hdfsSiteFilePath).exists()) {
                return Result.failed("在项目根目录下没有找到 core-site.xml/hdfs-site.xml/yarn-site.xml 文件，请先上传这些文件");
            }
            try {
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
     * @return {@link com.dlink.common.result.Result}
     */
    public Result uploadFile(String path, byte[] bytes) {
        Result initResult = init();
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
     * @return {@link com.dlink.common.result.Result}
     */
    public Result uploadFile(String path, MultipartFile file) {
        try {
            return uploadFile(path, file.getBytes());
        } catch (IOException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            return Result.failed("文件上传失败");
        }
    }

}
