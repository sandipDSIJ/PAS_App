package in.dsij.pas.database;

import in.dsij.pas.net.respose.ResSummeryDtls;
import io.realm.RealmObject;

public class DbSummeryDtl extends RealmObject {

    /*
    *  "valueType": "Start Value",
        "portfolioValue": 3091890,
        "sensexValue": 36139.98,
        "recoValue": 0*/

    private String valueType;
    private String portfolioValue;
    private String sensexValue;
    private String recoValue;

    public DbSummeryDtl(ResSummeryDtls.SummeryEntity entity)
    {
        this.valueType=entity.getValurType();
        this.portfolioValue=entity.getPortfolioValue();
        this.sensexValue=entity.getSensexValue();
        this.recoValue=entity.getRecoValue();
    }

    public DbSummeryDtl() {
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getPortfolioValue() {
        return portfolioValue;
    }

    public void setPortfolioValue(String portfolioValue) {
        this.portfolioValue = portfolioValue;
    }

    public String getSensexValue() {
        return sensexValue;
    }

    public void setSensexValue(String sensexValue) {
        this.sensexValue = sensexValue;
    }

    public String getRecoValue() {
        return recoValue;
    }

    public void setRecoValue(String recoValue) {
        this.recoValue = recoValue;
    }
}
