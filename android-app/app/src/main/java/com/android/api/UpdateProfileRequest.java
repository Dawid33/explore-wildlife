package com.android.api;

import com.android.Global;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class UpdateProfileRequest implements Callable<UpdateProfileRequest.UpdateProfileRequestResult> {
    private static final String registerApiUrl = Global.baseUrl + "/api/users/" + Global.loggedInUserID;
    String username, email, phoneNumber = "";

    public class UpdateProfileRequestResult {
        public String error;
        public boolean isRegistered;

        public UpdateProfileRequestResult(boolean isLoggedIn, String error) {
            this.error = error;
            this.isRegistered = isLoggedIn;
        }
    }

    public UpdateProfileRequest(String username, String email, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public UpdateProfileRequestResult call() {
        HttpURLConnection urlConnection = null;

        // Create Multipart form to send with the posts request.
        String boundary = "===" + System.currentTimeMillis() + "===";
        MultipartFormBody form = new MultipartFormBody(boundary);
        form.addField("email", this.email);
        form.addField("display_name", this.username);
        form.addField("phone_number", this.phoneNumber);

        // Adding the phone number filed.
        // form.addField("phone_number", this.phoneNumber);

        try {
            URL url = new URL(registerApiUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // Open a stream to write data to the request body
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            // Write the multipart form to the body of the request
            writer.write(form.buildForm());
            writer.close();
            urlConnection.connect();

            // Read the response
            String response = Utils.readStream(urlConnection.getInputStream());

            JSONObject json = new JSONObject(response);
            System.out.println(json);
            if (json.has("success")) {
                boolean success = (boolean) json.get("success");
                if (success) {
                    return new UpdateProfileRequestResult(true, "");
                } else if (json.has("error")) {
                    return new UpdateProfileRequestResult(false, (String) json.get("error"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Bypass login on error because of strange ssl issues on campus.
//            return true;
        } finally {
            if (urlConnection != null) { urlConnection.disconnect(); }
        }
        return new UpdateProfileRequestResult(false, "Unknown error occurred.");
    }
}