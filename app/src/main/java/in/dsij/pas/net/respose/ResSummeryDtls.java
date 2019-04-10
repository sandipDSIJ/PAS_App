package in.dsij.pas.net.respose;

import java.util.List;

public class ResSummeryDtls {

    private List<SummeryEntity> list;

    public List<SummeryEntity> getList() {
        return list;
    }

    public void setList(List<SummeryEntity> list) {
        this.list = list;
    }

    public static class SummeryEntity {
        private String valueType;
        private String portfolioValue;
        private String sensexValue;
        private String recoValue;

        public String getValurType() {
            return valueType;
        }

        public void setValurType(String valurType) {
            this.valueType = valurType;
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
}
