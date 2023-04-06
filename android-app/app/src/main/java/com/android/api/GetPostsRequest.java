package com.android.api;

import android.util.JsonReader;
import android.util.JsonWriter;

import com.android.Global;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

    public class GetPostsRequest implements Callable<GetPostsRequest.GetPostsRequestResult> {
        public static final String postsApiUrl = Global.baseUrl + "/api/posts";

        public class GetPostsRequestResult {
            public JSONArray posts;
            public boolean requestSucceeded;

            public GetPostsRequestResult(boolean requestSucceded, JSONArray posts) {
                this.requestSucceeded = requestSucceded;
                this.posts = posts;
            }
        }

        @Override
        public GetPostsRequestResult call() throws Exception {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(postsApiUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.connect();

                String response = Utils.readStream(urlConnection.getInputStream());
                JSONArray array = new JSONArray(response);

                return new GetPostsRequestResult(true, array);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return new GetPostsRequestResult(false, null);
        }
}
