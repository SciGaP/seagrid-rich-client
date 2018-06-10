package org.seagrid.desktop.connectors.Nextcloud;

import org.seagrid.desktop.connectors.Nextcloud.Exception.NextcloudApiException;
import org.seagrid.desktop.util.SEAGridContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NextcloudFileUploadTask extends NextcloudFileTask {

    private String remoterootpath;
    /**
     * Constructor
     */
    NextcloudFileUploadTask() {
        super();
        remoterootpath = (isusehttps ? "https" : "http") + "://" + servername + "/" + basepath + SEAGridContext.getInstance().getUserName();
    }

    /**
     * Upload the inputstream to the remote file path as specified to the nextcloud server
     *
     * @param localpath
     * @param remotepath
     * @throws IOException
     *
     * Reference: https://github.com/a-schild/nextcloud-java-api
     */
    protected boolean UploadFile(String localpath, String remotepath) throws IOException {
        boolean status = false;
        InputStream inputStream = new FileInputStream(localpath);
        String path;
            try {
                path = remoterootpath + remotepath;
                sardine.put(path, inputStream);
                status = true;
            } catch (IOException e) {
                throw new NextcloudApiException(e);
            } finally {
              inputStream.close();
              return status;
            }
    }
}
