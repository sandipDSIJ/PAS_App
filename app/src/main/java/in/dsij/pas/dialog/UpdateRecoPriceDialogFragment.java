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
import android.widget.EditText;
import android.widget.TextView;

import in.dsij.pas.R;

public class UpdateRecoPriceDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "ReportDialogFragment.Arg.Title";
    private static final String ARG_COMPANY_NAME = "ReportDialogFragment.Arg.companyName";
    private static final String ARG_COMPANY_CODE = "ReportDialogFragment.Arg.CompanyCode";
    private static final String ARG_QUANTITY = "ReportDialogFragment.Arg.Quantity";
    private static final String ARG_AVG_PRICE = "ReportDialogFragment.Arg.AvgPrice";
    private static final String ARG_OPERATION = "ReportDialogFragment.Arg.Operation";
    private static final String ARG_ORDER_Q_ID = "ReportDialogFragment.Arg.OrderQueId";
    private static final String ARG_CALLBACKS = "ReportDialogFragment.Arg.Callbacks";

    private String title;
    private String companyName,quantity,avgPrice,companyCode,operation;
    private long orderQueId;

    View rootView;
    private TextView tvTitle;
    private TextView tvCompanyName;
    private EditText etQuantity,etAvgPrice;
    private TextView tvBtnCancel;
    private TextView tvBtnUpdateOk;

    private Callbacks mCallbacks;

    public static UpdateRecoPriceDialogFragment newInstance(@Nullable String title, @Nullable String companyName,@Nullable String companyCode,@Nullable long quantity,@Nullable String avgPrice,@Nullable long orderQueueId,@Nullable String operation) {

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_COMPANY_NAME, companyName);
        args.putString(ARG_COMPANY_CODE, companyCode);
        args.putLong(ARG_QUANTITY, quantity);
        args.putString(ARG_AVG_PRICE, avgPrice);
        args.putString(ARG_OPERATION, operation);
        args.putLong(ARG_ORDER_Q_ID, orderQueueId);

        UpdateRecoPriceDialogFragment fragment = new UpdateRecoPriceDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public UpdateRecoPriceDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        title = args.getString(ARG_TITLE);
        companyName=args.getString(ARG_COMPANY_NAME);
        companyCode=args.getString(ARG_COMPANY_CODE);
        quantity=args.getLong(ARG_QUANTITY)+"";
        avgPrice=args.getString(ARG_AVG_PRICE);
        orderQueId = args.getLong(ARG_ORDER_Q_ID);
        operation=args.getString(ARG_OPERATION);
        this.setCancelable(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_update_reco_dialog, null);

        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvCompanyName = (TextView) rootView.findViewById(R.id.tvCompanyName);
        etQuantity = (EditText) rootView.findViewById(R.id.etQuantity);
        etAvgPrice = (EditText) rootView.findViewById(R.id.etAvgPrice);
        tvBtnCancel = (TextView) rootView.findViewById(R.id.tvBtnCancel);
        tvBtnUpdateOk = (TextView) rootView.findViewById(R.id.tvBtnUpdateOk);

        tvBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (!TextUtils.isEmpty(companyName)){
            tvCompanyName.setText(companyName);
        }
        if (!TextUtils.isEmpty(quantity)){
            etQuantity.setText(quantity);
        }
        if (!TextUtils.isEmpty(avgPrice)){
            etAvgPrice.setText(avgPrice);
        }

        tvBtnUpdateOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quantity = etQuantity.getText().toString().trim();
                String avgPrice = etAvgPrice.getText().toString().trim();

                if (TextUtils.isEmpty(quantity)) {
                    etQuantity.setError("Invalid Quantity");
                    return;
                } else if (TextUtils.isEmpty(avgPrice)) {
                    etAvgPrice.setError("Add Average price");
                    return;
                } else {
                    mCallbacks.updateRecoPrice(
                            companyName,companyCode,Long.valueOf(quantity),avgPrice,orderQueId,operation
                    );
                    dismiss();
                }
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .create();
    }

    public interface Callbacks {
        void updateRecoPrice(String companyName, String companyCode, long quantity, String avgPrice, long orderQueId, String operation);
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
