package org.dinky.resource.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import org.apache.flink.core.fs.FSDataOutputStream;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.fs.local.LocalDataOutputStream;
import org.dinky.data.model.SystemConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class HttpFsDataOutputStream extends FSDataOutputStream {
    private final String uuid = UUID.randomUUID().toString();

    private final Path path;
    final File file = FileUtil.file(FileUtil.getTmpDir(), "/dinky-tmp/" + uuid + ".tmp");
    final LocalDataOutputStream localDataOutputStream;

    public HttpFsDataOutputStream(Path f) throws IOException {
        this.path = f;
         FileUtil.mkParentDirs(file);
        localDataOutputStream= new LocalDataOutputStream(file);
    }

    @Override
    public void write(int b) throws IOException {
        localDataOutputStream.write(b);
    }

    @Override
    public long getPos() throws IOException {
        return localDataOutputStream.getPos();
    }

    @Override
    public void flush() throws IOException {
        localDataOutputStream.flush();
        sendFile();
    }
    private void sendFile() {
        try (HttpResponse httpResponse = HttpUtil.createPost(SystemConfiguration.getInstances().getDinkyAddr().getValue() + "/download/uploadFromRsByLocal").form("file", file).form("path", path.toString()).execute()) {
            httpResponse.body();
        }
    }

    @Override
    public void sync() throws IOException {
        localDataOutputStream.sync();
    }

    @Override
    public void close() throws IOException {
        localDataOutputStream.close();
        sendFile();
    }
}
