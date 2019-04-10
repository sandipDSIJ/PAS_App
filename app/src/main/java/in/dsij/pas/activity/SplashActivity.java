package in.dsij.pas.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import in.dsij.pas.MyApplication;
import in.dsij.pas.R;
import in.dsij.pas.constants.C;
import in.dsij.pas.database.DbDsijUser;

import in.dsij.pas.dialog.ErrorDialogFragment;
import in.dsij.pas.dialog.ForceExitDialogFragment;
import in.dsij.pas.dialog.OfflineDialogFragment;
import in.dsij.pas.dialog.ProgressDialogFragment;
import in.dsij.pas.dialog.UpdateDialogFragment;
import in.dsij.pas.net.respose.ResDsijLogin;
import in.dsij.pas.net.respose.ResFormDetails;
import in.dsij.pas.net.respose.ResMessage;
import in.dsij.pas.net.respose.ResSummeryDtls;
import in.dsij.pas.net.retrofit.CallGenerator;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity implements ErrorDialogFragment.Callbacks, UpdateDialogFragment.Callbacks, ForceExitDialogFragment.Callbacks, OfflineDialogFragment.Callbacks {
    private Realm realm;
    private int loginResponseCode;

    private ProgressBar mProgressBar;
    private ProgressDialogFragment mProgressDialog;
    private TextView tvVersion;
    private String body,address;
    private Callback<ResDsijLogin> loginCallback;

    private static final String LOG_TAG = "SplashActivity";

    private static final String KEY_SOCIAL_USER_ID = "SplashActivity.Key.SocialUserId";
    private static final String KEY_SOCIAL_ACCESS_TOKEN = "SplashActivity.Key.SocialAccessToken";
    private static final String KEY_SOCIAL_USER_NAME = "SplashActivity.Key.SocialUserName";
    private static final String KEY_SOCIAL_USER_LAST_NAME = "SplashActivity.Key.SocialLastName";
    private static final String KEY_SOCIAL_AVATAR = "SplashActivity.Key.SocialAvatar";
    private static final String KEY_SOCIAL_EMAIL = "SplashActivity.Key.SocialEmail";
    private static final String KEY_SOCIAL_GENDER = "SplashActivity.Key.SocialGender";
    private static final String KEY_SOCIAL_TYPE = "SplashActivity.Key.SocialType";
    private static final String KEY_FCM_TOKEN = "SplashActivity.Key.FCMToken";
    private static final String DIALOG_ERROR = "SplashActivity.Dialog.Error";
    private static final String DIALOG_UPDATE = "SplashActivity.Dialog.Update";
    private static final String DIALOG_FORCE_EXIT = "SplashActivity.Dialog.ForceExit";
    private static final String DIALOG_OFFLINE = "SplashActivity.Dialog.OfflineDialog";

    public static Intent getIntent(Context packageContext, String userId, String accessToken, String userName, String lastName, String avatar, String email, String gender, int socialType, String fcmToken) {
        Intent intent = new Intent(packageContext, SplashActivity.class);
        intent.putExtra(KEY_SOCIAL_USER_ID, userId);
        intent.putExtra(KEY_SOCIAL_ACCESS_TOKEN, accessToken);
        intent.putExtra(KEY_SOCIAL_USER_NAME, userName);
        intent.putExtra(KEY_SOCIAL_USER_LAST_NAME, lastName);
        intent.putExtra(KEY_SOCIAL_AVATAR, avatar);
        intent.putExtra(KEY_SOCIAL_EMAIL, email);
        intent.putExtra(KEY_SOCIAL_GENDER, gender);
        intent.putExtra(KEY_SOCIAL_TYPE, socialType);
        intent.putExtra(KEY_FCM_TOKEN, fcmToken);
        C.device.profileAvatar = avatar;
        return intent;
    }

    public static Intent getIntent(Context packageContext) {
        return new Intent(packageContext, SplashActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Fabric.with(this, new Crashlytics());*/
        setContentView(R.layout.activity_splash_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        tvVersion = (TextView) findViewById(R.id.tvVersion);
        String versionText = "V " + C.device.VERSION_NAME;
        tvVersion.setText(versionText);
        realm = Realm.getDefaultInstance();

        loginCallback = new Callback<ResDsijLogin>() {
            @Override
            public void onResponse(Call<ResDsijLogin> call, Response<ResDsijLogin> response) {
                if (response.isSuccessful()) {
                    try {
                        ResDsijLogin resLogin = response.body();
                        //insertUser(resLogin);
                        loginResponseCode = response.code();
                        insertDsijUser(resLogin);
                        Log.d(LOG_TAG, resLogin.toString());
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Parse Issues Error", e);
                    }
                } else if (response.code() == 403 || response.code() == 404 || response.code() == 500 || response.code() == 503) {
                    String strError = response.errorBody().toString();
                    showErrorDialog("Oops, the server is currently\n unavailable please try agein later");
                    return;
                } else {
                    String dialogMessage;
                    ResMessage errorMessage = null;
                    try {
                        String errorRes = response.errorBody().string();
                        errorMessage = new GsonBuilder().create().fromJson(errorRes, ResMessage.class);
                        dialogMessage = errorMessage.getMessage();

                        switch (response.code()) {
                            case C.net.loginWithToken.error.NOT_AUTHORISED:
                                //dialogMessage = "Not Authorised.\n\nPlease login other.";
                                break;
                            case C.net.loginWithToken.error.ALREADY_SIGNED_INTO_OTHER_DEVICE:
                                // dialogMessage = "Already signed into other Device.";
                                break;
                            case C.net.loginWithToken.error.TOKEN_EXPIRED:
                                //dialogMessage = "Token Expired \n\nPlease try login again.";
                                break;

                            case C.net.loginWithPassword.error.USERNAME_NOT_REGISTERED:
                                // dialogMessage = "Could Not Validate User.\n\nPlease try again.";
                                break;
                            default:
                                // dialogMessage = errorMessage.getMessage();
                                break;
                        }
                        if (dialogMessage != null)
                            showErrorDialog(dialogMessage);
                        else
                            showErrorDialog(C.net.tag.ERROR_TIMEOUT_MSG);
                        Log.e("Retrofit", "Error Response code : " + response.code());
                    } catch (IOException e) {
                        //
                    }
                }
            }

            @Override
            public void onFailure(Call<ResDsijLogin> call, Throwable t) {
                Log.e(LOG_TAG, "Login Error", t);

                if (!MyApplication.isConnected()) {
                    Snackbar.make(mProgressBar, C.net.tag.ERROR_CHECK_INTERNET, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(mProgressBar, "Oops, the server is currently\n unavailable please try agein later", Snackbar.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            mProgressBar.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!MyApplication.isConnected()) {
            /*FragmentManager fm = getSupportFragmentManager();
            OfflineDialogFragment offlineDialogFragment = OfflineDialogFragment.newInstance(null, "! No Internet Connection.\n\n" +
                    "Only downloaded issues are available offline.");
            offlineDialogFragment.show(fm, DIALOG_OFFLINE);

            showErrorDialog("Please check your internet connection");*/
            switchToOffline();
        } else
            continueOnline();
    }

    private void continueOnline() {

        String socialUserId = getIntent().getStringExtra(KEY_SOCIAL_USER_ID);
        String socialAccessToken = getIntent().getStringExtra(KEY_SOCIAL_ACCESS_TOKEN);
        String socialUserName = getIntent().getStringExtra(KEY_SOCIAL_USER_NAME);
        String socialUserLastName = getIntent().getStringExtra(KEY_SOCIAL_USER_LAST_NAME);
        String socialEmail = getIntent().getStringExtra(KEY_SOCIAL_EMAIL);
        String fcmToken = getIntent().getStringExtra(KEY_FCM_TOKEN);
        String socialAvatar = getIntent().getStringExtra(KEY_SOCIAL_AVATAR);
        int socialType = getIntent().getIntExtra(KEY_SOCIAL_TYPE, 0);
        boolean social = (!TextUtils.isEmpty(socialUserId)) &&
                !TextUtils.isEmpty(socialUserName);

        if (social) {

            switch (socialType) {
                case C.net.process.LOGIN_WITH_FACEBOOK:

                    CallGenerator.loginFacebook(socialUserId,
                            socialAccessToken,
                            socialUserName,
                            socialUserLastName,
                            socialAvatar,
                            socialEmail, fcmToken)
                            .enqueue(loginCallback);

                    break;

                /*case C.process.LOGIN_WITH_TWITTER:
                    mReqLoginWithTwitter = new ReqLoginWithTwitter(socialUserId, socialAccessToken, socialUserName, socialAvatar, socialEmail, this, this)
                            .makeCall();

                    CallGenerator.loginTwitter(socialUserId,
                            socialAccessToken,
                            socialUserName,
                            socialUserLastName,
                            socialAvatar,
                            socialEmail)
                            .enqueue(loginCallback);

                    break; */

                case C.net.process.LOGIN_WITH_GOOGLE:

                    CallGenerator.loginGoogle(socialUserId,
                            socialAccessToken,
                            socialUserName,
                            socialUserLastName,
                            socialAvatar,
                            socialEmail, fcmToken)
                            .enqueue(loginCallback);

                    break;
            }

        } else {
            DbDsijUser dbUser = realm.where(DbDsijUser.class).findFirst();

            if (dbUser != null) {
                if (!MyApplication.isConnected()) {
                    /*gotoMainActivity();*/
                } else {
                    CallGenerator.loginWithToken(dbUser.getUsername(), dbUser.getSessionToken()).enqueue(loginCallback);
                    /*getIssues();*/
                }
            } else {
                gotoLoginActivity();
            }
        }
    }


    private void showErrorDialog(String dialogMessage) {

        try {
            showProgressBar(false);

            ErrorDialogFragment errorDialog = ErrorDialogFragment.newInstance(null, dialogMessage);
            FragmentManager fragmentManager = getSupportFragmentManager();
            errorDialog.show(fragmentManager, DIALOG_ERROR);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }

    }

    private void insertDsijUser(final ResDsijLogin resLogin) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + "User");

                try {
                    realm.where(DbDsijUser.class).findAll().deleteAllFromRealm();
                } catch (Exception e) {
                    Log.v(LOG_TAG, C.net.tag.DB_TRANSACTION_WRITE + "Already no User present ", e);
                } finally {
                    realm.createOrUpdateObjectFromJson(DbDsijUser.class, new Gson().toJson(resLogin));
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + C.net.tag.DB_SUCCESS);
                if (loginResponseCode == 201) {
                    showUpdateDialog("Update", true);
                } else if (loginResponseCode == 202) {
                    showUpdateDialog("Update", false);
                } else {
                    getTabName();
                    // gotoMainActivity();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + C.net.tag.DB_FAIL,
                        error);
            }
        });
    }

    private void getTabName() {
        CallGenerator.getPasTabName().enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        gotoMainActivity();
                    } else if (response.code() == 201) {
                        gotoAccountOpeningForm();
                    } else if (response.code() == 202) {
                        gotoWaitingPage();
                    } else if (response.code() == 203) {
                        gotoRiskAssessmentForm();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {

            }
        });
    }

    private void gotoAccountOpeningForm() {

        //startActivity(DetailsActivity.getIntent(this.getApplicationContext()));
        Intent intent = AccOpentingFormActivity.getIntent(this.getApplicationContext());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void gotoWaitingPage() {
        //startActivity(DetailsActivity.getIntent(this.getApplicationContext()));
        Intent intent = DetailsActivity.getIntent(this.getApplicationContext());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void gotoRiskAssessmentForm() {
        Intent intent = AccountOpeningActivity.getIntent(this.getApplicationContext(), "RiskAssisment");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void getPortfolioDetails() {
        /*CallGenerator.getPortfolioMasterDetails().enqueue(new Callback<ResPortfolioDetail>() {
            @Override
            public void onResponse(Call<ResPortfolioDetail> call, Response<ResPortfolioDetail> response) {
                if (response.isSuccessful()) {
                    try {
                        ResPortfolioDetail resPortfolioDetail = response.body();

                        Log.v(LOG_TAG, C.net.getHoldings.TAG +
                                C.net.tag.RESPONSE +
                                new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(resPortfolioDetail));

                        if (resPortfolioDetail == null) {
                            Log.w(LOG_TAG, C.net.getHoldings.TAG +
                                    C.net.tag.RESPONSE + "Received NULL Product List");
                        } else {
                            insertPortfolios(resPortfolioDetail);
                        }

                    } catch (Exception e) {
                        Log.w(LOG_TAG, C.net.getHoldings.TAG + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
                    }
                } else {

                    //showEmptyView(true, "Not available at the moment");

                    ResMessage errorMessage = null;
                    try {
                        String errorRes = response.errorBody().string();
                        errorMessage = new GsonBuilder().create().fromJson(errorRes, ResMessage.class);
                    } catch (IOException e) {
                        //
                    }
                    switch (response.code()) {
                        default:
                            // TODO: 10/10/2017
                            break;
                    }
                    Log.w(LOG_TAG, C.net.getHoldings.TAG + C.net.tag.ERROR + "Error Response code : " + response.code() + " Response Body:\n" +
                            new GsonBuilder()
                                    .setPrettyPrinting()
                                    .create()
                                    .toJson(errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ResPortfolioDetail> call, Throwable t) {
                Log.e(LOG_TAG, C.net.getHoldings.TAG + C.net.tag.FAILED + "Failed API Call : ", t);

                if (!MyApplication.isConnected()) {
                    //showEmptyView(true, "Check your Internet connection");
                } else {
                    // showEmptyView(true, "Not available at the moment");
                }
            }
        });*/
    }

    /*private void insertPortfolios(ResPortfolioDetail resPortfolioDetail) {
        final List<ResPortfolioDetail.PortfilioEntity> portfilioEntities = resPortfolioDetail.getList();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + "PortfolioDetails");
                try {
                    realm.where(DbPortfolioDetails.class).findAll().deleteAllFromRealm();
                } catch (RealmException e) {
                    Log.e(LOG_TAG, e.toString());
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString());
                }

                try {
                    for (int i = 0; i < portfilioEntities.size(); i++) {

                        DbPortfolioDetails dbPortfolioDetails = new DbPortfolioDetails(portfilioEntities.get(i));

                        realm.copyToRealmOrUpdate(dbPortfolioDetails);
                    }

                } catch (Exception e) {
                    Log.v(LOG_TAG, C.net.tag.DB_TRANSACTION_WRITE + "Already no Contest List ", e);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + C.net.tag.DB_SUCCESS);
                gotoMainActivity();
                if (!MyApplication.isConnected()) {
                   // showEmptyView(true, "Check your Internet connection");
                } else {
                   // showEmptyView(true, "Not available at the moment");
                }
            }
        });
    }*/

    /*private void insertWachtList(final ResWacthList resGetWatchList) {

        final List<ResWacthList.listWacthListEntity> resWatchList = resGetWatchList.getList();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + "WatchList");
                try {
                    realm.where(DbWatchList.class).findAll().deleteAllFromRealm();
                } catch (RealmException r) {
                    Log.e(LOG_TAG, r.toString());
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString());
                }
                try {
                    for (int i = 0; i < resWatchList.size(); i++) {
                        //realm.where(DbWatchList.class).findAll().deleteAllFromRealm();
                        DbWatchList dbWatchList = new DbWatchList(resWatchList.get(i));
                        realm.copyToRealmOrUpdate(dbWatchList);
                    }
                } catch (Exception e) {
                    Log.v(LOG_TAG, C.net.tag.DB_TRANSACTION_WRITE + "Already no Watch List present ", e);
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + C.net.tag.DB_SUCCESS);

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + C.net.tag.DB_FAIL,
                        error);
            }
        });
    }*/

    private void getContestList() {

        /*CallGenerator.getContest().enqueue(new Callback<ResContest>() {
            @Override
            public void onResponse(Call<ResContest> call, Response<ResContest> response) {
                if (response.isSuccessful()) {
                    try {
                        ResContest resContest = response.body();

                        Log.v(LOG_TAG, C.net.getHoldings.TAG +
                                C.net.tag.RESPONSE +
                                new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(resContest));

                        if (resContest == null) {
                            Log.w(LOG_TAG, C.net.getHoldings.TAG +
                                    C.net.tag.RESPONSE + "Received NULL Product List");
                        } else {
                            insertContest(resContest);
                        }

                    } catch (Exception e) {
                        Log.w(LOG_TAG, C.net.getHoldings.TAG + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
                    }
                } else {

                    //showEmptyView(true, "Not available at the moment");

                    ResMessage errorMessage = null;

                    try {
                        String errorRes = response.errorBody().string();

                        errorMessage = new GsonBuilder().create().fromJson(errorRes, ResMessage.class);

                    } catch (IOException e) {
                        //
                    }

                    switch (response.code()) {
                        default:
                            // TODO: 10/10/2017
                            break;
                    }

                    Log.w(LOG_TAG, C.net.getHoldings.TAG + C.net.tag.ERROR + "Error Response code : " + response.code() + " Response Body:\n" +
                            new GsonBuilder()
                                    .setPrettyPrinting()
                                    .create()
                                    .toJson(errorMessage));

                }
            }

            @Override
            public void onFailure(Call<ResContest> call, Throwable t) {
                Log.e(LOG_TAG, C.net.getHoldings.TAG + C.net.tag.FAILED + "Failed API Call : ", t);

                if (!MyApplication.isConnected()) {
                    //showEmptyView(true, "Check your Internet connection");
                } else {
                    // showEmptyView(true, "Not available at the moment");
                }
            }
        });*/
    }

    private void insertContest() {


        /*final List<ResContest.ContestEntity> contestEntities = resContest.getList();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + "Products");
                try {
                    for (int i = 0; i < contestEntities.size(); i++) {
                        //realm.where(DbWatchList.class).findAll().deleteAllFromRealm();
                        DbContest dbContest = new DbContest(contestEntities.get(i));

                        realm.copyToRealmOrUpdate(dbContest);
                    }

                } catch (Exception e) {
                    Log.v(LOG_TAG, C.net.tag.DB_TRANSACTION_WRITE + "Already no Contest List ", e);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + C.net.tag.DB_SUCCESS);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + C.net.tag.DB_FAIL,
                        error);
            }
        });*/
    }


    private void showProgressBar(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void gotoMainActivity() {

        DbDsijUser dbUser = realm.where(DbDsijUser.class).findFirst();
        try {
            if (dbUser != null) {
                mProgressBar.setVisibility(View.INVISIBLE);
                Intent intent = MainActivity.getIntent(this.getApplicationContext());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
                Snackbar.make(mProgressBar, "Could not verify user, Please restart the app", Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }

    }

    @Override
    public void onErrorDialogResponse() {
        gotoLoginActivity();
    }

    private void gotoLoginActivity() {
        try {
            mProgressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //  SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.notification_preferences), MODE_PRIVATE);
        //  sharedPreferences.edit().putBoolean(NotificationPreferences.SHOW_NOTIFICATION, false).apply();
        startActivity(LoginPASActivity.getIntent(this.getApplicationContext()));
        finish();
    }

    private void showUpdateDialog(String msg, boolean cancelFlag) {

        try {
            mProgressBar.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            UpdateDialogFragment updateDialogFragment = UpdateDialogFragment.newInstance(null,
                    "New Version Available.\n\n" +
                            "Get the latest version " +
                            "to continue click go to setting.", cancelFlag);

            FragmentManager fm = getSupportFragmentManager();

            updateDialogFragment.show(fm, DIALOG_UPDATE);
        } catch (Exception e) {
            gotoMainActivity();
        }
    }

    private boolean checkVersion() {
        return true;
    }

    private void switchToOffline() {

        mProgressBar.setVisibility(View.INVISIBLE);

        ForceExitDialogFragment forceExitDialogFragment = ForceExitDialogFragment.newInstance(null,
                "Can not continue offline.");
        FragmentManager fm = getSupportFragmentManager();


        forceExitDialogFragment.show(fm, DIALOG_FORCE_EXIT);
                /*OfflineDialogFragment offlineDialogFragment = OfflineDialogFragment.newInstance(null, "! No Internet Connection.\n\n" +
                        "Only downloaded issues are available offline.");
                offlineDialogFragment.show(fm, DIALOG_OFFLINE);


                forceExitDialogFragment.show(fm, DIALOG_FORCE_EXIT);*/
    }

    @Override
    public void updateApp() {
        String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
        finish();
    }

    @Override
    public void exitApp() {
        finish();
    }

    @Override
    public void updateLater() {
        getContestList();
        getPortfolioDetails();
        //gotoMainActivity();
    }

    @Override
    public void continueOffline() {
        //   gotoMainActivity();
    }


}
