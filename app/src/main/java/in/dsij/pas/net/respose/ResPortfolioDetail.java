package in.dsij.pas.net.respose;

import java.util.List;

public class ResPortfolioDetail {

    private List<PortfilioEntity> list;

    public List<PortfilioEntity> getList() {
        return list;
    }

    public void setList(List<PortfilioEntity> list) {
        this.list = list;
    }

    public static class PortfilioEntity {
        private String networth;
        private String cashBalance;
        private String dsijPoints;
        private String todaysGain;
        private String profileImg;
        private String displayName;

        public String getNetworth() {
            return networth;
        }

        public void setNetworth(String networth) {
            this.networth = networth;
        }

        public String getCashBalance() {
            return cashBalance;
        }

        public void setCashBalance(String cashBalance) {
            this.cashBalance = cashBalance;
        }

        public String getDsijPoints() {
            return dsijPoints;
        }

        public void setDsijPoints(String dsijPoints) {
            this.dsijPoints = dsijPoints;
        }

        public String getTodaysGain() {
            return todaysGain;
        }

        public void setTodaysGain(String todaysGain) {
            this.todaysGain = todaysGain;
        }

        public String getProfileImg() {
            return profileImg;
        }

        public void setProfileImg(String profileImg) {
            this.profileImg = profileImg;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }
}
