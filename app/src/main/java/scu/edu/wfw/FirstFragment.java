package scu.edu.wfw;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import scu.edu.wfw.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        preferences = getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        editor = preferences.edit();
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @SuppressLint("SetJavaScriptEnabled")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WebView.setWebContentsDebuggingEnabled(true);
        WebView webView = binding.webView;
        webView.loadUrl("https://wfw.scu.edu.cn/ncov/wap/default/index");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String ur = request.getUrl().toString();
                if (preferences.getBoolean("isIntercept", false) && ur.contains("ipLocation")) {
                    StringBuilder stringBuilder = new StringBuilder();
                    BufferedReader bufferedReader = null;
                    try {
                        URL url = new URL(ur);
                        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                        httpsURLConnection.setConnectTimeout(10 * 1000);
                        httpsURLConnection.setReadTimeout(40 * 1000);
                        bufferedReader = new BufferedReader(new
                                InputStreamReader(httpsURLConnection
                                .getInputStream()));
                        String line = "";
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    String temps = stringBuilder.toString();
                    Pattern pattern = Pattern.compile("(jsonp.*?\\()(.*?)(\\).*)");
                    Matcher matcher = pattern.matcher(temps);
                    if (matcher.matches()) {
                        JSONObject jsonObject = JSON.parseObject(matcher.group(2));
                        //104.092372,30.635226 四川大学望江校区
                        jsonObject.put("lng", getFixPoint("lng"));
                        jsonObject.put("lat", getFixPoint("lat"));
                        temps = matcher.group(1) + jsonObject.toJSONString() + matcher.group(3);
                    } else {
                        temps = "";
                    }


                    return new WebResourceResponse("application/javascript",
                            "utf-8",
                            new ByteArrayInputStream(temps.getBytes()));

                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.equals("https://ua.scu.edu.cn/logout")) {
                    view.loadUrl("https://wfw.scu.edu.cn/ncov/wap/default/index");
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
//        ((SwitchCompat) getActivity().findViewById(R.id.custom_switch)).setChecked(preferences.getBoolean("isIntercept", false));
    }

    public String getFixPoint(String name) {
        if (name.equals("lng")) {
            return String.valueOf(Float.parseFloat(preferences.getString("lng", "104.065901")));
        } else {
            return String.valueOf(Float.parseFloat(preferences.getString("lat", "30.635226")));
        }
    }

    public void logout_click(Object item) {
        binding.webView.loadUrl("https://wfw.scu.edu.cn/uc/wap/login/logout?redirect=https://wfw.scu.edu.cn/site/center/personal");
    }

    public void load_click(Object item) {
        binding.webView.evaluateJavascript(
                "const currentdate = vm.info.date;\n" +
                        "vm.info = vm.oldInfo;\n" +
                        "vm.info.date = currentdate;\n"
                , null);
    }

    public void custom_click(Object item) {
        NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment);
    }

    public void custom_location_switch(Object item) {
        editor.putBoolean("isIntercept", ((SwitchCompat) item).isChecked());
        editor.commit();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}