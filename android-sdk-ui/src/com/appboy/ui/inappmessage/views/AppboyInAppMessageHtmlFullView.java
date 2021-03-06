package com.appboy.ui.inappmessage.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.appboy.Constants;
import com.appboy.support.AppboyLogger;
import com.appboy.ui.R;
import com.appboy.ui.inappmessage.jsinterface.AppboyInAppMessageHtmlJavascriptInterface;

public class AppboyInAppMessageHtmlFullView extends AppboyInAppMessageHtmlBaseView {
  private static final String TAG = String.format("%s.%s", Constants.APPBOY_LOG_TAG_PREFIX, AppboyInAppMessageHtmlFullView.class.getName());
  public static final String APPBOY_BRIDGE_PREFIX = "appboyInternalBridge";

  private WebView mMessageWebView;

  public AppboyInAppMessageHtmlFullView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public WebView getMessageWebView() {
    if (mMessageWebView == null) {
      mMessageWebView = (AppboyInAppMessageWebView) findViewById(R.id.com_appboy_inappmessage_html_full_webview);
      if (mMessageWebView != null) {
        WebSettings webSettings = mMessageWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
          webSettings.setDisplayZoomControls(false);
          // This enables hardware acceleration if the manifest also has it defined. If not defined, then the layer type will fallback to software
          mMessageWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        mMessageWebView.setBackgroundColor(Color.TRANSPARENT);

        // Set the client for console logging. See https://developer.android.com/guide/webapps/debugging.html
        mMessageWebView.setWebChromeClient(new WebChromeClient() {
          @Override
          public boolean onConsoleMessage(ConsoleMessage cm) {
            AppboyLogger.d(TAG, String.format("Html In-app log. Line: %s. SourceId: %s. Log Level: %s. Message: %s",
                cm.lineNumber(), cm.sourceId(), cm.messageLevel(), cm.message()));
            return true;
          }
        });

        mMessageWebView.addJavascriptInterface(new AppboyInAppMessageHtmlJavascriptInterface(getContext()), APPBOY_BRIDGE_PREFIX);
      }
    }
    return mMessageWebView;
  }
}
