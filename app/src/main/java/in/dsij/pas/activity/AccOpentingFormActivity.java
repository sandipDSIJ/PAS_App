package in.dsij.pas.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.dsij.pas.MyApplication;
import in.dsij.pas.R;
import in.dsij.pas.constants.C;
import in.dsij.pas.database.DbActivityLogs;
import in.dsij.pas.database.DbDsijUser;
import in.dsij.pas.database.DbLogs;
import in.dsij.pas.database.DbRecoAllPortfolio;
import in.dsij.pas.database.DbRiskAssessment;
import in.dsij.pas.database.DbSoldScripDetails;
import in.dsij.pas.database.DbSubmitedPortFolioScrip;
import in.dsij.pas.dialog.ProgressDialogFragment;
import in.dsij.pas.fragment.UpdateRecoFragment;
import in.dsij.pas.net.respose.ResFormDetails;
import in.dsij.pas.net.respose.ResMessage;
import in.dsij.pas.net.respose.ResMessageOld;
import in.dsij.pas.net.retrofit.CallGenerator;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccOpentingFormActivity extends AppCompatActivity {

    private WebView webView;
    private String body,address;
    private Button btnAccept;
    private Spinner spinner;
    private static final String LOG_TAG = "AccOpentingFormActivity";


    private static final String DIALOG_PROGRESS = "AccOpentingFormActivity.Dialog.Progress";
    private ProgressDialogFragment mProgressDialog;
    private Realm realm;

    private List<String> listSpinner = new ArrayList<String>();

    public static Intent getIntent(@NonNull Context packageContext) {
        return new Intent(packageContext, AccOpentingFormActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Account Opening Form..");
        setSupportActionBar(toolbar);

        webView=findViewById(R.id.webView);
        btnAccept=(Button)findViewById(R.id.btnAccept);
        spinner=(Spinner)findViewById(R.id.spinner);

        listSpinner.add("Advertisement");
        listSpinner.add("Receiving an email");
        listSpinner.add("Search Engine");
        listSpinner.add("Referred by a friend");
        listSpinner.add("other");
        realm = Realm.getDefaultInstance();

        ArrayAdapter<String> adapter= new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, listSpinner);
        spinner.setAdapter(adapter);

        webView.getSettings().setJavaScriptEnabled(true);
        CallGenerator.getFormDetails().enqueue(new Callback<ResFormDetails>() {
            @Override
            public void onResponse(Call<ResFormDetails> call, Response<ResFormDetails> response) {
                if (response.isSuccessful()) {
                    ResFormDetails resFormDetails = response.body();
                    if (resFormDetails == null) {

                    } else {
                        body = resFormDetails.getList().get(0).getPageBody();
                        address=resFormDetails.getList().get(0).getFooterAddress();
                        webView.loadDataWithBaseURL("",
                                "<!DOCTYPE html>\n" +
                                        "<html  lang=\"en-US\">\n" +
                                        "<head id=\"Head\">\n" +
                                        "<!--*********************************************-->\n" +
                                        "<!-- DNN Platform - http://www.dnnsoftware.com   -->\n" +
                                        "<!-- Copyright (c) 2002-2017, by DNN Corporation -->\n" +
                                        "<!-- Copyright (c) 2002-2017, by DNN Corporation -->\n" +
                                        "<!--*********************************************-->\n" +
                                        "<meta content=\"text/html; charset=UTF-8\" http-equiv=\"Content-Type\" />\n" +
                                        "<meta name=\"REVISIT-AFTER\" content=\"1 DAYS\" />\n" +
                                        "<meta name=\"RATING\" content=\"GENERAL\" />\n" +
                                        "<meta name=\"RESOURCE-TYPE\" content=\"DOCUMENT\" />\n" +
                                        "<meta content=\"text/javascript\" http-equiv=\"Content-Script-Type\" />\n" +
                                        "<meta content=\"text/css\" http-equiv=\"Content-Style-Type\" />\n" +
                                body+"</body>\n" +
                                        "</html>", "text/html", "UTF-8", "");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResFormDetails> call, Throwable t) {

            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });
    }

    private void submitReview() {
        CallGenerator.submitPortfolioforReview().enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                if (response.isSuccessful()) {
                    try {
                        ResMessage resMessage = response.body();

                        Log.v(LOG_TAG, "Offer Page" +
                                new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(resMessage));

                        if (resMessage == null) {
                            Log.w(LOG_TAG, C.net.tag.RESPONSE +
                                    "Received NULL Response for resOfferURL");
                        } else {
                            Snackbar.make(btnAccept, resMessage.getMessage(), Snackbar.LENGTH_SHORT).show();
                            gotoWaitingPage();
                        }

                    } catch (Exception e) {
                        Log.w(LOG_TAG, "Update Reco" + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
                    }
                } else
                {
                    ResMessage errorMessage = null;
                    try {
                        String errorRes = response.errorBody().string();

                        errorMessage = new GsonBuilder().create().fromJson(errorRes, ResMessage.class);
                        Snackbar.make(btnAccept, errorMessage.getMessage(), Snackbar.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(LOG_TAG,e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {

            }
        });
    }

    private void gotoWaitingPage() {
        Intent intent = DetailsActivity.getIntent(this.getApplicationContext());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.logout:
                logout();
                break;
        }
        return true;
    }

    private void logout() {
        mProgressDialog = ProgressDialogFragment.newInstance(null, "Logging out");
        mProgressDialog.show(getSupportFragmentManager(), DIALOG_PROGRESS);
        CallGenerator.logout()
                .enqueue(new Callback<ResMessageOld>() {
                    @Override
                    public void onResponse(Call<ResMessageOld> call, retrofit2.Response<ResMessageOld> response) {
                        if (response.isSuccessful()) {
                            try {
                                ResMessageOld resMessage = response.body();


                                deleteUser();
                                C.device.profileAvatar = "";
                                Log.w(LOG_TAG, "Logout" +
                                        C.net.tag.RESPONSE + "Received Empty Message");

                            } catch (Exception e) {
                                Log.w(LOG_TAG, "Logout" + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
                            }
                        } else {

                            ResMessage errorMessage = null;
                            mProgressDialog.dismiss();
                            /*  don't show Error if response.code=417 and */
                            try {
                                String errorRes = response.errorBody().string();

                                Snackbar.make(webView, C.net.tag.ERROR_TIMEOUT_MSG, Snackbar.LENGTH_SHORT).show();

                            } catch (IOException e) {
                            } catch (ParseException e) {
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResMessageOld> call, Throwable t) {
                        try {
                            mProgressDialog.dismiss();
                        } catch (Exception e) {
                            Log.e(LOG_TAG, C.net.tag.ERROR_UNKNOWN, e);
                        } finally {
                            Log.e(LOG_TAG, "Error Logout", t);
                            //deleteUser();
                        }

                        if (!MyApplication.isConnected()) {
                            Snackbar.make(webView, "! Not available in offline mode.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void deleteUser() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(DbDsijUser.class).findAll().deleteAllFromRealm();
                realm.where(DbRecoAllPortfolio.class).findAll().deleteAllFromRealm();
                realm.where(DbActivityLogs.class).findAll().deleteAllFromRealm();
                realm.where(DbLogs.class).findAll().deleteAllFromRealm();
                realm.where(DbRiskAssessment.class).findAll().deleteAllFromRealm();
                realm.where(DbSoldScripDetails.class).findAll().deleteAllFromRealm();
                realm.where(DbLogs.class).findAll().deleteAllFromRealm();
                realm.where(DbSubmitedPortFolioScrip.class).findAll().deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                gotoLoginActivity();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                gotoLoginActivity();
            }
        });
    }

    private void gotoLoginActivity() {
        try {
            mProgressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.notification_preferences), MODE_PRIVATE);
        // sharedPreferences.edit().putBoolean(NotificationPreferences.SHOW_NOTIFICATION, true).apply();
        startActivity(LoginPASActivity.getIntent(this.getApplicationContext()));
        finish();
    }

}
