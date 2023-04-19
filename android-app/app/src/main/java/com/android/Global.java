package com.android;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Global {
     // When running standalone on port 8080
//     public static String baseUrl = "http://10.0.2.2:8080";
     // When running docker compose up
//     public static String baseUrl = "http://10.0.2.2";
//     // Running production
     public static String baseUrl = "https://explorewildlife.net";
     public static final String imageApiUrl = Global.baseUrl + "/api/image?id=";
     public static ExecutorService executorService = Executors.newFixedThreadPool(4);
     public static String loggedInUserID = null;
}
