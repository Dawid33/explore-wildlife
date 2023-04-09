package com.android.api;

import com.android.Global;

import org.json.JSONArray;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class GetPopularPostsRequest implements Callable<GetPopularPostsRequest.GetPopularPostsRequestResult> {
    public static String postsApiUrl = Global.baseUrl + "/api/posts/popular" +  "?user_id=" + Global.loggedInUserID;
    int numResults = 10;

    public class GetPopularPostsRequestResult {
        public JSONArray posts;
        public boolean requestSucceeded;

        public GetPopularPostsRequestResult(boolean requestSucceded, JSONArray posts) {
            this.requestSucceeded = requestSucceded;
            this.posts = posts;
        }
    }

    public GetPopularPostsRequest(int numResults){
        this.numResults = numResults;
    }

    public GetPopularPostsRequest(){}

    private void updateUrl(){
        postsApiUrl = Global.baseUrl + "/api/posts/popular" +  "?user_id=" + Global.loggedInUserID + "&num_results=" + Integer.toString(this.numResults);
    }

    @Override
    public GetPopularPostsRequest.GetPopularPostsRequestResult call() throws Exception {
        updateUrl();
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(postsApiUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.connect();

            String response = Utils.readStream(urlConnection.getInputStream());
            JSONArray array = new JSONArray(response);

            return new GetPopularPostsRequest.GetPopularPostsRequestResult(true, array);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return new GetPopularPostsRequest.GetPopularPostsRequestResult(false, null);
    }
}
