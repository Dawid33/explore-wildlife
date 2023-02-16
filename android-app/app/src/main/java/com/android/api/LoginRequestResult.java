package com.android.api;

public class LoginRequestResult {
    public String error;
    public boolean isLoggedIn;

    public LoginRequestResult(boolean isLoggedIn, String error) {
        this.error = error;
        this.isLoggedIn = isLoggedIn;
    }
}
