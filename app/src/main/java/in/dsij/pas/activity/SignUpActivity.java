package in.dsij.pas.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.GsonBuilder;

import java.io.IOException;

import in.dsij.pas.R;
import in.dsij.pas.constants.C;
import in.dsij.pas.dialog.AlertDialogFragment;
import in.dsij.pas.dialog.ErrorDialogFragment;
import in.dsij.pas.dialog.ProgressDialogFragment;
import in.dsij.pas.net.respose.ResMessage;
import in.dsij.pas.net.retrofit.CallGenerator;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements ErrorDialogFragment.Callbacks{

    private static final String LOG_TAG = "SignUpActivity";
    private static final String DIALOG_PROGRESS = "SignUpActivity.Dialog.Progress";
    private static final String DIALOG_ALERT = "SignUpActivity.Dialog.Alert";
    private static final int REQ_SIGNED_UP = 1003;
    private static final String DIAG_SIGNED_UP = "SignUpActivity.Dialog.DIAG_SIGNED_UP";
    private static final String DIALOG_ERROR = "ChangePasswordActivity.Dialog.Error";

    private Realm realm;

    private TextView tvHeader;
    private TextInputLayout tilFirstName;
    private EditText etFirstName;
    private TextInputLayout tilLastName;
    private EditText etLastName;
    private TextInputLayout tilEmail;
    private EditText etEmail;
    private TextInputLayout tilUsername;
    private EditText etUsername;
    private TextInputLayout tilPhone;
    private EditText etPhone;
    private TextView tvLabelTerms;
    private TextView tvPrivacy;
    private TextView tvLabelAnd;
    private TextView tvTerms;
    private TextView tvBtnSignUp;
    private TextView tvBtnSignIn;
    private ProgressDialogFragment progressDialog;
    private AlertDialogFragment mAlertDialog;
   // LinearLayout llMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        realm = Realm.getDefaultInstance();

        findViews();

        setViews();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    /*private void clearDb() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                realm.where(DbDsijUser.class).findAll().deleteAllFromRealm();

                realm.where(DbCategory.class).findAll().deleteAllFromRealm();
                realm.where(DbResTracker.class).findAll().deleteAllFromRealm();
                realm.where(Columns.class).findAll().deleteAllFromRealm();
                realm.where(Entities.class).findAll().deleteAllFromRealm();
            }
        });
    }*/


    private void findViews() {
      //  llMain=(LinearLayout) findViewById(R.id.ll_main);
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tilFirstName = (TextInputLayout) findViewById(R.id.tilFirstName);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        tilLastName = (TextInputLayout)findViewById(R.id.tilLastName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        tilEmail = (TextInputLayout) findViewById(R.id.tilEmail);
        etEmail = (EditText) findViewById(R.id.etEmail);
        tilUsername = (TextInputLayout) findViewById(R.id.tilUsername);
        etUsername = (EditText) findViewById(R.id.etUsername);
        tilPhone = (TextInputLayout)findViewById(R.id.tilPhone);
        etPhone = (EditText) findViewById(R.id.etPhone);
        tvLabelTerms = (TextView)findViewById(R.id.tvLabelTerms);
        tvPrivacy = (TextView)findViewById(R.id.tvPrivacy);
        tvLabelAnd = (TextView) findViewById(R.id.tvLabelAnd);
        tvTerms = (TextView) findViewById(R.id.tvTerms);
        tvBtnSignUp = (TextView)findViewById(R.id.tvBtnSignUp);
        tvBtnSignIn = (TextView)findViewById(R.id.tvBtnSignIn);

    }


    private void setViews() {


        etFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    tilFirstName.setError(null);
                }
            }
        });

        etLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    tilLastName.setError(null);
                }
            }
        });

        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    tilEmail.setError(null);
                }
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etUsername.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    tilPhone.setError(null);
                }
            }
        });

        tvBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call Login Activity
            }
        });

        tvBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                } catch (Exception e) {
                    //do nothing
                }

                boolean valid =
                        validateFirstName() &&
                                validateLastName() &&
                                validateEmail() &&
                                validatePhone();

                if (valid) {
                    signUp(etFirstName.getText().toString().trim(),
                            etLastName.getText().toString().trim(),
                            etEmail.getText().toString().trim(),
                            etPhone.getText().toString().trim());


//                    mCallbacks.signUp();
                }
            }
        });

        tvPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrivacyPolicy();
            }
        });

        tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTermsAndConditions();
            }
        });
    }

    private void signUp(String firstName, String lastName, String email, String phone) {

        showProgressDialog();

        CallGenerator.signUp(firstName,lastName,email,phone)
                .enqueue(new Callback<ResMessage>() {
                    @Override
                    public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                        if (response.isSuccessful()) {
                            try {
                                ResMessage resMessage = response.body();

                                Log.v(LOG_TAG, C.net.signUp.TAG +
                                        C.net.tag.RESPONSE +
                                        new GsonBuilder()
                                                .setPrettyPrinting()
                                                .create()
                                                .toJson(resMessage));

                                if (resMessage == null) {
                                    Log.w(LOG_TAG, C.net.signUp.TAG +
                                            C.net.tag.RESPONSE + "Received NULL Response for SignUp");
                                } else {

                                    hideProgressDialog();

                                    showErrorDialog("Signed up!\nPassword has been sent to your email id.");
                                    mAlertDialog.show(getSupportFragmentManager(), DIALOG_ALERT);
                                }

                            } catch (Exception e) {
                                Log.w(LOG_TAG, C.net.signUp.TAG + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
                            }
                        } else {

                            hideProgressDialog();

                            ResMessage errorMessage = null;

                            try {
                                String errorRes = response.errorBody().string();

                                errorMessage = new GsonBuilder().create().fromJson(errorRes, ResMessage.class);

                            } catch (IOException e) {
                                //
                            }

                            switch (response.code()) {
                                case C.net.signUp.error.INTERNAL_SERVER_ERROR:

                                    Snackbar.make(tvBtnSignUp, "! Server ran into trouble.", Snackbar.LENGTH_LONG).show();

                                    break;
                                case C.net.signUp.error.EMAIL_ALREADY_EXISTS:
                                    tilEmail.setError("! Email Already Registered");

                                    break;
                                case C.net.signUp.error.ERROR_IN_SENDING_MAIL:

                                    Snackbar.make(tvBtnSignUp, "! Failed to sent email, please check email id & Try again.", Snackbar.LENGTH_LONG).show();

                                    // TODO: 9/13/2017 Show Dialog
                                    break;
                                case C.net.signUp.error.EMPTY_PARAMS:
                                    Snackbar.make(tvBtnSignUp, "! App ran into trouble.", Snackbar.LENGTH_LONG).show();

                                    // TODO: 9/13/2017 Reload Fragment

                                    break;
                                default:
                                    Snackbar.make(tvBtnSignUp, "! Server ran into trouble.", Snackbar.LENGTH_LONG).show();
                                    break;
                            }
                            Log.w(LOG_TAG, C.net.signUp.TAG + C.net.tag.ERROR + "Error Response code : " + response.code() + " Response Body:\n" +
                                    new GsonBuilder()
                                            .setPrettyPrinting()
                                            .create()
                                            .toJson(errorMessage));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResMessage> call, Throwable t) {
                        Log.e(LOG_TAG, C.net.signUp.TAG + C.net.tag.FAILED + "Failed API Call : ", t);
                        hideProgressDialog();
                    }
                });
    }

    private void showErrorDialog(String dialogMessage) {

        //showProgressBar(false);

        ErrorDialogFragment errorDialog = ErrorDialogFragment.newInstance("Password Changed", dialogMessage);
        FragmentManager fragmentManager = getSupportFragmentManager();
        errorDialog.show(fragmentManager, DIALOG_ERROR);
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

    private boolean validateEmail() {
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("! Invalid Email");
            return false;
        } else {
            tilEmail.setError(null);
            return true;
        }
    }
    private boolean validateFirstName() {
        String name = etFirstName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            tilFirstName.setError("! Empty Name");
            return false;
        } else {
            tilFirstName.setError(null);
            return true;
        }
    }

    private boolean validateLastName() {
        String name = etLastName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            tilLastName.setError("! Empty Last Name");
            return false;
        } else {
            tilLastName.setError(null);
            return true;
        }
    }

    private boolean validatePhone() {
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || !Patterns.PHONE.matcher(phone).matches()) {
            tilPhone.setError("! Invalid Phone");
            return false;
        } else {
            tilPhone.setError(null);
            return true;
        }
    }
    private void showProgressDialog() {
        progressDialog = ProgressDialogFragment.newInstance(null, "Wait...\n" + "");
        progressDialog.setCancelable(false);
        progressDialog.show(getSupportFragmentManager(), DIALOG_PROGRESS);
    }

    private void hideProgressDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onErrorDialogResponse() {
        Intent intent = LoginPASActivity.getIntent(this.getApplicationContext());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
      //  VolleySingleton.getInstance().getRequestQueue().cancelAll(this);
    }

    public static Intent getIntent(Context applicationContext) {
        return new Intent(applicationContext, SignUpActivity.class);
    }

    @Override
    public void onBackPressed() {
        //onBackPressed();
        startActivity(LoginPASActivity.getIntent(SignUpActivity.this));
        finish();
    }
}
