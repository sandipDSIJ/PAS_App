package in.dsij.pas.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.google.gson.GsonBuilder;

import java.io.IOException;

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
import in.dsij.pas.net.respose.ResMessage;
import in.dsij.pas.net.respose.ResMessageOld;
import in.dsij.pas.net.retrofit.CallGenerator;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;

public class DetailsActivity extends AppCompatActivity {

    private WebView webView;
    private static final String LOG_TAG = "DetailsActivity";
    private static final String DIALOG_PROGRESS = "DetailsActivity.Dialog.Progress";
    private ProgressDialogFragment mProgressDialog;
    private Realm realm;

    public static Intent getIntent(@NonNull Context packageContext) {
        return new Intent(packageContext, DetailsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pas Waiting..");
        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();


        webView=findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL("", C.pasDetailsMessage, "text/html", "UTF-8", "");
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
