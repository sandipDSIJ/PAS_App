package in.dsij.pas.net.respose;

import java.util.List;

public class ResProfileDetails {


    /*
    * "userName": "gayatril",
"token": "f7944f9f305a82005c39cabd6d952f0309a7d6e395c71d82be68c26efd192ca3",
"userId": 542596,
"firstName": "Gayatri",
"lastName": "Lahoti",
"email": "gayatril@dsij.in",
"country": "India",
"state": null,
"city": "Pune",
"cellNumber": "8237301617",
"prefix": "Ms.",
"dateOfBirth": null,
"address": null,
"zipcode": null,
"gender": null,
"messageDelivery": null,
"rewardpoints": 20*/
    private List<ProfiledetailsEntity> list;

    public List<ProfiledetailsEntity> getList() {
        return list;
    }

    public void setList(List<ProfiledetailsEntity> list) {
        this.list = list;
    }

    public static class ProfiledetailsEntity {
        private long userId;
        private String userName;
        private String firstName;
        private String lastName;
        private String email;
        private String country;
        private String state;
        private String city;
        private String cellNumber;
        private String prefix;
        private String dateOfBirth;
        private String address;
        private String zipcode;
        private String gender;
        private String messageDelivery;
        private String rewardpoints;

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCellNumber() {
            return cellNumber;
        }

        public void setCellNumber(String cellNumber) {
            this.cellNumber = cellNumber;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getZipcode() {
            return zipcode;
        }

        public void setZipcode(String zipcode) {
            this.zipcode = zipcode;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getMessageDelivery() {
            return messageDelivery;
        }

        public void setMessageDelivery(String messageDelivery) {
            this.messageDelivery = messageDelivery;
        }

        public String getRewardpoints() {
            return rewardpoints;
        }

        public void setRewardpoints(String rewardpoints) {
            this.rewardpoints = rewardpoints;
        }
    }
}
