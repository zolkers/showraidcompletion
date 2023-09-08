package net.edgn.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;

public class WynnApiUtils {
    public static String getStringFromURL(String url) {
        StringBuilder response = new StringBuilder();
        try {
            URL turl = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) turl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}

