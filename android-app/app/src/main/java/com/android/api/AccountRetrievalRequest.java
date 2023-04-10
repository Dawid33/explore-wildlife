package com.android.api;

import android.graphics.Bitmap;
import android.util.JsonReader;
import android.util.JsonWriter;

import com.android.Global;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PushbackReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class AccountRetrievalRequest implements Callable<AccountRetrievalRequest.AccountRetrievalRequestResult> {
    private String userID;
    public String userApiUrl = Global.baseUrl + "/api/users/";

    public class AccountRetrievalRequestResult {
        public JSONObject account;
        public boolean requestSucceeded;
        public Bitmap pfp;

        public AccountRetrievalRequestResult(boolean requestSucceeded, JSONObject account, Bitmap pfp) {
            this.requestSucceeded = requestSucceeded;
            this.account = account;
        }
    }

//    This can probably set the user ID to some sort of global value?
    public AccountRetrievalRequest(String userID) {
        this.userID = userID;
    }

    @Override
    public AccountRetrievalRequestResult call() throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(userApiUrl + userID);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.connect();

            // Getting the response as a String, the JSONObject constructor will convert the string to an Object
            String response = Utils.readStream(urlConnection.getInputStream());
            JSONObject object = new JSONObject(response);

            return new AccountRetrievalRequestResult(true, object, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return new AccountRetrievalRequestResult(false, null, null);
    }
}
