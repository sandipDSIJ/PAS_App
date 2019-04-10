package in.dsij.pas.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
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
import in.dsij.pas.dialog.ExitDialogFragment;
import in.dsij.pas.dialog.ProgressDialogFragment;
import in.dsij.pas.dialog.RatingBottomSheetDialogFragment;
import in.dsij.pas.dialog.ReportDialogFragment;
import in.dsij.pas.dialog.UpdateRecoPriceDialogFragment;
import in.dsij.pas.fragment.ActivityLogFragment;
import in.dsij.pas.fragment.HoldingsFragment;
import in.dsij.pas.fragment.RecoPortfolioAllFragment;
import in.dsij.pas.fragment.RiskAssessmentFragment;
import in.dsij.pas.fragment.SoldScripFragment;
import in.dsij.pas.fragment.SubmitedRecoPortFragment;
import in.dsij.pas.fragment.UpdateRecoFragment;
import in.dsij.pas.fragment.WebViewFragment;
import in.dsij.pas.net.respose.ResMessage;
import in.dsij.pas.net.respose.ResMessageOld;
import in.dsij.pas.net.respose.ResOfferURL;
import in.dsij.pas.net.retrofit.CallGenerator;
import in.dsij.pas.util.DrawerLocker;
import in.dsij.pas.util.NetworkReceiver;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener, DrawerLocker, HoldingsFragment.Callbacks, RecoPortfolioAllFragment.Callbacks,
        SoldScripFragment.Callbacks, UpdateRecoFragment.Callbacks, SubmitedRecoPortFragment.Callbacks,
        ActivityLogFragment.Callbacks, UpdateRecoPriceDialogFragment.Callbacks, RiskAssessmentFragment.Callbacks, ReportDialogFragment.Callbacks, RatingBottomSheetDialogFragment.Callbacks,
        ExitDialogFragment.Callbacks {

    private static final String EXTRA_TOOLBAR_TITLE = "WebViewActivity.Extra.ToolbarTitle";
    private static final String EXTRA_URL = "WebViewActivity.Extra.Url";
    private static final String LOG_TAG = "MainActivity";
    private static final String DIALOG_PROGRESS = "MainActivity.Dialog.Progress";
    private static final String DIAG_RATE = "MainActivity.Dialog.DIAG_RATE";
    private static final String DIALOG_REPORT = "MainActivity.Dialog.Report";
    private static final String EXIT_DIALOG = "MainActivity.Dialog.Exit";

    private NetworkReceiver receiver;

    private ProgressDialogFragment mProgressDialog;
    private FrameLayout frag_container_main;
    private FragmentManager mFragmentManager;
    private DbDsijUser currentUser;
    private String currentUserName;
    private Realm realm;
    SimpleDraweeView ivNavProfile;
    private TextView tvNavName, tvNavEmail;

    private BottomNavigationView mNavigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            popAllFragments();

            switch (item.getItemId()) {
                case R.id.nav_summary:
                    setFragment(UpdateRecoFragment.newInstance());
                    return true;
                case R.id.nav_portfolio:
                    setFragment(RecoPortfolioAllFragment.newInstance());
                    return true;
                case R.id.nav_sold_scrip:
                    setFragment(SoldScripFragment.newInstance());
                    return true;
                case R.id.nav_activity_log:
                    setFragment(ActivityLogFragment.newInstance());
                    return true;
                case R.id.nav_submit_portfolio:
                    setFragment(SubmitedRecoPortFragment.newInstance());
                    return true;
            }
            return false;
        }

    };
    public static Intent getIntent(@NonNull Context packageContext, @Nullable String toolbarTitle, @NonNull String url) {
        Intent intent = new Intent(packageContext, WebViewActivity.class);
        intent.putExtra(EXTRA_TOOLBAR_TITLE, toolbarTitle);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    public static Intent getIntent(@NonNull Context packageContext) {
        return new Intent(packageContext, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        frag_container_main = (FrameLayout) findViewById(R.id.frag_container_main);
        mFragmentManager = getSupportFragmentManager();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        findNavViews(navigationView.getHeaderView(0));
        setNavView();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawer.addDrawerListener(this);



        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // BottomNavigationViewHelper.disableShiftMode(mNavigation);

        showBottomNavigation(true);
        setDrawerEnabled(true);
        gotoMainFlow();
//        showActionBar(false);

        /*drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setDrawerEnabled(false);*/

        realm = Realm.getDefaultInstance();

        receiver = new NetworkReceiver();

//        receiverNotification=new MyReceiver();
    }

    private void findNavViews(View headerView) {
        ivNavProfile = (SimpleDraweeView) headerView.findViewById(R.id.ivNavProfile);
        tvNavName = (TextView) headerView.findViewById(R.id.tvNavName);
        tvNavEmail = (TextView) headerView.findViewById(R.id.tvNavEmail);
    }


    private void setNavView() {

        try {
            Realm realm = Realm.getDefaultInstance();
            DbDsijUser currentUser = realm.where(DbDsijUser.class).findFirst();
            String profilePic = currentUser.getProfileImg();
            currentUserName = currentUser.getUsername();
            String email = currentUser.getEmail();
            String displayName = currentUser.getDisplayName();
            this.currentUser=currentUser;
            //  logUser();
            if (C.device.profileAvatar.isEmpty() || C.device.profileAvatar.equals("null") || C.device.profileAvatar.length() == 0)
                ivNavProfile.setImageURI(Uri.parse("res:/" + R.drawable.com_facebook_profile_picture_blank_portrait));
            else if(profilePic.length()!=0)
                ivNavProfile.setImageURI(Uri.parse(profilePic));
            else
                ivNavProfile.setImageURI(C.device.profileAvatar);

            tvNavName.setText(currentUser.getDisplayName());
            if (TextUtils.isEmpty(email)) {
                tvNavEmail.setVisibility(View.GONE);
            } else {
                tvNavEmail.setText(email);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(frag_container_main, "User Details not available.", Snackbar.LENGTH_LONG).show();
        }
    }

    private void gotoMainFlow() {
        popAllFragments();
        showBottomNavigation(true);
        setDrawerEnabled(true);

        mNavigation.setSelectedItemId(R.id.nav_summary);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        /* if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {

            ExitDialogFragment exitDialogFragment = ExitDialogFragment.newInstance("", "Do you want to exit ?");
            exitDialogFragment.show(mFragmentManager, EXIT_DIALOG);
        } else {
            try {
                super.onBackPressed();
            } catch (IllegalStateException e) {
                // home();
            }

        }
    }

    private void showBottomNavigation(boolean show) {
        if (show) {
            mNavigation.setVisibility(View.VISIBLE);
        } else {
            mNavigation.setVisibility(View.GONE);
        }

    }

    private void popAllFragments() {
        try {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFragment(Fragment fragment) {
  /*      getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_container_main, fragment, null)
                .addToBackStack(null)
                .commitAllowingStateLoss();*/

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frag_container_main, fragment);
        transaction.commit();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        boolean closeDrawer = true;
        if (id == R.id.drawer_nav_home) {
            /* home();*/
        } else if (id == R.id.drawer_nav_terms) {
            startActivity(WebViewActivity.getIntent(this.getApplicationContext(), "Terms & Conditions", C.about.URL_TERMS));
        } else if (id == R.id.drawer_nav_privacy) {
            startActivity(WebViewActivity.getIntent(this.getApplicationContext(), "Privacy Policies", C.about.URL_PRIVACY));
        } else if (id == R.id.drawer_nav_offers) {

            startActivity(WebViewActivity.getIntent(this.getApplicationContext(), "OFFERS", C.about.URL_PRIVACY));
        } else if (id == R.id.drawer_nav_rate_us) {
            RatingBottomSheetDialogFragment dialogFragment = RatingBottomSheetDialogFragment.newInstance();
            dialogFragment.show(getSupportFragmentManager(), DIAG_RATE);
        } else if (id == R.id.drawer_nav_about_us) {
            startActivity(WebViewActivity.getIntent(this.getApplicationContext(), "About Us", C.about.URL_ABOUT_US));
        } else if (id == R.id.drawer_nav_contact_us) {
            contactUs();
        } else if (id == R.id.drawer_nav_help) {
            startActivity(WebViewActivity.getIntent(this.getApplicationContext(), "Help", C.about.URL_HELP_US));
        } else if (id == R.id.drawer_nav_invite_friends) {
            inviteFriends();
        } else if (id == R.id.drawer_nav_logout) {
            logout();
        } else if (id == R.id.drawer_nav_report_issue) {
            reportApp();
        } else if (id == R.id.nav_change_password) {
            startActivity(ChangePasswordActivity.getIntent(this.getApplicationContext()));
        }

        if (closeDrawer) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return false;
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

                                Log.v(LOG_TAG, "Logout" +
                                        C.net.tag.RESPONSE +
                                        new GsonBuilder()
                                                .setPrettyPrinting()
                                                .create()
                                                .toJson(resMessage));
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

                                Snackbar.make(frag_container_main, C.net.tag.ERROR_TIMEOUT_MSG, Snackbar.LENGTH_SHORT).show();

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
                            Snackbar.make(frag_container_main, "! Not available in offline mode.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void contactUs() {

        String email = C.about.EMAIL_CONTACT_US;
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback/Enquiry for DSIJ PAS android app");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello,\n");
        try {
            startActivity(
                    Intent.createChooser(emailIntent, "Send email via : ")
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            );
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(LOG_TAG, "Email Client Not Found", e);
            Snackbar.make(frag_container_main, "! Email Client Not Found", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void reportApp() {

        if (!MyApplication.isConnected()) {
            Snackbar.make(frag_container_main, "! Not Available Offline", Snackbar.LENGTH_LONG).show();
            return;
        }

        ReportDialogFragment reportDialog = ReportDialogFragment.newInstance("Report Issue", null, currentUser.getEmail());
        reportDialog.show(mFragmentManager, DIALOG_REPORT);
    }

    public void inviteFriends() {

        String appPackageName = getPackageName();

        String shareLink = "https://play.google.com/store/apps/details?id=" + appPackageName;

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, C.SHARE_SUBJECT + shareLink);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "DSIJ");
        emailIntent.setType("message/rfc822");
        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        Intent openInChooser = Intent.createChooser(emailIntent, "Share via...");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if (packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("whatsapp") || packageName.contains("linkedin")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "DSIJ");
                intent.putExtra(Intent.EXTRA_TEXT, C.SHARE_SUBJECT + shareLink);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }
        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
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

    @Override
    public void onDrawerSlide(@NonNull View view, float v) {

    }

    @Override
    public void onDrawerOpened(@NonNull View view) {

    }

    @Override
    public void onDrawerClosed(@NonNull View view) {

    }

    @Override
    public void onDrawerStateChanged(int i) {

    }

    @Override
    public void setDrawerEnabled(boolean enabled) {

    }

    @Override
    public void showActionBar(boolean shown) {

    }

    @Override
    public void showUpButton(boolean shown) {

    }

    @Override
    public void setViewPagerWithTabBar(ViewPager viewPager) {

    }

    @Override
    public void setActionBarTitle(String title) {

    }

    private void callOffers() {
        CallGenerator.offerURL()
                .enqueue(new Callback<ResOfferURL>() {
                    @Override
                    public void onResponse(Call<ResOfferURL> call, retrofit2.Response<ResOfferURL> response) {
                        if (response.isSuccessful()) {
                            try {
                                ResOfferURL resOfferURL = response.body();

                                Log.v(LOG_TAG, "Offer Page" +
                                        new GsonBuilder()
                                                .setPrettyPrinting()
                                                .create()
                                                .toJson(resOfferURL));

                                if (resOfferURL == null) {
                                    Log.w(LOG_TAG, C.net.tag.RESPONSE +
                                            "Received NULL Response for resOfferURL");
                                } else {
                                    resOfferURL.getURL();
                                    setFragment(WebViewFragment.newInstance(C.url.API_BASE_URL + resOfferURL.getURL()));
                                }

                            } catch (Exception e) {
                                Log.w(LOG_TAG, "OfFrePage" + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
                            }
                        } else {

                            ResMessage errorMessage = null;
                            try {
                                String errorRes = response.errorBody().string();

                                errorMessage = new GsonBuilder().create().fromJson(errorRes, ResMessage.class);

                            } catch (IOException e) {
                                //
                            }

                            switch (response.code()) {
                                case 410:
                                    Snackbar.make(mNavigation, "! Server ran into trouble.", Snackbar.LENGTH_LONG).show();
                                    break;
                                case 413:
                                    Snackbar.make(mNavigation, "! already have signed in via other device.", Snackbar.LENGTH_LONG).show();
                                    break;
                                case C.net.resetPassword.error.EMPTY_PARAMS:
                                    Snackbar.make(mNavigation, "! App ran into trouble.", Snackbar.LENGTH_LONG).show();
                                    break;
                                default:
                                    Snackbar.make(mNavigation, "! Server ran into trouble.", Snackbar.LENGTH_LONG).show();
                                    break;
                            }
                            Log.w(LOG_TAG, "Offer page" + C.net.tag.ERROR + "Error Response code : " + response.code() + " Response Body:\n" +
                                    new GsonBuilder()
                                            .setPrettyPrinting()
                                            .create()
                                            .toJson(errorMessage));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResOfferURL> call, Throwable t) {
                        Log.e(LOG_TAG, C.net.resetPassword.TAG + C.net.tag.FAILED + "Failed API Call : ", t);

                        Snackbar.make(mNavigation, "! Something went wrong.", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onTokanChange() {

    }

    @Override
    public void updateRecoPrice(String companyName, String companyCode, long quantity, String avgPrice, long orderQueId, String operation) {
        CallGenerator.updateRecommededScrip(companyCode, companyName, quantity, avgPrice, orderQueId, operation).enqueue(new Callback<ResMessage>() {
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
                            Snackbar.make(frag_container_main, resMessage.getMessage(), Snackbar.LENGTH_SHORT).show();
                            setFragment(UpdateRecoFragment.newInstance());
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
                        Snackbar.make(frag_container_main, errorMessage.getMessage(), Snackbar.LENGTH_SHORT).show();
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

    @Override
    public void submitReport(String email, String phone, String description) {

        CallGenerator.postFeedback(email, phone, "DSIJ PAS Android Issue", description)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            //Todo:  Msg by response code
                            Snackbar.make(frag_container_main, "Sent Successfully", Snackbar.LENGTH_SHORT).show();

                        } else {
                            switch (response.code()) {
                                case 400:
                                    break;
                                case 401:
                                    break;
                                case 402:
                                    break;
                                case 403:
                                    break;
                                default:
                                    break;
                            }
                            Log.e("Retrofit", "Error Response code : " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(LOG_TAG, "Error Post Feedback", t);
                    }
                });
    }

    @Override
    public void postLowRating(int rating, String comments) {
        CallGenerator.postLowRating(rating, comments).enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                if (response.isSuccessful()) {
                    try {
                        ResMessage resMessage = response.body();

                        Log.v(LOG_TAG, C.net.postLowRating.TAG +
                                C.net.tag.RESPONSE +
                                new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(resMessage));

                        Log.w(LOG_TAG, C.net.postLowRating.TAG +
                                C.net.tag.RESPONSE + "Received Empty Message");

                    } catch (Exception e) {
                        Log.w(LOG_TAG, C.net.postLowRating.TAG + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
                    }
                } else {

                    ResMessage errorMessage = null;

                    try {
                        String errorRes = response.errorBody().string();

                        errorMessage = new GsonBuilder().create().fromJson(errorRes, ResMessage.class);

                    } catch (IOException e) {
                        //
                    }

                    Log.w(LOG_TAG, C.net.postLowRating.TAG + C.net.tag.ERROR + "Error Response code : " + response.code() + " Response Body:\n" +
                            new GsonBuilder()
                                    .setPrettyPrinting()
                                    .create()
                                    .toJson(errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {
                Log.e(LOG_TAG, C.net.postLowRating.TAG + C.net.tag.FAILED + "Failed API Call : ", t);
            }
        });
    }

    @Override
    public void exitApp() {
        finish();
    }
}
