package in.dsij.pas.constants;

public class C {

    public static class device {
        public static String DEVICE_SERIAL = "unidentified";
        public static String VERSION_CODE = "0";
        public static String VERSION_NAME = "undefined";
        public static final int APP_ID = 71;
        public static String productid = "133";
        public static String profileAvatar = "";
    }

    public static class about {
        public static final String URL_TERMS = "http://www.dsij.in/home/terms.aspx";
        public static final String URL_ABOUT_US = "http://www.dsij.in/about-us.aspx";
        public static final String URL_HELP_US = "https://www.dsij.in/DesktopModules/PortfolioAdvisorySystemV3/portfolio_creation.html";
        public static final String URL_PRIVACY = "http://www.dsij.in/privacy-policy.aspx";
        public static final String URL_DISCLAIMER = "https://www.dsij.in/disclaimer";
        public static final String URL_PRIZES = "https://www.dsij.in/prizes";
        public static final String URL_RULES = "https://www.dsij.in/DesktopModules/StockMarketGame/SMC-help/Rules1.html";
        public static final String EMAIL_CONTACT_US = "rajeshp@dsij.in";
    }

    public static final String SHARE_SUBJECT = "Hey, I just play an Interesting Trading Game in \"Dalal Street Investment Journal\"!\n\n";

    public static class url {
        public static final String BASE_URL_DSIJ = "https://www.dsij.in";//VOLLEY
        public static final String API_BASE_URL = "https://www.dsij.in";//RETROFIT
    }

    public static class net {
        public static final int NETWORK_AVAILABLE = 1;
        public static final int NETWORK_CONNECTING = 2;
        public static final int NETWORK_DISCONNECTED = 3;

        public static class tag {
            public static final String RESPONSE = " :: RESPONSE << ";
            public static final String ERROR = " :: ERROR !! ";
            public static final String ERROR_UNKNOWN = " ?? UNKNOWN ERROR ?? ";
            public static final String FAILED = " :: FAILED !! ";

            public static final String DB_TRANSACTION_WRITE = " $$ DB WRITE ";
            public static final String DB_SUCCESS = "SUCCESS ";
            public static final String DB_FAIL = "FAIL ";

            public static final String ERROR_TIMEOUT_MSG = "Something went wrong";
            public static final String ERROR_CHECK_INTERNET = "Please Check your Internet connection";
        }

        public static class loginWithPassword {

            public static final String ENDPOINT = "/desktopmodules/services/api/SMGWebAPI/ValidateUser";

            public static final String TAG = "Login with Password";

            public static class error {
                public static final int INTERNAL_SERVER_ERROR = 410;
                public static final int INCORRECT_PASSWORD = 411;
                public static final int USER_LOCKED = 412;
                public static final int NOT_AUTHORISED = 413;
                public static final int SERVER_ERROR = 414;
                public static final int USERNAME_NOT_REGISTERED = 415;
                public static final int EMPTY_PARAMS = 416;

            }
        }

        public static class loginWithToken {

            public static final String ENDPOINT = "/desktopmodules/services/api/SMGWebAPI/CheckLogin";

            public static final String TAG = "Login with Token";

            public static class error {
                public static final int ALREADY_SIGNED_INTO_OTHER_DEVICE = 411;
                public static final int TOKEN_EXPIRED = 412;
                public static final int NOT_AUTHORISED = 413;
            }
        }

        public static class signUp {

            public static final String TAG = "Sign Up";

            public static class error {
                public static final int INTERNAL_SERVER_ERROR = 411;
                public static final int EMAIL_ALREADY_EXISTS = 412;
                public static final int ERROR_IN_SENDING_MAIL = 414;
                public static final int EMPTY_PARAMS = 416;

            }
        }

        public static class resetPassword {

            public static final String TAG = "Reset Password";

            public static class error {
                public static final int INVALID_USERNAME = 412;
                public static final int INTERNAL_SERVER_ERROR = 414;
                public static final int ERROR_IN_SENDING_EMAIL = 411;
                public static final int EMPTY_PARAMS = 416;

            }
        }

        public static class changePassword {

            public static final String TAG = "Reset Password";

