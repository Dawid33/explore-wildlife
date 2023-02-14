package com.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.AppActivity;
import com.android.LoginAndRegisterActivity;
import com.android.R;
import com.android.databinding.FragmentLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.goToRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_LoginFragment_to_registerFragment);
            }
        });
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(binding.emailInput.getText());
                String password = String.valueOf(binding.passwordInput.getText());

                FutureTask<Boolean> login = new FutureTask<>(() -> {
                    HttpURLConnection urlConnection = null;
                    StringBuilder textBuilder = new StringBuilder("DEFAULT");
                    String boundary = "===" + System.currentTimeMillis() + "===";
                    final String LINE_FEED = "\r\n";
                    try {
                        URL url = new URL("https://explorewildlife.net/api/login");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setDoOutput(true);
                        urlConnection.setChunkedStreamingMode(0);
                        urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(urlConnection.getOutputStream()));

                        writer.append("--" + boundary).append(LINE_FEED);
                        writer.append("Content-Disposition: form-data; name=\"" + "email" + "\"")
                                .append(LINE_FEED);
                        writer.append("Content-Type: text/plain;").append(
                                LINE_FEED);
                        writer.append(LINE_FEED);
                        writer.append(email).append(LINE_FEED);
                        writer.flush();

                        writer.append("--" + boundary).append(LINE_FEED);
                        writer.append("Content-Disposition: form-data; name=\"" + "password" + "\"").append(LINE_FEED);
                        writer.append("Content-Type: text/plain;").append(LINE_FEED);
                        writer.append(LINE_FEED);
                        writer.append(password).append(LINE_FEED);
                        writer.flush();

                        writer.append("--" + boundary + "--").append(LINE_FEED);
                        writer.append(LINE_FEED).flush();
                        writer.flush();
                        writer.close();

                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        textBuilder = new StringBuilder();
                        try (Reader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                            int c = 0;
                            while ((c = reader.read()) != -1) {
                                textBuilder.append((char) c);
                            }
                        }

                        JSONObject response = new JSONObject(textBuilder.toString());
                        if (response.has("success")) {
                            boolean success = (boolean) response.get("success");
                            if (success) {
                                return true;
                            } else if (response.has("error")) {
                                System.out.println("Login Error: " + (String)response.get("error"));
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        urlConnection.disconnect();
                    }
                    return false;
                });

                boolean has_logged_in = false;
                try {
                    ExecutorService exec = Executors.newSingleThreadExecutor();
                    exec.submit(login);
                    if (login.get()) {
                        has_logged_in = true;
                    } else {
                        has_logged_in = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LoginAndRegisterActivity currentActivity = (LoginAndRegisterActivity)getActivity();
                Intent app = new Intent(currentActivity, AppActivity.class);
                if (has_logged_in) { startActivity(app); }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
