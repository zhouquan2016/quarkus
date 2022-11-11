package com.zhqn.common.grpc.platform;

import com.google.protobuf.ByteString;
import com.zhqn.common.platform.UploadRequest;
import io.netty.util.internal.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.function.Consumer;

public class FileUploadIterator implements Iterable<UploadRequest>, Iterator<UploadRequest> {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件流
     */
    private InputStream is;

    /**
     * 第一次迭代
     */
    private boolean first;
    /**
     * 读取到的大小
     */
    private int size;

    /**
     * 读取buff
     */
    private byte[] buff;

    public FileUploadIterator(String fileName, InputStream is) {
        this.fileName = fileName;
        this.is = is;
        this.first = true;
        this.buff = new byte[4098];
        this.size = 0;
    }
    @Override
    public Iterator<UploadRequest> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {

        return size != -1;
    }

    @Override
    public UploadRequest next() {
        if (first) {
            first = false;
            return sendMeta();
        }
        return sendData();
    }

    /**
     * 发送元数据
     * @return 请求数据
     */
    private UploadRequest sendMeta() {
        return UploadRequest.newBuilder().setFilename(fileName).build();
    }

    /**
     * 发送文件数据
     * @return 请求数据
     */
    private UploadRequest sendData() {
        try {
            size = is.read(buff);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ByteString data = size != -1 ? ByteString.copyFrom(buff, 0, size) : ByteString.EMPTY ;
        return UploadRequest.newBuilder().setContents(data).build();
    }
}
