package com.android.api;

import org.json.JSONArray;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class GetImageRequest implements Callable<GetImageRequest.GetImageRequestResult> {
    public static final String postsApiUrl = "https://explorewildlife.net/api/image?id=";
    String imageId;

    public class GetImageRequestResult {
        public JSONArray posts;
        public boolean requestSucceeded;

        public GetImageRequestResult(boolean requestSucceded, JSONArray posts) {
            this.requestSucceeded = requestSucceded;
            this.posts = posts;
        }
    }

    public GetImageRequest(String imageId) {
        this.imageId = imageId;
    }

    @Override
    public GetImageRequestResult call() throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(postsApiUrl + imageId);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.connect();

            return new GetImageRequestResult(true, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return new GetImageRequestResult(false, null);
    }
}
