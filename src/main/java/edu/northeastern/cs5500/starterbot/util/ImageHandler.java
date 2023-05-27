package edu.northeastern.cs5500.starterbot.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

/** The Image handler Util Class. */
public class ImageHandler {

    /**
     * Upload to imgur string.
     *
     * @param inputStream the input stream
     * @return the string
     * @throws IOException the io exception
     */
    public static String uploadToImgur(InputStream inputStream) throws IOException {
        byte[] imageData = toByteArray(inputStream);
        return uploadToImgur(imageData);
    }

    /**
     * Gets imgur uri.
     *
     * @return the imgur uri
     */
    public static String getImgurURI() {
        String uri = new ProcessBuilder().environment().get("IMGUR_URI");
        if (uri == null) {
            throw new IllegalStateException("IMGUR_URI environment variable not set");
        }
        return uri;
    }

    /**
     * Gets imgur client id.
     *
     * @return the imgur client id
     */
    public static String getImgurClientId() {
        String clientId = new ProcessBuilder().environment().get("IMGUR_CLIENT_ID");
        if (clientId == null) {
            throw new IllegalStateException("IMGUR_CLIENT_ID environment variable not set");
        }
        return clientId;
    }

    /**
     * To byte array byte [ ].
     *
     * @param inputStream the input stream
     * @return the byte [ ]
     */
    public static byte[] toByteArray(InputStream inputStream) {
        byte[] bytes = new byte[1024];
        try (inputStream) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            bytes = output.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * Upload to imgur string.
     *
     * @param imageData the image data
     * @return the string
     * @throws IOException the io exception
     */
    public static String uploadToImgur(byte[] imageData) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String clientId = getImgurClientId();
        String url = getImgurURI();
        RequestBody requestBody =
                new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(
                                "image",
                                "image.png",
                                RequestBody.create(MediaType.parse("image/png"), imageData))
                        .build();
        Request request =
                new Request.Builder()
                        .header("Authorization", "Client-ID " + clientId)
                        .url(url)
                        .post(requestBody)
                        .build();
        JSONObject response = new JSONObject(client.newCall(request).execute().body().string());
        return response.getJSONObject("data").getString("link");
    }
}
