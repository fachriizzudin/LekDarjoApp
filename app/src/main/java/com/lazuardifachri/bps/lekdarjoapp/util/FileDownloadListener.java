package com.lazuardifachri.bps.lekdarjoapp.util;

import java.io.IOException;

public interface FileDownloadListener {
    String onStartDownload(String fileName) throws IOException;

    void onProgress(int progress);

    void onFinishDownload();

    void onFail(String errorInfo);
}
