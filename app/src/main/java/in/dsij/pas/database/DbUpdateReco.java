package in.dsij.pas.database;

import java.util.List;

import in.dsij.pas.net.respose.ResSoldScripDetails;
import in.dsij.pas.net.respose.ResUpdateReco;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class DbUpdateReco extends RealmObject {

    @PrimaryKey
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

    public DbUpdateReco() {}
    public DbUpdateReco(ResUpdateReco.listUpdateRecoEntity entity) {
        this.recommendationMasterId=entity.getRecommendationMasterId();
        this.portfolioId=entity.getPortfolioId();
        this.companyName=entity.getCompanyName();
        this.companyCode=entity.getCompanyCode();
        this.avgPrice=entity.getAvgPrice();
        this.cmp=entity.getCmp();
        this.updated=entity.getUpdated();
        this.orderQueueId=entity.getOrderQueueId();
        this.indexName=entity.getIndexName();
        this.ticker=entity.getTicker();
        this.quantity=entity.getQuantity();
        this.amtInvested=entity.getAmtInvested();
        this.gain=entity.getGain();
        this.absoluteReturn=entity.getAbsoluteReturn();
        this.industry=entity.getIndustry();
        this.marketValue=entity.getMarketValue();
        this.pPrice=entity.getpPrice();
        this.operation=entity.getOperation();

    }

    public long getRecommendationMasterId() {
        return recommendationMasterId;
    }

    public void setRecommendationMasterId(long recommendationMasterId) {
        this.recommendationMasterId = recommendationMasterId;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
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

    public boolean isUpdated() {
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
