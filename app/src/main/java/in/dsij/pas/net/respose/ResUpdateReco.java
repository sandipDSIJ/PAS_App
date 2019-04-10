package in.dsij.pas.net.respose;

import java.util.List;

public class ResUpdateReco {

    private List<listUpdateRecoEntity> list;

    public List<listUpdateRecoEntity> getList() {
        return list;
    }

    public void setList(List<listUpdateRecoEntity> list) {
        this.list = list;
    }

    public static class listUpdateRecoEntity {

        /*
"RecommendedPortfolioId": 0,
"RecommendationMasterId": 1377,
"PortfolioId": 3386,
"CompanyCode": 10610046,
"CompanyName": "GNA Axles Ltd.",
"Ticker": "GNA Axles",
"Quantity": 200,
"AvgPrice": 319.75,
"CMP": 340.75,
"AmtInvested": 63950,
"MarketValue": 68150,
"Gain": 4200,
"AbsoluteReturn": 6.5676309616888195,
"Industry": "Auto Ancillaries",
"Updated": false,
"OrderQueueId": 1047699,
"IndexName": "Others",
"Column1": "2019-03-11T00:00:00",
"Column2": "2019-03-15T00:00:00",
"Column3": "2019-03-08T00:00:00",
"PPrice": 319.75,
"Operation": "BUY"*/

        private long recommendationMasterId;
        private long portfolioId;
        private String companyCode;
        private String companyName;
        private String ticker;
        private long quantity;
        private String avgPrice;
        private String cmp;
        private double amtInvested;
        private double marketValue;
        private double gain;
        private double absoluteReturn;
        private String industry;
        private boolean updated;
        private long orderQueueId;
        private String indexName;
        private String column1;
        private String column2;
        private String column3;
        private String pPrice;
        private String operation;

        public long getRecommendationMasterId() {
            return recommendationMasterId;
        }

        public void setRecommendationMasterId(long recommendationMasterId) {
            this.recommendationMasterId = recommendationMasterId;
        }

        public long getPortfolioId() {
            return portfolioId;
        }

        public void setPortfolioId(long portfolioId) {
            this.portfolioId = portfolioId;
        }

        public String getCompanyCode() {
            return companyCode;
        }

        public void setCompanyCode(String companyCode) {
            this.companyCode = companyCode;
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

        public boolean getUpdated() {
            return updated;
        }

        public void setUpdated(boolean updated) {
            this.updated = updated;
        }

        public long getOrderQueueId() {
            return orderQueueId;
        }

        public void setOrderQueueId(long orderQueueId) {
            this.orderQueueId = orderQueueId;
        }

        public String getIndexName() {
            return indexName;
        }

        public void setIndexName(String indexName) {
            this.indexName = indexName;
        }

        public String getColumn1() {
            return column1;
        }

        public void setColumn1(String column1) {
            this.column1 = column1;
        }

        public String getColumn2() {
            return column2;
        }

        public void setColumn2(String column2) {
            this.column2 = column2;
        }

        public String getColumn3() {
            return column3;
        }

        public void setColumn3(String column3) {
            this.column3 = column3;
        }

        public String getpPrice() {
            return pPrice;
        }

        public void setpPrice(String pPrice) {
            this.pPrice = pPrice;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }
    }
}
