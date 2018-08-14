package org.seagrid.desktop.connectors.NextcloudStorage;

import com.github.sardine.DavResource;
import com.jcraft.jsch.SftpException;
import org.seagrid.desktop.connectors.NextcloudStorage.Exception.NextcloudApiException;
import org.seagrid.desktop.util.SEAGridContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class NextCloudFolderdownloadtask extends NextcloudFileTask {

    private final static Logger logger = LoggerFactory.getLogger(NextCloudFolderdownloadtask.class);

    private String rootpath;
    private String remoteDirPath, localDirPath;
    private double totalBytesRead = 0, totalSize = 0;

    public NextCloudFolderdownloadtask(String remoteDirPath, String localDirPath) throws IOException {
        super();
        rootpath = (isusehttps ? "https" : "http") +"://"+servername+"/"+basepath+ "/" + SEAGridContext.getInstance().getUserName();
        this.remoteDirPath = remoteDirPath;
        this.localDirPath = localDirPath;
    }

    @Override
    protected Boolean call() throws Exception {
        calculateNextcloudTotalSize(remoteDirPath);
        boolean status = downloadFolder(remoteDirPath, localDirPath);
        return status;
    }

    /**
     * Downloads the folder at the specified remotepath to the rootdownloadirpath
     *
     * @param remotepath the path in the nextcloud server with respect to the specific folder
     * @param rootdownloadirpath the local path in the system where the folder needs be saved
     * @return
     * @throws IOException
     */

    public boolean downloadFolder(String remotepath, String rootdownloadirpath) throws IOException {
        int depth=1;
        String newdownloadir = rootdownloadirpath;
        File localDir = new File(newdownloadir);

        if(!localDir.exists()){
            localDir.mkdirs();
        }

        String rootpathnew = rootpath + remotepath ;

        int count = 0;
        String filepath;

        List<String> retVal= new LinkedList<>();
        List<DavResource> resources;
        try {
            resources = sardine.list(rootpathnew, depth);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }

        for (DavResource res : resources)
        {
            if(count != 0) {
                if(res.equals(".") || res.equals("..")){
                    continue;
                }

                else if(res.isDirectory()) {
                    String subFoldername = res.getName();
                    String downloadDirtosend = newdownloadir + "/" + subFoldername;
                    String pathToSend = remotepath + "/" + subFoldername;
                    downloadFolder(pathToSend,downloadDirtosend);
                }

                else {
                    String filename = res.getName();
                    filepath = rootpathnew + "/" + filename;
                    retVal.add(res.getName());
                    InputStream in = null;
                    if (sardine.exists(filepath)) {
                        in = sardine.get(filepath);
                        byte[] buffer = new byte[in.available()];
                        File targetFile = new File(newdownloadir + "/" + filename);
                        OutputStream outStream = new FileOutputStream(targetFile);
                        int bytesRead = -1;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            outStream.write(buffer);
                            totalBytesRead += bytesRead;
                            updateMessage("Downloaded " + totalBytesRead + " bytes");
                            updateProgress(totalBytesRead, totalSize);
                        }
                        in.close();
                        outStream.close();
                    }
                }
            }
            count ++;
        }
        return true;
    }

    private void calculateNextcloudTotalSize(String remoteDirPath) throws SftpException, IOException {
        List<DavResource> davResource = listDirectories(remoteDirPath);
        int count = 0;
        for (DavResource res : davResource) {
            if(count != 0) {
                if (res.getName().equals(".") || res.getName().equals("..")) {
                    continue;
                }
                if (res.isDirectory()) {
                    String tempRemoteDir = remoteDirPath + "/" + res.getName();
                    calculateNextcloudTotalSize(tempRemoteDir);
                } else {
                    totalSize += res.getContentLength().intValue();
                }
            }
            count++;
        }
    }
}
