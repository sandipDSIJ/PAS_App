package in.dsij.pas.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import in.dsij.pas.R;
import in.dsij.pas.fragment.WebViewFragment;


public class WebViewActivity extends AppCompatActivity {

    private static final String EXTRA_TOOLBAR_TITLE = "WebViewActivity.Extra.ToolbarTitle";
    private static final String EXTRA_URL = "WebViewActivity.Extra.Url";

    private String toolbarTitle;
    private String url;

    private TextView tvToolbarTitle;

    public static Intent getIntent(@NonNull Context packageContext, @Nullable String toolbarTitle, @NonNull String url) {
        Intent intent = new Intent(packageContext, WebViewActivity.class);
        intent.putExtra(EXTRA_TOOLBAR_TITLE, toolbarTitle);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        toolbarTitle = getIntent().getStringExtra(EXTRA_TOOLBAR_TITLE);
        url = getIntent().getStringExtra(EXTRA_URL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);

        if (TextUtils.isEmpty(toolbarTitle)) {

            if (TextUtils.isEmpty(url)) {
                Toast.makeText(WebViewActivity.this, "Can't Open Page", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    getSupportActionBar().hide();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

        } else {
            tvToolbarTitle.setText(toolbarTitle);
        }

        Fragment fragment = WebViewFragment.newInstance(url);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.frag_container_view, fragment, null)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
    }
}
