package tipkuu.padi.com.tipkuu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UnionBankLoginActivity extends AppCompatActivity {
    private static final String TAG = UnionBankLoginActivity.class.getName();
    private WebView loginWebView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_union_bank_login);
        intent = getIntent();
        String url = intent.getStringExtra("url");
        loginWebView = (WebView) findViewById(R.id.unionbank_redirect_page);
        loginWebView.getSettings().setJavaScriptEnabled(true);

        loginWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, " >>>>>>>>>>>>>>>>> " + url);
                super.onPageFinished(view, url);

                if (url.contains("/merchants/unionbank_callback")) {
                    try {
                        URL aURL = new URL(url);
                        Map<String, List<String>> params = splitQuery(aURL);
                        String code = params.get("code").get(0);
                        Intent intent = new Intent();
                        intent.putExtra("code", code);
                        setResult(TipeeProfileActivity.RESULT_OK, intent);
                        finish();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }
        });

        loginWebView.loadUrl(url);
    }

    public static Map<String, List<String>> splitQuery(URL url) throws UnsupportedEncodingException {
        final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
        final String[] pairs = url.getQuery().split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            if (!query_pairs.containsKey(key)) {
                query_pairs.put(key, new LinkedList<String>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            query_pairs.get(key).add(value);
        }
        return query_pairs;
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
