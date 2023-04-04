package com.android.api;

import android.graphics.Bitmap;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class CreatePostRequest implements Callable<CreatePostRequest.CreatePostRequestResult>  {
//    private static final String registerApiUrl = "https://explorewildlife.net/api/register";
private static final String registerApiUrl = "http://localhost/api/register";

    String createdBy, postTitle, postDescription;
    float latitude, longitude;
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

    public CreatePostRequest(String createdBy, String postTitle, String postDescription, float latitude, float longitude, Bitmap postImage) {
        this.createdBy = createdBy;
        this.postTitle = postTitle;
        this.postDescription = postDescription;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postImage = postImage;
    }

    @Override
    public CreatePostRequest.CreatePostRequestResult call() {
        HttpURLConnection urlConnection = null;

        // Create Multipart form to send with the posts request.
        String boundary = "===" + System.currentTimeMillis() + "===";
        MultipartFormBody form = new MultipartFormBody(boundary);
        form.addField("created_by", this.createdBy);
        form.addField("post_title", this.postTitle);
        form.addField("post_description", this.postDescription);
        form.addField("post_latitude", String.valueOf(this.latitude));
        form.addField("post_longitude", String.valueOf(this.longitude));

        try {
            URL url = new URL(registerApiUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // Open a stream to write data to the request body
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            // Write the multipart form to the body of the request
            writer.write(form.toString());
            writer.close();
            urlConnection.connect();

            // Read the response
            String response = Utils.readStream(urlConnection.getInputStream());

            JSONObject json = new JSONObject(response);
            System.out.println(json);
            if (json.has("success")) {
                boolean success = (boolean) json.get("success");
                if (success) {
                    return new CreatePostRequest.CreatePostRequestResult(true, "");
                } else if (json.has("error")) {
                    return new CreatePostRequest.CreatePostRequestResult(false, (String) json.get("error"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Bypass login on error because of strange ssl issues on campus.
//            return true;
        } finally {
            if (urlConnection != null) { urlConnection.disconnect(); }
        }
        return new CreatePostRequest.CreatePostRequestResult(false, "Unknown error occurred.");
    }
}
