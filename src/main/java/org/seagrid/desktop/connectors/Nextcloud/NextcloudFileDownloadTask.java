package org.seagrid.desktop.connectors.Nextcloud;

import org.seagrid.desktop.connectors.Nextcloud.Exception.NextcloudApiException;
import org.seagrid.desktop.util.SEAGridContext;

import java.io.*;

public class NextcloudFileDownloadTask extends NextcloudFileTask {

    private String remoterootpath;
    private String downloadirpath;
    NextcloudFileDownloadTask() {
        super();
        remoterootpath = (isusehttps ? "https" : "http") + "://" + servername + "/" + basepath + SEAGridContext.getInstance().getUserName();
        downloadirpath = SEAGridContext.getInstance().getBaseDownloadPath() + SEAGridContext.getInstance().getUserName();
    }

    /**
     * Download the file from the remote nextcloud server using the remotepath to the specified directory
     *
     * @param remotepath
     * @return
     * @throws IOException
     */
    public boolean downloadFile(String remotepath) throws IOException {
        boolean status=false;
        String path = remoterootpath + remotepath;
        InputStream in = null;
        if(Exists(remotepath)) {
            //Extract the Filename from the path
            String[] segments = path.split("/");
            String filename = segments[segments.length - 1];
            downloadirpath = downloadirpath + "/" + filename;
        }
        try {
            in = sardine.get(path);
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            File targetFile = new File(downloadirpath);
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
            outStream.close();
            status = true;
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        } finally {
            sardine.shutdown();
            in.close();
            return status;
        }
    }




}
