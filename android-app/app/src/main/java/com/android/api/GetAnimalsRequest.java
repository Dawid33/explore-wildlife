package com.android.api;

import com.android.Global;

import org.json.JSONArray;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class GetAnimalsRequest implements Callable<GetAnimalsRequest.GetAnimalRequestResponse> {
    private String userID;
    public String animalApiUrl = Global.baseUrl + "/api/species/";

    public class GetAnimalRequestResponse {
        public JSONArray animals;
        public boolean requestSucceeded;

        public GetAnimalRequestResponse(boolean requestSucceeded, JSONArray account) {
            this.requestSucceeded = requestSucceeded;
            this.animals = account;
        }
    }

//    This can probably set the user ID to some sort of global value?
    public GetAnimalsRequest(String userID) {
        this.userID = userID;
    }

    @Override
    public GetAnimalRequestResponse call() throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(animalApiUrl + userID);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.connect();

            // Getting the response as a String, the JSONObject constructor will convert the string to an Object
            String response = Utils.readStream(urlConnection.getInputStream());
            JSONArray object = new JSONArray(response);

            return new GetAnimalRequestResponse(true, object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return new GetAnimalRequestResponse(false, null);
    }
}
