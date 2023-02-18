package com.android.api;

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
    private static final String registerApiUrl = "https://explorewildlife.net/api/register";
    String userName, firstName, lastName, email, password, phoneNumber;

    public class RegisterRequestResult {
        public String error;
        public boolean isRegistered;

        public RegisterRequestResult(boolean isLoggedIn, String error) {
            this.error = error;
            this.isRegistered = isLoggedIn;
        }
    }

    public RegisterRequest(String userName, String firstName, String lastName, String email, String password, String phoneNumber) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
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

//        --------- TEMPORARY SOLUTION - DISPLAY NAME = FIRST + LAST NAME

        String displayName = this.firstName + '_' + this.lastName;

        form.addField("display_name", displayName);
//        form.addField("first_name", this.firstName);
//        form.addField("last_name", this.lastName);
//        form.addField("phone_number", this.phoneNumber);

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
                    return new RegisterRequestResult(true, "");
                } else if (json.has("error")) {
                    return new RegisterRequestResult(false, (String) json.get("error"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Bypass login on error because of strange ssl issues on campus.
//            return true;
        } finally {
            if (urlConnection != null) { urlConnection.disconnect(); }
        }
        return new RegisterRequestResult(false, "Unknown error occurred.");
    }
}