            public static class error {
                public static final int INTERNAL_SERVER_ERROR = 410;
                public static final int INVALID_PASSWORD_FORMAT = 411;
                public static final int NOT_AUTHORISED = 413;
                public static final int EMPTY_PARAMS = 416;

            }
        }
        public static class GetSoldScripDetails {

            public static final String ENDPOINT = "/desktopmodules/services/api/PASWebAPI/GetSoldScripDetails";
            public static final String TAG = "GetHoldingList";
        }

        public static class GetAllRecommendedPortfolioScrip {

            public static final String ENDPOINT = "/desktopmodules/services/api/PASWebAPI/GetAllRecommendedPortfolioScrip";
            public static final String TAG = "GetAllRecoPortfolioScrip";
        }

        public static class GetRecommendedPortfolioScrip {

            public static final String ENDPOINT = "/desktopmodules/services/api/PASWebAPI/GetRecommendedPortfolioScrip";
            public static double totalMarketValue = 0;
            public static String mCashBalance = "1";
            public static final String TAG = "GetHoldingList";
        }
        public static class GetActivityLog {

            public static final String ENDPOINT = "/desktopmodules/services/api/PASWebAPI/GetActivityLog";
            public static final String TAG = "GetActivityLog";
        }
        public static class GetRiskAssessmentQuestionAnswer {

            public static final String ENDPOINT = "/desktopmodules/services/api/PASWebAPI/GetRiskAssessmentQuestionAnswer";
            public static final String TAG = "GetRiskAssessmentQuestionAnswer";
        }

        public static class SubmitRiskQuestionAnswer {

            public static final String ENDPOINT = "/desktopmodules/services/api/PASWebAPI/SubmitRiskQuestionAnswer";
            public static final String TAG = "SubmitRiskQuestionAnswer";
            public static class error {
                public static final int INTERNAL_SERVER_ERROR = 410;
                public static final int SERVER_ERROR = 414;
                public static final int ERROR_IN_SENDING_EMAIL = 411;
                public static final int EMPTY_PARAMS = 416;

            }
        }
        public static class UpdateRecommededScrip {

            public static final String ENDPOINT = "/desktopmodules/services/api/PASWebAPI/UpdateRecommededScrip";
            public static final String TAG = "UpdateRecommededScrip";
        }

        public static class GetSubmitedPortFolioScrip {

            public static final String ENDPOINT = "/desktopmodules/services/api/PASWebAPI/GetSubmitedPortFolioScrip";
            public static final String TAG = "GetSubmitedPortFolioScrip";
            public static class error {
                public static final int INTERNAL_SERVER_ERROR = 410;
                public static final int SERVER_ERROR = 414;
                public static final int ERROR_IN_SENDING_EMAIL = 411;
                public static final int EMPTY_PARAMS = 416;

            }
        }
        public static class AddORWithdrawCash {

            public static final String ENDPOINT = "/desktopmodules/services/api/PASWebAPI/AddORWithdrawCash";
            public static final String TAG = "AddORWithdrawCash";
            public static class error {
                public static final int INTERNAL_SERVER_ERROR = 410;
                public static final int SERVER_ERROR = 414;
                public static final int ERROR_IN_SENDING_EMAIL = 411;
                public static final int EMPTY_PARAMS = 416;

            }
        }

        public static class GetSummeryDtls {

            public static final String ENDPOINT = "/desktopmodules/services/api/PASWebAPI/GetSummaryDetails";
            public static final String TAG = "GetSummeryDtls";
            public static class error {
                public static final int INTERNAL_SERVER_ERROR = 410;
                public static final int SERVER_ERROR = 414;
                public static final int ERROR_IN_SENDING_EMAIL = 411;
                public static final int EMPTY_PARAMS = 416;

            }
        }

        public static class Get_FormDetails {

            public static final String ENDPOINT = "/desktopmodules/services/api/PASWebAPI/Get_FormDetails";
            public static final String TAG = "Get_FormDetails";
            public static class error {
                public static final int INTERNAL_SERVER_ERROR = 410;
                public static final int SERVER_ERROR = 414;
                public static final int ERROR_IN_SENDING_EMAIL = 411;
                public static final int EMPTY_PARAMS = 416;

            }
        }
        public static class SubmitPortfolioforReview {

