package in.dsij.pas.dialog;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import in.dsij.pas.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RatingBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public static final int RESULT_CANCELED = 903;
    public static final int RESULT_PRIMARY = 901;
    public static final int RESULT_SECONDARY = 902;

    private static final String ARG_MESSAGE = "AlertBottomSheetDialogFragment.Arg.Message";
    private static final String ARG_PRIMARY_BUTTON_TEXT = "AlertBottomSheetDialogFragment.Arg.PrimaryButtonText";
    private static final String ARG_SECONDARY_BUTTON_TEXT = "AlertBottomSheetDialogFragment.Arg.SecondaryButtonText";
    private static final String ARG_DISABLE_CLOSE = "AlertBottomSheetDialogFragment.Arg.DisableClose";
    private static final String TXT_DONE = "Done";
    public static final String TXT_PLAY_STORE = "Continue";
    private static final String LOG_TAG = "RatingDialogFragment";

    private int resultCode = RESULT_CANCELED;

    private Callbacks mCallbacks;

    private ImageView ivClose;
    private RatingBar ratingBar;
    private TextView tvLabelMessage;
    private TextInputLayout tilComments;
    private EditText etComments;
    private LinearLayout llActionButtons;
    private TextView tvBtnSecondary;
    private TextView tvBtnPrimary;


    public static RatingBottomSheetDialogFragment newInstance() {

        Bundle args = new Bundle();

        RatingBottomSheetDialogFragment fragment = new RatingBottomSheetDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public RatingBottomSheetDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public void onStart() {
        super.onStart();

        setViews();
    }

    private void setViews() {
        tvBtnSecondary.setVisibility(View.GONE);

        /*ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (v < 1) {
                    ratingBar.setRating(1);
                } else {
                    ratingBar.setRating(v);
                }
            }
        });*/
        ratingBar.setIsIndicator(false);
        tvBtnPrimary.setText(TXT_PLAY_STORE);
        tvBtnPrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingBar.setIsIndicator(true);
                if (ratingBar.getRating() >= 4f && TXT_PLAY_STORE.equalsIgnoreCase(tvBtnPrimary.getText().toString())) {
                    String appPackageName = getContext().getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    dismissDialog(RESULT_PRIMARY);
                } else if (ratingBar.getRating() < 4 && TXT_DONE.equalsIgnoreCase(tvBtnPrimary.getText().toString())) {
                    int rating = Math.round(ratingBar.getRating());
                    if (rating < 1) {
                        rating = 1;
                    }
                    mCallbacks.postLowRating(rating, etComments.getText().toString());
                    dismissDialog(RESULT_SECONDARY);
                } else {
                    tvLabelMessage.setVisibility(View.GONE);
                    tilComments.setVisibility(View.VISIBLE);
                    tvBtnPrimary.setText(TXT_DONE);
                }
            }
        });


        tilComments.setVisibility(View.GONE);
        tvLabelMessage.setVisibility(View.VISIBLE);

        ivClose.setVisibility(View.VISIBLE);
        setCancelable(true);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog(RESULT_CANCELED);
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rating_bottom_sheet_dialog, container, false);

        findViews(rootView);

        return rootView;
    }

    private void findViews(View rootView) {
        ivClose = (ImageView) rootView.findViewById(R.id.ivClose);
        ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
        tvLabelMessage = (TextView) rootView.findViewById(R.id.tvLabelMessage);
        tilComments = (TextInputLayout) rootView.findViewById(R.id.tilComments);
        etComments = (EditText) rootView.findViewById(R.id.etComments);
        llActionButtons = (LinearLayout) rootView.findViewById(R.id.llActionButtons);
        tvBtnSecondary = (TextView) rootView.findViewById(R.id.tvBtnSecondary);
        tvBtnPrimary = (TextView) rootView.findViewById(R.id.tvBtnPrimary);
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

    public interface Callbacks {
        void postLowRating(int rating, String comments);
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
}
