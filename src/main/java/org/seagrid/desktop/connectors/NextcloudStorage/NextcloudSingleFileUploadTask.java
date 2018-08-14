package org.seagrid.desktop.connectors.NextcloudStorage;

import org.seagrid.desktop.connectors.NextcloudStorage.Exception.NextcloudApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NextcloudSingleFileUploadTask extends NextcloudFileTask{

    private final static Logger logger = LoggerFactory.getLogger(NextcloudSingleFileUploadTask.class);

    private String remoteFilePath, localFilePath;
    private String remoterootpath;

    public NextcloudSingleFileUploadTask(String remoteFilePath, String localFilePath) throws IOException {
        super();
        this.remoteFilePath = remoteFilePath;
        this.localFilePath = localFilePath;
    }


    @Override
    protected Boolean call() throws Exception {
        return uploadFile(remoteFilePath, localFilePath);
    }

    /**
     * Upload the inputstream to the remote file path as specified to the nextcloud server
     *
     * @param localpath
     * @param remotepath
     * @throws IOException
     *
     */
    protected boolean uploadFile(String localpath, String remotepath) throws IOException {
        boolean status = false;
        InputStream inputStream = new FileInputStream(localpath);
        String path;
        try {
            //createpath if not exists
            String[] segments = remotepath.split("/");
            String appendpath="";
            for(int i = 1; i < segments.length - 1 ; i++)
            {
                appendpath = appendpath + "/" + segments[i];
                if(!sardine.exists(remoterootpath + "/" + appendpath)) {
                    createFolder(appendpath);
                }
            }
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
