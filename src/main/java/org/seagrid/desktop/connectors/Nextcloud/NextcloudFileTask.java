package org.seagrid.desktop.connectors.Nextcloud;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import org.seagrid.desktop.util.SEAGridContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NextcloudFileTask {
    private final static Logger logger = LoggerFactory.getLogger(NextcloudFileTask.class);

    protected Sardine sardine = SardineFactory.begin();

    protected String servername;
    protected String basepath;
    protected boolean isusehttps;
    private String token;

    /**
     * Constructor
     */
    NextcloudFileTask() {
        servername = SEAGridContext.getInstance().getNextcloudServername();
        basepath = SEAGridContext.getInstance().getDavBasepath();
        isusehttps = SEAGridContext.getInstance().isUseHttps();
    }

    /**
     *Authenticate the session with the token and set the credentials to login
     *
     * @return boolean if the session is authenticated returns true
     * @throws IOException
     */
    public boolean AuthenticateSession() throws IOException {
        GenerateTokenTask t1 = new GenerateTokenTask();
        token = t1.generateToken();
        ValidateTokenTask t2 = new ValidateTokenTask();
        if(t2.ValidateToken(token) == "200") {
            sardine.setCredentials(SEAGridContext.getInstance().getUserName(), token);
            sardine.enablePreemptiveAuthentication(SEAGridContext.getInstance().getNextcloudServername());
            return true;
        }
       return false;
    }

    /**
     * method to check if a file/folder is already present in the remote path
     *
     * @param rootpath of the file (full path)
     * @return boolean value is returned depending on the existance of the file
     *
     * Reference: https://github.com/a-schild/nextcloud-java-api
     */

    public boolean Exists(String rootpath){
        String path = (isusehttps ? "https" : "http") + "://" + servername + "/" + basepath  + SEAGridContext.getInstance().getUserName() + rootpath;
        try {
            if(sardine.exists(rootpath)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
