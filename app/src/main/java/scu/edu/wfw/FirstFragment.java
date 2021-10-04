package scu.edu.wfw;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Context;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import scu.edu.wfw.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        });
    }

    public void logout_click(MenuItem item) {
        binding.webView.loadUrl("https://wfw.scu.edu.cn/uc/wap/login/logout?redirect=https://wfw.scu.edu.cn/site/center/personal");
    }

    public void load_click(MenuItem item) {
        binding.webView.evaluateJavascript(
                "const currentdate = vm.info.date;\n" +
                        "vm.info = vm.oldInfo;\n" +
                        "vm.info.date = currentdate;\n" +
                        "vm.save();", null);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}