package org.seagrid.desktop.connectors.Nextcloud;

import org.json.JSONObject;
import org.seagrid.desktop.util.SEAGridContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class GenerateTokenTask {

    private String client_id;
    private String username;
    private String password;
    private String tokenendpoint;
    private String grant_type;
    private String client_secret;

    GenerateTokenTask() {
        client_id = SEAGridContext.getInstance().getClientID();
        username = SEAGridContext.getInstance().getUserName();
        //Still need to work on this
        password = "embedpassword";
        tokenendpoint = SEAGridContext.getInstance().getTokenGenerateEndpoint();
        grant_type = SEAGridContext.getInstance().getGrantType();
    }

    /**
     * Generates the token and returns the access_token if the user credentials are valid
     *
     * @return String
     * @throws IOException
     */
    public String generateToken() throws IOException {
        String access_token;
        URL url = new URL(tokenendpoint);
        Map<String, String> params = new LinkedHashMap<>();
        params.put("client_id", client_id);
        params.put("username", username);
        //Need to check this and work on getting the password as it is required to generate the token
        params.put("password", password);
        params.put("grant_type", grant_type);
        //Need to find a better way to emebed the client-secret
        params.put("client_secret",client_secret);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(String.valueOf(param.getKey()), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));

        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;)
            sb.append((char)c);
        String response = sb.toString();
        JSONObject myResponse = new JSONObject(response.toString());
        access_token = myResponse.getString("access_token");
        return access_token;
    }
}
