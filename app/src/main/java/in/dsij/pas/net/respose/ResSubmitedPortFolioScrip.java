package in.dsij.pas.net.respose;

import java.util.List;

public class ResSubmitedPortFolioScrip {

    private List<SubmitedPortFolioScrip> list;

    public List<SubmitedPortFolioScrip> getList() {
        return list;
    }

    public void setList(List<SubmitedPortFolioScrip> list) {
        this.list = list;
    }

    public List<SubmitedPortFolioScrip> getlsSoldScripEntity() {
        return list;
    }

    public void setlsHoldingEntity(List<SubmitedPortFolioScrip> lsSoldScrip) {
        this.list = lsSoldScrip;
    }

    public static class SubmitedPortFolioScrip {
          /*"openingPortfolioId": 44755,
            "portfolioId": 3386,
            "companyCode": 16690562,
            "companyName": "BLS International Services Ltd.",
            "ticker": "BLS Intl",
            "quantity": 300,
            "cmp": 114.35,
            "purchasePrice": 0,
            "amtInvested": 73110,
            "marketValue": 34305,
            "gain": -38805,
            "absoluteReturn": -53.08,
            "industry": "Miscellaneous"*/

        private long openingPortfolioId;
        private long portfolioId;
        private String companyName;
        private String companyCode;
        private String ticker;
        private long quantity;
        private String cmp;
        private String purchasePrice;
        private String amtInvested;
        private double marketValue;
        private String gain;
        private String absoluteReturn;
        private String industry;
        private List<LogsEntity> logs;

        public long getOpeningPortfolioId() {
            return openingPortfolioId;
        }

        public void setOpeningPortfolioId(long openingPortfolioId) {
            this.openingPortfolioId = openingPortfolioId;
        }

        public long getPortfolioId() {
            return portfolioId;
        }

        public void setPortfolioId(long portfolioId) {
            this.portfolioId = portfolioId;
        }

        public String getPurchasePrice() {
            return purchasePrice;
        }

        public void setPurchasePrice(String purchasePrice) {
            this.purchasePrice = purchasePrice;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getCompanyCode() {
            return companyCode;
        }

        public void setCompanyCode(String companyCode) {
            this.companyCode = companyCode;
        }

        public String getTicker() {
            return ticker;
        }

        public void setTicker(String ticker) {
            this.ticker = ticker;
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }

        public String getCmp() {
            return cmp;
        }

        public void setCmp(String cmp) {
            this.cmp = cmp;
        }

        public String getAmtInvested() {
            return amtInvested;
        }

        public void setAmtInvested(String amtInvested) {
            this.amtInvested = amtInvested;
        }

        public double getMarketValue() {
            return marketValue;
        }

        public void setMarketValue(double marketValue) {
            this.marketValue = marketValue;
        }

        public String getGain() {
            return gain;
        }

        public void setGain(String gain) {
            this.gain = gain;
        }

        public String getAbsoluteReturn() {
            return absoluteReturn;
        }

        public void setAbsoluteReturn(String absoluteReturn) {
            this.absoluteReturn = absoluteReturn;
        }

        public String getIndustry() {
            return industry;
        }

        public void setIndustry(String industry) {
            this.industry = industry;
        }

        public List<LogsEntity> getLogs() {
            return logs;
        }

        public void setLogs(List<LogsEntity> Logs) {
            this.logs = logs;
        }

        public static class LogsEntity{
            private long logId;
            private long companySoldId;
            private long openingPortfolioId;
            private long recommendedPortfolioId;
            private String companyCode;
            private String logDate;
            private String comment;
            private  String reason;

            public long getLogId() {
                return logId;
            }

            public void setLogId(long logId) {
                this.logId = logId;
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

            public String getCompanyCode() {
                return companyCode;
            }

            public void setCompanyCode(String companyCode) {
                this.companyCode = companyCode;
            }

            public String getLogDate() {
                return logDate;
            }

            public void setLogDate(String logDate) {
                this.logDate = logDate;
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
        }
    }

}
