package in.dsij.pas.net.retrofit;

import in.dsij.pas.constants.C;
import in.dsij.pas.database.DbDsijUser;
import in.dsij.pas.net.respose.ResActivityLog;
import in.dsij.pas.net.respose.ResDsijLogin;
import in.dsij.pas.net.respose.ResFormDetails;
import in.dsij.pas.net.respose.ResHoldings;
import in.dsij.pas.net.respose.ResMessage;
import in.dsij.pas.net.respose.ResMessageOld;
import in.dsij.pas.net.respose.ResOfferURL;
import in.dsij.pas.net.respose.ResRecoAllPortfolio;
import in.dsij.pas.net.respose.ResRiskAssessmentQA;
import in.dsij.pas.net.respose.ResSoldScripDetails;
import in.dsij.pas.net.respose.ResSubmitPortfolio;
import in.dsij.pas.net.respose.ResSubmitedPortFolioScrip;
import in.dsij.pas.net.respose.ResSummeryDtls;
import in.dsij.pas.net.respose.ResUpdateReco;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class CallGenerator {

    public static String currentUserName;
    public static String portfolioid;
    public static String token;
    public static long userId;

    public static String currentUserName() {
        Realm realm = Realm.getDefaultInstance();
        DbDsijUser currentUser = realm.where(DbDsijUser.class).findFirst();

        try {
            currentUserName = currentUser.getUsername();
            token = currentUser.getSessionToken();
            userId = currentUser.getUserId();
            portfolioid = currentUser.getPortfolioId(); //"3386";
        } catch (Exception e) {
            currentUserName = null;
        } finally {
            realm.close();
        }
        return currentUserName;
    }

    public static Call<ResDsijLogin> loginWithPassword(String userName, String password, String gcmId) {
        TNService tnService = ServiceGenerator.createServiceLocal(TNService.class);
        return tnService.loginWithPassword(userName, password, C.device.DEVICE_SERIAL, C.device.VERSION_CODE, gcmId, C.device.APP_ID);
    }

    public static Call<ResDsijLogin> loginWithToken(String userName, String token) {
        TNService dService = ServiceGenerator.createServiceLocal(TNService.class);
        return dService.loginWithToken(userName, token, C.device.DEVICE_SERIAL, C.device.VERSION_CODE, C.device.APP_ID);
    }

    public static Call<ResDsijLogin> loginFacebook(String socialUserId, String socialAccessToken, String socialUserName, String socialUserLastName, String avatar, String email, String gcmId) {
        TNService dService = ServiceGenerator.createService(TNService.class);
        // logRequest("loginFacebook",header,body);
        return dService.login(socialUserName, socialUserLastName, email, "", C.device.DEVICE_SERIAL, C.device.VERSION_CODE, socialUserId, C.device.APP_ID, gcmId);
    }

    public static Call<ResDsijLogin> loginGoogle(String socialUserId, String socialAccessToken, String socialUserName, String socialUserLastName, String avatar, String email, String gcmId) {

        TNService tNService = ServiceGenerator.createServiceLocal(TNService.class);
        //  logRequest("loginGoogle",header,body);
        return tNService.login(socialUserName, socialUserLastName, email, "", C.device.DEVICE_SERIAL, C.device.VERSION_CODE, socialUserId, C.device.APP_ID, gcmId);
    }

    public static Call<ResMessageOld> logout() {
        TNService dService = ServiceGenerator.createServiceLocal(TNService.class);
        return dService.logout(currentUserName(), token, C.device.APP_ID, C.device.DEVICE_SERIAL, C.device.VERSION_CODE);
    }

    public static Call<ResMessage> signUp(String firstName, String lastName, String email, String phone) {
        TNService dService = ServiceGenerator.createServiceLocal(TNService.class);
        return dService.signUp(firstName, lastName, email, phone, C.device.DEVICE_SERIAL, C.device.VERSION_CODE, C.device.APP_ID);
    }

    public static Call<ResMessage> changePassword(String oldPassword, String newPassword) {
        TNService dService = ServiceGenerator.createServiceLocal(TNService.class);
        // TODO: 9/12/2017 Change after implementing device serial and version code
        return dService.changePassword(currentUserName(), token, oldPassword, newPassword, C.device.APP_ID);
    }

    public static Call<ResMessage> resetPassword(String userName) {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.resetPassword(userName, C.device.productid, C.device.APP_ID);
    }

    public static Call<ResOfferURL> offerURL() {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.offerURL(currentUserName(), token);
    }

    public static Call<ResSoldScripDetails> getSoldScripDetails() {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.getSoldScripDetails(currentUserName(), token, portfolioid, C.device.DEVICE_SERIAL, C.device.VERSION_CODE);
    }

    public static Call<ResUpdateReco> getRecommendedPortfolioScrip() {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.getRecommendedPortfolioScrip(currentUserName(), token, portfolioid, C.device.DEVICE_SERIAL, C.device.VERSION_CODE);
    }


    public static Call<ResRecoAllPortfolio> getAllRecommendedPortfolioScrip() {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.getAllRecommendedPortfolioScrip(currentUserName(), token, portfolioid, C.device.DEVICE_SERIAL, C.device.VERSION_CODE);
    }

    public static Call<ResActivityLog> getActivityLog() {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.getActivityLog(currentUserName(), token, portfolioid, C.device.DEVICE_SERIAL, C.device.VERSION_CODE);
    }

    public static Call<ResMessage> updateRecommededScrip(String companyCode, String companyName, long quantity, String avgPrice, long orderQueueId, String operation) {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.updateRecommededScrip(currentUserName(), token, portfolioid, userId, companyCode, companyName, quantity, avgPrice, orderQueueId, operation, C.device.DEVICE_SERIAL, C.device.VERSION_CODE);
    }

    public static Call<ResRiskAssessmentQA> getRiskAssessmentQuestionAnswer() {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.getRiskAssessmentQuestionAnswer(currentUserName(), token, portfolioid, C.device.DEVICE_SERIAL, C.device.VERSION_CODE);
    }

    public static Call<ResMessage> submitRiskQuestionAnswer(String answer) {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.submitRiskQuestionAnswer(currentUserName(), token, portfolioid, userId, answer, C.device.DEVICE_SERIAL, C.device.VERSION_CODE);
    }

    public static Call<ResSubmitedPortFolioScrip> getSubmitedPortFolioScrip() {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.getSubmitedPortFolioScrip(currentUserName(), token, userId, portfolioid, C.device.DEVICE_SERIAL, C.device.VERSION_CODE);
    }

    public static Call<ResMessage> addORWithdrawCash(String openingcash, int isaddcash) {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.addORWithdrawCash(currentUserName(), token, userId, portfolioid, openingcash, isaddcash, C.device.DEVICE_SERIAL, C.device.VERSION_CODE);
    }

    public static Call<ResSubmitPortfolio> submitPortfolioName(String portfolioName) {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.submitPortfolioName(currentUserName(), token, userId, C.device.DEVICE_SERIAL, C.device.VERSION_CODE);
    }

    public static Call<ResFormDetails> getFormDetails() {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.getFormDetails(currentUserName(), token, userId, portfolioid, C.device.DEVICE_SERIAL, C.device.VERSION_CODE);
    }

    public static Call<ResMessage> submitPortfolioforReview() {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.submitPortfolioforReview(currentUserName(), token, userId, portfolioid, C.device.DEVICE_SERIAL, C.device.VERSION_CODE);
    }

    public static Call<ResSummeryDtls> getSummaryDetails() {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.getSummeryDtls(currentUserName(), token, portfolioid, C.device.DEVICE_SERIAL, C.device.VERSION_CODE);
    }

    public static Call<ResMessage> getPasTabName() {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.getPasTabName(currentUserName(), token, userId, C.device.DEVICE_SERIAL, C.device.VERSION_CODE);
    }

    public static Call<ResponseBody> postFeedback(String email, String phone, String subject, String description) {
        TNService TNService = ServiceGenerator.createServiceLocal(TNService.class);
        return TNService.submitFeedback(currentUserName(), token, email, phone, subject, description);
    }

    public static Call<ResMessage> postLowRating(int rating, String description) {
        TNService dService = ServiceGenerator.createService(TNService.class);
        return dService.postLowRating(currentUserName(), token, C.device.APP_ID, C.device.VERSION_NAME, C.device.VERSION_CODE, rating, description, C.device.DEVICE_SERIAL);
    }
}