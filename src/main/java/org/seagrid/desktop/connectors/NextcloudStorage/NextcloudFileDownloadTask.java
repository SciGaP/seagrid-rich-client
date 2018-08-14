package org.seagrid.desktop.connectors.NextcloudStorage;

import org.seagrid.desktop.connectors.NextcloudStorage.Exception.NextcloudApiException;
import org.seagrid.desktop.util.SEAGridContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class NextcloudFileDownloadTask extends NextcloudFileTask {

    private final static Logger logger = LoggerFactory.getLogger(NextcloudFileDownloadTask.class);

    private String remoteFilePath, localFilePath;
    private String remoterootpath;

    public NextcloudFileDownloadTask(String remoteFilePath, String localFilePath) throws IOException {
        super();
        this.remoteFilePath = remoteFilePath;
        this.localFilePath = localFilePath;
        remoterootpath = (isusehttps ? "https" : "http") + "://" + servername + "/" + basepath + "/" + SEAGridContext.getInstance().getUserName();
    }

    @Override
    protected Boolean call() throws Exception {
        return downloadFile(localFilePath, remoteFilePath);
    }

    public boolean downloadFile(String sourceFile, String destFile) {
        String path = remoterootpath + destFile;
        File downloadFilepath = new File(sourceFile);
        if(!downloadFilepath.getParentFile().exists()) {
            downloadFilepath.getParentFile().mkdirs();
        }
        try {
            InputStream in = sardine.get(path);
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            File targetFile = new File(sourceFile);
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
            outStream.close();
            in.close();
            return true;
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }
}
