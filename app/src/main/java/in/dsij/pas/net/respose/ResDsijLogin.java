package in.dsij.pas.net.respose;

/**
 * Created by Vikas on 9/12/2017.
 */

public class ResDsijLogin {

    /**
     * "code": 201,
     * "message": "Update available",
     * "sessionToken": "9d6682b654036f7fe67a03b2c8a3fd626684f07aeda3669289d012a5d5a6cb98",
     * "userName": "gayatril",
     * "displayName": "Gayatri Lahoti",
     * "email": "gayatril@dsij.in",
     * "profileImg": "https://www.dsij.in/productattachment/UserProfilePhoto/Profile.jpg?time=11/22/2018 11:53:10 AM",
     * "portfolioId": "156062",
     * "userId": 542596
     */

    private String sessionToken;
    private String userName;
    private String displayName;
    private String email;
    private String profileImg;
    private String portfolioId;
    private long userId;
    private String code;
    private String message;

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
