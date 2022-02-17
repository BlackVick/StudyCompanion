package esw.peeplo.studentstudycom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import esw.peeplo.studentstudycom.databinding.ActivityQuerySearchBinding;
import esw.peeplo.studentstudycom.util.Common;
import esw.peeplo.studentstudycom.util.Methods;

public class QuerySearch extends AppCompatActivity {

    //binding
    private ActivityQuerySearchBinding activity;

    //values
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = DataBindingUtil.setContentView(this, R.layout.activity_query_search);

        //value
        url = getIntent().getStringExtra(Common.SEARCH_DATA);
        
        //init
        initialize();
    }

    private void initialize() {

        //back
        activity.backButton.setOnClickListener(v -> onBackPressed());

        //load page
        loadPage();

    }

    private void loadPage() {

        //progress bar limit
        activity.webPageProgress.setMax(100);

        //modified url
        activity.webPageUrl.setText(url);

        //web view
        activity.webPageView.getSettings().setJavaScriptEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true);
        activity.webPageView.setFocusable(true);
        activity.webPageView.setFocusableInTouchMode(true);
        activity.webPageView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        activity.webPageView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        activity.webPageView.getSettings().setDomStorageEnabled(true);
        activity.webPageView.getSettings().setDatabaseEnabled(true);
        activity.webPageView.getSettings().setAppCacheEnabled(true);
        activity.webPageView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        activity.webPageView.loadUrl(url);

        //set web client
        activity.webPageView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                //start loading
                activity.setIsLoading(true);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                //stop loading
                activity.setIsLoading(false);

                //button set
                activity.setCanGoBack(activity.webPageView.canGoBack());

            }
        });

        //set chrome client
        activity.webPageView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                activity.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                activity.webPageTitle.setText(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                activity.webPageIcon.setImageBitmap(icon);

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (activity.webPageView.canGoBack()){
            //go back
            activity.webPageView.goBack();
        } else {
            //close browser
            finish();
            
            //close anim
            Methods.slideRight(this);
        }
    }
}