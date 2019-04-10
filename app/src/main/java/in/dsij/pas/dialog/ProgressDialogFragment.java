package in.dsij.pas.dialog;


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

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgressDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "ProgressDialogFragment.Arg.Title";
    private static final String ARG_MSG = "ProgressDialogFragment.Arg.Msg";

    private String title;
    private String msg;

    View rootView;
    private TextView tvTitle;
    private TextView tvMsg;

    public static ProgressDialogFragment newInstance(@Nullable String title, @Nullable String msg) {

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MSG, msg);
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ProgressDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title = getArguments().getString(ARG_TITLE);
        msg = getArguments().getString(ARG_MSG);

        this.setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_progress_dialog, null);

        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvMsg = (TextView) rootView.findViewById(R.id.tvMsg);

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


}
