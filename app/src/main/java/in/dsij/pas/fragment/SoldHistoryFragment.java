package in.dsij.pas.fragment;


import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import in.dsij.pas.MyApplication;
import in.dsij.pas.R;
import in.dsij.pas.constants.C;
import in.dsij.pas.database.DbLogs;
import in.dsij.pas.database.DbSoldHistory;
import in.dsij.pas.database.DbSoldScripDetails;
import in.dsij.pas.net.respose.ResMessage;
import in.dsij.pas.net.respose.ResSoldHistoryList;
import in.dsij.pas.net.respose.ResSoldScripDetails;
import in.dsij.pas.net.retrofit.CallGenerator;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SoldHistoryFragment extends Fragment {

    private static final String LOG_TAG = "SoldHistoryFragment";

    private Realm realm;

    private RecyclerView mRecyclerView;
    private FrameLayout flEmptyView;
    private TextView tvError;
    private Callbacks mCallbacks;
    private RealmResults<DbSoldHistory> mDbSoldHistories;

    public static SoldHistoryFragment newInstance() {

        Bundle args = new Bundle();
        SoldHistoryFragment fragment = new SoldHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SoldHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sold_history, container, false);
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
       // Toast.makeText(getActivity(),"on Resume",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {
        super.onPause();
       // LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getSoldHistories();

    }


    private void getSoldHistories() {

        showEmptyView(true,"Loading...");

        CallGenerator.getSoldScripDetails().enqueue(new Callback<ResSoldScripDetails>() {
            @Override
            public void onResponse(Call<ResSoldScripDetails> call, Response<ResSoldScripDetails> response) {
                if (response.isSuccessful()) {
                    try {
                        ResSoldScripDetails resSoldHistoryList = response.body();

                        Log.v(LOG_TAG, C.net.GetSoldScripDetails.TAG +
                                C.net.tag.RESPONSE +
                                new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(resSoldHistoryList));

                        if (resSoldHistoryList == null) {
                            Log.w(LOG_TAG, C.net.GetSoldScripDetails.TAG +
                                    C.net.tag.RESPONSE + "Received NULL Product List");
                        } else {
                            //insertHistory(resSoldHistoryList);
                        }

                    } catch (Exception e) {
                        Log.w(LOG_TAG, C.net.GetSoldScripDetails.TAG + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
                    }
                } else if (response.code() == 413) {
                   // Snackbar.make(tvError, "Already signed into other Device", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(getContext(),"Already signed into other Device",Toast.LENGTH_LONG).show();
                    mCallbacks.onTokanChange();
                    return;
                } else if (response.code() == 403 || response.code() == 404 || response.code() == 500 || response.code() == 503) {
                    Snackbar.make(tvError, "Sorry, the server is currently\n unavailable please try agein later", Snackbar.LENGTH_LONG).show();
                    return;
                } else {
                    showEmptyView(true, "Not available at the moment");
                    ResMessage errorMessage = null;

                    try {
                        String errorRes = response.errorBody().string();

                        errorMessage = new GsonBuilder().create().fromJson(errorRes, ResMessage.class);

                    } catch (IOException e) {
                        //
                    }

                    switch (response.code()) {
                        default:
                            // TODO: 10/10/2017
                            break;
                    }

                    Log.w(LOG_TAG, C.net.GetSoldScripDetails.TAG + C.net.tag.ERROR + "Error Response code : " + response.code() + " Response Body:\n" +
                            new GsonBuilder()
                                    .setPrettyPrinting()
                                    .create()
                                    .toJson(errorMessage));

                }
            }

            @Override
            public void onFailure(Call<ResSoldScripDetails> call, Throwable t) {
                Log.e(LOG_TAG, C.net.GetSoldScripDetails.TAG + C.net.tag.FAILED + "Failed API Call : ", t);

                if (!MyApplication.isConnected())
                {
                    showEmptyView(true,C.net.tag.ERROR_CHECK_INTERNET);
                }
                else
                {
                    showEmptyView(true,"Not available at the moment");
                }
            }
        });
    }

    private void insertHoldings(final ResSoldScripDetails resSoldScripDetails) {

        final List<ResSoldScripDetails.listSoldScripEntity> resListSoldScripEntities = resSoldScripDetails.getlsSoldScripEntity();


        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + "Products");
                realm.where(DbSoldScripDetails.class).findAll().deleteAllFromRealm();
                realm.where(DbLogs.class).findAll().deleteAllFromRealm();
                for (int i = 0; i < resListSoldScripEntities.size(); i++) {

                    DbSoldScripDetails dbHolding = new DbSoldScripDetails(resListSoldScripEntities.get(i));

                    realm.copyToRealmOrUpdate(dbHolding);
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

    }

    private void findViews(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvSoldHistory);
        flEmptyView = (FrameLayout) rootView.findViewById(R.id.flEmptyView);
        tvError = (TextView) rootView.findViewById(R.id.tvError);
    }

    private void setUpRecyclerView() {
        mDbSoldHistories = realm.where(DbSoldHistory.class)
                .findAll();

        if (mDbSoldHistories.isEmpty()) {
            showEmptyView(true, "Not Available\n\n Trade History");
        } else {
            showEmptyView(false, null);
        }


        try {
            mRecyclerView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {

            TradrHistoryAdapter adapter = new TradrHistoryAdapter();
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
            });
        }
    }


    private class TradeHistoryHolder extends RecyclerView.ViewHolder {

        private TextView tvSHCompanyName,tvSHDate,tvSHQty,tvSHBuyPrice,tvSHSellPrice,tvSHGain,tvSHAbsReturn;
        private DbSoldHistory dbSoldHistory;

        public TradeHistoryHolder(View itemView) {
            super(itemView);

            tvSHCompanyName= (TextView) itemView.findViewById(R.id.tvSHCompanyName);
            tvSHDate= (TextView) itemView.findViewById(R.id.tvSHDate);
            tvSHQty= (TextView) itemView.findViewById(R.id.tvSHQty);
            tvSHBuyPrice= (TextView) itemView.findViewById(R.id.tvSHBuyPrice);
            tvSHSellPrice= (TextView) itemView.findViewById(R.id.tvSHSellPrice);
            tvSHGain= (TextView) itemView.findViewById(R.id.tvSHGain);
            tvSHAbsReturn= (TextView) itemView.findViewById(R.id.tvSHAbsReturn);
        }

        public void bindFeed(DbSoldHistory dbSoldHistory) {
            this.dbSoldHistory = dbSoldHistory;

            tvSHCompanyName.setText(this.dbSoldHistory.getTicker());
            tvSHDate.setText(this.dbSoldHistory.getSellDate());
            tvSHQty.setText(this.dbSoldHistory.getQuantity()+"");
            tvSHBuyPrice.setText(this.dbSoldHistory.getBuyPrice()+"");
            tvSHSellPrice.setText(this.dbSoldHistory.getSellPrice()+"");
            tvSHGain.setText(this.dbSoldHistory.getGain()+"");
            tvSHAbsReturn.setText(this.dbSoldHistory.getAbsoluteReturn()+"");
        }
    }

    private class TradrHistoryAdapter extends RecyclerView.Adapter<TradeHistoryHolder> {

        @Override
        public TradeHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
                return new TradeHistoryHolder(inflater.inflate(R.layout.item_sold_history, parent, false));
        }

        @Override
        public void onBindViewHolder(TradeHistoryHolder holder, int position) {
            holder.bindFeed(mDbSoldHistories.get(position));
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public int getItemCount() {
            return mDbSoldHistories.size();
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
        realm.close();
        mCallbacks = null;
    }

    public interface Callbacks {

        void onTokanChange();
    }
}
