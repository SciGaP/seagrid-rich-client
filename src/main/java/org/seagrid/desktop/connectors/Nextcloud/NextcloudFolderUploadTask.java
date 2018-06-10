package org.seagrid.desktop.connectors.Nextcloud;

import org.seagrid.desktop.connectors.Nextcloud.Exception.NextcloudApiException;
import org.seagrid.desktop.util.SEAGridContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NextcloudFolderUploadTask extends NextcloudFileTask {

    private String remoterootpath;
    NextcloudFolderUploadTask() {
        super();
        remoterootpath = (isusehttps ? "https" : "http") + "://" + servername + "/" + basepath + SEAGridContext.getInstance().getUserName();
    }

    /**
     * Create the folder at the specified path, if the folder doesn't exist a new folder with
     * the specific foldername is created
     *
     * @param localpath
     * @param remotepath
     * @throws IOException
     */
    public void uploadFolder(String localpath, String remotepath) throws IOException {
        String path;
        path = remoterootpath + remotepath;
        String ip = localpath;
        try {
            File f = new File(ip);
            if (f.exists() && f.isDirectory()) {
                //Extract the Foldername from the path
                String[] segments = ip.split("/");
                String foldername = segments[segments.length - 1];
                System.out.println(foldername);
                path = path + "/" + foldername;
                System.out.println(path);
                //if the folder doesn't exist in the remote server then create the folder
                if (!Exists(remotepath)) {
                    createFolder(remotepath);
                }

                File[] listfil = f.listFiles();
                if (listfil != null) {
                    for (File child : listfil) {
                        String filename = child.getName();
                        String newpath = path + "/" + filename;
                        InputStream input = new FileInputStream(child.getAbsolutePath());
                        sardine.put(newpath, input);
                        input.close();
                    }
                }
            }
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }

    }

    /**
     * Create the folder at the specified path
     * @param remotepath
     */
    public void createFolder(String remotepath)
    {
        String path=  remoterootpath+remotepath;
        try {
            sardine.createDirectory(path);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }
}
