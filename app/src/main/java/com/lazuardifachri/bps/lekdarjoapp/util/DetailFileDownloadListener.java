package com.lazuardifachri.bps.lekdarjoapp.util;

import java.io.IOException;

public interface DetailFileDownloadListener {
    String onStartDownload(String fileName, String documentUri) throws IOException;

    void onProgressDownload(int progress);

    void onFinishDownload(String documentUri);

    void onFailDownload(String errorInfo, String documentUri);
}
