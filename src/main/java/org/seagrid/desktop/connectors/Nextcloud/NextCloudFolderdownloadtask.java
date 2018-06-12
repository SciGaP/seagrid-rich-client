package org.seagrid.desktop.connectors.Nextcloud;

import com.github.sardine.DavResource;
import org.seagrid.desktop.connectors.Nextcloud.Exception.NextcloudApiException;
import org.seagrid.desktop.util.SEAGridContext;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class NextCloudFolderdownloadtask extends NextcloudFileTask {

    private String rootpath;
    private String rootdownloadirpath = SEAGridContext.getInstance().getBaseDownloadPath();

    /**
     * Constructor
     */
    NextCloudFolderdownloadtask() {
        super();
        rootpath = (isusehttps ? "https" : "http") +"://"+servername+"/"+basepath+SEAGridContext.getInstance().getUserName();

    }

    /**
     * Downloads all the files inside the folder at the specified path
     *
     * @param remotepath
     * @param depth
     * @return
     * @throws IOException
     */
    public void downloadFolder(String remotepath,int depth) throws IOException {
        int count = 0;
        String filepath;
        String newrootpath = rootpath + remotepath;
        String newdownloadir = rootdownloadirpath + "/" + remotepath ;
        File downloadfilepathobject = new File(newdownloadir);
        if(!downloadfilepathobject.exists()) {
            downloadfilepathobject.mkdir();
        }

        List<String> retVal= new LinkedList<>();
        List<DavResource> resources;
        try {
            resources = sardine.list(newrootpath, depth);
            for (DavResource res : resources) {
                //Skip the first folder which is listed as default as first by the sardine output
                if (count != 0) {
                    if (res.isDirectory()) {
                        String filename = res.getName();
                        String recursefilepath = remotepath + "/" + filename;
                        downloadFolder(recursefilepath, depth);
                    } else {
                        String filename = res.getName();
                        filepath = newrootpath + "/" + filename;
                        retVal.add(res.getName());
                        InputStream in = null;
                        if (sardine.exists(filepath)) {
                            in = sardine.get(filepath);
                            byte[] buffer = new byte[in.available()];
                            in.read(buffer);
                            File targetFile = new File(newdownloadir + "/" + filename);
                            OutputStream outStream = new FileOutputStream(targetFile);
                            outStream.write(buffer);
                            in.close();
                            outStream.close();
                        }
                    }
                }
                count++;
            }
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }

}
