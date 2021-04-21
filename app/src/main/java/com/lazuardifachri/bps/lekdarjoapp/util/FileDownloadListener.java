package com.lazuardifachri.bps.lekdarjoapp.util;

import java.io.IOException;

public interface FileDownloadListener {

    String onStartDownload(String fileName) throws IOException;

    void onProgressDownload(int progress);

    void onFinishDownload();

    void onFailDownload(String errorInfo);
}
