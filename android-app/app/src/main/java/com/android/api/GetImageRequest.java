package com.android.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaCodecList;

import org.json.JSONArray;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class GetImageRequest implements Callable<GetImageRequest.GetImageRequestResult> {
    public static final String postsApiUrl = "https://explorewildlife.net/api/image?id=";
    String imageId;

    public class GetImageRequestResult {
        public Image image;
        public boolean requestSucceeded;

        public GetImageRequestResult(boolean requestSucceded, Image image) {
            this.requestSucceeded = requestSucceded;
            this.image = image;
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
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BigInteger contentLength = new BigInteger(urlConnection.getHeaderField("content-length"));
                System.out.println(contentLength);
                DataInputStream s = new DataInputStream(urlConnection.getInputStream());
                byte[] rawBytes =  new byte[contentLength.intValue()];
                s.readFully(rawBytes);
            } else {
                return new GetImageRequestResult(false, null);
            }

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
