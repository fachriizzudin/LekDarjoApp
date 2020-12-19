package com.lazuardifachri.bps.lekdarjoapp.model.response;

import com.lazuardifachri.bps.lekdarjoapp.util.FileDownloadListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class DownloadResponseBody extends ResponseBody {

    private ResponseBody responseBody;

    private FileDownloadListener listener;

    private BufferedSource bufferedSource;

    public DownloadResponseBody(ResponseBody responseBody, FileDownloadListener listener) {
        this.responseBody = responseBody;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {

        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }

        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead =  0L;
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                if (null != listener) {
                    if (bytesRead != -1) {
                        listener.onProgressDownload((int) (totalBytesRead * 100 / responseBody.contentLength()));
                    }
                }
                return bytesRead;
            }
        };
    }

}
