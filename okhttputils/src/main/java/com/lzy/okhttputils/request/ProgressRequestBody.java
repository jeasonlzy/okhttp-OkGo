package com.lzy.okhttputils.request;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 包装的请求体，处理进度，可以处理任何的 RequestBody，
 * 但是一般用在 multipart requests 上传大文件的时候
 */
public class ProgressRequestBody extends RequestBody {

    protected RequestBody delegate;  //实际的待包装请求体
    protected Listener listener;     //进度回调接口
    protected CountingSink countingSink; //包装完成的BufferedSink
    private long mPreviousTime;  //开始下载时间，用户计算下载速度

    public ProgressRequestBody(RequestBody delegate, Listener listener) {
        this.delegate = delegate;
        this.listener = listener;
    }

    /** 重写调用实际的响应体的contentType */
    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    /** 重写调用实际的响应体的contentLength */
    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /** 重写进行写入 */
    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        mPreviousTime = System.currentTimeMillis();
        countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();  //必须调用flush，否则最后一部分数据可能不会被写入
    }

    /** 包装 */
    protected final class CountingSink extends ForwardingSink {
        private long bytesWritten = 0;  //当前写入字节数
        private long contentLength = 0; //总字节长度，避免多次调用contentLength()方法

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            if (contentLength <= 0) contentLength = contentLength(); //获得contentLength的值，后续不再调用
            bytesWritten += byteCount;

            //计算下载速度
            long totalTime = (System.currentTimeMillis() - mPreviousTime) / 1000;
            if (totalTime == 0) totalTime += 1;
            long networkSpeed = bytesWritten / totalTime;
            listener.onRequestProgress(bytesWritten, contentLength, networkSpeed);
        }

    }

    /** 回调接口 */
    public interface Listener {
        void onRequestProgress(long bytesWritten, long contentLength, long networkSpeed);
    }
}