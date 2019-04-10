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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import in.dsij.pas.R;
import in.dsij.pas.fragment.RiskAssessmentFragment;
import in.dsij.pas.net.respose.ResSubmitPortfolio;
import in.dsij.pas.net.retrofit.CallGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PortfolioNameActivity extends AppCompatActivity {

    private static final String EXTRA_TOOLBAR_TITLE = "WebViewActivity.Extra.ToolbarTitle";
    private static final String EXTRA_URL = "WebViewActivity.Extra.Url";

    private String toolbarTitle;
    private String url;
    private Button btnSubmit;
    private TextView etPortfolioName;


    private TextView tvToolbarTitle;

    public static Intent getIntent(@NonNull Context packageContext, @Nullable String toolbarTitle) {
        Intent intent = new Intent(packageContext, PortfolioNameActivity.class);
        intent.putExtra(EXTRA_TOOLBAR_TITLE, toolbarTitle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        toolbarTitle = getIntent().getStringExtra(EXTRA_TOOLBAR_TITLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvToolbarTitle = (TextView) findViewById(R.id.tvToolbarTitle);

            tvToolbarTitle.setText(toolbarTitle);

        etPortfolioName=(EditText)findViewById(R.id.etPortfolioName);
        btnSubmit=(Button)findViewById(R.id.btnSubmit);

        Fragment fragment = RiskAssessmentFragment.newInstance();

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.frag_container_view, fragment, null)
                .addToBackStack(null)
                .commit();

        setView();
    }

    private void setView() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String portfolioName="";
                submitPortFolioName(portfolioName);
            }
        });
    }

    private void submitPortFolioName(String portfolioName) {
        CallGenerator.submitPortfolioName(portfolioName).enqueue(new Callback<ResSubmitPortfolio>() {
            @Override
            public void onResponse(Call<ResSubmitPortfolio> call, Response<ResSubmitPortfolio> response) {

            }

            @Override
            public void onFailure(Call<ResSubmitPortfolio> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
    }
}
