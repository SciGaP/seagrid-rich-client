package org.seagrid.desktop.connectors.NextcloudStorage;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.jcraft.jsch.JSchException;
import org.seagrid.desktop.connectors.NextcloudStorage.Exception.NextcloudApiException;
import org.seagrid.desktop.util.SEAGridContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class NextcloudStorageManager {
    private final static Logger logger = LoggerFactory.getLogger(NextcloudStorageManager.class);

    private static NextcloudStorageManager instance;

    protected Sardine sardine = SardineFactory.begin();

    private String servername;
    private String basepath;
    private boolean isusehttps;
    private String token;
    private String rootremotepath;

    public NextcloudStorageManager() throws IOException, JSchException {
        connect();
    }

    private void connect() throws JSchException, IOException {
        servername = SEAGridContext.getInstance().getNextcloudServername();
        basepath = SEAGridContext.getInstance().getDavBasepath();
        isusehttps = SEAGridContext.getInstance().isUseHttps();
        rootremotepath = (isusehttps ? "https" : "http") + "://" + servername + "/" + basepath + "/" + SEAGridContext.getInstance().getUserName();
        token = SEAGridContext.getInstance().getOAuthToken();
            sardine.setCredentials(SEAGridContext.getInstance().getUserName(), SEAGridContext.getInstance().getClientID());
            sardine.enablePreemptiveAuthentication(SEAGridContext.getInstance().getNextcloudServername());
    }

    public static NextcloudStorageManager getInstance() throws JSchException, IOException {
        if(instance==null) {
            instance = new NextcloudStorageManager();
        }
        return instance;
    }

    public List<DavResource> listDirectories(String remotepath) throws IOException {
        String path = rootremotepath + remotepath;
        List<DavResource> resources;
        if(sardine.exists(path)) {
            try {
                resources = sardine.list(path, 1);
                return resources;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Creates the folder if the folder is not present at the remote path of the nextcloud storage
     * @param remotepath
     * @throws IOException
     */
    public void createFolderifNotExist(String remotepath) throws IOException {
        String[] segments = remotepath.split("/");

        String tempath = "";
        for (int i = 1; i < segments.length; i++) {
            tempath = tempath + "/" + segments[i];
            String path = rootremotepath + tempath;
            if (!sardine.exists(path)) {
                createFolder(tempath);
            }
        }
    }

    /**
     * Create the folder at the specified path
     * @param remotepath
     */
    public void createFolder(String remotepath)
    {
        String path=  rootremotepath+remotepath;

        try {
            sardine.createDirectory(path);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
    }
}
