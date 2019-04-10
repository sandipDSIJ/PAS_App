package in.dsij.pas.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import in.dsij.pas.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "ReportDialogFragment.Arg.Title";
    private static final String ARG_MSG = "ReportDialogFragment.Arg.Msg";
    private static final String ARG_CALLBACKS = "ReportDialogFragment.Arg.Callbacks";
    private static final String ARG_EMAIL = "ReportDialogFragment.Arg.Callbacks";

    private String title;
    private String label;
    private String email;

    View rootView;
    private TextView tvTitle;
    private TextView tvLabel;
    private AutoCompleteTextView etEmail;
    private AutoCompleteTextView etPhone;
    private AutoCompleteTextView etDescription;
    private TextView tvBtnCancel;
    private TextView tvBtnOk;

    private Callbacks mCallbacks;

    public static ReportDialogFragment newInstance(@Nullable String title, @Nullable String label, @Nullable String email) {

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MSG, label);
        args.putString(ARG_EMAIL,email);
        ReportDialogFragment fragment = new ReportDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ReportDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        title = args.getString(ARG_TITLE);
        label = args.getString(ARG_MSG);
        email=args.getString(ARG_EMAIL);
        this.setCancelable(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_report_dialog, null);

        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvLabel = (TextView) rootView.findViewById(R.id.tvLabel);
        etEmail = (AutoCompleteTextView) rootView.findViewById(R.id.etEmail);
        etPhone = (AutoCompleteTextView) rootView.findViewById(R.id.etPhone);
        etDescription = (AutoCompleteTextView) rootView.findViewById(R.id.etDescription);
        tvBtnCancel = (TextView) rootView.findViewById(R.id.tvBtnCancel);
        tvBtnOk = (TextView) rootView.findViewById(R.id.tvBtnOk);

        tvBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (!TextUtils.isEmpty(email)){
            etEmail.setText(email);
        }

        etPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        tvBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String phone = "";
                if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.setError("Invalid Email");
                    return;
                } else if (TextUtils.isEmpty(description)) {
                    etDescription.setError("Add Description");
                    return;
                } else {

                    String tempPhone = etPhone.getText().toString().replaceAll("\\+", "").replaceAll(" ", "").replaceAll("-", "").trim();
                    if (!TextUtils.isEmpty(tempPhone) && tempPhone.length() > 10) {
                        char[] phoneChars = new char[10];
                        int j = 9;
                        for (int i = tempPhone.length() - 1; j >= 0; i--) {
                            phoneChars[j] = tempPhone.charAt(i);
                            j--;
                        }
                        tempPhone = new String(phoneChars);
                    }

                    if (TextUtils.isEmpty(tempPhone)) {
                        phone = tempPhone;
                    } else if (!android.util.Patterns.PHONE.matcher(tempPhone).matches() || tempPhone.length() < 10) {
                        etPhone.setError("Invalid");
                        return;
                    } else {
                        phone = tempPhone;
//                    PhoneNumberUtils.
                    }

                    mCallbacks.submitReport(
                            email,
                            phone,
                            description
                    );
                    dismiss();
                }
            }
        });

        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }

        if (TextUtils.isEmpty(label)) {
            tvLabel.setVisibility(View.GONE);
        } else {
            tvLabel.setText(label);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .create();
    }

    public interface Callbacks {
        void submitReport(String email, String phone, String description);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
