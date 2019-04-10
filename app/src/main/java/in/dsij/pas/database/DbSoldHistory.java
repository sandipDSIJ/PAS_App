package in.dsij.pas.database;

import in.dsij.pas.net.respose.ResSoldHistoryList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class DbSoldHistory extends RealmObject {

    @PrimaryKey
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



    public DbSoldHistory() {}
    public DbSoldHistory(ResSoldHistoryList.SoldHistoryEntity soldHistoryEntity) {
        this.companySoldId=soldHistoryEntity.getCompanySoldId();
        this.portfolioId=soldHistoryEntity.getPortfolioId();
        this.companyCode=soldHistoryEntity.getCompanyCode();
        this.companyName=soldHistoryEntity.getCompanyName();
        this.ticker=soldHistoryEntity.getTicker();
        this.quantity=soldHistoryEntity.getQuantity();
        this.sellPrice=soldHistoryEntity.getSellPrice();
        this.buyPrice=soldHistoryEntity.getBuyPrice();
        this.amtInvested=soldHistoryEntity.getAmtInvested();
        this.marketValue=soldHistoryEntity.getMarketValue();
        this.gain=soldHistoryEntity.getGain();
        this.absoluteReturn=soldHistoryEntity.getAbsoluteReturn();
        this.brokerage=soldHistoryEntity.getBrokerage();
        this.industry=soldHistoryEntity.getIndustry();
        this.sellDate=soldHistoryEntity.getSellDate();
        this.createdDate=soldHistoryEntity.getCreatedDate();
        this.quantityToSell=soldHistoryEntity.getQuantityToSell();
        this.createdBy=soldHistoryEntity.getCreatedBy();

    }

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

    public DbSoldHistory setId(long companySoldId) {
        this.companySoldId = companySoldId;
        return this;
    }

}
