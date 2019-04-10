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
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.dsij.pas.MyApplication;
import in.dsij.pas.R;
import in.dsij.pas.constants.C;
import in.dsij.pas.database.DbLogs;
import in.dsij.pas.database.DbSoldHistory;
import in.dsij.pas.database.DbSoldScripDetails;
import in.dsij.pas.database.DbSummeryDtl;
import in.dsij.pas.database.DbUpdateReco;
import in.dsij.pas.dialog.ErrorDialogFragment;
import in.dsij.pas.dialog.ProgressDialogFragment;
import in.dsij.pas.dialog.UpdateRecoPriceDialogFragment;
import in.dsij.pas.net.respose.ResMessage;
import in.dsij.pas.net.respose.ResSoldScripDetails;
import in.dsij.pas.net.respose.ResSummeryDtls;
import in.dsij.pas.net.respose.ResUpdateReco;
import in.dsij.pas.net.retrofit.CallGenerator;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateRecoFragment extends Fragment {

    private static final String LOG_TAG = "UpdateRecoFragment";
    private static final String DIALOG_PROGRESS = "UpdateRecoFragment.Dialog.Progress";
    private static final String DIALOG_ERROR = "UpdateRecoFragment.Dialog.Error";
    private static final String DIALOG_UPDATE_RECO = "MainActivity.Dialog.UpdateReco";
    private Callbacks mCallbacks;
    private Realm realm;
    private LinearLayout llBottomTotal;
    private FragmentManager mFragmentManager;
    private Handler mHandler;
    /*private List<Double> lsAmtInv,lsmktValue,lsGain,lsAbs;*/

    private RecyclerView mRecyclerView, rvSummery;
    private FrameLayout flEmptyView;
    private TextView tvError;
    private TextView tvTotAmtInv, tvTotMktValue, tvTotGain, tvTotAbs;
    private double mTotAmtInv, mTotMktValue, mTotGain, mTotAbs;
    private ProgressDialogFragment progressDialog;

    private RealmResults<DbUpdateReco> mDbUpdateRecos;
    private RealmResults<DbSummeryDtl> mDbSummeryDtls;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public static UpdateRecoFragment newInstance() {

        Bundle args = new Bundle();

        UpdateRecoFragment fragment = new UpdateRecoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public UpdateRecoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_update_reco, container, false);
        findViews(rootView);
        mFragmentManager = getFragmentManager();
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GetRecommendedPortfolioScrip();
        mHandler=new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSummaryDetails();
            }
        }, 550); //3000 L = 3 detik


    }

    private void GetRecommendedPortfolioScrip() {

        showEmptyView(true, "Loading...");

        CallGenerator.getRecommendedPortfolioScrip().enqueue(new Callback<ResUpdateReco>() {
            @Override
            public void onResponse(Call<ResUpdateReco> call, Response<ResUpdateReco> response) {
                if (response.isSuccessful()) {
                    try {
                        ResUpdateReco resSoldScripDetails = response.body();
                        Log.v(LOG_TAG, C.net.GetRecommendedPortfolioScrip.TAG +
                                C.net.tag.RESPONSE +
                                new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(resSoldScripDetails));

                        if (resSoldScripDetails == null) {
                            Log.w(LOG_TAG, C.net.GetRecommendedPortfolioScrip.TAG +
                                    C.net.tag.RESPONSE + "Received NULL Product List");
                        } else {
                            insertUpdatedReco(resSoldScripDetails);
                        }

                    } catch (Exception e) {
                        Log.w(LOG_TAG, C.net.GetRecommendedPortfolioScrip.TAG + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
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
                            clearDb();
                            showHoldingDetailsView(true, errorMessage.getMessage());
                        } else
                            Snackbar.make(llBottomTotal, errorMessage.getMessage(), Snackbar.LENGTH_LONG).show();

                    } catch (IOException e) {
                        //showEmptyView(true, "Not available at the moment");
                        Snackbar.make(llBottomTotal, C.net.tag.ERROR_TIMEOUT_MSG, Snackbar.LENGTH_LONG).show();
                    } catch (Exception e) {
                        //showEmptyView(true, "Not available at the moment");
                        Snackbar.make(llBottomTotal, C.net.tag.ERROR_TIMEOUT_MSG, Snackbar.LENGTH_LONG).show();
                    }

                    switch (response.code()) {
                        default:
                            break;
                    }

                    Log.w(LOG_TAG, C.net.GetRecommendedPortfolioScrip.TAG + C.net.tag.ERROR + "Error Response code : " + response.code() + " Response Body:\n" +
                            new GsonBuilder()
                                    .setPrettyPrinting()
                                    .create()
                                    .toJson(errorMessage));

                }
            }

            @Override
            public void onFailure(Call<ResUpdateReco> call, Throwable t) {
                Log.e(LOG_TAG, C.net.GetRecommendedPortfolioScrip.TAG + C.net.tag.FAILED + "Failed API Call : ", t);

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

    private void getSummaryDetails() {

      //  showEmptyView(true, "Loading...");

        CallGenerator.getSummaryDetails().enqueue(new Callback<ResSummeryDtls>() {
            @Override
            public void onResponse(Call<ResSummeryDtls> call, Response<ResSummeryDtls> response) {
                if (response.isSuccessful()) {
                    try {
                        ResSummeryDtls resSummeryDtls = response.body();
                        Log.v(LOG_TAG, C.net.GetSummeryDtls.TAG +
                                C.net.tag.RESPONSE +
                                new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(resSummeryDtls));

                        if (resSummeryDtls == null) {
                            Log.w(LOG_TAG, C.net.GetSummeryDtls.TAG +
                                    C.net.tag.RESPONSE + "Received NULL Product List");
                        } else {
                            insertSummeryDetails(resSummeryDtls);
                        }

                    } catch (Exception e) {
                        Log.w(LOG_TAG, C.net.GetSummeryDtls.TAG + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
                    }
                } else if (response.code() == 413) {
                    Toast.makeText(getActivity(), "Already signed into other Device", Toast.LENGTH_LONG).show();
                    mCallbacks.onTokanChange();
                } else if (response.code() == 403 || response.code() == 404 || response.code() == 500 || response.code() == 503) {
                    //showHoldingDetailsView(true, "Sorry, the server is currently\n unavailable please try agein later");
                    return;
                } else {
                    ResMessage errorMessage = null;
                    try {
                        String errorRes = response.errorBody().string();
                        errorMessage = new GsonBuilder().create().fromJson(errorRes, ResMessage.class);
                        if (errorMessage.getCode() == 417) {
                            clearDb();
                            //showHoldingDetailsView(true, errorMessage.getMessage());
                        } else
                            Toast.makeText(getActivity(), errorMessage.getMessage(), Toast.LENGTH_LONG).show();

                    } catch (IOException e) {
                        //showEmptyView(true, "Not available at the moment");
                        Toast.makeText(getActivity(), C.net.tag.ERROR_TIMEOUT_MSG, Toast.LENGTH_LONG).show();
                        //Snackbar.make(llBottomTotal, C.net.tag.ERROR_TIMEOUT_MSG, Snackbar.LENGTH_LONG).show();
                    } catch (Exception e) {
                        //showEmptyView(true, "Not available at the moment");
//                        Snackbar.make(llBottomTotal, "Already signed into other Device", Snackbar.LENGTH_LONG).show();
                        //Snackbar.make(llBottomTotal, C.net.tag.ERROR_TIMEOUT_MSG, Snackbar.LENGTH_LONG).show();
                    }

                    switch (response.code()) {
                        default:
                            break;
                    }

                    Log.w(LOG_TAG, C.net.GetSummeryDtls.TAG + C.net.tag.ERROR + "Error Response code : " + response.code() + " Response Body:\n" +
                            new GsonBuilder()
                                    .setPrettyPrinting()
                                    .create()
                                    .toJson(errorMessage));

                }
            }

            @Override
            public void onFailure(Call<ResSummeryDtls> call, Throwable t) {
                Log.e(LOG_TAG, C.net.GetSummeryDtls.TAG + C.net.tag.FAILED + "Failed API Call : ", t);

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

    private void insertUpdatedReco(final ResUpdateReco resUpdateReco) {

        mTotAmtInv = 0;
        mTotMktValue = 0;
        mTotGain = 0;
        mTotAbs = 0;
        final List<ResUpdateReco.listUpdateRecoEntity> listUpdateRecoEntities = resUpdateReco.getList();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + "DbUpdateReco");
                realm.where(DbUpdateReco.class).findAll().deleteAllFromRealm();
                realm.where(DbLogs.class).findAll().deleteAllFromRealm();
                for (int i = 0; i < listUpdateRecoEntities.size(); i++) {

                    DbUpdateReco dbUpdateReco = new DbUpdateReco(listUpdateRecoEntities.get(i));
                    realm.copyToRealmOrUpdate(dbUpdateReco);
                }

                try {
                   /* for (int i = 0; i < listUpdateRecoEntities.size(); i++) {
                        double valueAmtInv = Double.valueOf(listUpdateRecoEntities.get(i).getAmtInvested());
                        double valueMktValue = Double.valueOf(listUpdateRecoEntities.get(i).getMarketValue());
                        double valueAbs = Double.valueOf(listUpdateRecoEntities.get(i).getAbsoluteReturn());
                        double valueGain = Double.valueOf(listUpdateRecoEntities.get(i).getGain());

                        mTotAmtInv = mTotAmtInv + valueAmtInv;
                        mTotMktValue = mTotMktValue + valueMktValue;
                        mTotGain = mTotGain + valueGain;
                        mTotAbs = mTotAbs + valueAbs;

                    }*/
                } catch (Exception e) {
                    Log.v(LOG_TAG, C.net.tag.DB_TRANSACTION_WRITE + "DbUpdateReco ", e);
                } finally {
                    //realm.createOrUpdateAllFromJson(DbHolding.class, new Gson().toJson(resGetHolding.getlsHoldingEntity()));
                }
//

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

    private void insertSummeryDetails(final ResSummeryDtls resSummeryDtls) {

        final List<ResSummeryDtls.SummeryEntity> summeryEntities = resSummeryDtls.getList();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + "Products");
                realm.where(DbSummeryDtl.class).findAll().deleteAllFromRealm();
                for (int i = 0; i < summeryEntities.size(); i++) {
                    DbSummeryDtl dbSummeryDtl = new DbSummeryDtl(summeryEntities.get(i));
                    realm.copyToRealm(dbSummeryDtl);
                }

                try {
//                    realm.where(DbProduct.class).findAll().deleteAllFromRealm();
                } catch (Exception e) {
                    Log.v(LOG_TAG, C.net.tag.DB_TRANSACTION_WRITE + "Already no Product present ", e);
                } finally {
                    //realm.createOrUpdateAllFromJson(DbSummeryDtl.class, new Gson().toJson(summeryEntities));
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + C.net.tag.DB_SUCCESS);

                setUpSummeryView();

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

    }

    private void findViews(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvUpdateReco);
        rvSummery = (RecyclerView) rootView.findViewById(R.id.rvSummery);
        flEmptyView = (FrameLayout) rootView.findViewById(R.id.flEmptyView);
        tvError = (TextView) rootView.findViewById(R.id.tvError);
        llBottomTotal = (LinearLayout) rootView.findViewById(R.id.llBottomTotal);

        tvTotAmtInv = (TextView) rootView.findViewById(R.id.tvTotAmtInv);
        tvTotMktValue = (TextView) rootView.findViewById(R.id.tvTotMktValue);
        tvTotGain = (TextView) rootView.findViewById(R.id.tvTotGain);
        tvTotAbs = (TextView) rootView.findViewById(R.id.tvTotAbs);
    }

    private void setUpRecyclerView() {

        mDbUpdateRecos = realm.where(DbUpdateReco.class)
                .findAll();

        if (mDbUpdateRecos.isEmpty()) {
            llBottomTotal.setVisibility(View.GONE);
        } else {
            llBottomTotal.setVisibility(View.VISIBLE);
            //  long  totAmtInv= realm.where(DbUpdateReco.class).sum("portfolioId").;
            tvTotAbs.setText(realm.where(DbUpdateReco.class).sum("absoluteReturn").toString());
            tvTotAmtInv.setText(realm.where(DbUpdateReco.class).sum("amtInvested").toString());
            tvTotGain.setText(realm.where(DbUpdateReco.class).sum("gain").toString());
            tvTotMktValue.setText(realm.where(DbUpdateReco.class).sum("marketValue").toString());
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

    private void setUpSummeryView() {

        mDbSummeryDtls = realm.where(DbSummeryDtl.class)
                .findAll();



        try {
            rvSummery.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {

            SummeryAdapter adapter = new SummeryAdapter();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            rvSummery.setLayoutManager(layoutManager);
            rvSummery.setAdapter(adapter);

            rvSummery.addItemDecoration(new RecyclerView.ItemDecoration() {
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

        private TextView tvCompanyName, tvQuantity, tvAvgPrice, tvCMP, tvAmtInv, tvMarketValue, tvGainLoss, tvAbsReturns, tvIndustry, tvOperation, tvEdit;
        private LinearLayout layoutHolder;
        private DbUpdateReco dbUpdateReco;
        private RealmList<DbLogs> dbLogs;
        private RealmResults<DbLogs> DbLogsResult;

        public CallHolder(View itemView) {
            super(itemView);

            tvCompanyName = (TextView) itemView.findViewById(R.id.tvCompanyName);
            tvQuantity = (TextView) itemView.findViewById(R.id.tvQuantity);
            tvAvgPrice = (TextView) itemView.findViewById(R.id.tvAvgPrice);
            tvCMP = (TextView) itemView.findViewById(R.id.tvCMP);
            tvAmtInv = (TextView) itemView.findViewById(R.id.tvAmtInv);
            tvMarketValue = (TextView) itemView.findViewById(R.id.tvMarketValue);
            tvGainLoss = (TextView) itemView.findViewById(R.id.tvGainLoss);
            tvAbsReturns = (TextView) itemView.findViewById(R.id.tvAbsReturns);
            tvEdit = (TextView) itemView.findViewById(R.id.tvEdit);
            tvIndustry = (TextView) itemView.findViewById(R.id.tvIndustry);
            layoutHolder = (LinearLayout) itemView.findViewById(R.id.llActivityLog);
            tvOperation = (TextView) itemView.findViewById(R.id.tvOperation);
            tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UpdateRecoPrice(dbUpdateReco.getCompanyName(), dbUpdateReco.getCompanyCode(), dbUpdateReco.getQuantity(), dbUpdateReco.getAvgPrice(), dbUpdateReco.getOrderQueueId(), dbUpdateReco.getOperation());
                }
            });
        }

        public void bindFeed(DbUpdateReco dbUpdateReco) {
            this.dbUpdateReco = dbUpdateReco;

            tvCompanyName.setText(this.dbUpdateReco.getCompanyName());
            tvQuantity.setText(this.dbUpdateReco.getQuantity() + "");
            tvAvgPrice.setText(this.dbUpdateReco.getAvgPrice());
            tvCMP.setText(this.dbUpdateReco.getCmp());
            tvAmtInv.setText(this.dbUpdateReco.getAmtInvested()+"");
            tvMarketValue.setText(this.dbUpdateReco.getMarketValue() + "");
            tvIndustry.setText(this.dbUpdateReco.getIndustry());
            tvOperation.setText(this.dbUpdateReco.getOperation());

            if (!dbUpdateReco.isUpdated())
                tvEdit.setVisibility(View.VISIBLE);
            else
                tvEdit.setVisibility(View.GONE);

            double value = Double.valueOf(this.dbUpdateReco.getGain());
            if (value >= 0)
                tvGainLoss.setTextColor(Color.GREEN);
            else
                tvGainLoss.setTextColor(Color.RED);
            tvGainLoss.setText(this.dbUpdateReco.getGain()+"");
            tvAbsReturns.setText(this.dbUpdateReco.getAbsoluteReturn()+"");

        }
    }

    private class SummeryHolder extends RecyclerView.ViewHolder {

        private TextView tvType, tvPortValue, tvSensexValue, tvRecoPref;
        private DbSummeryDtl dbSummeryDtl;

        public SummeryHolder(View itemView) {
            super(itemView);

            tvType = (TextView) itemView.findViewById(R.id.tvType);
            tvPortValue = (TextView) itemView.findViewById(R.id.tvPortValue);
            tvSensexValue = (TextView) itemView.findViewById(R.id.tvSensexValue);
            tvRecoPref = (TextView) itemView.findViewById(R.id.tvRecoPref);

        }

        public void bindFeed(DbSummeryDtl dbSummeryDtl) {
            this.dbSummeryDtl = dbSummeryDtl;
            tvType.setText(this.dbSummeryDtl.getValueType());
            tvPortValue.setText(this.dbSummeryDtl.getPortfolioValue());
            tvSensexValue.setText(this.dbSummeryDtl.getSensexValue());
            tvRecoPref.setText(this.dbSummeryDtl.getRecoValue());

        }
    }

    private void UpdateRecoPrice(String companyName, @Nullable String companyCode, @Nullable long quantity,
                                 @Nullable String avgPrice, @Nullable long orderQueueId, @Nullable String operation) {
        UpdateRecoPriceDialogFragment UpdateRecoDialog = UpdateRecoPriceDialogFragment.newInstance("Update Recommendation",
                companyName, companyCode, quantity, avgPrice, orderQueueId, operation);
        UpdateRecoDialog.show(mFragmentManager, DIALOG_UPDATE_RECO);

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
            return new CallHolder(inflater.inflate(R.layout.item_update_reco, parent, false));
        }

        @Override
        public void onBindViewHolder(CallHolder holder, int position) {
            holder.bindFeed(mDbUpdateRecos.get(position));
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
            return mDbUpdateRecos.size();
        }
    }

    private class SummeryAdapter extends RecyclerView.Adapter<SummeryHolder> {
        @Override
        public SummeryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            return new SummeryHolder(inflater.inflate(R.layout.item_summery, parent, false));
        }

        @Override
        public void onBindViewHolder(SummeryHolder holder, int position) {
            holder.bindFeed(mDbSummeryDtls.get(position));
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull SummeryHolder holder) {
            super.onViewDetachedFromWindow(holder);
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public int getItemCount() {
            return mDbSummeryDtls.size();
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
