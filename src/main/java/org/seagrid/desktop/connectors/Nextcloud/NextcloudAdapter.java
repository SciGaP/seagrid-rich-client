package org.seagrid.desktop.connectors.Nextcloud;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class NextcloudAdapter {
    private final NextcloudFileUploadTask uplfile;
    private final NextcloudFileDownloadTask dnlfile;
    private final NextCloudFolderdownloadtask dnlfolder;

    /**
     * Constructor
     */
    public NextcloudAdapter() {
        uplfile = new NextcloudFileUploadTask();
        dnlfile = new NextcloudFileDownloadTask();
        dnlfolder = new NextCloudFolderdownloadtask();
    }

    /**
     *Upload the file to the remote path of the nextcloud server
     *
     * @param localpath
     * @param remotepath
     * @return boolean
     *
     * Reference: https://github.com/a-schild/nextcloud-java-api
     */
    public boolean UploadFile(String localpath, String remotepath) {
        boolean status=false;
        try {
            if(uplfile.AuthenticateSession()) {
                status = uplfile.UploadFile(localpath, remotepath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return status;
        }
    }

    /**
     * Download the file from the remotepath to the download path specified
     *
     * @param remotepath
     * @return boolean
     */
    public boolean downloadFile(String remotepath) {
        boolean status = false;
        try {
            if(dnlfile.AuthenticateSession()) {
                status = dnlfile.downloadFile(remotepath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return status;
        }
    }

    /**
     * Downloads all the files at the specified folder of the remote server
     * @param path
     * @throws IOException
     */
    public void downloadFolder(String path) throws IOException {
        try {
            List<String> downloadlist= new LinkedList<>();
            if(dnlfolder.AuthenticateSession()) {
                dnlfolder.downloadFolder(path, 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Check if the remotepath exists at the nextcloud server
     *
     * @param rootpath
     * @return
     *
     * Reference: https://github.com/a-schild/nextcloud-java-api
     */
    public boolean Exists(String rootpath) {
            if(uplfile.Exists(rootpath)) {
                return true;
            }
            return false;
    }
}
