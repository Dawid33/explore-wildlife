package com.android.api;
import com.android.Global;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class CreatePostRequest implements Callable<CreatePostRequest.CreatePostRequestResult> {
    public static final String createPostApiUrl = Global.baseUrl + "/api/login";
    String email, password;

    public class CreatePostRequestResult {
        public String error;
        public boolean isLoggedIn;

        public CreatePostRequestResult(boolean isLoggedIn, String error) {
            this.error = error;
            this.isLoggedIn = isLoggedIn;
        }
    }

    public CreatePostRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public CreatePostRequestResult call() {
        HttpURLConnection urlConnection = null;

        // Create Multipart form to send with the posts request.
        String boundary = "===" + System.currentTimeMillis() + "===";
        MultipartFormBody form = new MultipartFormBody(boundary);
        form.addField("email", this.email);
        form.addField("password", this.password);

        try {
            URL url = new URL(createPostApiUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // Open a stream to write data to the request body
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            // Write the multipart form to the body of the request
//            writer.write(form.buildForm());
            writer.close();
            urlConnection.connect();

            // Read the response
            String response = Utils.readStream(urlConnection.getInputStream());

            JSONObject json = new JSONObject(response);
            System.out.println(json);
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
            // Bypass login on error because of strange ssl issues on campus.
//            return true;
        } finally {
            if (urlConnection != null) { urlConnection.disconnect(); }
        }
        return new CreatePostRequestResult(false, "Unknown error occurred.");
    }
}
