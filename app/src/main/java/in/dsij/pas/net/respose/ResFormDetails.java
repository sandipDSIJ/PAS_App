package in.dsij.pas.net.respose;

import java.util.List;

public class ResFormDetails {

    private List<FormDetailsEntity> list;

    public List<FormDetailsEntity> getList() {
        return list;
    }

    public void setList(List<FormDetailsEntity> list) {
        this.list = list;
    }

    public static class FormDetailsEntity {

        private String pageBody;
        private String footerAddress;

        public String getPageBody() {
            return pageBody;
        }

        public void setPageBody(String pageBody) {
            this.pageBody = pageBody;
        }

        public String getFooterAddress() {
            return footerAddress;
        }

        public void setFooterAddress(String footerAddress) {
            this.footerAddress = footerAddress;
        }
    }
}
