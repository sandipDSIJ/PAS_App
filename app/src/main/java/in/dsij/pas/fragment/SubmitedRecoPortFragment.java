package in.dsij.pas.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import in.dsij.pas.MyApplication;
import in.dsij.pas.R;
import in.dsij.pas.constants.C;
import in.dsij.pas.database.DbLogs;
import in.dsij.pas.database.DbRecoAllPortfolio;
import in.dsij.pas.database.DbSoldHistory;
import in.dsij.pas.database.DbSoldScripDetails;
import in.dsij.pas.database.DbSubmitedPortFolioScrip;
import in.dsij.pas.dialog.AlertDialogFragment;
import in.dsij.pas.dialog.ErrorDialogFragment;
import in.dsij.pas.dialog.ProgressDialogFragment;
import in.dsij.pas.net.respose.ResMessage;
import in.dsij.pas.net.respose.ResSubmitedPortFolioScrip;
import in.dsij.pas.net.retrofit.CallGenerator;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmitedRecoPortFragment extends Fragment {

    private static final String LOG_TAG = "RecoPortfolioAllFrag";
    private static final String DIALOG_PROGRESS = "RecoPortfolioAllFragment.Dialog.Progress";
    private static final String DIALOG_ERROR = "RecoPortfolioAllFragment.Dialog.Error";
    private static final String DIALOG_alert = "RecoPortfolioAllFragment.Dialog.Alert";
    private Callbacks mCallbacks;
    private Realm realm;
    //private LinearLayout llBottomTotal;

    private RecyclerView mRecyclerView;
    private FrameLayout flEmptyView;
    private TextView tvError;
    private Button btnAddCash, btnAddOrWithdraw;
    private RadioGroup radioGroup;
    private EditText etAddCash;
    private RadioButton radioAdd, radioWithdraw;
    private LinearLayout llAddCash;
    private String addCash;
    private int isaddcash;
    private ProgressDialogFragment progressDialog;
    private RealmResults<DbSubmitedPortFolioScrip> mDbSubmitedPortFolioScrips;

    public static SubmitedRecoPortFragment newInstance() {

        Bundle args = new Bundle();

        SubmitedRecoPortFragment fragment = new SubmitedRecoPortFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SubmitedRecoPortFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_submited_port, container, false);
        findViews(rootView);
        setViews();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        setUpRecyclerView();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GetSubmitedPortFolioScrip();
    }

    private void GetSubmitedPortFolioScrip() {

        showEmptyView(true, "Loading...");

        CallGenerator.getSubmitedPortFolioScrip().enqueue(new Callback<ResSubmitedPortFolioScrip>() {
            @Override
            public void onResponse(Call<ResSubmitedPortFolioScrip> call, Response<ResSubmitedPortFolioScrip> response) {
                if (response.isSuccessful()) {
                    try {
                        ResSubmitedPortFolioScrip resSubmitedPortFolioScrip = response.body();
                        Log.v(LOG_TAG, C.net.GetSubmitedPortFolioScrip.TAG +
                                C.net.tag.RESPONSE +
                                new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(resSubmitedPortFolioScrip));

                        if (resSubmitedPortFolioScrip == null) {
                            Log.w(LOG_TAG, C.net.GetSubmitedPortFolioScrip.TAG +
                                    C.net.tag.RESPONSE + "Received NULL Product List");
                        } else {
                            insertIntoTable(resSubmitedPortFolioScrip);
                        }

                    } catch (Exception e) {
                        Log.w(LOG_TAG, C.net.GetSubmitedPortFolioScrip.TAG + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
                    }
                } else if (response.code() == 413) {
                    Snackbar.make(btnAddCash, "Already signed into other Device", Snackbar.LENGTH_LONG).show();
                    mCallbacks.onTokanChange();
                } else if (response.code() == 403 || response.code() == 404 || response.code() == 500 || response.code() == 503) {
                    showHoldingDetailsView(true, "Sorry, the server is currently\n unavailable please try agein later");
                    return;
                } else {
                    ResMessage errorMessage = null;
                    try {
                        String errorRes = response.errorBody().string();
                        errorMessage = new GsonBuilder().create().fromJson(errorRes, ResMessage.class);
                        if (errorMessage.getCode() == 417) {
                            //clearDb();
                            showHoldingDetailsView(true, errorMessage.getMessage());
                        } else
                            Snackbar.make(btnAddCash, errorMessage.getMessage(), Snackbar.LENGTH_LONG).show();

                    } catch (IOException e) {
                        //showEmptyView(true, "Not available at the moment");
                        Snackbar.make(btnAddCash, C.net.tag.ERROR_TIMEOUT_MSG, Snackbar.LENGTH_LONG).show();
                    } catch (Exception e) {
                        //showEmptyView(true, "Not available at the moment");
                        Snackbar.make(btnAddCash, C.net.tag.ERROR_TIMEOUT_MSG, Snackbar.LENGTH_LONG).show();
                    }

                    switch (response.code()) {
                        default:
                            break;
                    }

                    Log.w(LOG_TAG, C.net.GetSubmitedPortFolioScrip.TAG + C.net.tag.ERROR + "Error Response code : " + response.code() + " Response Body:\n" +
                            new GsonBuilder()
                                    .setPrettyPrinting()
                                    .create()
                                    .toJson(errorMessage));

                }
            }

            @Override
            public void onFailure(Call<ResSubmitedPortFolioScrip> call, Throwable t) {
                Log.e(LOG_TAG, C.net.GetSubmitedPortFolioScrip.TAG + C.net.tag.FAILED + "Failed API Call : ", t);

                if (!MyApplication.isConnected()) {
                    // llBottomTotal.setVisibility(View.GONE);
                    showEmptyView(true, C.net.tag.ERROR_CHECK_INTERNET);
                } else {
                    //Snackbar.make(llBottomTotal, C.net.tag.ERROR_TIMEOUT_MSG, Snackbar.LENGTH_LONG).show();
                    showEmptyView(true, C.net.tag.ERROR_TIMEOUT_MSG);
                }
            }
        });
    }

    private void clearDb() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                realm.where(DbSoldScripDetails.class).findAll().deleteAllFromRealm();
                realm.where(DbLogs.class).findAll().deleteAllFromRealm();
                realm.where(DbSoldHistory.class).findAll().deleteAllFromRealm();
            }
        });
    }

    private void insertIntoTable(final ResSubmitedPortFolioScrip resSubmitedPortFolioScrip) {

        final List<ResSubmitedPortFolioScrip.SubmitedPortFolioScrip> resSubmitedPortFolioScrips = resSubmitedPortFolioScrip.getlsSoldScripEntity();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + "Products");
                realm.where(DbSubmitedPortFolioScrip.class).findAll().deleteAllFromRealm();
                //realm.where(DbLogs.class).findAll().deleteAllFromRealm();
                for (int i = 0; i < resSubmitedPortFolioScrips.size(); i++) {
                    DbSubmitedPortFolioScrip dbSubmitedPortFolioScrip = new DbSubmitedPortFolioScrip(resSubmitedPortFolioScrips.get(i));
                    realm.copyToRealmOrUpdate(dbSubmitedPortFolioScrip);
                }
                try {
//                    realm.where(DbProduct.class).findAll().deleteAllFromRealm();
                } catch (Exception e) {
                    Log.v(LOG_TAG, C.net.tag.DB_TRANSACTION_WRITE + "Already no Product present ", e);
                } finally {
                    //realm.createOrUpdateAllFromJson(DbHolding.class, new Gson().toJson(resGetHolding.getlsHoldingEntity()));
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + C.net.tag.DB_SUCCESS);
                setUpRecyclerView();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + C.net.tag.DB_FAIL,
                        error);
            }
        });
    }

    private void showHoldingDetailsView(boolean show, @Nullable String message) {
        if (show) {
            flEmptyView.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

            if (TextUtils.isEmpty(message)) {
                tvError.setText("Please wait...");
            } else {
                tvError.setGravity(Gravity.LEFT);
                tvError.setText(message);
            }
        } else {
            flEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.GONE);
        }
    }

    private void showEmptyView(boolean show, @Nullable String message) {
        if (show) {
            flEmptyView.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

            if (TextUtils.isEmpty(message)) {
                tvError.setText("Please wait...");
            } else {
                tvError.setText(message);
            }
        } else {
            flEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.GONE);
        }
    }

    private void setViews() {

        btnAddCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llAddCash.isShown())
                    llAddCash.setVisibility(View.GONE);
                else
                    llAddCash.setVisibility(View.VISIBLE);
            }
        });

        btnAddOrWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                addOrWithdrawCash();
            }
        });
    }

    private boolean validate() {
        addCash =etAddCash.getText().toString();
        if(addCash.isEmpty()|| addCash.length()<1)
        {
            etAddCash.setError("Add correct cash");
            etAddCash.requestFocus();
            return false;
        }
        else if(radioAdd.isChecked())
            isaddcash=1;
        else isaddcash=0;
        return true;
    }

    private void addOrWithdrawCash() {
        CallGenerator.addORWithdrawCash(addCash,isaddcash).enqueue(new Callback<ResMessage>() {
            @Override
            public void onResponse(Call<ResMessage> call, Response<ResMessage> response) {
                if (response.isSuccessful()) {
                    try {
                        ResMessage resMessage = response.body();

                        Log.v(LOG_TAG, C.net.SubmitRiskQuestionAnswer.TAG +
                                C.net.tag.RESPONSE +
                                new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(resMessage));

                        if (resMessage == null) {
                            Log.w(LOG_TAG, C.net.SubmitRiskQuestionAnswer.TAG +
                                    C.net.tag.RESPONSE + "Received NULL Response for SubmitRiskQuestionAnswer");
                        } else {
                            hideProgressDialog();
                            String strMsg = resMessage.getMessage();
                            Snackbar.make(btnAddCash, strMsg, Snackbar.LENGTH_LONG).show();
                            llAddCash.setVisibility(View.GONE);
                            GetSubmitedPortFolioScrip();
                        }

                    } catch (Exception e) {
                        Log.w(LOG_TAG, C.net.SubmitRiskQuestionAnswer.TAG + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
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
                        case C.net.SubmitRiskQuestionAnswer.error.INTERNAL_SERVER_ERROR:
                            Snackbar.make(btnAddCash, "! Server ran into trouble.", Snackbar.LENGTH_LONG).show();
                            break;
                        case C.net.SubmitRiskQuestionAnswer.error.SERVER_ERROR:
                            Snackbar.make(btnAddCash, "! Something went wrong !", Snackbar.LENGTH_LONG).show();
                            break;
                        case C.net.SubmitRiskQuestionAnswer.error.EMPTY_PARAMS:
                            Snackbar.make(btnAddCash, "! App ran into trouble.", Snackbar.LENGTH_LONG).show();
                            break;
                        default:
                            Snackbar.make(btnAddCash, "! Server ran into trouble.", Snackbar.LENGTH_LONG).show();
                            break;
                    }
                    Log.w(LOG_TAG, C.net.SubmitRiskQuestionAnswer.TAG + C.net.tag.ERROR + "Error Response code : " + response.code() + " Response Body:\n" +
                            new GsonBuilder()
                                    .setPrettyPrinting()
                                    .create()
                                    .toJson(errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ResMessage> call, Throwable t) {

            }
        });
    }

    private void findViews(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvSubmitedProtfolio);
        flEmptyView = (FrameLayout) rootView.findViewById(R.id.flEmptyView);
        tvError = (TextView) rootView.findViewById(R.id.tvError);
        btnAddCash = (Button) rootView.findViewById(R.id.btnAddCash);
        btnAddOrWithdraw = (Button) rootView.findViewById(R.id.btnAddOrWithdraw);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        radioAdd = (RadioButton) rootView.findViewById(R.id.radioAdd);
        radioWithdraw = (RadioButton) rootView.findViewById(R.id.radioWithdraw);
        llAddCash = (LinearLayout) rootView.findViewById(R.id.llAddCash);
        etAddCash=(EditText)rootView.findViewById(R.id.etAddCash);
    }

    private void setUpRecyclerView() {

        mDbSubmitedPortFolioScrips = realm.where(DbSubmitedPortFolioScrip.class)
                .findAll();

        if (mDbSubmitedPortFolioScrips.isEmpty()) {
            // showEmptyView(true, "Not Available\n");
            // llBottomTotal.setVisibility(View.GONE);
        } else {
            //llBottomTotal.setVisibility(View.VISIBLE);
            showEmptyView(false, null);
        }

        try {
            mRecyclerView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {

            CallAdapter adapter = new CallAdapter();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(adapter);

            mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                        outRect.bottom = getResources().getDimensionPixelSize(R.dimen.gap_feed_items);
                    } else {
                        outRect.bottom = getResources().getDimensionPixelSize(R.dimen.default_gap);
                    }
                }
            });
        }
    }

    private class CallHolder extends RecyclerView.ViewHolder {

        private TextView tvCompanyName, tvQuantity, tvMore, tvStartPrice, tvAmtInv, tvWt, tvIndustry,txtStartPrice,txtQuantity;
        private LinearLayout layoutHolder;
        private DbSubmitedPortFolioScrip dbSubmitedPortFolioScrip;
        private RealmList<DbLogs> dbLogs;
        private RealmResults<DbLogs> DbLogsResult;

        public CallHolder(View itemView) {
            super(itemView);

            tvCompanyName = (TextView) itemView.findViewById(R.id.tvCompanyName);
            tvQuantity = (TextView) itemView.findViewById(R.id.tvQuantity);
            tvStartPrice = (TextView) itemView.findViewById(R.id.tvSPrice);
            tvWt = (TextView) itemView.findViewById(R.id.tvWt);
            tvAmtInv = (TextView) itemView.findViewById(R.id.tvAmtInv);
            tvMore = (TextView) itemView.findViewById(R.id.tvMore);
            tvIndustry = (TextView) itemView.findViewById(R.id.tvIndustry);
            txtStartPrice= (TextView) itemView.findViewById(R.id.txtQuantity);
            txtQuantity= (TextView) itemView.findViewById(R.id.txtStartPrice);
            layoutHolder = (LinearLayout) itemView.findViewById(R.id.llActivityLog);
            tvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!layoutHolder.isShown())
                        layoutHolder.setVisibility(View.VISIBLE);
                    else
                        layoutHolder.setVisibility(View.GONE);
                }
            });
        }

        public void bindFeed(DbSubmitedPortFolioScrip dbSubmitedPortFolioScrip) {
            this.dbSubmitedPortFolioScrip = dbSubmitedPortFolioScrip;
            if(this.dbSubmitedPortFolioScrip.getTicker().equals("Cash"))
            {
                tvCompanyName.setText(this.dbSubmitedPortFolioScrip.getTicker());
                txtStartPrice.setVisibility(View.GONE);
                txtQuantity.setVisibility(View.GONE);
                tvQuantity.setVisibility(View.GONE);
                tvIndustry.setVisibility(View.GONE);
                tvStartPrice.setVisibility(View.GONE);
            } else
            {
                tvCompanyName.setText(this.dbSubmitedPortFolioScrip.getCompanyName());
                tvQuantity.setText(this.dbSubmitedPortFolioScrip.getQuantity() + "");
                tvAmtInv.setText(this.dbSubmitedPortFolioScrip.getAmtInvested());
                tvStartPrice.setText("0");
                tvIndustry.setText(this.dbSubmitedPortFolioScrip.getIndustry());
            }
            tvAmtInv.setText(this.dbSubmitedPortFolioScrip.getAmtInvested());
            tvWt.setText(this.dbSubmitedPortFolioScrip.getAbsoluteReturn());

            RealmList<DbLogs> mLogs;
            long id = this.dbSubmitedPortFolioScrip.getOpeningPortfolioId();
            try {
                mLogs = realm.where(DbSubmitedPortFolioScrip.class).equalTo("openingPortfolioId", id).findFirst().getLogs();
                showVerticalLayout(layoutHolder, mLogs);
            } catch (RealmException e) {
                Log.e(LOG_TAG, e.toString());
            }
        }
    }

    private void showVerticalLayout(LinearLayout layoutHolder, final RealmList<DbLogs> mDbLogs) {
        layoutHolder.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 0, 0);

        for (int i = 0; i < mDbLogs.size(); i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView button = new TextView(getContext());
            TextView button1 = new TextView(getContext());//Creating Button
            TextView btnReason = new TextView(getContext());//Creating Button

            button.setId(i);//Setting Id for using in future
            button1.setId(i);
            btnReason.setId(i);
            btnReason.setText(i+"  Reason");
            String logDate = "", reason = "", comments = "";
             final long logId=mDbLogs.get(i).getLogId();
            try {
                logDate = mDbLogs.get(i).getLogdate();
                reason = mDbLogs.get(i).getReason();
                comments = mDbLogs.get(i).getComment();

            } catch (RealmException ex) {

            } catch (Exception ex) {
            }
            if (!logDate.isEmpty() || !logDate.equals("")) {
                button.setText("Log Date : " + logDate);
                button1.setText("comments : " + comments);
            }
            button.setPadding(5, 10, 5, 0);
            button.setLayoutParams(params);
            button.setTextColor(Color.parseColor("#464646"));
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            button.setTypeface(null, Typeface.BOLD);
            button.setGravity(Gravity.LEFT);
            layoutHolder.addView(button);

            button1.setPadding(5, 5, 5, 0);
            button1.setLayoutParams(params);
            button1.setTextColor(Color.parseColor("#464646"));
            button1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            button1.setGravity(Gravity.LEFT);

            layoutHolder.setOrientation(LinearLayout.VERTICAL);
            layoutHolder.addView(button1);
             final String strReason=mDbLogs.get(i).getReason();
            if(true)
            {
                btnReason.setPadding(5, 5, 5, 10);
                btnReason.setLayoutParams(params);
                btnReason.setTextColor(Color.parseColor("#30B55A"));
                btnReason.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                btnReason.setGravity(Gravity.LEFT);

                layoutHolder.setOrientation(LinearLayout.VERTICAL);
                layoutHolder.addView(btnReason);

            }

            btnReason.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),"Position"+logId ,Toast.LENGTH_LONG).show();
                    AlertDialogFragment alertDialogFragment=AlertDialogFragment.newInstance("Reason",strReason);
                    FragmentManager fragmentManager = getFragmentManager();
                    alertDialogFragment.show(fragmentManager, DIALOG_alert);
                }
            });
        }
    }

    private void showProgressDialog() {
        progressDialog = ProgressDialogFragment.newInstance(null, "Wait...\n" + "");
        progressDialog.setCancelable(false);
        progressDialog.show(getFragmentManager(), DIALOG_PROGRESS);
    }

    private void hideProgressDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            //
        }
    }

    private void showErrorDialog(String dialogMessage) {

        ErrorDialogFragment errorDialog = ErrorDialogFragment.newInstance("Sold Scrip", dialogMessage);
        FragmentManager fragmentManager = getFragmentManager();
        errorDialog.show(fragmentManager, DIALOG_ERROR);
    }


    private class CallAdapter extends RecyclerView.Adapter<CallHolder> {
        @Override
        public CallHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            return new CallHolder(inflater.inflate(R.layout.item_submited_portfolio_scrip, parent, false));
        }

        @Override
        public void onBindViewHolder(CallHolder holder, int position) {
            holder.bindFeed(mDbSubmitedPortFolioScrips.get(position));
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull CallHolder holder) {
            super.onViewDetachedFromWindow(holder);
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public int getItemCount() {
            return mDbSubmitedPortFolioScrips.size();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        realm.close();
    }

    public void confirmDialog(final long orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // callCanclePendingOrder(orderId);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public interface Callbacks {

        void onTokanChange();
    }
}
