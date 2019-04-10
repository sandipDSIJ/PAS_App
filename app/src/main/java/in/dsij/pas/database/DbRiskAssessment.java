package in.dsij.pas.database;

import java.util.List;

import in.dsij.pas.net.respose.ResHoldings;
import in.dsij.pas.net.respose.ResRiskAssessmentQA;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class DbRiskAssessment extends RealmObject {

    @PrimaryKey
    private long riskQuestionId;
    private String question;
    private RealmList<DbAns> dbAns;


    public DbRiskAssessment() {}
    public DbRiskAssessment(ResRiskAssessmentQA.listQuestionsEntity entity) {
        this.riskQuestionId=entity.getRiskQuestionId();
        this.question=entity.getQuestion();
        List<ResRiskAssessmentQA.listQuestionsEntity.AnsEntity> ansEntityList= entity.getAns();
        RealmList<DbAns> dbAns= new RealmList<>();
        for (int i = 0; i < ansEntityList.size(); i++) {
             dbAns.add(new DbAns(ansEntityList.get(i)));
        }
        this.dbAns = dbAns;
    }

    public long getRiskQuestionId() {
        return riskQuestionId;
    }

    public void setRiskQuestionId(long riskQuestionId) {
        this.riskQuestionId = riskQuestionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public RealmList<DbAns> getDbAns() {
        return dbAns;
    }

    public void setDbAns(RealmList<DbAns> dbAns) {
        this.dbAns = dbAns;
    }
}
