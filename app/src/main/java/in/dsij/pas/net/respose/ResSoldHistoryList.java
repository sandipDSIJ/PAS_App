package in.dsij.pas.net.respose;

import java.util.List;

public class ResSoldHistoryList {

    private List<SoldHistoryEntity> list;

    public List<SoldHistoryEntity> getList() {
        return list;
    }

    public void setList(List<SoldHistoryEntity> list) {
        this.list = list;
    }

    public static class SoldHistoryEntity {
        private long companySoldId;
        private long portfolioId;
        private String companyCode;
        private String companyName;
        private String ticker;
        private long quantity;
        private String sellPrice;
        private String buyPrice;
        private String amtInvested;
        private String marketValue;
        private String gain;
        private String absoluteReturn;
        private String brokerage;
        private String industry;
        private String sellDate;
        private String createdDate;
        private long quantityToSell;
        private long createdBy;

        public long getCompanySoldId() {
            return companySoldId;
        }

        public void setCompanySoldId(long companySoldId) {
            this.companySoldId = companySoldId;
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

        public String getAmtInvested() {
            return amtInvested;
        }

        public void setAmtInvested(String amtInvested) {
            this.amtInvested = amtInvested;
        }

        public String getMarketValue() {
            return marketValue;
        }

        public void setMarketValue(String marketValue) {
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

        public String getBrokerage() {
            return brokerage;
        }

        public void setBrokerage(String brokerage) {
            this.brokerage = brokerage;
        }

        public String getIndustry() {
            return industry;
        }

        public void setIndustry(String industry) {
            this.industry = industry;
        }

        public String getSellDate() {
            return sellDate;
        }

        public void setSellDate(String sellDate) {
            this.sellDate = sellDate;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public long getQuantityToSell() {
            return quantityToSell;
        }

        public void setQuantityToSell(long quantityToSell) {
            this.quantityToSell = quantityToSell;
        }

        public long getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(long createdBy) {
            this.createdBy = createdBy;
        }
    }
}
