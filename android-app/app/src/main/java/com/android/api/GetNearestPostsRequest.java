package com.android.api;

import com.android.Global;

import org.json.JSONArray;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class GetNearestPostsRequest implements Callable<GetNearestPostsRequest.GetNearestPostsRequestResult> {
    public static String postsApiUrl = Global.baseUrl + "/api/post/nearest";
    int numResults = 10;
    double longitude;
    double latitude;

    public class GetNearestPostsRequestResult {
        public JSONArray posts;
        public boolean requestSucceeded;

        public GetNearestPostsRequestResult(boolean requestSucceded, JSONArray posts) {
            this.requestSucceeded = requestSucceded;
            this.posts = posts;
        }
    }

    public GetNearestPostsRequest(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void updateUrl(){
        postsApiUrl = Global.baseUrl + "/api/posts/popular" +  "?user_id=" + Global.loggedInUserID + "&num_results=" + Integer.toString(this.numResults);
    }

    @Override
    public GetNearestPostsRequestResult call() throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(postsApiUrl + "?long=" + this.longitude + "&lat=" + this.latitude);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.connect();

            String response = Utils.readStream(urlConnection.getInputStream());
            JSONArray array = new JSONArray(response);

            return new GetNearestPostsRequestResult(true, array);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return new GetNearestPostsRequestResult(false, null);
    }
}
