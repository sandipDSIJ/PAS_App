package in.dsij.pas.database;

import java.util.List;

import in.dsij.pas.net.respose.ResRecoAllPortfolio;
import in.dsij.pas.net.respose.ResSubmitedPortFolioScrip;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DbSubmitedPortFolioScrip extends RealmObject {

    @PrimaryKey
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
    private RealmList<DbLogs> logs;

    public DbSubmitedPortFolioScrip() {
    }

    public RealmList<DbLogs> getLogs() {
        return logs;
    }

    public void setLogs(RealmList<DbLogs> logs) {
        this.logs = logs;
    }

    public DbSubmitedPortFolioScrip(ResSubmitedPortFolioScrip.SubmitedPortFolioScrip entity) {
        this.openingPortfolioId=entity.getOpeningPortfolioId();
        this.portfolioId=entity.getOpeningPortfolioId();
        this.companyCode=entity.getCompanyCode();
        this.companyName=entity.getCompanyName();
        this.purchasePrice=entity.getPurchasePrice();
        this.ticker=entity.getTicker();
        this.quantity=entity.getQuantity();
        this.cmp=entity.getCmp();
        this.amtInvested=entity.getAmtInvested();
        this.marketValue=entity.getMarketValue();
        this.gain=entity.getGain();
        this.absoluteReturn=entity.getAbsoluteReturn();
        this.industry=entity.getIndustry();

        List<ResSubmitedPortFolioScrip.SubmitedPortFolioScrip.LogsEntity> resLogsEntityList = entity.getLogs();
        RealmList<DbLogs> dbLogs= new RealmList<>();

        for (int i = 0; i < resLogsEntityList.size(); i++) {
            dbLogs.add(new DbLogs(resLogsEntityList.get(i)));
        }
        this.logs = dbLogs;
    }

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

}
