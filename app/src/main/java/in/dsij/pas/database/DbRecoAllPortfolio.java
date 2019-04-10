package in.dsij.pas.database;

import java.util.List;

import in.dsij.pas.net.respose.ResRecoAllPortfolio;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DbRecoAllPortfolio extends RealmObject {

    @PrimaryKey
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
    private RealmList<DbLogs> logs;
    private String weightage;

    public String getWeightage() {
        return weightage;
    }

    public void setWeightage(String weightage) {
        this.weightage = weightage;
    }

    public DbRecoAllPortfolio() {
    }

    public RealmList<DbLogs> getLogs() {
        return logs;
    }

    public void setLogs(RealmList<DbLogs> logs) {
        this.logs = logs;
    }

    public DbRecoAllPortfolio(ResRecoAllPortfolio.listRecoAllPortfolioEntity entity) {
        this.recommendedPortfolioId=entity.getRecommendedPortfolioId();
        this.companyCode=entity.getCompanyCode();
        this.companyName=entity.getCompanyName();
        this.ticker=entity.getTicker();
        this.quantity=entity.getQuantity();
        this.avgPrice=entity.getAvgPrice();
        this.cmp=entity.getCmp();
        this.amtInvested=entity.getAmtInvested();
        this.marketValue=entity.getMarketValue();
        this.gain=entity.getGain();
        this.absoluteReturn=entity.getAbsoluteReturn();
        this.industry=entity.getIndustry();
        this.operation=entity.getOperation();
        this.weightage=entity.getWeightage();

        List<ResRecoAllPortfolio.listRecoAllPortfolioEntity.LogsEntity> resLogsEntityList = entity.getLogs();
        RealmList<DbLogs> dbLogs= new RealmList<>();

        for (int i = 0; i < resLogsEntityList.size(); i++) {
            dbLogs.add(new DbLogs(resLogsEntityList.get(i)));
        }
        this.logs = dbLogs;
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

    public DbRecoAllPortfolio setId(long recommendedportfolioid) {
        this.recommendedPortfolioId = recommendedportfolioid;
        return this;
    }
}
