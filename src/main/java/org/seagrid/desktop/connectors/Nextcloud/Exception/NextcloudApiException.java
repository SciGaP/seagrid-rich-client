package org.seagrid.desktop.connectors.Nextcloud.Exception;

public class NextcloudApiException extends RuntimeException {
    private static final long serialVersionUID = 8088239559973590632L;

    public NextcloudApiException(Throwable cause) {
        super(cause);
    }

    public NextcloudApiException(String message) {
        super(message);
    }
}
