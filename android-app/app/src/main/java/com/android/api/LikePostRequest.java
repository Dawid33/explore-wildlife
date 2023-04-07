package com.android.api;

import com.android.Global;

import org.json.JSONArray;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class LikePostRequest implements Callable<LikePostRequest.LikePostRequestResult> {

    private String likeApiUrl = Global.baseUrl + "/api/posts/";

    private String postID;

    public LikePostRequest(String postID){
        this.postID = postID;

        likeApiUrl = Global.baseUrl + "api/" + this.postID + "/toggle-like";
    }

    public class LikePostRequestResult {
        public boolean requestSucceeded;

        public LikePostRequestResult(boolean requestSucceeded) {
            this.requestSucceeded = requestSucceeded;
        }
    }

    @Override
    public LikePostRequest.LikePostRequestResult call() throws Exception {
        HttpURLConnection urlConnection = null;

        String boundary = "===" + System.currentTimeMillis() + "===";
        MultipartFormBody form = new MultipartFormBody(boundary);
        form.addField("user_id", Global.loggedInUserID);

        try {
            URL url = new URL(likeApiUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.connect();
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // Open a stream to write data to the request body
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            // Write the multipart form to the body of the request
            writer.write(form.buildForm());
            writer.close();
            urlConnection.connect();

            // Read the response
            String response = Utils.readStream(urlConnection.getInputStream());

            return new LikePostRequest.LikePostRequestResult(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return new LikePostRequest.LikePostRequestResult(false);
    }

}
