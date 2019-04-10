package in.dsij.pas.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import in.dsij.pas.R;


public class ErrorDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "SuccessDialogFragment.Arg.Title";
    private static final String ARG_MSG = "SuccessDialogFragment.Arg.Msg";

    private Callbacks mCallbacks;

    private String title;
    private String msg;

    View rootView;
    private TextView tvTitle;
    private TextView tvMsg;
    private TextView tvBtnOk;

    public static ErrorDialogFragment newInstance(@Nullable String title, @Nullable String msg) {

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MSG, msg);
        ErrorDialogFragment fragment = new ErrorDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ErrorDialogFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        title = args.getString(ARG_TITLE);
        msg = args.getString(ARG_MSG);
        this.setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_error_dialog, null);

        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvMsg = (TextView) rootView.findViewById(R.id.tvMsg);
        tvBtnOk = (TextView) rootView.findViewById(R.id.tvBtnOk);

        tvBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onErrorDialogResponse();
                dismiss();
            }
        });

        if (title == null || title.isEmpty()) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }

        if (msg == null || msg.isEmpty()) {
            tvMsg.setVisibility(View.GONE);
        } else {
            tvMsg.setText(msg);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .create();
    }

    public interface Callbacks {
        void onErrorDialogResponse();
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
