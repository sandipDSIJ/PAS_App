package in.dsij.pas.fragment;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
import in.dsij.pas.database.DbActivityLogs;
import in.dsij.pas.database.DbLogs;
import in.dsij.pas.database.DbSoldHistory;
import in.dsij.pas.database.DbSoldScripDetails;
import in.dsij.pas.dialog.ErrorDialogFragment;
import in.dsij.pas.dialog.ProgressDialogFragment;
import in.dsij.pas.net.respose.ResActivityLog;
import in.dsij.pas.net.respose.ResMessage;
import in.dsij.pas.net.respose.ResRiskAssessmentQA;
import in.dsij.pas.net.retrofit.CallGenerator;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiskAssessmentQuestionsFragment extends Fragment {

    private static final String LOG_TAG = "RiskAsseFragment";
    private static final String DIALOG_PROGRESS = "RiskAsseFragment.Dialog.Progress";
    private static final String DIALOG_ERROR = "RiskAsseFragment.Dialog.Error";
    private Callbacks mCallbacks;
    private Realm realm;


    private RecyclerView mRecyclerView;
    private FrameLayout flEmptyView;
    private TextView tvError, tvTotalMarketValue, tvTotalGain;

    private ProgressDialogFragment progressDialog;
    private List<ResRiskAssessmentQA.listQuestionsEntity> mResRiskAssessmentQAList;

    private RealmResults<DbActivityLogs> mDbActivityLogs;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public static RiskAssessmentQuestionsFragment newInstance() {

        Bundle args = new Bundle();

        RiskAssessmentQuestionsFragment fragment = new RiskAssessmentQuestionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public RiskAssessmentQuestionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_risk_assessment, container, false);
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
      //  setUpRecyclerView();
    }

    @Override
    public void onPause() {
        super.onPause();
     //
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GetActivityLog();
    }

    private void GetActivityLog() {

        showEmptyView(true, "Loading...");

        CallGenerator.getRiskAssessmentQuestionAnswer().enqueue(new Callback<ResRiskAssessmentQA>() {
            @Override
            public void onResponse(Call<ResRiskAssessmentQA> call, Response<ResRiskAssessmentQA> response) {
                if (response.isSuccessful()) {
                    try {
                        ResRiskAssessmentQA resRiskAssessmentQA = response.body();
                        Log.v(LOG_TAG, C.net.GetActivityLog.TAG +
                                C.net.tag.RESPONSE +
                                new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create()
                                        .toJson(resRiskAssessmentQA));

                        if (resRiskAssessmentQA == null) {
                            Log.w(LOG_TAG, C.net.GetActivityLog.TAG +
                                    C.net.tag.RESPONSE + "Received NULL Product List");
                        } else {
                            mResRiskAssessmentQAList = resRiskAssessmentQA.getList();
                           // insertActivityLog(resRiskAssessmentQA);
                            if(mResRiskAssessmentQAList.size()>0)
                            setUpRecyclerView();
                        }

                    } catch (Exception e) {
                        Log.w(LOG_TAG, C.net.GetActivityLog.TAG + C.net.tag.RESPONSE + "Parse response Error: " + response.body(), e);
                    }
                } else if (response.code() == 413) {
                    Snackbar.make(tvError, "Already signed into other Device", Snackbar.LENGTH_LONG).show();
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
                            Snackbar.make(tvError, errorMessage.getMessage(), Snackbar.LENGTH_LONG).show();

                    } catch (IOException e) {
                        //showEmptyView(true, "Not available at the moment");
                        Snackbar.make(tvError, C.net.tag.ERROR_TIMEOUT_MSG, Snackbar.LENGTH_LONG).show();
                    } catch (Exception e) {
                        //showEmptyView(true, "Not available at the moment");
                        Snackbar.make(tvError, C.net.tag.ERROR_TIMEOUT_MSG, Snackbar.LENGTH_LONG).show();
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
            public void onFailure(Call<ResRiskAssessmentQA> call, Throwable t) {
                Log.e(LOG_TAG, C.net.GetActivityLog.TAG + C.net.tag.FAILED + "Failed API Call : ", t);

                if (!MyApplication.isConnected()) {
                    tvError.setVisibility(View.GONE);
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
    }

    private void findViews(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvRiskAssessment);
        flEmptyView = (FrameLayout) rootView.findViewById(R.id.flEmptyView);
        tvError = (TextView) rootView.findViewById(R.id.tvError);
    }

    private void setUpRecyclerView() {


        if(mResRiskAssessmentQAList.size()>0) {
            CallAdapter adapter = new CallAdapter();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(adapter);
        }

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

    private class CallQuestions extends RecyclerView.ViewHolder {

        private TextView tvQuestion, tvQuestionNo;
        private RadioButton radioOption1, radioOption2, radioOption3, radioOption4;
        private RadioGroup radioGroup;

        private ResRiskAssessmentQA.listQuestionsEntity listQuestionsEntity;

        public CallQuestions(View itemView) {
            super(itemView);

            tvQuestion = (TextView) itemView.findViewById(R.id.tvQuestion);
            tvQuestionNo = (TextView) itemView.findViewById(R.id.tvQuestionNo);
            radioOption1 = (RadioButton) itemView.findViewById(R.id.radioButton1);
            radioOption2 = (RadioButton) itemView.findViewById(R.id.radioButton2);
            radioOption3 = (RadioButton) itemView.findViewById(R.id.radioButton3);
            radioOption4 = (RadioButton) itemView.findViewById(R.id.radioButton4);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.radioButton1) {
                        Toast.makeText(getContext(), tvQuestionNo.getText().toString(), Toast.LENGTH_LONG);
                    }
                }
            });
        }

            /*radioOption1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!layoutHolder.isShown())
                        layoutHolder.setVisibility(View.VISIBLE);
                    else
                        layoutHolder.setVisibility(View.GONE);
                }
            });*/


        public void bindFeed(ResRiskAssessmentQA.listQuestionsEntity listQuestionsEntity) {
            this.listQuestionsEntity = listQuestionsEntity;
            tvQuestion.setText(this.listQuestionsEntity.getQuestion());
            tvQuestionNo.setText(this.listQuestionsEntity.getRiskQuestionId()+"");
           /* List<ResRiskAssessmentQA.listQuestionsEntity.AnsEntity> ansEntityList;
            ansEntityList=listQuestionsEntity.getAns();
            radioOption1.setText(ansEntityList.get(0).getQuestion());
            radioOption2.setText(ansEntityList.get(1).getQuestion());
            radioOption3.setText(ansEntityList.get(2).getQuestion());
            radioOption4.setText(ansEntityList.get(3).getQuestion());*/

        }
    }

        private class CallAdapter extends RecyclerView.Adapter<CallQuestions> {
            @Override
            public CallQuestions onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                return new CallQuestions(inflater.inflate(R.layout.item_question_answer, parent, false));
            }

            @Override
            public void onBindViewHolder(CallQuestions holder, int position) {
                holder.bindFeed(mResRiskAssessmentQAList.get(position));
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull CallQuestions holder) {
                super.onViewDetachedFromWindow(holder);
            }

            @Override
            public int getItemViewType(int position) {
                return 1;
            }

            @Override
            public int getItemCount() {
                return mResRiskAssessmentQAList.size();
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

