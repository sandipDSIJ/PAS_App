package in.dsij.pas.database;

import in.dsij.pas.net.respose.ResActivityLog;
import in.dsij.pas.net.respose.ResRecoAllPortfolio;
import in.dsij.pas.net.respose.ResSoldScripDetails;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DbActivityLogs extends RealmObject {

    @PrimaryKey
    private long logId;
    private String companyCode;
    private String comments;
    private String reason;
    private String createdDate;
    private long logCategoryId;

    public DbActivityLogs(ResActivityLog.ActivityLogEntity entity) {
        this.companyCode = entity.getCompanyCode();
        this.logId = entity.getLogId();
        this.createdDate=entity.getCreatedDate();
        this.reason=entity.getReason();
        this.comments=entity.getComments();
    }

    public DbActivityLogs() {
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public long getLogCategoryId() {
        return logCategoryId;
    }

    public void setLogCategoryId(long logCategoryId) {
        this.logCategoryId = logCategoryId;
    }
}
