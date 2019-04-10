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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChangePasswordActivity extends AppCompatActivity implements ErrorDialogFragment.Callbacks {

    private static final String LOG_TAG = "ChangePasswordActivity";
    private static final int REQ_FORGOT_PASSWORD = 1004;
    private static final String DIAG_FORGOT_PASSWORD = "ChangePasswordActivity.Dialog.DIAG_FORGOT_PASSWORD";
    private static final int REQ_TRY_AGAIN = 1005;
    private static final String DIAG_TRY_AGAIN = "ChangePasswordActivity.Dialog.DIAG_TRY_AGAIN";
    private static final String DIALOG_PROGRESS = "ChangePasswordActivity.Dialog.Progress";
    private static final String DIALOG_ALERT = "ChangePasswordActivity.Dialog.Alert";
    private static final String DIALOG_ERROR = "ChangePasswordActivity.Dialog.Error";

    private TextView tvHeader;
    private TextInputLayout tilOldPassword,tilNewPassword,tilConfirmPassword;
    private EditText etOldPassword,etNewPassword,etConfirmPassword;
    private TextView tvLabelHint;
    private TextView tvBtnSendLink;
    private ProgressDialogFragment progressDialog;
    private AlertDialogFragment mAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        findViews();

        setViews();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //finish();
    }


    private void findViews() {
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tilOldPassword = (TextInputLayout)findViewById(R.id.tilOldPassword);
        tilNewPassword= (TextInputLayout)findViewById(R.id.tilNewPassword);
        tilConfirmPassword= (TextInputLayout)findViewById(R.id.tilConfirmPassword);
        etOldPassword = (EditText) findViewById(R.id.etOldPassword);
        etNewPassword= (EditText) findViewById(R.id.etNewPassword);
        etConfirmPassword= (EditText) findViewById(R.id.etConfirmPassword);
        tvLabelHint = (TextView) findViewById(R.id.tvLabelHint);
        tvBtnSendLink = (TextView)findViewById(R.id.tvBtnSendLink);
    }


    private void setViews() {


        etOldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    tilOldPassword.setError(null);
                }
            }
        });
        etNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    tilNewPassword.setError(null);
                }
            }
        });
        etConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    tilConfirmPassword.setError(null);
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

                boolean valid = validatePassword() &&
                        validateNewPassword() &&
                        validateConfirmPassword();

                if (valid) {

                    resetPassword(etOldPassword.getText().toString().trim(),etConfirmPassword.getText().toString());
                }
            }
        });
    }

    
    private boolean validatePassword() {
        String text = etOldPassword.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            tilOldPassword.setError("! Empty Password");
            return false;
        } else {
            tilOldPassword.setError(null);
            return true;
        }
    }
    private boolean validateNewPassword() {
        String text = etNewPassword.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            tilNewPassword.setError("! Empty Password");
            return false;
        } else if(text.length()<6)
        {
            tilNewPassword.setError("! Minimum six characters");
            return false;
        }
        else {
            tilNewPassword.setError(null);
            return true;
        }
    }
    private boolean validateConfirmPassword() {
        String textOld = etConfirmPassword.getText().toString().trim();
        String textNew = etNewPassword.getText().toString().trim();
        if (TextUtils.isEmpty(textOld)) {
            tilConfirmPassword.setError("! Empty Password");
            return false;
        } else if(!textNew.equals(textOld))
        {
            tilConfirmPassword.setError("! Password not matched");
            return false;
        }
        else {
            tilConfirmPassword.setError(null);
            return true;
        }
    }

    private void resetPassword(String oldPassword, String newPassword) {
        /*
        *
        * Here username is Email Id or Username
        *
        * */

        showProgressDialog();

        CallGenerator.changePassword(oldPassword,newPassword)
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

                                    showErrorDialog("Password Changed Successfully \n Click OK to Login");
                                    mAlertDialog.show(getSupportFragmentManager(), DIALOG_ALERT);
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
                                case C.net.changePassword.error.INTERNAL_SERVER_ERROR:
                                    Snackbar.make(tvBtnSendLink, "! Server ran into trouble.", Snackbar.LENGTH_LONG).show();
                                    break;
                                case C.net.changePassword.error.INVALID_PASSWORD_FORMAT:
                                    tilOldPassword.setError("! Invalid Password");
                                    break;
                                case C.net.changePassword.error.NOT_AUTHORISED:
                                    tilOldPassword.setError("! Incorrect Password Id");
                                    break;
                                case C.net.changePassword.error.EMPTY_PARAMS:
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

    private void showErrorDialog(String dialogMessage) {

        //showProgressBar(false);

        ErrorDialogFragment errorDialog = ErrorDialogFragment.newInstance("Password Changed", dialogMessage);
        FragmentManager fragmentManager = getSupportFragmentManager();
        errorDialog.show(fragmentManager, DIALOG_ERROR);
    }

    private void showProgressBar(boolean show) {
        if (show) {
            //mProgressBar.setVisibility(View.VISIBLE);
        } else {
           // mProgressBar.setVisibility(View.GONE);
        }
    }

    private void hideProgressDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            //
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
       // VolleySingleton.getInstance().getRequestQueue().cancelAll(this);
    }

    public static Intent getIntent(Context applicationContext) {
        return new Intent(applicationContext, ChangePasswordActivity.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onErrorDialogResponse() {
    /*    Intent intent = LoginSMGActivity.getIntent(this.getApplicationContext());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();*/
    }
}
