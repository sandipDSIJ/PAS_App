package in.dsij.pas.database;

import in.dsij.pas.net.respose.ResRecoAllPortfolio;
import in.dsij.pas.net.respose.ResSoldScripDetails;
import in.dsij.pas.net.respose.ResSubmitedPortFolioScrip;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DbLogs extends RealmObject {

    @PrimaryKey
    private long logId;
    private long companySoldId;
    private long openingPortfolioId;
    private long recommendedPortfolioId;
    private String companyCode;
    private String logDate;
    private String comment;
    private  String reason;

    public DbLogs(ResSoldScripDetails.listSoldScripEntity.LogsEntity logs) {
        this.companyCode = logs.getCompanyCode();
        this.logId = logs.getLogId();
        this.logDate=logs.getLogdate();
        this.companySoldId=logs.getCompanySoldId();
        this.openingPortfolioId=logs.getOpeningPortfolioId();
        this.recommendedPortfolioId=logs.getRecommendedPortfolioId();
        this.reason=logs.getReason();
        this.comment=logs.getComment();

    }

    public DbLogs(ResRecoAllPortfolio.listRecoAllPortfolioEntity.LogsEntity logsEntity) {
        this.companyCode = logsEntity.getCompanyCode();
        this.logId = logsEntity.getLogId();
        this.logDate=logsEntity.getLogDate();
        this.companySoldId=logsEntity.getCompanySoldId();
        this.openingPortfolioId=logsEntity.getOpeningPortfolioId();
        this.recommendedPortfolioId=logsEntity.getRecommendedPortfolioId();
        this.reason=logsEntity.getReason();
        this.comment=logsEntity.getComment();
    }

    public DbLogs(ResSubmitedPortFolioScrip.SubmitedPortFolioScrip.LogsEntity logsEntity) {
        this.companyCode = logsEntity.getCompanyCode();
        this.logId = logsEntity.getLogId();
        this.logDate=logsEntity.getLogDate();
        this.companySoldId=logsEntity.getCompanySoldId();
        this.openingPortfolioId=logsEntity.getOpeningPortfolioId();
        this.recommendedPortfolioId=logsEntity.getRecommendedPortfolioId();
        this.reason=logsEntity.getReason();
        this.comment=logsEntity.getComment();
    }

    public long getCompanySoldId() {
        return companySoldId;
    }

    public void setCompanySoldId(long companySoldId) {
        this.companySoldId = companySoldId;
    }

    public long getOpeningPortfolioId() {
        return openingPortfolioId;
    }

    public void setOpeningPortfolioId(long openingPortfolioId) {
        this.openingPortfolioId = openingPortfolioId;
    }

    public long getRecommendedPortfolioId() {
        return recommendedPortfolioId;
    }

    public void setRecommendedPortfolioId(long recommendedPortfolioId) {
        this.recommendedPortfolioId = recommendedPortfolioId;
    }

    public String getLogdate() {
        return logDate;
    }

    public void setLogdate(String logdate) {
        this.logDate = logdate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public DbLogs() {
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

}
