package org.seagrid.desktop.connectors.NextcloudStorage;

import org.seagrid.desktop.connectors.NextcloudStorage.Exception.NextcloudApiException;
import org.seagrid.desktop.util.SEAGridContext;

import java.io.*;
import java.util.Map;

public class NextcloudFileUploadTask extends NextcloudFileTask {

    private String remoterootpath;
    private Map<String, File> uploadFiles;
    /**
     * Constructor
     */
    public NextcloudFileUploadTask(Map<String, File> uploadFiles) throws IOException {
        super();
        this.uploadFiles = uploadFiles;
        remoterootpath = (isusehttps ? "https" : "http") + "://" + servername + "/" + basepath + "/" + SEAGridContext.getInstance().getUserName();
    }

    @Override
    protected Boolean call() throws Exception {
        return uploadToNextcloud();
    }

    public boolean uploadToNextcloud() throws IOException {
        for(String remoteFilePath : uploadFiles.keySet()){
            int numberOfFiles = uploadFiles.size();
            int index = 1;
            remoteFilePath = remoteFilePath.replace("\\","/");
            File localFile = uploadFiles.get(remoteFilePath);
            String localpath = localFile.getPath();
            String remotepath = remoteFilePath;
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
                long fileSize = localFile.length();
                byte[] buffer = new byte[inputStream.available()];
                int bytesRead = -1;
                int totalBytesRead = 0;
                double percentCompleted = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    totalBytesRead += bytesRead;
                    sardine.put(path, buffer);
                    percentCompleted = ((double)totalBytesRead) / fileSize * index / numberOfFiles;
                    updateMessage("Uploaded " + totalBytesRead + " bytes");
                    updateProgress(percentCompleted, 1);
                }
                status = true;
            } catch (IOException e) {
                throw new NextcloudApiException(e);
            } finally {
                inputStream.close();
                return status;
            }
        }
        return true;
    }

}
