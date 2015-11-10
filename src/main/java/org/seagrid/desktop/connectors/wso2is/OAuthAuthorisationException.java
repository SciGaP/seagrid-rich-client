package org.seagrid.desktop.connectors.wso2is;


public class OAuthAuthorisationException extends Exception{
    public OAuthAuthorisationException () {

    }

    public OAuthAuthorisationException (String message) {
        super (message);
    }

    public OAuthAuthorisationException (Throwable cause) {
        super (cause);
    }

    public OAuthAuthorisationException (String message, Throwable cause) {
        super (message, cause);
    }
}
