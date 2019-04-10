package in.dsij.pas.database;

import java.util.List;

import in.dsij.pas.net.respose.ResHoldings;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class DbHolding extends RealmObject {

    @PrimaryKey
    private long portfolioDetailId;
    private long portfolioId;
    private String companyCode;
    private String companyName;
    private String ticker;
    private long quantity;
    private String avgPrice;
    private String cmp;
    private String amtInvested;
    private String gain;
    private String absoluteReturn;
    private String industry;
    private double marketValue;
    private RealmList<DbLogs> logs;

    public RealmList<DbLogs> getLogs() {
        return logs;
    }

    public void setLogs(RealmList<DbLogs> logs) {
        this.logs = logs;
    }

    public DbHolding() {}
    public DbHolding(ResHoldings.listHoldingsEntity holdings) {
       // this.portfolioDetailId=holdings.getPortfolioDetailId();
        this.portfolioId=holdings.getPortfolioId();
     //   this.companyCode=holdings.getCompanyCode();
        this.companyName=holdings.getCompanyName();
        this.ticker=holdings.getTicker();
        this.quantity=holdings.getQuantity();
        this.avgPrice=holdings.getAvgPrice();
        this.cmp=holdings.getCmp();
        this.amtInvested=holdings.getAmtInvested();
        this.gain=holdings.getGain();
        this.absoluteReturn=holdings.getAbsoluteReturn();
        this.marketValue=holdings.getMarketValue();

        List<ResHoldings.listHoldingsEntity.LogsEntity> resLogs= holdings.getLogs();

        RealmList<DbLogs> dbLogs= new RealmList<>();

        for (int i = 0; i < resLogs.size(); i++) {
           // dbLogs.add(new DbLogs(resLogs.get(i)));
        }
        this.logs = dbLogs;
    }

    public long getPortfolioDetailId() {
        return portfolioDetailId;
    }

    public void setPortfolioDetailId(long portfolioDetailId) {
        this.portfolioDetailId = portfolioDetailId;
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



    public long getId() {
        return portfolioDetailId;
    }

    public DbHolding setId(long portfolioDetailId) {
        this.portfolioDetailId = portfolioDetailId;
        return this;
    }

}
