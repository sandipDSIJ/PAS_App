package in.dsij.pas.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vikas on 9/13/2017.
 */

public class DbDsijUser extends RealmObject {

   /* public static final String SESSION_TOKEN = "sessionToken";
    public static final String USERNAME = "username";
    public static final String DISPLAY_NAME = "displayName";
    public static final String EMAIL = "email";*/

    @PrimaryKey
    private String userName;
    private String sessionToken;
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

    public DbDsijUser setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
        return this;
    }

    public String getUsername() {
        return userName;
    }

    public DbDsijUser setUsername(String username) {
        this.userName = username;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public DbDsijUser setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public DbDsijUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}
