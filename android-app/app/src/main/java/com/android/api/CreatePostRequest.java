package com.android.api;

import android.graphics.Bitmap;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.Callable;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreatePostRequest implements Callable<CreatePostRequest.CreatePostRequestResult> {
//        private static final String createPostApiUrl = "https://explorewildlife.net/api/register";
//    Loopback to dev machine
    private static final String createPostApiUrl = "http://10.0.2.2:8080/api/create-post";

    private final OkHttpClient client = new OkHttpClient();

    String createdBy, postTitle, postDescription;
    double latitude, longitude;
    Bitmap postImage;

    public class CreatePostRequestResult {
        public String error;
        public boolean hasError;

//        public CreatePostRequestResult(boolean isLoggedIn, String error) {
//            this.error = error;
//            this.isRegistered = isLoggedIn;
//        }

        public CreatePostRequestResult(boolean hasError, String error) {
            this.hasError = hasError;
            this.error = error;
        }
    }

    public CreatePostRequest(String createdBy, String postTitle, String postDescription, double latitude, double longitude, Bitmap postImage) {
        this.createdBy = createdBy;
        this.postTitle = postTitle;
        this.postDescription = postDescription;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postImage = postImage;
    }

    @Override
    public CreatePostRequest.CreatePostRequestResult call() {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        postImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("created_by", this.createdBy)
                .addFormDataPart("post_title", this.postTitle)
                .addFormDataPart("post_description", this.postDescription)
                .addFormDataPart("post_latitude", String.valueOf(this.latitude))
                .addFormDataPart("post_longitude", String.valueOf(this.longitude))
                .addFormDataPart("image", generateRandomString(12) +".jpg",
                        RequestBody.create(byteArray, MediaType.parse("image/*jpg")))
                .build();

        Request request = new Request.Builder()
                .url(createPostApiUrl)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected: " + response);
            }

            System.out.println(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new CreatePostRequest.CreatePostRequestResult(true, "");

//        HttpURLConnection urlConnection = null;
//
//        // Create Multipart form to send with the posts request.
//        String boundary = "===" + System.currentTimeMillis() + "===";
//        MultipartFormBody form = new MultipartFormBody(boundary);
//        form.addField("created_by", this.createdBy);
//        form.addField("post_title", this.postTitle);
//        form.addField("post_description", this.postDescription);
//        form.addField("post_latitude", String.valueOf(this.latitude));
//        form.addField("post_longitude", String.valueOf(this.longitude));
//
//
//
//        try {
//            URL url = new URL(createPostApiUrl);
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setDoOutput(true);
//            urlConnection.setChunkedStreamingMode(0);
//            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
//
//            // Open a stream to write data to the request body
//            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
//            // Write the multipart form to the body of the request
//            writer.write(form.toString());
//            writer.close();
//            urlConnection.connect();
//
//            // Read the response
//            String response = Utils.readStream(urlConnection.getInputStream());
//
//            JSONObject json = new JSONObject(response);
//            System.out.println(json);
//            if (json.has("success")) {
//                boolean success = (boolean) json.get("success");
//                if (success) {
//                    return new CreatePostRequest.CreatePostRequestResult(true, "");
//                } else if (json.has("error")) {
//                    return new CreatePostRequest.CreatePostRequestResult(false, (String) json.get("error"));
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            // Bypass login on error because of strange ssl issues on campus.
////            return true;
//        } finally {
//            if (urlConnection != null) { urlConnection.disconnect(); }
//        }
//        return new CreatePostRequest.CreatePostRequestResult(false, "Unknown error occurred.");
    }

    private static String generateRandomString(int numberChars) {
        byte[] array = new byte[numberChars];
        new Random().nextBytes(array);
        String generatedString = new String(array, StandardCharsets.UTF_8);

        return generatedString;
    }
}
