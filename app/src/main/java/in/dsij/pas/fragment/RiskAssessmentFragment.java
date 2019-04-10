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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.dsij.pas.MyApplication;
import in.dsij.pas.R;
import in.dsij.pas.constants.C;
import in.dsij.pas.database.DbAns;
import in.dsij.pas.database.DbLogs;
import in.dsij.pas.database.DbRiskAssessment;
import in.dsij.pas.database.DbSoldHistory;
import in.dsij.pas.database.DbSoldScripDetails;
import in.dsij.pas.dialog.ErrorDialogFragment;
import in.dsij.pas.dialog.ProgressDialogFragment;
import in.dsij.pas.net.respose.ResMessage;
import in.dsij.pas.net.respose.ResRiskAssessmentQA;
import in.dsij.pas.net.respose.ResSoldScripDetails;
import in.dsij.pas.net.retrofit.CallGenerator;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiskAssessmentFragment extends Fragment {

    private static final String LOG_TAG = "RiskAssessmentFragment";
    private static final String DIALOG_PROGRESS = "RiskAssessmentFragment.Dialog.Progress";
    private static final String DIALOG_ERROR = "RiskAssessmentFragment.Dialog.Error";
    private Callbacks mCallbacks;
    private Realm realm;
    private String answers;
    private List<Long> answerId;
    //private LinearLayout tvError;

    private RecyclerView mRecyclerView;
    private FrameLayout flEmptyView;
    private TextView tvError;
    private Button btnSubmit;
    private ProgressDialogFragment progressDialog;

    private RealmResults<DbRiskAssessment> mDbRiskAssessments;


    public static RiskAssessmentFragment newInstance() {

        Bundle args = new Bundle();

        RiskAssessmentFragment fragment = new RiskAssessmentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public RiskAssessmentFragment() {
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
        setUpRecyclerView();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getRiskAssessment();
    }

    private void getRiskAssessment() {

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
                             insertIntoTable(resRiskAssessmentQA);
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

    private void insertIntoTable(final ResRiskAssessmentQA resListQuestionsEntity) {

        final List<ResRiskAssessmentQA.listQuestionsEntity> resListQuestionsEntityList = resListQuestionsEntity.getList();


        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(LOG_TAG,
                        C.net.tag.DB_TRANSACTION_WRITE + "Products");
                realm.where(DbRiskAssessment.class).findAll().deleteAllFromRealm();
                realm.where(DbAns.class).findAll().deleteAllFromRealm();
                for (int i = 0; i < resListQuestionsEntityList.size(); i++) {

                    DbRiskAssessment dbRiskAssessment = new DbRiskAssessment(resListQuestionsEntityList.get(i));
                    realm.copyToRealmOrUpdate(dbRiskAssessment);
                    answerId.add(resListQuestionsEntityList.get(i).getAns().get(0).getRiskAnswerId());
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
        answerId=new ArrayList<>();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answers=answerId.toString();
                submitAnswers();
            }
        });
    }

    private void submitAnswers() {
        CallGenerator.submitRiskQuestionAnswer(answers).enqueue(new Callback<ResMessage>() {
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
                            Snackbar.make(btnSubmit, strMsg, Snackbar.LENGTH_LONG).show();
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
                            Snackbar.make(btnSubmit, "! Server ran into trouble.", Snackbar.LENGTH_LONG).show();
                            break;
                        case C.net.SubmitRiskQuestionAnswer.error.SERVER_ERROR:
                            Snackbar.make(btnSubmit, "! Something went wrong !", Snackbar.LENGTH_LONG).show();
                            break;
                        case C.net.SubmitRiskQuestionAnswer.error.EMPTY_PARAMS:
                            Snackbar.make(btnSubmit, "! App ran into trouble.", Snackbar.LENGTH_LONG).show();
                            break;
                        default:
                            Snackbar.make(btnSubmit, "! Server ran into trouble.", Snackbar.LENGTH_LONG).show();
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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvRiskAssessment);
        flEmptyView = (FrameLayout) rootView.findViewById(R.id.flEmptyView);
        tvError = (TextView) rootView.findViewById(R.id.tvError);
        btnSubmit=(Button)rootView.findViewById(R.id.btnSubmit);

    }

    private void setUpRecyclerView() {

        mDbRiskAssessments = realm.where(DbRiskAssessment.class)
                .findAll();

        if (mDbRiskAssessments.isEmpty()) {
            // showEmptyView(true, "Not Available\n");
            tvError.setVisibility(View.GONE);
        } else {
            tvError.setVisibility(View.VISIBLE);
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

        private TextView tvQuestion, tvQuestionNo;
        private RadioButton radioOption1, radioOption2, radioOption3, radioOption4,radioOption5,radioButton6;
        private RadioGroup radioGroup;
        private DbRiskAssessment dbRiskAssessment;
        private RealmList<DbAns> dbAns;
        //private List<Long> answerId;
        public CallHolder(View itemView) {
            super(itemView);


            tvQuestion = (TextView) itemView.findViewById(R.id.tvQuestion);
            tvQuestionNo = (TextView) itemView.findViewById(R.id.tvQuestionNo);
            radioGroup=(RadioGroup) itemView.findViewById(R.id.radioGroup);
            radioOption1= (RadioButton) itemView.findViewById(R.id.radioButton1);
            radioOption2= (RadioButton) itemView.findViewById(R.id.radioButton2);
            radioOption3= (RadioButton) itemView.findViewById(R.id.radioButton3);
            radioOption4= (RadioButton) itemView.findViewById(R.id.radioButton4);
            radioOption5= (RadioButton) itemView.findViewById(R.id.radioButton5);
            radioButton6=(RadioButton)itemView.findViewById(R.id.radioButton6);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(checkedId==R.id.radioButton1)
                    {
                        answerId.set(getAdapterPosition(),dbAns.get(0).getRiskAnswerId());
                    }
                    else if(checkedId==R.id.radioButton2)
                    {
                        answerId.set(getAdapterPosition(),dbAns.get(1).getRiskAnswerId());
                    }
                    else if(checkedId==R.id.radioButton3)
                    {
                        answerId.set(getAdapterPosition(),dbAns.get(2).getRiskAnswerId());
                    }
                    else if(checkedId==R.id.radioButton4)
                    {
                        answerId.set(getAdapterPosition(),dbAns.get(3).getRiskAnswerId());
                    }
                    else if(checkedId==R.id.radioButton5)
                    {
                        answerId.set(getAdapterPosition(),dbAns.get(4).getRiskAnswerId());
                    }
                    else if(checkedId==R.id.radioButton6)
                    {
                        answerId.set(getAdapterPosition(),dbAns.get(5).getRiskAnswerId());
                    }
                }
            });
        }

        public void bindFeed(DbRiskAssessment dbRiskAssessment) {
            this.dbRiskAssessment = dbRiskAssessment;

            tvQuestion.setText(this.dbRiskAssessment.getQuestion());
            tvQuestionNo.setText(this.dbRiskAssessment.getRiskQuestionId() + "");
            //radioOption1.setText( radioOption2, radioOption3, radioOption4
            try
            {
                dbAns = realm.where(DbRiskAssessment.class).equalTo("riskQuestionId", dbRiskAssessment.getRiskQuestionId()).findFirst().getDbAns();
                if(dbAns.size()==6)
                {
                    radioOption1.setText(dbAns.get(0).getAnswer());
                    radioOption2.setText(dbAns.get(1).getAnswer());
                    radioOption3.setText(dbAns.get(2).getAnswer());
                    radioOption4.setText(dbAns.get(3).getAnswer());
                    radioOption5.setText(dbAns.get(4).getAnswer());
                    radioButton6.setText(dbAns.get(5).getAnswer());
                }

                if(dbAns.size()==5)
                {
                    radioOption1.setText(dbAns.get(0).getAnswer());
                    radioOption2.setText(dbAns.get(1).getAnswer());
                    radioOption3.setText(dbAns.get(2).getAnswer());
                    radioOption4.setText(dbAns.get(3).getAnswer());
                    radioOption5.setText(dbAns.get(4).getAnswer());
                    radioButton6.setVisibility(View.GONE);
                }
                if(dbAns.size()==4)
                {
                    radioOption1.setText(dbAns.get(0).getAnswer());
                    radioOption2.setText(dbAns.get(1).getAnswer());
                    radioOption3.setText(dbAns.get(2).getAnswer());
                    radioOption4.setText(dbAns.get(3).getAnswer());
                    radioOption5.setVisibility(View.GONE);
                    radioButton6.setVisibility(View.GONE);
                }
                if(dbAns.size()==3)
                {
                    radioOption1.setText(dbAns.get(0).getAnswer());
                    radioOption2.setText(dbAns.get(1).getAnswer());
                    radioOption3.setText(dbAns.get(2).getAnswer());
                    radioOption4.setVisibility(View.GONE);
                    radioOption5.setVisibility(View.GONE);
                    radioButton6.setVisibility(View.GONE);
                }
                if(dbAns.size()==2)
                {
                    radioOption1.setText(dbAns.get(0).getAnswer());
                    radioOption2.setText(dbAns.get(1).getAnswer());
                    radioOption3.setVisibility(View.GONE);
                    radioOption4.setVisibility(View.GONE);
                    radioOption5.setVisibility(View.GONE);
                    radioButton6.setVisibility(View.GONE);
                }
                if(dbAns.size()==1)
                {
                    radioOption1.setText(dbAns.get(0).getAnswer());
                    radioOption2.setVisibility(View.GONE);
                    radioOption3.setVisibility(View.GONE);
                    radioOption4.setVisibility(View.GONE);
                    radioOption5.setVisibility(View.GONE);
                    radioButton6.setVisibility(View.GONE);
                }
                //answerId.set(getAdapterPosition(),dbAns.get(0).getRiskAnswerId());
            } catch (RealmException e)
            {
             Log.e(LOG_TAG,e.toString());
            } catch (Exception e)
            {
                Log.e(LOG_TAG,e.toString());
            }
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
            return new CallHolder(inflater.inflate(R.layout.item_question_answer, parent, false));
        }

        @Override
        public void onBindViewHolder(CallHolder holder, int position) {
            holder.bindFeed(mDbRiskAssessments.get(position));
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
            return mDbRiskAssessments.size();
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
