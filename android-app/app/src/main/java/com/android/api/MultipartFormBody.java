package com.android.api;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class MultipartFormBody {
    final String LINE_FEED = "\r\n";
    HashMap<String, Value> keys;
    String boundary;

    public class Value {
        public Value(String str) {
            this.str = str;
            this.image= null;
        }

        public Value(Bitmap image) {
            this.str = "";
            this.image = image;
        }
        public String str;
        public Bitmap image;
    }

    public MultipartFormBody(String boundary) {
        this.keys = new HashMap<>();
        this.boundary = boundary;
    }

    public void addField(String key, String value) {
        this.keys.put(key, new Value(value));
    }

    public void addFile(String key, Bitmap value) {
        this.keys.put(key, new Value(value));
    }

    @NonNull
    public String buildForm(File path) {
        StringBuilder b = new StringBuilder();

        for(Entry<String, Value> e : this.keys.entrySet()) {
            if (e.getValue().image == null) {
                b.append("--").append(this.boundary).append(LINE_FEED);
                b.append("Content-Disposition: form-data; name=\"").append(e.getKey()).append("\"") .append(LINE_FEED);
                b.append("Content-Type: text/plain;").append( LINE_FEED);
                b.append(LINE_FEED);
                b.append(e.getValue().str).append(LINE_FEED);
            } else {
                b.append("--").append(this.boundary).append(LINE_FEED);
                b.append("Content-Disposition: form-data; name=\"").append(e.getKey()).append("\"; filename=\"").append("test").append("\"").append(LINE_FEED);
                b.append("Content-Type: application/octet-stream").append(LINE_FEED);// + URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
                b.append(LINE_FEED);
                try (ByteArrayOutputStream s = new ByteArrayOutputStream()) {
                    e.getValue().image.compress(Bitmap.CompressFormat.PNG, 50, s);
                    byte[] raw = s.toByteArray();
                    File f = new File(path + "test.png");
                    FileOutputStream sf = new FileOutputStream(f);

                    sf.write(raw);
                    b.append(new String(raw)).append(LINE_FEED);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

//                InputStream inputStream = null;
//                try {

//                    inputStream = activity.getContentResolver().openInputStream(e.getValue());
//                    assert inputStream != null;
//
//                    // read file content
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                    String mLine;
//                    StringBuilder stringBuilder = new StringBuilder();
//                    while ((mLine = bufferedReader.readLine()) != null) {
//                        stringBuilder.append(mLine);
//                    }
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                    b.append("lol").append(LINE_FEED);
//                }
            }
        }
        b.append("--").append(boundary).append("--").append(LINE_FEED);
        b.append(LINE_FEED);
        return b.toString();
    }
}
