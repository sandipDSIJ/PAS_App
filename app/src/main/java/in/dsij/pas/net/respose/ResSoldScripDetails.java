package in.dsij.pas.net.respose;

import java.util.List;

public class ResSoldScripDetails {

    private List<listSoldScripEntity> list;

    public List<listSoldScripEntity> getList() {
        return list;
    }

    public void setList(List<listSoldScripEntity> list) {
        this.list = list;
    }

    public List<listSoldScripEntity> getlsSoldScripEntity() {
        return list;
    }


    public static class listSoldScripEntity {

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

        private long companySoldId;
        private long portfolioId;
        private String companyName;
        private String ticker;
        private long quantity;
        private String sellPrice;
        private String buyPrice;
        private double amtInvested;
        private double marketValue;
        private double gain;
        private double absoluteReturn;
        private String industry;
        private String sellDate;
        private String weightage;
        private long quantityToSell;
        private long riskId;
        private String riskType;
        private List<LogsEntity> logs;

        public String getSellPrice() {
            return sellPrice;
        }

        public void setSellPrice(String sellPrice) {
            this.sellPrice = sellPrice;
        }

        public String getBuyPrice() {
            return buyPrice;
        }

        public void setBuyPrice(String buyPrice) {
            this.buyPrice = buyPrice;
        }

        public String getWeightage() {
            return weightage;
        }

        public void setWeightage(String weightage) {
            this.weightage = weightage;
        }

        public long getQuantityToSell() {
            return quantityToSell;
        }

        public void setQuantityToSell(long quantityToSell) {
            this.quantityToSell = quantityToSell;
        }

        public long getRiskId() {
            return riskId;
        }

        public void setRiskId(long riskId) {
            this.riskId = riskId;
        }

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

        public double getAmtInvested() {
            return amtInvested;
        }

        public void setAmtInvested(double amtInvested) {
            this.amtInvested = amtInvested;
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
        }
    }

}
