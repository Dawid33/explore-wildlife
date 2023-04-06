package com.android.api;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.textclassifier.TextLinks;

import com.android.Global;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadImageRequest implements Callable<UploadImageRequest.UploadImageRequestResult> {

    public static final String uploadRequestApi = Global.baseUrl + "/api/upload_image";
    public Bitmap image;

    public class UploadImageRequestResult {
        public String error;
        public boolean requestSucceeded;
        public JSONObject data;

        public UploadImageRequestResult(boolean requestSucceeded, String error, JSONObject data) {
            this.error = error;
            this.requestSucceeded = requestSucceeded;
            this.data = data;
        }
    }

    public UploadImageRequest(Bitmap image) {
        this.image = image;
    }

    @Override
    public UploadImageRequestResult call() throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = null;
        try (ByteArrayOutputStream s = new ByteArrayOutputStream()) {
            image.compress(Bitmap.CompressFormat.PNG, 50, s);
            byte[] raw = s.toByteArray();

            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "test", RequestBody.create(raw))
                    .build();

            request = new Request.Builder()
                    .url(uploadRequestApi)
                    .post(body)
                    .build();
        } catch(Exception e) {
            e.printStackTrace();
            return new UploadImageRequestResult(false, "Unknown error occurred.", null);
        }

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return new UploadImageRequestResult(false, "Unexpected code " + response, null);
            }

            JSONObject json = new JSONObject(response.body().string());
            if (json.has("success")) {
                boolean success = (boolean) json.get("success");
                if (success) {
                    return new UploadImageRequestResult(true, "", json);
                } else if (json.has("error")) {
                    return new UploadImageRequestResult(false, (String) json.get("error"), null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//             Bypass login on error because of strange ssl issues on campus.
//            return true;
        }
        return new UploadImageRequestResult(false, "Unknown error occurred.", null);
    }
}