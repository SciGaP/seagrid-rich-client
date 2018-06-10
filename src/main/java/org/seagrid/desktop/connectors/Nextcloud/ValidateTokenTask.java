package org.seagrid.desktop.connectors.Nextcloud;

import org.json.JSONObject;
import org.seagrid.desktop.util.SEAGridContext;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class ValidateTokenTask {

    /**
     * Returns 200 status if the token is successfully validated.
     *
     * @param token
     * @return
     * @throws IOException
     */
    public String ValidateToken(String token) throws IOException {
        URL url = null;
        try {
            url = new URL(SEAGridContext.getInstance().getValidationURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Map<String, String> params = new LinkedHashMap<>();
        params.put("username", SEAGridContext.getInstance().getUserName());
        params.put("token", token);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            try {
                postData.append(URLEncoder.encode(String.valueOf(param.getKey()), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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
        System.out.println(response);
        JSONObject myResponse = new JSONObject(response.toString());
        return myResponse.get("status").toString();
    }
}
