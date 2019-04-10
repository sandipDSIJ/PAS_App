package in.dsij.pas.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import in.dsij.pas.dialog.AlertBottomSheetDialogFragment;
import in.dsij.pas.dialog.AlertDialogFragment;
import in.dsij.pas.dialog.ErrorDialogFragment;
import in.dsij.pas.dialog.ProgressDialogFragment;
import in.dsij.pas.net.respose.ResMessage;
import in.dsij.pas.net.retrofit.CallGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForgetPasswordActivity extends AppCompatActivity implements ErrorDialogFragment.Callbacks,AlertBottomSheetDialogFragment.Callbacks {

    private static final String LOG_TAG = "ForgotFragment";
    private static final int REQ_FORGOT_PASSWORD = 1004;
    private static final String DIAG_FORGOT_PASSWORD = "ForgotActivity.Dialog.DIAG_FORGOT_PASSWORD";
    private static final int REQ_TRY_AGAIN = 1005;
    private static final String DIAG_TRY_AGAIN = "ForgotActivity.Dialog.DIAG_TRY_AGAIN";
    private static final String DIALOG_PROGRESS = "ForgotActivity.Dialog.Progress";
    private static final String DIALOG_ALERT = "ForgotActivity.Dialog.Alert";
    private static final String DIALOG_ERROR = "ForgetPasswordActivity.Dialog.Error";
 
    private TextView tvHeader;
    private TextInputLayout tilUsername;
    private EditText etUsername;
    private TextView tvLabelHint;
    private TextView tvBtnSendLink;
    private ProgressDialogFragment progressDialog;
    private AlertDialogFragment mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        

        findViews();

        setViews();

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
    

    private void findViews() {
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tilUsername = (TextInputLayout)findViewById(R.id.tilUsername);
        etUsername = (EditText) findViewById(R.id.etUsername);
        tvLabelHint = (TextView) findViewById(R.id.tvLabelHint);
        tvBtnSendLink = (TextView)findViewById(R.id.tvBtnSendLink);
    }


    private void setViews() {


        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    tilUsername.setError(null);
                }
            }
        });

        tvBtnSendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                } catch (Exception e) {
                    //do nothing
                }

                boolean valid = validateEmail();

                if (valid) {
                    resetPassword(etUsername.getText().toString().trim());
                }
            }
        });
    }


    private boolean validateUsername() {
        String userName = etUsername.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(userName).matches() &&
                (TextUtils.isEmpty(userName) ||
                        userName.contains(" ") ||
                        userName.length() < 6 ||
                        userName.length() > 99)) {
            tilUsername.setError("! Invalid Username/Email Id");
            return false;
        } else {
            tilUsername.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String email = etUsername.getText().toString().trim();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etUsername.setError("! Invalid Email");
            return false;
        } else {
            tilUsername.setError(null);
            return true;
        }
    }


    private void resetPassword(String username) {
        /*
        *
        * Here username is Email Id or Username
        *
        * */

        showProgressDialog();

        CallGenerator.resetPassword(username)
                .enqueue(new Callback<ResMessage>() {
                    @Override
                    public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                        if (response.isSuccessful()) {
                            try {
                                ResMessage resMessage = response.body();

                                Log.v(LOG_TAG, C.net.resetPassword.TAG +
                                        C.net.tag.RESPONSE +
                                        new GsonBuilder()
                                                .setPrettyPrinting()
                                                .create()
                                                .toJson(resMessage));

                                if (resMessage == null) {
                                    Log.w(LOG_TAG, C.net.resetPassword.TAG +
                                            C.net.tag.RESPONSE + "Received NULL Response for resetPassword");
                                } else {

                                    hideProgressDialog();
                                    showErrorDialog("\"Password has been sent to your email id.");
                                }

                            } catch (Exception e) {
                                Log.w(LOG_TAG, C.net.resetPassword.TAG + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
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
                                case C.net.resetPassword.error.INTERNAL_SERVER_ERROR:

                                    Snackbar.make(tvBtnSendLink, "! Server ran into trouble.", Snackbar.LENGTH_LONG).show();

                                    break;
                                case C.net.resetPassword.error.INVALID_USERNAME:
                                    tilUsername.setError("! Invalid Username/Email Id");

                                    break;
                                case C.net.resetPassword.error.ERROR_IN_SENDING_EMAIL:
                                    AlertBottomSheetDialogFragment dialogFragment;
                                    dialogFragment = AlertBottomSheetDialogFragment.newInstance(
                                            "Failed to sent email, please contact DSIJ Customer Support.",
                                            "Try Again",
                                            null,
                                            false

                                    );

                                    dialogFragment.show(getSupportFragmentManager(),null);

                                    break;
                                case C.net.resetPassword.error.EMPTY_PARAMS:
                                    Snackbar.make(tvBtnSendLink, "! App ran into trouble.", Snackbar.LENGTH_LONG).show();
                                    break;
                                default:
                                    Snackbar.make(tvBtnSendLink, "! Server ran into trouble.", Snackbar.LENGTH_LONG).show();
                                    break;
                            }
                            Log.w(LOG_TAG, C.net.resetPassword.TAG + C.net.tag.ERROR + "Error Response code : " + response.code() + " Response Body:\n" +
                                    new GsonBuilder()
                                            .setPrettyPrinting()
                                            .create()
                                            .toJson(errorMessage));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResMessage> call, Throwable t) {
                        Log.e(LOG_TAG, C.net.resetPassword.TAG + C.net.tag.FAILED + "Failed API Call : ", t);

                        hideProgressDialog();
                    }
                });
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

    private void showErrorDialog(String dialogMessage) {

        ErrorDialogFragment errorDialog = ErrorDialogFragment.newInstance("Sign Up", dialogMessage);
        FragmentManager fragmentManager = getSupportFragmentManager();
        errorDialog.show(fragmentManager, DIALOG_ERROR);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        VolleySingleton.getInstance().getRequestQueue().cancelAll(this);
    }

    public static Intent getIntent(Context applicationContext) {
        return new Intent(applicationContext, ForgetPasswordActivity.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(LoginPASActivity.getIntent(getApplicationContext()));
    }

    @Override
    public void onErrorDialogResponse() {
        Intent intent = LoginPASActivity.getIntent(this.getApplicationContext());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onJoinOk() {

    }

    @Override
    public void onJoinCancel() {

    }
}
