package in.dsij.pas.net.retrofit;

import in.dsij.pas.constants.C;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TNService {


    @FormUrlEncoded
    @POST("/desktopmodules/services/api/SMGWebAPI/SignUpDsijSocial")
    Call<ResDsijLogin> login(
            @Field("firstName") String firstName, @Field("lastName") String lastName,
            @Field("email") String email, @Field("phone") String phone,
            @Field("deviceId") String deviceNo, @Field("versionNo") String versionNo,
            @Field("socialId") String socialId, @Field("appId") long appId,
            @Field("gcmId") String gcmId
    );

    @FormUrlEncoded
    @POST("/desktopmodules/services/api/MobileApp/AppLogout_DSIJ")
    Call<ResMessageOld> logout(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("AppId") int appId,
            @Field("DeviceId") String deviceId,
            @Field("VersionNo") String version
    );

    @FormUrlEncoded
    @POST("/desktopmodules/services/api/MobileApp/SignUpDsij_DSIJ")
    Call<ResMessage> signUp(
            @Field("FirstName") String firstName,
            @Field("LastName") String lastName,
            @Field("Email") String email,
            @Field("Phone") String phone,
            @Field("DeviceId") String deviceId,
            @Field("VersionNo") String version,
            @Field("appid") int appId
    );

    @FormUrlEncoded
    @POST("/desktopmodules/services/api/MobileApp/ForgotUserNameORPassword_DSIJ")
    Call<ResMessage> resetPassword(
            @Field("email") String userName,
            @Field("productid") String deviceId,
            @Field("appid") int appId
    );

    @FormUrlEncoded
    @POST("/desktopmodules/services/api/MobileApp/DsijMobileAppChangePassword")
    Call<ResMessage> changePassword(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("CurrentPassword") String oldPassword,
            @Field("NewPassword") String newPassword,
            @Field("appid") int appId
    );

    @FormUrlEncoded
    @POST("/desktopmodules/services/api/MobileApp/RegisterOrDeregisterForPushNotification_DSIJ")
    Call<ResMessage> notification(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("AppId") int appId,
            @Field("GCMId") String gcmId,
            @Field("Enable") boolean enable,
            @Field("DeviceId") String deviceId,
            @Field("VersionNo") String version
    );

    @FormUrlEncoded
    @POST("/desktopmodules/services/api/MobileApp/DsijMobileAppComplaintOrFeedback")
    Call<ResponseBody> submitFeedback(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("Email") String email,
            @Field("Phone") String phone,
            @Field("subject") String subject,
            @Field("Description") String description

    );

    @FormUrlEncoded
    @POST(C.net.loginWithPassword.ENDPOINT)
    Call<ResDsijLogin> loginWithPassword(
            @Field("userName") String userName,
            @Field("password") String password,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version,
            @Field("gcmId") String gcmId,
            @Field("appId") long appId
    );

    @FormUrlEncoded
    @POST(C.net.loginWithToken.ENDPOINT)
    Call<ResDsijLogin> loginWithToken(
            @Field("userName") String userName,
            @Field("token") String token,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version,
            @Field("appId") long appId
    );

    @FormUrlEncoded
    @POST("/desktopmodules/services/api/SMGWebAPI/GetOfferPage")
    Call<ResOfferURL> offerURL(
            @Field("UserName") String userName,
            @Field("Token") String token
    );

    @FormUrlEncoded
    @POST(C.net.GetSoldScripDetails.ENDPOINT)
    Call<ResSoldScripDetails> getSoldScripDetails(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("portfolioId") String portfolioId,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version

    );

    @FormUrlEncoded
    @POST(C.net.GetAllRecommendedPortfolioScrip.ENDPOINT)
    Call<ResRecoAllPortfolio> getAllRecommendedPortfolioScrip(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("portfolioId") String portfolioId,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version

    );

    @FormUrlEncoded
    @POST(C.net.GetRecommendedPortfolioScrip.ENDPOINT)
    Call<ResUpdateReco> getRecommendedPortfolioScrip(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("portfolioId") String portfolioId,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version

    );

    @FormUrlEncoded
    @POST(C.net.GetSoldScripDetails.ENDPOINT)
    Call<ResHoldings> getHoldings(
            @Field("username") String username,
            @Field("token") String token,
            @Field("portfolioid") String portfolioid,
            @Field("userId") long userId
    );

    @FormUrlEncoded
    @POST(C.net.GetActivityLog.ENDPOINT)
    Call<ResActivityLog> getActivityLog(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("portfolioId") String portfolioId,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version

    );

    @FormUrlEncoded
    @POST(C.net.UpdateRecommededScrip.ENDPOINT)
    Call<ResMessage> updateRecommededScrip(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("portfolioId") String portfolioId,
            @Field("userId") long userId,
            @Field("companyCode") String companycode,
            @Field("companyName") String companyName,
            @Field("quantity") long quantity,
            @Field("avgPrice") String avgPrice,
            @Field("orderQueueId") long orderQueueId,
            @Field("operation") String operation,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version

    );

    @FormUrlEncoded
    @POST(C.net.GetRiskAssessmentQuestionAnswer.ENDPOINT)
    Call<ResRiskAssessmentQA> getRiskAssessmentQuestionAnswer(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("portfolioId") String portfolioId,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version
    );

    @FormUrlEncoded
    @POST(C.net.SubmitRiskQuestionAnswer.ENDPOINT)
    Call<ResMessage> submitRiskQuestionAnswer(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("portfolioId") String portfolioId,
            @Field("userId") long userId,
            @Field("strRiskAnswerIds") String strRiskAnswerIds,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version
    );

    @FormUrlEncoded
    @POST(C.net.GetSubmitedPortFolioScrip.ENDPOINT)
    Call<ResSubmitedPortFolioScrip> getSubmitedPortFolioScrip(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("userId") long userId,
            @Field("portfolioId") String portfolioId,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version
    );

    @FormUrlEncoded
    @POST(C.net.AddORWithdrawCash.ENDPOINT)
    Call<ResMessage> addORWithdrawCash(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("userId") long userId,
            @Field("portfolioId") String portfolioId,
            @Field("openingcash") String openingcash,
            @Field("isaddcash") int isaddcash,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version
    );

    @FormUrlEncoded
    @POST(C.net.GetSummeryDtls.ENDPOINT)
    Call<ResSummeryDtls> getSummeryDtls(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("portfolioId") String portfolioId,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version
    );

    @FormUrlEncoded
    @POST(C.net.SubmitPortfolioName.ENDPOINT)
    Call<ResSubmitPortfolio> submitPortfolioName(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("userId") long userId,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version
    );

    @FormUrlEncoded
    @POST(C.net.GetPasTabName.ENDPOINT)
    Call<ResMessage> getPasTabName(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("userId") long userId,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version
    );

    @FormUrlEncoded
    @POST(C.net.postLowRating.ENDPOINT)
    Call<ResMessage> postLowRating(
            @Field("username") String userName,
            @Field("token") String token,
            @Field("appid") int appId,
            @Field("versionname") String versionName,
            @Field("versionno") String version,
            @Field("rating") int rating,
            @Field("description") String description,
            @Field("deviceid") String deviceId
    );

    @FormUrlEncoded
    @POST(C.net.Get_FormDetails.ENDPOINT)
    Call<ResFormDetails> getFormDetails(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("userId") long userId,
            @Field("portfolioId") String portfolioId,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version
    );

    @FormUrlEncoded
    @POST(C.net.SubmitPortfolioforReview.ENDPOINT)
    Call<ResMessage> submitPortfolioforReview(
            @Field("UserName") String userName,
            @Field("Token") String token,
            @Field("userId") long userId,
            @Field("portfolioId") String portfolioId,
            @Field("deviceId") String deviceId,
            @Field("versionNo") String version
    );
}
