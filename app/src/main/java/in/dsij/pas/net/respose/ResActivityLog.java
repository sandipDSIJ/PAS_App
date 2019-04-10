package in.dsij.pas.net.respose;

import java.util.List;

public class ResActivityLog {

    private List<ActivityLogEntity> list;

    public List<ActivityLogEntity> getList() {
        return list;
    }

    public void setList(List<ActivityLogEntity> list) {
        this.list = list;
    }

    public static class ActivityLogEntity {
        private long logId;
        private String companyCode;
        private String comments;
        private String reason;
        private String createdDate;
        private long logCategoryId;

        public long getLogId() {
            return logId;
        }

        public void setLogId(long logId) {
            logId = logId;
        }

        public String getCompanyCode() {
            return companyCode;
        }

        public void setCompanyCode(String companyCode) {
            companyCode = companyCode;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            comments = comments;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            reason = reason;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            createdDate = createdDate;
        }

        public long getLogCategoryId() {
            return logCategoryId;
        }

        public void setLogCategoryId(long logCategoryId) {
            logCategoryId = logCategoryId;
        }
    }
}
