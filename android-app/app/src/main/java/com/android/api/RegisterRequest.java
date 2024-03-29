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

public class RegisterRequest implements Callable<RegisterRequest.RegisterRequestResult> {
    private static final String registerApiUrl = Global.baseUrl + "/api/register";
    String username, email, password, phoneNumber;

    public class RegisterRequestResult {
        public String error;
        public boolean isRegistered;
        public String registeredUuid;

        public RegisterRequestResult(boolean isLoggedIn, String error, String registeredUuid) {
            this.error = error;
            this.isRegistered = isLoggedIn;
            this.registeredUuid = registeredUuid;
        }
    }

    public RegisterRequest(String username, String email, String password, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public RegisterRequestResult call() {
        HttpURLConnection urlConnection = null;

        // Create Multipart form to send with the posts request.
        String boundary = "===" + System.currentTimeMillis() + "===";
        MultipartFormBody form = new MultipartFormBody(boundary);
        form.addField("email", this.email);
        form.addField("password", this.password);
        form.addField("display_name", this.username);

        // Adding the phone number filed.
        // form.addField("phone_number", this.phoneNumber);

        try {
            URL url = new URL(registerApiUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
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
                    return new RegisterRequestResult(true, "", (String) json.get("user_id"));
                } else if (json.has("error")) {
                    return new RegisterRequestResult(false, (String) json.get("error"), null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Bypass login on error because of strange ssl issues on campus.
//            return true;
        } finally {
            if (urlConnection != null) { urlConnection.disconnect(); }
        }
        return new RegisterRequestResult(false, "Unknown error occurred.", null);
    }
}