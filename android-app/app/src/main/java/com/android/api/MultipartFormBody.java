package com.android.api;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map.Entry;

public class MultipartFormBody {
    final String LINE_FEED = "\r\n";
    HashMap<String, String> keys;
    String boundary;

    public MultipartFormBody(String boundary) {
        this.keys = new HashMap<>();
        this.boundary = boundary;
    }

    public void addField(String key, String value) {
        this.keys.put(key, value);
    }

    @NonNull
    public String toString() {
        StringBuilder b = new StringBuilder();

        for(Entry<String, String> e : this.keys.entrySet()) {
            b.append("--").append(this.boundary).append(LINE_FEED);
            b.append("Content-Disposition: form-data; name=\"").append(e.getKey()).append("\"") .append(LINE_FEED);
            b.append("Content-Type: text/plain;").append( LINE_FEED);
            b.append(LINE_FEED);
            b.append(e.getValue()).append(LINE_FEED);
        }
        b.append("--").append(boundary).append("--").append(LINE_FEED);
        b.append(LINE_FEED);
        return b.toString();
    }
}
