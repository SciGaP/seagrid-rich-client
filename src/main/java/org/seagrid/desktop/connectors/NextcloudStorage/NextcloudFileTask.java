package org.seagrid.desktop.connectors.NextcloudStorage;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import javafx.concurrent.Task;
import org.seagrid.desktop.connectors.NextcloudStorage.Exception.NextcloudApiException;
import org.seagrid.desktop.util.SEAGridContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public abstract class NextcloudFileTask extends Task<Boolean> {
    private final static Logger logger = LoggerFactory.getLogger(org.seagrid.desktop.connectors.NextcloudStorage.NextcloudFileTask.class);

    protected Sardine sardine = SardineFactory.begin();

    protected String servername;
    protected String basepath;
    protected boolean isusehttps;
    private String token;
    private String rootremotepath;

    public NextcloudFileTask() throws IOException {
        servername = SEAGridContext.getInstance().getNextcloudServername();
        basepath = SEAGridContext.getInstance().getDavBasepath();
        isusehttps = SEAGridContext.getInstance().isUseHttps();
        rootremotepath = (isusehttps ? "https" : "http") + "://" + servername + "/" + basepath + "/" + SEAGridContext.getInstance().getUserName();
        token = SEAGridContext.getInstance().getOAuthToken();
            sardine.setCredentials(SEAGridContext.getInstance().getUserName(), token);
            sardine.enablePreemptiveAuthentication(SEAGridContext.getInstance().getNextcloudServername());
    }

    /**
     * Create the folder at the specified path
     * @param remotepath
     */
    public void createFolder(String remotepath){
        String path=  rootremotepath+remotepath;

        try {
            sardine.createDirectory(path);
        } catch (IOException e) {
            throw new NextcloudApiException(e);
        }
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
}


