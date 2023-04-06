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
    String title, imageId;

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

    @Override
    public CreatePostRequestResult call() {
        OkHttpClient client = new OkHttpClient();
        Request request = null;
        try (ByteArrayOutputStream s = new ByteArrayOutputStream()) {
            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("post_title", this.title)
                    .addFormDataPart("post_description", "default desc")
                    .addFormDataPart("post_latitude", "1")
                    .addFormDataPart("post_longitude", "1")
                    .addFormDataPart("created_by", Global.loggedInUserID)
                    .addFormDataPart("post_image_id", this.imageId)
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
