package in.dsij.pas.net.respose;

import java.util.List;

public class ResRecoAllPortfolio {

    private String Available;
    private String PercentWeight;

    public String getAvailable() {
        return Available;
    }

    public void setAvailable(String available) {
        Available = available;
    }

    public String getPercentWeight() {
        return PercentWeight;
    }

    public void setPercentWeight(String percentWeight) {
        PercentWeight = percentWeight;
    }

    private List<listRecoAllPortfolioEntity> list;

    public List<listRecoAllPortfolioEntity> getList() {
        return list;
    }

    public void setList(List<listRecoAllPortfolioEntity> list) {
        this.list = list;
    }

    public List<listRecoAllPortfolioEntity> getlsSoldScripEntity() {
        return list;
    }

    public void setlsHoldingEntity(List<listRecoAllPortfolioEntity> lsSoldScrip) {
        this.list = lsSoldScrip;
    }

    public static class listRecoAllPortfolioEntity {

        /*
        "recommendedPortfolioId": 117865,
        "companyName": "Bank of Baroda",
        "companyCode": 14030010,
        "ticker": "BankofBaroda",
        "quantity": 1075,
        "avgPrice": 137.52,
        "cmp": 119.6,
        "amtInvested": 147834,
        "marketValue": 128570,
        "gain": -19264,
        "absoluteReturn": -13.0308318789994,
        "industry": "Finance",
        "operation": null
        * */

        private long recommendedPortfolioId;
        private String companyName;
        private String companyCode;
        private String ticker;
        private long quantity;
        private String avgPrice;
        private String cmp;
        private double amtInvested;
        private double marketValue;
        private double gain;
        private double absoluteReturn;
        private String industry;
        private String operation;
        private List<LogsEntity> logs;
        private String weightage;

        public String getWeightage() {
            return weightage;
        }

        public void setWeightage(String weightage) {
            this.weightage = weightage;
        }

        public long getRecommendedPortfolioId() {
            return recommendedPortfolioId;
        }

        public void setRecommendedPortfolioId(long recommendedPortfolioId) {
            this.recommendedPortfolioId = recommendedPortfolioId;
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

        public String getAvgPrice() {
            return avgPrice;
        }

        public void setAvgPrice(String avgPrice) {
            this.avgPrice = avgPrice;
        }

        public String getCmp() {
            return cmp;
        }

        public void setCmp(String cmp) {
            this.cmp = cmp;
        }

        public double getAmtInvested() {
            return amtInvested;
        }

        public void setAmtInvested(double amtInvested) {
            this.amtInvested = amtInvested;
        }

        public double getMarketValue() {
            return marketValue;
        }

        public void setMarketValue(double marketValue) {
            this.marketValue = marketValue;
        }

        public double getGain() {
            return gain;
        }

        public void setGain(double gain) {
            this.gain = gain;
        }

        public double getAbsoluteReturn() {
            return absoluteReturn;
        }

        public void setAbsoluteReturn(double absoluteReturn) {
            this.absoluteReturn = absoluteReturn;
        }

        public String getIndustry() {
            return industry;
        }

        public void setIndustry(String industry) {
            this.industry = industry;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
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
