package in.dsij.pas.fragment;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import in.dsij.pas.dialog.ErrorDialogFragment;
import in.dsij.pas.dialog.ProgressDialogFragment;
import in.dsij.pas.net.respose.ResMessage;
import in.dsij.pas.net.respose.ResRecoAllPortfolio;
import in.dsij.pas.net.respose.ResSoldScripDetails;
import in.dsij.pas.net.respose.ResSubmitedPortFolioScrip;
import in.dsij.pas.net.retrofit.CallGenerator;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecoPortfolioAllFragment extends Fragment {

    private static final String LOG_TAG = "RecoPortfolioAllFrag";
    private static final String DIALOG_PROGRESS = "RecoPortfolioAllFragment.Dialog.Progress";
    private static final String DIALOG_ERROR = "RecoPortfolioAllFragment.Dialog.Error";
    private Callbacks mCallbacks;
    private Realm realm;
    private LinearLayout llBottomTotal;

    private RecyclerView mRecyclerView;
    private FrameLayout flEmptyView;
    private TextView tvError;
    private TextView tvCash, tvWt;

    private String available;
    private String percentWeight;

    private TextView tvTotAmtInv, tvTotMktValue, tvTotGain, tvTotAbs;
    private double mTotAmtInv, mTotMktValue, mTotGain, mTotAbs;

    private ProgressDialogFragment progressDialog;

    private RealmResults<DbRecoAllPortfolio> mDbRecoAllPortfolios;

    public static RecoPortfolioAllFragment newInstance() {

        Bundle args = new Bundle();

        RecoPortfolioAllFragment fragment = new RecoPortfolioAllFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public RecoPortfolioAllFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_reco_all_portfolio, container, false);
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

        GetAllRecommendedPortfolioScrip();

    }


    private void GetAllRecommendedPortfolioScrip() {

        showEmptyView(true, "Loading...");

        CallGenerator.getAllRecommendedPortfolioScrip().enqueue(new Callback<ResRecoAllPortfolio>() {
            @Override
            public void onResponse(Call<ResRecoAllPortfolio> call, Response<ResRecoAllPortfolio> response) {
                if (response.isSuccessful()) {
                    try {
                        ResRecoAllPortfolio resRecoAllPortfolio = response.body();
                        Log.v(LOG_TAG, C.net.GetAllRecommendedPortfolioScrip.TAG +
                                C.net.tag.RESPONSE +
                                new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(resRecoAllPortfolio));

                        if (resRecoAllPortfolio == null) {
                            Log.w(LOG_TAG, C.net.GetAllRecommendedPortfolioScrip.TAG +
                                    C.net.tag.RESPONSE + "Received NULL Product List");
                        } else {
                            insertIntoTable(resRecoAllPortfolio);
                        }

                    } catch (Exception e) {
                        Log.w(LOG_TAG, C.net.GetAllRecommendedPortfolioScrip.TAG + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
                    }
                } else if (response.code() == 413) {
                    Snackbar.make(llBottomTotal, "Already signed into other Device", Snackbar.LENGTH_LONG).show();
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
                            Snackbar.make(llBottomTotal, errorMessage.getMessage(), Snackbar.LENGTH_LONG).show();

                    } catch (IOException e) {
                        //showEmptyView(true, "Not available at the moment");
                        // Snackbar.make(llBottomTotal, C.net.tag.ERROR_TIMEOUT_MSG, Snackbar.LENGTH_LONG).show();
                    } catch (Exception e) {
                        //showEmptyView(true, "Not available at the moment");
//                        Snackbar.make(llBottomTotal, C.net.tag.ERROR_TIMEOUT_MSG, Snackbar.LENGTH_LONG).show();
                    }

                    switch (response.code()) {
                        default:
                            break;
                    }

                    Log.w(LOG_TAG, C.net.GetAllRecommendedPortfolioScrip.TAG + C.net.tag.ERROR + "Error Response code : " + response.code() + " Response Body:\n" +
                            new GsonBuilder()
                                    .setPrettyPrinting()
                                    .create()
                                    .toJson(errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ResRecoAllPortfolio> call, Throwable t) {
                Log.e(LOG_TAG, C.net.GetAllRecommendedPortfolioScrip.TAG + C.net.tag.FAILED + "Failed API Call : ", t);

                if (!MyApplication.isConnected()) {
                    llBottomTotal.setVisibility(View.GONE);
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

    private void insertIntoTable(final ResRecoAllPortfolio recoAllPortfolio) {

        final List<ResRecoAllPortfolio.listRecoAllPortfolioEntity> recoAllPortfolioEntities = recoAllPortfolio.getList();
        mTotAmtInv = 0;
        mTotMktValue = 0;
        mTotGain = 0;
        mTotAbs = 0;
        available = recoAllPortfolio.getAvailable();
        percentWeight = recoAllPortfolio.getPercentWeight();
        try {
            tvCash.setText(available);
            tvWt.setText(percentWeight);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + "DbRecoAllPortfolio");
                realm.where(DbRecoAllPortfolio.class).findAll().deleteAllFromRealm();
                realm.where(DbLogs.class).findAll().deleteAllFromRealm();
                for (int i = 0; i < recoAllPortfolioEntities.size(); i++) {

                    DbRecoAllPortfolio dbRecoAllPortfolio = new DbRecoAllPortfolio(recoAllPortfolioEntities.get(i));

                    realm.copyToRealmOrUpdate(dbRecoAllPortfolio);
                }

                try {
                    /*for (int i = 0; i < recoAllPortfolioEntities.size(); i++) {
                        double valueAmtInv = Double.valueOf(recoAllPortfolioEntities.get(i).getAmtInvested());
                        double valueMktValue = Double.valueOf(recoAllPortfolioEntities.get(i).getMarketValue());
                        double valueAbs = Double.valueOf(recoAllPortfolioEntities.get(i).getAbsoluteReturn());
                        double valueGain = Double.valueOf(recoAllPortfolioEntities.get(i).getGain());

                        mTotAmtInv = mTotAmtInv + valueAmtInv;
                        mTotMktValue = mTotMktValue + valueMktValue;
                        mTotGain = mTotGain + valueGain;
                        mTotAbs = mTotAbs + valueAbs;

                    }
                    tvTotAbs.setText(mTotAbs + "");
                    tvTotAmtInv.setText(mTotAmtInv + "");
                    tvTotGain.setText(mTotGain + "");
                    tvTotMktValue.setText(mTotMktValue + "");*/
                } catch (Exception e) {
                    Log.v(LOG_TAG, C.net.tag.DB_TRANSACTION_WRITE + "DbRecoAllPortfolio ", e);
                } finally {
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
        // TODO: 10/10/2017 Set default View/

        /*tvFeedName.setText("Pop Stocks");

        CallAdapter adapter = new CallAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
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
        });*/
    }

    private void findViews(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvRecoAllPortfolio);
        flEmptyView = (FrameLayout) rootView.findViewById(R.id.flEmptyView);
        tvError = (TextView) rootView.findViewById(R.id.tvError);
        tvCash = (TextView) rootView.findViewById(R.id.tvCash);
        tvWt = (TextView) rootView.findViewById(R.id.tvWt);
        llBottomTotal = (LinearLayout) rootView.findViewById(R.id.llBottomTotal);

        tvTotAmtInv = (TextView) rootView.findViewById(R.id.tvTotAmtInv);
        tvTotMktValue = (TextView) rootView.findViewById(R.id.tvTotMktValue);
        tvTotGain = (TextView) rootView.findViewById(R.id.tvTotGain);
        tvTotAbs = (TextView) rootView.findViewById(R.id.tvTotAbs);
    }

    private void setUpRecyclerView() {

        mDbRecoAllPortfolios = realm.where(DbRecoAllPortfolio.class)
                .findAll();

        if (mDbRecoAllPortfolios.isEmpty()) {
            // showEmptyView(true, "Not Available\n");
            llBottomTotal.setVisibility(View.GONE);
        } else {
            tvTotAbs.setText(realm.where(DbRecoAllPortfolio.class).sum("absoluteReturn").toString());
            tvTotAmtInv.setText(realm.where(DbRecoAllPortfolio.class).sum("amtInvested").toString());
            tvTotGain.setText(realm.where(DbRecoAllPortfolio.class).sum("gain").toString());
            tvTotMktValue.setText(realm.where(DbRecoAllPortfolio.class).sum("marketValue").toString());

            llBottomTotal.setVisibility(View.VISIBLE);
            showEmptyView(false, null);
        }

        /*mDbCalls = realm.where(DbCall.class).findAllSorted(DbCall.TIME, Sort.DESCENDING);
        mRecyclerView.setVisibility(View.VISIBLE);
        flEmptyView.setVisibility(View.GONE);*/

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

        private TextView tvCompanyName, tvQuantity, tvGainLoss, tvAbsReturns, tvMore, tvAvgPrice, tvCMP, tvAmtInv, tvMktValue, tvWt, tvIndustry;
        private LinearLayout layoutHolder;
        private DbRecoAllPortfolio dbRecoAllPortfolio;
        private RealmList<DbLogs> dbLogs;
        private RealmResults<DbLogs> DbLogsResult;

        public CallHolder(View itemView) {
            super(itemView);

            tvCompanyName = (TextView) itemView.findViewById(R.id.tvCompanyName);
            tvQuantity = (TextView) itemView.findViewById(R.id.tvQuantity);
            tvAvgPrice = (TextView) itemView.findViewById(R.id.tvAvgPrice);
            tvCMP = (TextView) itemView.findViewById(R.id.tvCMP);
            tvAmtInv = (TextView) itemView.findViewById(R.id.tvAmtInv);
            tvMktValue = (TextView) itemView.findViewById(R.id.tvMktValue);
            tvWt = (TextView) itemView.findViewById(R.id.tvWt);
            tvGainLoss = (TextView) itemView.findViewById(R.id.tvGainLoss);
            tvAbsReturns = (TextView) itemView.findViewById(R.id.tvAbsReturns);
            tvMore = (TextView) itemView.findViewById(R.id.tvMore);
            tvIndustry = (TextView) itemView.findViewById(R.id.tvIndustry);
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

        public void bindFeed(DbRecoAllPortfolio dbRecoAllPortfolio) {
            this.dbRecoAllPortfolio = dbRecoAllPortfolio;

            tvCompanyName.setText(this.dbRecoAllPortfolio.getCompanyName());
            tvQuantity.setText(this.dbRecoAllPortfolio.getQuantity() + "");
            tvAmtInv.setText(this.dbRecoAllPortfolio.getAmtInvested()+"");
            tvCMP.setText(this.dbRecoAllPortfolio.getCmp());
            tvAmtInv.setText(this.dbRecoAllPortfolio.getAmtInvested()+"");
            tvAvgPrice.setText(this.dbRecoAllPortfolio.getAvgPrice());
            tvMktValue.setText(this.dbRecoAllPortfolio.getMarketValue() + "");
            tvWt.setText(this.dbRecoAllPortfolio.getWeightage());
            tvIndustry.setText(this.dbRecoAllPortfolio.getIndustry());
            double value = this.dbRecoAllPortfolio.getAbsoluteReturn();
            if (value < 0) {
                Drawable img = getContext().getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp);
                img.setBounds(0, 0, 60, 60);
                tvAbsReturns.setCompoundDrawables(img, null, null, null);
            } else {
                Drawable img = getContext().getResources().getDrawable(R.drawable.ic_arrow_drop_up_black_24dp);
                img.setBounds(0, 0, 60, 60);
                tvAbsReturns.setCompoundDrawables(img, null, null, null);
            }

            value = this.dbRecoAllPortfolio.getGain();
            if (value < 0) {
                tvGainLoss.setTextColor(Color.RED);
            } else {
                tvGainLoss.setTextColor(Color.parseColor("#01b21e"));
            }
            tvGainLoss.setText(this.dbRecoAllPortfolio.getGain()+"");
            tvAbsReturns.setText(this.dbRecoAllPortfolio.getAbsoluteReturn()+"");
            /*DbLogsResult= realm.where(DbLogs.class).equalTo("portfolioDetailId",this.dbHolding.getPortfolioDetailId()).findAll();
            RealmResults<DbLogs> logList=realm.where(DbLogs.class).findAll();*/
            RealmList<DbLogs> mLogs;
            long id = this.dbRecoAllPortfolio.getRecommendedPortfolioId();

            mLogs = realm.where(DbRecoAllPortfolio.class).equalTo("recommendedPortfolioId", id).findFirst().getLogs();
            showVerticalLayout(layoutHolder, mLogs);
        }
    }

    private void showVerticalLayout(LinearLayout layoutHolder, RealmList<DbLogs> mDbLogs) {
        layoutHolder.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 0, 0);

        for (int i = 0; i < mDbLogs.size(); i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            TextView button = new TextView(getContext());
            TextView button1 = new TextView(getContext());//Creating Button

            button.setId(i);//Setting Id for using in future
            button1.setId(i);
            String logDate = "", reason = "", comments = "";
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
            return new CallHolder(inflater.inflate(R.layout.item_reco_all_portfolio, parent, false));
        }

        @Override
        public void onBindViewHolder(CallHolder holder, int position) {
            holder.bindFeed(mDbRecoAllPortfolios.get(position));
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
            return mDbRecoAllPortfolios.size();
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
