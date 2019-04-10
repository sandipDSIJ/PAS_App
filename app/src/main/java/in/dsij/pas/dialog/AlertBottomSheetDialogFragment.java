package in.dsij.pas.dialog;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.dsij.pas.R;

public class AlertBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public static final int RESULT_CANCELED = 903;
    public static final int RESULT_PRIMARY = 901;
    public static final int RESULT_SECONDARY = 902;

    private static final String ARG_MESSAGE = "AlertBottomSheetDialogFragment.Arg.Message";
    private static final String ARG_PRIMARY_BUTTON_TEXT = "AlertBottomSheetDialogFragment.Arg.PrimaryButtonText";
    private static final String ARG_SECONDARY_BUTTON_TEXT = "AlertBottomSheetDialogFragment.Arg.SecondaryButtonText";
    private static final String ARG_DISABLE_CLOSE = "AlertBottomSheetDialogFragment.Arg.DisableClose";


    private int resultCode = RESULT_CANCELED;

    private ImageView ivClose;
    private TextView tvMessage;
    private TextView tvBtnSecondary;
    private TextView tvBtnPrimary;
    private LinearLayout llActionButtons;

    private String message;
    private String primaryButtonText;
    private String secondaryButtonText;
    private boolean disableClose;
    private Callbacks mCallbacks;

    public static AlertBottomSheetDialogFragment newInstance(@NonNull String message,
                                                             @Nullable String primaryButtonText,
                                                             @Nullable String secondaryButtonText,
                                                             boolean disableClose) {

        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_PRIMARY_BUTTON_TEXT, primaryButtonText);
        args.putString(ARG_SECONDARY_BUTTON_TEXT, secondaryButtonText);
        args.putBoolean(ARG_DISABLE_CLOSE, disableClose);
        AlertBottomSheetDialogFragment fragment = new AlertBottomSheetDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static AlertBottomSheetDialogFragment newInstance(@NonNull String message,
                                                             @Nullable String primaryButtonText,
                                                             @Nullable String secondaryButtonText) {
        return AlertBottomSheetDialogFragment.newInstance(message, primaryButtonText, secondaryButtonText, false);
    }

    public AlertBottomSheetDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle args = getArguments();
        message = args.getString(ARG_MESSAGE);
        primaryButtonText = args.getString(ARG_PRIMARY_BUTTON_TEXT);
        secondaryButtonText = args.getString(ARG_SECONDARY_BUTTON_TEXT);
        disableClose = args.getBoolean(ARG_DISABLE_CLOSE);
    }

    @Override
    public void onStart() {
        super.onStart();

        setViews();
    }

    private void setViews() {

        tvMessage.setText(message);

        if (TextUtils.isEmpty(primaryButtonText) && TextUtils.isEmpty(secondaryButtonText)) {
            llActionButtons.setVisibility(View.GONE);
        } else {
            if (!TextUtils.isEmpty(primaryButtonText)) {
                tvBtnPrimary.setText(primaryButtonText);
                tvBtnPrimary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallbacks.onJoinOk();
                        dismissDialog(RESULT_PRIMARY);
                    }
                });
            }

            if (!TextUtils.isEmpty(secondaryButtonText)) {
                tvBtnSecondary.setText(secondaryButtonText);
                tvBtnSecondary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismissDialog(RESULT_SECONDARY);
                    }
                });
            }
        }

        if (disableClose) {
            ivClose.setVisibility(View.GONE);
            setCancelable(false);
        } else {
            ivClose.setVisibility(View.VISIBLE);
            setCancelable(true);
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismissDialog(RESULT_CANCELED);
                    mCallbacks.onJoinCancel();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_alert_bottom_sheet_dialog, container, false);

        findViews(rootView);

        return rootView;
    }

    private void findViews(View rootView) {
        ivClose = (ImageView) rootView.findViewById(R.id.ivClose);
        tvMessage = (TextView) rootView.findViewById(R.id.tvMessage);
        tvBtnSecondary = (TextView) rootView.findViewById(R.id.tvBtnSecondary);
        tvBtnPrimary = (TextView) rootView.findViewById(R.id.tvBtnPrimary);
        llActionButtons = (LinearLayout) rootView.findViewById(R.id.llActionButtons);
    }

    public void dismissDialog(int resultCode) {
        this.resultCode = resultCode;
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        Intent resultData = new Intent();
        try {
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, resultData);
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public interface Callbacks {
        void onJoinOk();
        void onJoinCancel();
    }
}
