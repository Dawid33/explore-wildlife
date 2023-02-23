package com.android.api;

import android.util.JsonReader;
import android.util.JsonWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class AccountRetrievalRequest implements Callable<AccountRetrievalRequest.AccountRetrievalRequestResult> {

//    NOT TESTED This will point to a link of a specific user
    public static String userID;
    public static final String userApiUrl = "https://explorewildlife.net/api/users/" + userID;

    public class AccountRetrievalRequestResult {
        public JSONArray posts;
        public boolean requestSucceeded;

        public AccountRetrievalRequestResult(boolean requestSucceeded, JSONArray posts) {
            this.requestSucceeded = requestSucceeded;
            this.posts = posts;
        }
    }

    @Override
    public AccountRetrievalRequestResult call() throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(userApiUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.connect();

            // Getting the response as a String, the JSONArray constructor will convert the string to a
            String response = Utils.readStream(urlConnection.getInputStream());
            JSONArray array = new JSONArray(response);

            return new AccountRetrievalRequestResult(true, array);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return new AccountRetrievalRequestResult(false, null);
    }
}
