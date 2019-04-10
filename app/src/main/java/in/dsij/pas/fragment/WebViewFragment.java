package in.dsij.pas.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import in.dsij.pas.MyApplication;
import in.dsij.pas.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends Fragment {

    private static final String ARG_URL = "WebViewFragment.Arg.Key.Url";
    private static final String LOG_TAG = "WebViewFragment";

    private String url;

    private View view;
    private WebView mWebView;

    public static WebViewFragment newInstance(String url) {

        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url = getArguments().getString(ARG_URL);

    }

    public WebViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_webview, container, false);

        findViews();


        if (MyApplication.isConnected()) {
            setViews();
            setEventListeners();
        } else {
            showEmptyView(true);
        }

        return view;
    }

    private void findViews() {
        mWebView = (WebView) view.findViewById(R.id.webView);
    }

    private void setViews() {
        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(url);
        //      mWebView.postUrl("https://secure.payu.in/_payment?", EncodingUtils.getBytes("key=BgagrhpC&txnid=309a0458a6bf9bdf03df&amount=1&productinfo=Flash News&firstname=akshaylunge&email=sandippatil.sam@gmail.com&phone=9730274012&surl=https://www.dsij.in/payumoneysuccess.aspx&furl=https://www.dsij.in/payumoney-failure.aspx&hash=f8df84a4f7a7bd72387232e7135967708fa2fda1f8e1dc2a1b42d265241c2d3a05c4190583aaac656c74a38c360af31b2f66ad634da42a2ad6c01b9f2c31ef07&service_provider=payu_paisa", "utf-8"));
        /*
         *
         * This method pushes the layout/webview out of the screen
         * use android:windowSoftInputMode="adjustResize" in Manifest.xml instead
         *
         * */
        /*try {
            AndroidBug5497Workaround.assistActivity(getActivity()); // Used for sliding editbox
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                }
                return false;
            }
        });

        mWebView.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                        showProgressBar(false);
                        showEmptyView(true);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        showProgressBar(true);

                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        showProgressBar(false);
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {

                        if (url.startsWith("mailto:")) {
                            MailTo mt = MailTo.parse(url);

                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mt.getTo(), null));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
                            emailIntent.putExtra(Intent.EXTRA_TEXT, mt.getBody());
                            try {
                                startActivity(emailIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.w(LOG_TAG, "Email Client Not Found", e);
                                Snackbar.make(view, "! Email Client Not Found", Snackbar.LENGTH_SHORT).show();
                            }
                        } else if (url.startsWith("http://www.capitalfirst.com/")) {
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(i);
                        } else {
                            view.loadUrl(url);
                        }

                        return true;
                    }
                }

        );

        mWebView.setDownloadListener(
                new DownloadListener() {
                    public void onDownloadStart(String url, String userAgent,
                                                String contentDisposition, String mimetype,
                                                long contentLength) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }

        );
    }

    private void setEventListeners() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    showProgressBar(false);
                }
            }
        });
    }

    private void showProgressBar(boolean show) {
        //Todo show/hide progress bar
        if (show) {
            view.findViewById(R.id.progress_bar_horizontal_top).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.progress_bar_horizontal_top).setVisibility(View.GONE);
        }
    }

    private void showEmptyView(boolean show) {
        if (show) {
            mWebView.setVisibility(View.GONE);
            view.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        } else {
            mWebView.setVisibility(View.VISIBLE);
            view.findViewById(R.id.empty_view).setVisibility(View.GONE);
        }
    }

}
