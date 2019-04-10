package in.dsij.pas.net.respose;

import java.util.List;

public class ResHoldings {

    private List<listHoldingsEntity> list;

    public List<listHoldingsEntity> getList() {
        return list;
    }

    public void setList(List<listHoldingsEntity> list) {
        this.list = list;
    }

    public List<listHoldingsEntity> getlsHoldingEntity() {
        return list;
    }

    public void setlsHoldingEntity(List<listHoldingsEntity> lsHoldingEntity) {
        this.list = lsHoldingEntity;
    }

    /*
    "portfolioId": 3386,
    "companySoldId": 203535,
    "companyName": "Fineotex Chemical Ltd.",
    "ticker": "FineotexChem",
    "quantity": 380,
    "sellPrice": 78.5,
    "buyPrice": 65.75,
    "amtInvested": 24985,
    "marketValue": 29830,
    "gain": 4845,
    "absoluteReturn": 19.3916,
    "industry": "Chemical",
    "sellDate": "4/10/2018 10:30:04 AM",
    "quantityToSell": 0,
    "riskId": 2,
    "riskType": "High",
    "oqid": 980941
    * */

    public static class listHoldingsEntity {

        private long portfolioId;
        private long companySoldId;
        private String companyName;
        private long quantity;
        private String ticker;
        private String avgPrice;
        private String cmp;
        private String amtInvested;
        private double marketValue;
        private String gain;
        private String absoluteReturn;
        private String industry;
        private String riskType;
        private String sellDate;
        private long oqid;
        private List<LogsEntity> logs;

        public long getCompanySoldId() {
            return companySoldId;
        }

        public void setCompanySoldId(long companySoldId) {
            this.companySoldId = companySoldId;
        }

        public String getIndustry() {
            return industry;
        }

        public void setIndustry(String industry) {
            this.industry = industry;
        }

        public String getRiskType() {
            return riskType;
        }

        public void setRiskType(String riskType) {
            this.riskType = riskType;
        }

        public String getSellDate() {
            return sellDate;
        }

        public void setSellDate(String sellDate) {
            this.sellDate = sellDate;
        }

        public long getOqid() {
            return oqid;
        }

        public void setOqid(long oqid) {
            this.oqid = oqid;
        }

        public double getMarketValue() {
            return marketValue;
        }

        public void setMarketValue(double marketValue) {
            this.marketValue = marketValue;
        }

        public long getPortfolioId() {
            return portfolioId;
        }

        public void setPortfolioId(long portfolioId) {
            this.portfolioId = portfolioId;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
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

        public String getAmtInvested() {
            return amtInvested;
        }

        public void setAmtInvested(String amtInvested) {
            this.amtInvested = amtInvested;
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
            private String logdate;
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

            public String getLogdate() {
                return logdate;
            }

            public void setLogdate(String logdate) {
                this.logdate = logdate;
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
