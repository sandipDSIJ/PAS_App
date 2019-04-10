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
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import in.dsij.pas.MyApplication;
import in.dsij.pas.R;
import in.dsij.pas.constants.C;
import in.dsij.pas.database.DbActivityLogs;
import in.dsij.pas.database.DbLogs;
import in.dsij.pas.database.DbSoldHistory;
import in.dsij.pas.database.DbSoldScripDetails;
import in.dsij.pas.dialog.ErrorDialogFragment;
import in.dsij.pas.dialog.ProgressDialogFragment;
import in.dsij.pas.net.respose.ResActivityLog;
import in.dsij.pas.net.respose.ResMessage;
import in.dsij.pas.net.respose.ResSoldScripDetails;
import in.dsij.pas.net.retrofit.CallGenerator;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityLogFragment extends Fragment {

    private static final String LOG_TAG = "ActivityLogFragment";
    private static final String DIALOG_PROGRESS = "ActivityLogFragment.Dialog.Progress";
    private static final String DIALOG_ERROR = "ActivityLogFragment.Dialog.Error";
    private Callbacks mCallbacks;
    private Realm realm;
    private LinearLayout llBottomTotal;

    private RecyclerView mRecyclerView;
    private FrameLayout flEmptyView;
    private TextView tvError, tvTotalMarketValue, tvTotalGain;

    private ProgressDialogFragment progressDialog;

    private RealmResults<DbActivityLogs> mDbActivityLogs;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public static ActivityLogFragment newInstance() {

        Bundle args = new Bundle();

        ActivityLogFragment fragment = new ActivityLogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ActivityLogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_activity_log, container, false);
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
       // LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GetActivityLog();
    }

    private void GetActivityLog() {

        showEmptyView(true, "Loading...");

        CallGenerator.getActivityLog().enqueue(new Callback<ResActivityLog>() {
            @Override
            public void onResponse(Call<ResActivityLog> call, Response<ResActivityLog> response) {
                if (response.isSuccessful()) {
                    try {
                        ResActivityLog resActivityLog = response.body();
                        Log.v(LOG_TAG, C.net.GetActivityLog.TAG +
                                C.net.tag.RESPONSE +
                                new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(resActivityLog));

                        if (resActivityLog == null) {
                            Log.w(LOG_TAG, C.net.GetActivityLog.TAG +
                                    C.net.tag.RESPONSE + "Received NULL Product List");
                        } else {
                            insertActivityLog(resActivityLog);
                        }

                    } catch (Exception e) {
                        Log.w(LOG_TAG, C.net.GetActivityLog.TAG + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
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

                    Log.w(LOG_TAG, C.net.GetActivityLog.TAG + C.net.tag.ERROR + "Error Response code : " + response.code() + " Response Body:\n" +
                            new GsonBuilder()
                                    .setPrettyPrinting()
                                    .create()
                                    .toJson(errorMessage));

                }
            }

            @Override
            public void onFailure(Call<ResActivityLog> call, Throwable t) {
                Log.e(LOG_TAG, C.net.GetActivityLog.TAG + C.net.tag.FAILED + "Failed API Call : ", t);

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

    private void insertActivityLog(final ResActivityLog resActivityLog) {

        final List<ResActivityLog.ActivityLogEntity> resActivityLogList = resActivityLog.getList();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + "Products");
                realm.where(DbActivityLogs.class).findAll().deleteAllFromRealm();
                for (int i = 0; i < resActivityLogList.size(); i++) {

                    DbActivityLogs dbActivityLogs = new DbActivityLogs(resActivityLogList.get(i));

                    realm.copyToRealmOrUpdate(dbActivityLogs);
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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvActivityLog);
        flEmptyView = (FrameLayout) rootView.findViewById(R.id.flEmptyView);
        tvError = (TextView) rootView.findViewById(R.id.tvError);
        tvTotalMarketValue = (TextView) rootView.findViewById(R.id.tvTotalMarketValue);
        tvTotalGain = (TextView) rootView.findViewById(R.id.tvTotalGain);
        llBottomTotal = (LinearLayout) rootView.findViewById(R.id.llBottomTotal);
    }

    private void setUpRecyclerView() {

        mDbActivityLogs = realm.where(DbActivityLogs.class)
                .findAll();

        if (mDbActivityLogs.isEmpty()) {
            // showEmptyView(true, "Not Available\n");
            llBottomTotal.setVisibility(View.GONE);
        } else {
            llBottomTotal.setVisibility(View.VISIBLE);
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

        private TextView tvLogDate, tvComments, tvMore, tvReason;
        private LinearLayout layoutHolder;
        private DbActivityLogs dbActivityLogs;

        public CallHolder(View itemView) {
            super(itemView);

            tvLogDate = (TextView) itemView.findViewById(R.id.tvLogDate);
            tvComments = (TextView) itemView.findViewById(R.id.tvComments);
            tvReason=(TextView) itemView.findViewById(R.id.tvReason);
            tvMore = (TextView) itemView.findViewById(R.id.tvMore);
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

        public void bindFeed(DbActivityLogs dbActivityLogs) {
            this.dbActivityLogs = dbActivityLogs;

            tvLogDate.setText(this.dbActivityLogs.getCreatedDate());
            tvComments.setText(this.dbActivityLogs.getComments() + "");
            if(this.dbActivityLogs.getReason().equals("") || this.dbActivityLogs.getReason().isEmpty())
                Toast.makeText(getContext(),"sds",Toast.LENGTH_LONG).show();
            tvReason.setText(this.dbActivityLogs.getReason());
        }
    }

    private void showErrorDialog(String dialogMessage) {

        ErrorDialogFragment errorDialog = ErrorDialogFragment.newInstance("Activity Log", dialogMessage);
        FragmentManager fragmentManager = getFragmentManager();
        errorDialog.show(fragmentManager, DIALOG_ERROR);
    }


    private class CallAdapter extends RecyclerView.Adapter<CallHolder> {
        @Override
        public CallHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            return new CallHolder(inflater.inflate(R.layout.item_activity_log, parent, false));
        }

        @Override
        public void onBindViewHolder(CallHolder holder, int position) {
            holder.bindFeed(mDbActivityLogs.get(position));
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
            return mDbActivityLogs.size();
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