            public static final String ENDPOINT = "/desktopmodules/services/api/PASWebAPI/SubmitPortfolioforReview";
            public static final String TAG = "SubmitPortfolioforReview";
            public static class error {
                public static final int INTERNAL_SERVER_ERROR = 410;
                public static final int SERVER_ERROR = 414;
                public static final int ERROR_IN_SENDING_EMAIL = 411;
                public static final int EMPTY_PARAMS = 416;

            }
        }

        public static class SubmitPortfolioName {

            public static final String ENDPOINT = "/desktopmodules/services/api/PASWebAPI/SubmitPortfolioName";
            public static final String TAG = "SubmitPortfolioName";
            public static class error {
                public static final int INTERNAL_SERVER_ERROR = 410;
                public static final int SERVER_ERROR = 414;
                public static final int ERROR_IN_SENDING_EMAIL = 411;
                public static final int EMPTY_PARAMS = 416;

            }
        }

        public static class GetPasTabName {

            public static final String ENDPOINT = "/desktopmodules/services/api/PASWebAPI/GetPasTabName";
            public static final String TAG = "GetPasTabName";
            public static class error {
                public static final int INTERNAL_SERVER_ERROR = 410;
                public static final int SERVER_ERROR = 414;
                public static final int ERROR_IN_SENDING_EMAIL = 411;
                public static final int EMPTY_PARAMS = 416;

            }
        }
        public static class postLowRating {
            public static final String ENDPOINT = "/desktopmodules/services/api/MobileApp/SubmitLowRating_DSIJ";
            public static final String TAG = "Post Low Rating";
        }

        public class Config {

            public static final String TOPIC_GLOBAL = "global";

            public static final String REGISTRATION_COMPLETE = "registrationComplete";
            public static final String PUSH_NOTIFICATION = "pushNotification";
        }

        public static class process {
            public static final int LOGIN_WITH_FACEBOOK = 2;
            public static final int LOGIN_WITH_GOOGLE = 102;
            public static final int ACCOUNT_OPENING_FORM = 201;
            public static final int RISK_ASSESSEMENT = 202;
        }
    }
    public static String productid = "115";

    public static String pasDetailsMessage="<!DOCTYPE html>\n" +
            "<html  lang=\"en-US\">\n" +
            "<head id=\"Head\">\n" +
            "<!--*********************************************-->\n" +
            "<!-- DNN Platform - http://www.dnnsoftware.com   -->\n" +
            "<!-- Copyright (c) 2002-2017, by DNN Corporation -->\n" +
            "<!-- Copyright (c) 2002-2017, by DNN Corporation -->\n" +
            "<!--*********************************************-->\n" +
            "<meta content=\"text/html; charset=UTF-8\" http-equiv=\"Content-Type\" />\n" +
            "<meta name=\"REVISIT-AFTER\" content=\"1 DAYS\" />\n" +
            "<meta name=\"RATING\" content=\"GENERAL\" />\n" +
            "<meta name=\"RESOURCE-TYPE\" content=\"DOCUMENT\" />\n" +
            "<meta content=\"text/javascript\" http-equiv=\"Content-Script-Type\" />\n" +
            "<meta content=\"text/css\" http-equiv=\"Content-Style-Type\" />\n" +
            "\n" +
            "                \n" +
            "                 \t\t\t\t<div style=\"margin:25px auto; background-color: #f0f0f0; width:70%; padding:30px; \"><h2 class=\"purpleColor\">Important Instruction to continue your PAS service </h2>\n" +
            "<p>Post giving a name to your portfolio, you are now requested to email your current portfolio, if any, to the email id <strong>pas@dsij.in</strong> In case you do not have an existing portfolio to begin with then just mention <strong>'No existing portfolio'</strong> in the mail sent <strong>pas@dsij.in</strong> Once, the PAS team receives your email, they will analyse the portfolio.</p>\n" +
            "<p>Do note <strong>only the Top 50 companies by weightage will be analysed</strong>, the rest will be ignored. Post analysis, a member from PAS team will contact you and run you through the stocks in your existing portfolio and decide the stocks that will get uploaded in your account. This uploading will be done by the PAS team member. Post this your PAS account will be made ‘Active’. In case there is no existing portfolio, the cash amount to start with will be updated and Portfolio made active.</p>\n" +
            "</div>\n" +
            "                \n" +
            "            \n" +
            "    \n" +
            "</body>\n" +
            "</html>";
}
