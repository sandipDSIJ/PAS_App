package in.dsij.pas.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import in.dsij.pas.MyApplication;
import in.dsij.pas.R;
import in.dsij.pas.activity.SplashActivity;
import in.dsij.pas.constants.C;
import in.dsij.pas.database.DbAns;
import in.dsij.pas.database.DbDsijUser;
import in.dsij.pas.database.DbLogs;
import in.dsij.pas.database.DbRecoAllPortfolio;
import in.dsij.pas.database.DbRiskAssessment;
import in.dsij.pas.database.DbSoldScripDetails;
import in.dsij.pas.database.DbSubmitedPortFolioScrip;
import in.dsij.pas.database.DbUpdateReco;
import in.dsij.pas.dialog.ProgressDialogFragment;
import in.dsij.pas.fcm.MyFirebaseInstanceIDService;
import in.dsij.pas.net.respose.ResDsijLogin;
import in.dsij.pas.net.retrofit.CallGenerator;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Response;

public class LoginPASActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = "LoginPASActivity";

    private static final String DIALOG_PROGRESS = "LoginFlashNewsActivity.Dialog.Progress";
    private static final int REQ_UPDATE = 1010;
    private static final String DIAG_UPDATE = "LoginPASActivity.Dialog.DIAG_UPDATE";

    private static final String EMAIL = "email";
    private static final int RC_SIGN_IN = 9001;
    private Realm realm;

    private Callbacks mCallbacks;
    private CallbackManager fbCallbackManager;

    private LoginButton fbLoginButton;

    private ImageButton ibFacebookLogin;
    private ImageButton ibGoogleLogin;

    private ProfileTracker fbProfileTracker;
    private Profile fbCurrentProfile;
    private AccessToken fbAccessToken;
    private String fcmToken="test";


    private EditText etEmail, etPassword;
    private CheckBox ckbShowPassword;
    private TextView tvBtnContinue, tvSignUp,tvHelp;
    private TextView tvTerms;
    private TextView tvPrivacy;
    private TextView tvVersion;
    private SimpleDraweeView ivHeader;
    private TextView tvForgotPassword;
    private int loginResponseCode;
    private String email;
    private FirebaseAuth mAuth;
    LinearLayout llMain;
    public GoogleApiClient mGoogleApiClient;
    public GoogleSignInClient mGoogleSignInClient;
    private ProgressDialogFragment mProgressDialog;
    private RelativeLayout topLevelLayout;
    private ImageView ivInstruction;

    public static Intent getIntent(@NonNull Context packageContext) {
        return new Intent(packageContext, LoginPASActivity.class);
    }

    private void clearDb() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(DbDsijUser.class).findAll().deleteAllFromRealm();
                realm.where(DbUpdateReco.class).findAll().deleteAllFromRealm();
                realm.where(DbSoldScripDetails.class).findAll().deleteAllFromRealm();
                realm.where(DbRecoAllPortfolio.class).findAll().deleteAllFromRealm();
                realm.where(DbLogs.class).findAll().deleteAllFromRealm();
                realm.where(DbRiskAssessment.class).findAll().deleteAllFromRealm();
                realm.where(DbLogs.class).findAll().deleteAllFromRealm();
                realm.where(DbAns.class).findAll().deleteAllFromRealm();
                realm.where(DbSubmitedPortFolioScrip.class).findAll().deleteAllFromRealm();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiivity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        realm = Realm.getDefaultInstance();
        fbCallbackManager = CallbackManager.Factory.create();
        try {
            LoginManager.getInstance().logOut();
        } catch (FacebookException fe) {
            //do nothing
        }
        clearDb();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this.getApplicationContext())
                .enableAutoManage(this/* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        findView();
        mAuth = FirebaseAuth.getInstance();

        setView();

        if (isFirstTime()) {
           /* mUserHelpDialog=UserHelpDialog.newInstance();
            mUserHelpDialog.show(getSupportFragmentManager(),DIALOG_USER_HELP);*/
            /*mMagicLinkDialog = MagicLinkDialogFragment.newInstance(email);
                mMagicLinkDialog.show(getSupportFragmentManager(), DIALOG_MAGIC_LINK);*/
            topLevelLayout.setVisibility(View.VISIBLE);
            llMain.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
    }

    private String getFCMToken() {
        MyFirebaseInstanceIDService myFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
        myFirebaseInstanceIDService.sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken());
        return FirebaseInstanceId.getInstance().getToken();
    }
    
    private void findView() {

        llMain = (LinearLayout) findViewById(R.id.ll_main);
        ibFacebookLogin = (ImageButton) findViewById(R.id.ibFacebookLogin);
        ibGoogleLogin = (ImageButton) findViewById(R.id.ibGoogleLogin);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etpass);
        topLevelLayout=(RelativeLayout)findViewById(R.id.top_layout);
        fbLoginButton = (LoginButton) findViewById(R.id.login_button);
        tvBtnContinue = (TextView) findViewById(R.id.tvBtnContinue);
        tvTerms = (TextView) findViewById(R.id.tvTerms);
        tvPrivacy = (TextView) findViewById(R.id.tvPrivacy);
        tvVersion = (TextView) findViewById(R.id.tvVersion);
        ivHeader = (SimpleDraweeView) findViewById(R.id.ivHeader);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgetPassword);
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        tvHelp= (TextView) findViewById(R.id.tv_help);
        ivInstruction=(ImageView)findViewById(R.id.ivInstruction);
    }

    private void setView() {
        ivHeader.setImageURI(Uri.parse("res:/" + R.drawable.dsij_stocks));

        String versionText = "V " + C.device.VERSION_NAME;
        tvVersion.setText(versionText);

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tvBtnContinue.performClick();
                    return true;
                }
                return false;
            }
        });
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(ForgetPasswordActivity.getIntent(LoginPASActivity.this));
            }
        });

        tvBtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString().trim();
               /* if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.setError("Invalid Email");
                    return;
                }*/

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                boolean valid =
                        validateUsername() &&
                                validatePassword();

                if (valid) {
                    String userName = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    if(fcmToken!=null)
                    signIn(userName, password, fcmToken);
                    else {
                        //fcmToken=getFCMToken();
                        signIn(userName, password, fcmToken);
                    }
                }

                /*mMagicLinkDialog = MagicLinkDialogFragment.newInstance(email);
                mMagicLinkDialog.show(getSupportFragmentManager(), DIALOG_MAGIC_LINK);*/

            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SignUpActivity.getIntent(LoginPASActivity.this));
            }
        });
        ibGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MyApplication.isConnected()) {
                    signOutFromGoogle();
/*
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);*/
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                } else {
                    Snackbar.make(llMain, C.net.tag.ERROR_CHECK_INTERNET, Snackbar.LENGTH_INDEFINITE)
                            .setAction("Exit", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //mCallbacks.exitApp();
                                }
                            })
                            .show();
                }
            }
        });


        ibFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.isConnected())
                    fbLoginButton.performClick();
                else
                    Snackbar.make(llMain, C.net.tag.ERROR_CHECK_INTERNET, Snackbar.LENGTH_INDEFINITE)
                            .setAction("Exit", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //mCallbacks.exitApp();
                                }
                            })
                            .show();
            }
        });

        fbLoginButton.setReadPermissions(Arrays.asList(EMAIL));

        LoginManager.getInstance().registerCallback(fbCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                        if (isLoggedIn) {
                            getUserDetails(loginResult);
                        }
                        String accessToken1 = loginResult.getAccessToken().getToken();
                        Log.i("accessToken", accessToken1);
                        fbAccessToken = loginResult.getAccessToken();
                        fbCurrentProfile = Profile.getCurrentProfile();

                        if (fbCurrentProfile == null) {
                            fbProfileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                    // App code
                                    fbCurrentProfile = currentProfile;
                                    fbProfileTracker.stopTracking();
                                }
                            };
                        }

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        //Toast.makeText(getApplicationContext(), "Cancle", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        //Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                });
        tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTermsAndConditions();
            }
        });

        tvPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrivacyPolicy();
            }
        });

        tvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*mUserHelpDialog=UserHelpDialog.newInstance();
                mUserHelpDialog.show(getSupportFragmentManager(),DIALOG_USER_HELP);*/
                topLevelLayout.startAnimation(inFromRightAnimation(200));
                topLevelLayout.setVisibility(View.VISIBLE);
                // llMain.startAnimation(inFromLeftAnimation(200));
                llMain.setVisibility(View.VISIBLE);
            }
        });

        ivInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topLevelLayout.startAnimation(outToRightAnimation(200));
                topLevelLayout.setVisibility(View.INVISIBLE);
                llMain.startAnimation(inFromLeftAnimation(200));
                llMain.setVisibility(View.VISIBLE);
                return;
            }
        });

    }

    private boolean isFirstTime()
    {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("SMC_FIRST_TIME", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("SMC_FIRST_TIME", true);
            editor.commit();
        }
        return !ranBefore;
    }

    private void signOutFromGoogle() {
        FirebaseAuth.getInstance().signOut();
    }


    private void showPrivacyPolicy() {
        //Todo
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(C.about.URL_PRIVACY));
        startActivity(intent);
    }

    private void showTermsAndConditions() {
        //Todo
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(C.about.URL_TERMS));
        startActivity(intent);
    }

    protected void getUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {

                        String email = "", gender = "";
                        try {
                            if (json_object.has("email"))
                                email = json_object.getString("email");
                            if (json_object.has("gender"))
                                gender = json_object.getString("gender");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (Exception e) {
                            Log.e(LOG_TAG,e.toString());
                        }
                        try {
                            String fbSocialId = fbCurrentProfile.getId();
                            if (fbSocialId != null && !fbSocialId.isEmpty()) {
                                loginWithFacebook(fbCurrentProfile.getId(), fbAccessToken.getToken(),
                                        fbCurrentProfile.getFirstName(), fbCurrentProfile.getLastName(),
                                        fbCurrentProfile.getProfilePictureUri(96, 96).toString(), email, gender);
                            } else {
                                loginWithFacebook("Not get social_ID", fbAccessToken.getToken(),
                                        fbCurrentProfile.getFirstName(), fbCurrentProfile.getLastName(),
                                        fbCurrentProfile.getProfilePictureUri(96, 96).toString(), email, gender);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    private void loginWithFacebook(@Nullable String socialId, String token, String name, String lastName, String avatar, String email, String gender) {
        if(fcmToken!=null)
        {
            startActivity(
                    SplashActivity.getIntent(
                            this.getApplicationContext(), socialId, token, name, lastName, avatar, email, gender,
                            C.net.process.LOGIN_WITH_FACEBOOK, fcmToken));
            finish();
        }
        else {
            fcmToken=getFCMToken();
            startActivity(
                    SplashActivity.getIntent(
                            this.getApplicationContext(), socialId, token, name, lastName, avatar, email, gender,
                            C.net.process.LOGIN_WITH_FACEBOOK, fcmToken));
            finish();
        }

    }

    private void loginWithGoogle(String socialId, String token, String name, String lastName, String avatar, String email, String gender) {
        if(fcmToken!=null) {
            startActivity(
                    SplashActivity.getIntent(
                            this.getApplicationContext(),
                            socialId,
                            "",
                            name,
                            lastName,
                            avatar,
                            email,
                            gender,
                            C.net.process.LOGIN_WITH_GOOGLE, fcmToken));

            finish();
        }
        else {
            fcmToken=getFCMToken();
            startActivity(
                    SplashActivity.getIntent(
                            this.getApplicationContext(),
                            socialId,
                            "",
                            name,
                            lastName,
                            avatar,
                            email,
                            gender,
                            C.net.process.LOGIN_WITH_GOOGLE, fcmToken));

            finish();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //  handleSignInResult
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(llMain, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //   updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            fbCallbackManager.onActivityResult(requestCode, resultCode, data);
            // twLoginButton.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            try {
                GoogleSignInAccount googleAccount = result.getSignInAccount();
                assert googleAccount != null;
                Log.d("google acc : ", "\n\n" +
                        "\nid : " + googleAccount.getId() +
                        "\nname : " + googleAccount.getDisplayName() +
                        "\nemail : " + googleAccount.getEmail() +
                        "\ntoken : " + googleAccount.getIdToken() +
                        "\nserver auth code : " + googleAccount.getServerAuthCode() +
                        "\nscope : " + googleAccount.getGrantedScopes() +
                        "\nphoto : " + googleAccount.getPhotoUrl() +
                        "\n\n");
                loginWithGoogle(googleAccount.getId(), "123", googleAccount.getDisplayName(), googleAccount.getFamilyName(), String.valueOf(googleAccount.getPhotoUrl()), googleAccount.getEmail(), "");
            } catch (Exception e) {
                Log.e(LOG_TAG, "Google Login Error", e);
            }
        }
    }

    private void signIn(final String userName, final String password, final String fcmToken) {
        retrofit2.Callback<ResDsijLogin> loginCallback = new retrofit2.Callback<ResDsijLogin>() {
            @Override
            public void onResponse(Call<ResDsijLogin> call, Response<ResDsijLogin> response) {
                if (response.isSuccessful()) {
                    try {
                        ResDsijLogin resLogin = response.body();

                        Log.v(LOG_TAG, C.net.loginWithPassword.TAG +
                                C.net.tag.RESPONSE +
                                new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(resLogin));

                        loginResponseCode = response.code();

                        if (resLogin == null) {
                            Log.w(LOG_TAG, C.net.loginWithPassword.TAG +
                                    C.net.tag.RESPONSE + "Received NULL User");
                        } else {
                            insertUser(resLogin);
                        }

                    } catch (Exception e) {
                        Log.w(LOG_TAG, C.net.loginWithPassword.TAG + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
                    }
                } else {
                    hideProgressDialog();
                    View.OnClickListener retryClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            signIn(userName, password, getFCMToken());
                        }
                    };

                    switch (response.code()) {
                        case C.net.loginWithPassword.error.INTERNAL_SERVER_ERROR:

                            Snackbar.make(llMain, "!Server ran into trouble.", Snackbar.LENGTH_LONG)
                                    .setAction("Retry", retryClickListener).show();
                            break;
                        case C.net.loginWithPassword.error.INCORRECT_PASSWORD:
                            etPassword.setFocusable(true);
                            etPassword.setError("! Incorrect Password");
                            break;
                        case C.net.loginWithPassword.error.USER_LOCKED:
                            Snackbar.make(llMain, "User is locked", Snackbar.LENGTH_LONG).show();
                            break;
                        case C.net.loginWithPassword.error.NOT_AUTHORISED:
                            break;
                        case C.net.loginWithPassword.error.SERVER_ERROR:
                            Snackbar.make(llMain, "!Server ran into trouble.", Snackbar.LENGTH_LONG)
                                    .setAction("Retry", retryClickListener).show();
                            break;
                        case C.net.loginWithPassword.error.USERNAME_NOT_REGISTERED:
                            etEmail.setFocusable(true);
                            etEmail.setError("! Username not Registered");
                            break;
                        case C.net.loginWithPassword.error.EMPTY_PARAMS:
                            Snackbar.make(llMain, "!App ran into trouble.", Snackbar.LENGTH_LONG).show();
                            break;
                        default:
                            Snackbar.make(llMain, "!App ran into trouble.", Snackbar.LENGTH_LONG).show();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ResDsijLogin> call, Throwable t) {
                Log.e(LOG_TAG, C.net.loginWithPassword.TAG + " < FAILED >::  " + "Failed API Call : ", t);
                hideProgressDialog();
                if (!MyApplication.isConnected()) {
                    Snackbar.make(llMain, C.net.tag.ERROR_CHECK_INTERNET, Snackbar.LENGTH_INDEFINITE)
                            .setAction("Exit", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //mCallbacks.exitApp();
                                }
                            })
                            .show();
                } else {
                    Snackbar.make(llMain, "Oops, the server is currently\n unavailable please try agein later", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Exit", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //mCallbacks.exitApp();
                                    finish();
                                }
                            })
                            .show();
                }
            }
        };

        showProgressDialog();
        try {
            if (fcmToken != null) {
                CallGenerator.loginWithPassword(userName, password, this.fcmToken)
                        .enqueue(loginCallback);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            CallGenerator.loginWithPassword(userName, password, getFCMToken())
                    .enqueue(loginCallback);
        }

    }

    private void insertUser(final ResDsijLogin resLogin) {

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

                hideProgressDialog();
                startActivity(SplashActivity.getIntent(LoginPASActivity.this));
                finish();
                //handleLoginResponseCode();

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + C.net.tag.DB_FAIL,
                        error);
                hideProgressDialog();
            }
        });

    }

    private void showProgressDialog() {
        mProgressDialog = ProgressDialogFragment.newInstance(null, "Wait...\n" + email);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show(getSupportFragmentManager(), DIALOG_PROGRESS);
    }

    private void hideProgressDialog() {
        try {
            mProgressDialog.dismiss();
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public interface Callbacks {

        void loggedIn();

        void onClickSignUp();

        void onClickForgotPassword();

        void showProgressDialog(String message);

        void dismissProgressDialog();

        void showOfflineDialog();
    }

    private boolean validateUsername() {

        String userName = etEmail.getText().toString().trim();

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userName).matches() &&
                (TextUtils.isEmpty(userName) ||
                        userName.contains(" ") ||
                        userName.length() < 6 ||
                        userName.length() > 99)) {
            etEmail.setError("! Invalid Username/Email Id");
            etEmail.setFocusable(true);
            return false;
        } else {
            etEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        Editable password = etPassword.getText();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("! Empty Password");
            etPassword.setFocusable(true);
            return false;
        } else {
            etEmail.setError(null);
            return true;
        }
    }

    public static Animation outToRightAnimation(long durationMillis) {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f,
                Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        outtoRight.setDuration(durationMillis);
        return outtoRight;
    }

    public static Animation inFromLeftAnimation(long durationMillis) {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
                Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        inFromLeft.setDuration(durationMillis);
        return inFromLeft;
    }

    public static Animation inFromRightAnimation(long durationMillis) {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
                Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        inFromRight.setDuration(durationMillis);
        return inFromRight;
    }
}
