package in.dsij.pas.database;

import java.util.List;

import in.dsij.pas.net.respose.ResHoldings;
import in.dsij.pas.net.respose.ResSoldScripDetails;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class DbSoldScripDetails extends RealmObject {

    @PrimaryKey
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

    private RealmList<DbLogs> logs;

    public RealmList<DbLogs> getLogs() {
        return logs;
    }

    public void setLogs(RealmList<DbLogs> logs) {
        this.logs = logs;
    }

    public DbSoldScripDetails() {}
    public DbSoldScripDetails(ResSoldScripDetails.listSoldScripEntity soldScrip) {
        this.companySoldId=soldScrip.getCompanySoldId();
        this.portfolioId=soldScrip.getPortfolioId();
        this.companyName=soldScrip.getCompanyName();
        this.ticker=soldScrip.getTicker();
        this.quantity=soldScrip.getQuantity();
        this.sellPrice=soldScrip.getSellPrice();
        this.buyPrice=soldScrip.getBuyPrice();
        this.amtInvested=soldScrip.getAmtInvested();
        this.gain=soldScrip.getGain();
        this.absoluteReturn=soldScrip.getAbsoluteReturn();
        this.industry=soldScrip.getIndustry();
        this.sellDate=soldScrip.getSellDate();
        this.weightage=soldScrip.getWeightage();
        this.quantityToSell=soldScrip.getQuantityToSell();
        this.riskId=soldScrip.getRiskId();
        this.riskType=soldScrip.getRiskType();
        this.marketValue=soldScrip.getMarketValue();

        List<ResSoldScripDetails.listSoldScripEntity.LogsEntity> resLogs= soldScrip.getLogs();

        RealmList<DbLogs> dbLogs= new RealmList<>();

        for (int i = 0; i < resLogs.size(); i++) {
            dbLogs.add(new DbLogs(resLogs.get(i)));
        }
        this.logs = dbLogs;
    }

    public long getCompanySoldId() {
        return companySoldId;
    }

    public void setCompanySoldId(long companySoldId) {
        this.companySoldId = companySoldId;
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

    public String getSellDate() {
        return sellDate;
    }

    public void setSellDate(String sellDate) {
        this.sellDate = sellDate;
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

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
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

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
    }


}
