package com.android.api;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
            this.path = null;
        }

        public Value(Path path) {
            this.str = "";
            this.path = null;
        }
        public String str;
        public Path path;
    }

    public MultipartFormBody(String boundary) {
        this.keys = new HashMap<>();
        this.boundary = boundary;
    }

    public void addField(String key, String value) {
        this.keys.put(key, new Value(value));
    }

    public void addFile(String key, Path value) {
        this.keys.put(key, new Value(value));
    }

    @NonNull
    public String toString() {
        StringBuilder b = new StringBuilder();

        for(Entry<String, Value> e : this.keys.entrySet()) {
            if (e.getValue().path == null) {
                b.append("--").append(this.boundary).append(LINE_FEED);
                b.append("Content-Disposition: form-data; name=\"").append(e.getKey()).append("\"") .append(LINE_FEED);
                b.append("Content-Type: text/plain;").append( LINE_FEED);
                b.append(LINE_FEED);
                b.append(e.getValue().str).append(LINE_FEED);
            } else {
                b.append("--").append(this.boundary).append(LINE_FEED);
                b.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(e.getValue().path.getFileName()).append("\"").append(LINE_FEED);
                b.append("Content-Type: application/octet-stream").append(LINE_FEED);// + URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
                b.append(LINE_FEED);

                try {
                    byte[] image = Files.readAllBytes(e.getValue().path);
                    b.append(Arrays.toString(image)).append(LINE_FEED);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        b.append("--").append(boundary).append("--").append(LINE_FEED);
        b.append(LINE_FEED);
        return b.toString();
    }
}
