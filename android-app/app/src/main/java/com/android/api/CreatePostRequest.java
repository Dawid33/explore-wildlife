package com.android.api;
import android.graphics.Bitmap;

import com.android.Global;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreatePostRequest implements Callable<CreatePostRequest.CreatePostRequestResult> {
    public static final String createPostApiUrl = Global.baseUrl + "/api/create-post";
    String title, imageId, description = "", category = "SCENERY", species = "";
    double longitude = 0, latitude = 0;

    public class CreatePostRequestResult {
        public String error;
        public boolean success;

        public CreatePostRequestResult(boolean success, String error) {
            this.error = error;
            this.success = success;
        }
    }

    public CreatePostRequest(String title, String imageId) {
        this.title = title;
        this.imageId = imageId;
    }

    public CreatePostRequest(String title, String imageId, double longitude, double latitude, String description, String category, String species) {
        this.title = title.replaceAll("'", "''").replaceAll("\"", "\"\"");
        this.imageId = imageId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description.replaceAll("'", "''").replaceAll("\"", "\"\"");
        this.category = category;
        this.species = species;
    }

    @Override
    public CreatePostRequestResult call() {
        OkHttpClient client = new OkHttpClient();
        Request request = null;
        try (ByteArrayOutputStream s = new ByteArrayOutputStream()) {
            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("post_title", this.title)
                    .addFormDataPart("post_description", this.description)
                    .addFormDataPart("post_latitude", String.valueOf(this.latitude))
                    .addFormDataPart("post_longitude", String.valueOf(this.longitude))
                    .addFormDataPart("created_by", Global.loggedInUserID)
                    .addFormDataPart("post_image_id", this.imageId)
                    .addFormDataPart("post_category", this.category)
                    .addFormDataPart("species_name", this.species)
                    .build();

            request = new Request.Builder()
                    .url(createPostApiUrl)
                    .post(body)
                    .build();
        } catch(Exception e) {
            e.printStackTrace();
            return new CreatePostRequestResult(false, "Unknown error occurred.");
        }

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return new CreatePostRequestResult(false, "Unexpected code " + response);
            }

            JSONObject json = new JSONObject(response.body().string());
            if (json.has("success")) {
                boolean success = (boolean) json.get("success");
                if (success) {
                    return new CreatePostRequestResult(true, "");
                } else if (json.has("error")) {
                    return new CreatePostRequestResult(false, (String) json.get("error"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CreatePostRequestResult(false, "Unknown error occurred.");
    }
}
