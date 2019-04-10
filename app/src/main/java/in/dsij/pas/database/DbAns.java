package in.dsij.pas.database;

import in.dsij.pas.net.respose.ResRecoAllPortfolio;
import in.dsij.pas.net.respose.ResRiskAssessmentQA;
import in.dsij.pas.net.respose.ResSoldScripDetails;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DbAns extends RealmObject {

    @PrimaryKey
    private long riskAnswerId;
    private String answer;
    private long answerPoints;
    private long riskQuestionId;
    private String question;

    public DbAns(ResRiskAssessmentQA.listQuestionsEntity.AnsEntity ansEntity) {
        this.riskAnswerId = ansEntity.getRiskAnswerId();
        this.answer = ansEntity.getAnswer();
        this.answerPoints=ansEntity.getAnswerPoints();
        this.riskQuestionId=ansEntity.getRiskQuestionId();
        this.question=ansEntity.getQuestion();
    }
    public DbAns() {}
    public long getRiskAnswerId() {
        return riskAnswerId;
    }

    public void setRiskAnswerId(long riskAnswerId) {
        this.riskAnswerId = riskAnswerId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getAnswerPoints() {
        return answerPoints;
    }

    public void setAnswerPoints(long answerPoints) {
        this.answerPoints = answerPoints;
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
}
