package com.lht.cloudjob.interfaces.net;


import com.lht.cloudjob.MainApplication;
import com.lht.cloudjob.customview.FilterSheet;
import com.lht.cloudjob.customview.SortSheet;
import com.lht.cloudjob.interfaces.IVerifyHolder;
import com.lht.cloudjob.mvp.model.CheckLoginStateModel;
import com.lht.cloudjob.mvp.model.EAuthenticQueryModel;
import com.lht.cloudjob.mvp.model.ReadAgreementModel;
import com.lht.cloudjob.mvp.model.SendSmsModel;
import com.lht.cloudjob.mvp.model.SignAgreementModel;
import com.lht.cloudjob.mvp.model.TpLoginModel;
import com.lht.cloudjob.mvp.model.UndertakeModel;
import com.lht.cloudjob.mvp.model.UnfollowModel;
import com.lht.cloudjob.mvp.model.UnreadMessageModel;
import com.lht.cloudjob.mvp.model.UpdateBindPhoneModel;
import com.lht.cloudjob.mvp.model.VerifyCodeCheckModel;
import com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean;
import com.lht.cloudjob.mvp.model.bean.CategoryResBean;
import com.lht.cloudjob.mvp.model.bean.EAuthenticQueryResBean;
import com.lht.cloudjob.mvp.model.bean.LetterBean;
import com.lht.cloudjob.mvp.model.bean.ReadAgreementResBean;
import com.lht.cloudjob.mvp.model.bean.TpLoginResBean;
import com.lht.cloudjob.mvp.model.bean.UnreadMsgResBean;
import com.lht.cloudjob.mvp.model.pojo.DemandAttachFile;
import com.lht.cloudjob.tplogin.TPLoginVerifyBean;
import com.lht.cloudjob.util.VersionUtil;
import com.lht.cloudjob.util.debug.DLog;
import com.lht.cloudjob.util.phonebasic.SettingUtil;
import com.lht.cloudjob.util.string.StringUtil;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p><b>Package</b> com.lht.cloudjob.interfaces.net
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IRestfulApi
 * <p><b>Description</b>: Restful API 管理</br>
 * <li>LoginApi,used to login ,also see {@link com.lht.cloudjob.interfaces.net.IRestfulApi
 * .LoginApi}</li>
 * Created by leobert on 2016/6/30.
 */
public interface IRestfulApi {


    enum Protocol {
        HTTP("http://"),

        HTTPS("https://");

        private final String protocol;

        Protocol(String s) {
            protocol = s;
        }

        @Override
        public String toString() {
            return protocol;
        }
    }

    Protocol DEFAULT_PROTOCAL = Protocol.HTTPS;

    String HOST = "api.vsochina.com";

    String SEPARATOR = "/";

    String QUERY_SYMBOL = "?";

    String AND_SYMBOL = "&";

    String getHost();


    /**
     * default schema is http
     *
     * @param pathParams params in path
     * @return request url
     */
    String formatUrl(String[] pathParams);

    /**
     * @param pathParams        params in path
     * @param queryStringParams
     * @return
     */
    String formatUrl(String[] pathParams, String[] queryStringParams);

    /**
     * @param protocol
     * @param pathParams params in path
     * @return
     */
    String formatUrl(Protocol protocol, String[] pathParams);

    /**
     * @param protocol          协议
     * @param pathParams        params in path
     * @param queryStringParams params in querystrings
     * @return
     */
    String formatUrl(Protocol protocol, String[] pathParams, String[] queryStringParams);

    /**
     * rest风格的
     */
    abstract class AbsRestApiBase implements IRestfulApi {
        @Override
        public String getHost() {
            return HOST;
        }

        protected abstract String getUnformatedPath();

        /**
         * @return null if do not need to format qs
         */
        protected abstract String getUnformatedQuerystring();

        @Override
        public String formatUrl(String[] pathParams) {
            return this.formatUrl(pathParams, null);
        }

        @Override
        public String formatUrl(String[] pathParams, String[] queryStringParams) {
            return this.formatUrl(DEFAULT_PROTOCAL, pathParams, queryStringParams);
        }

        @Override
        public String formatUrl(Protocol protocol, String[] pathParams) {
            return this.formatUrl(protocol, pathParams, null);
        }

        @Override
        public String formatUrl(Protocol protocol, String[] pathParams, String[]
                queryStringParams) {
            StringBuilder builder = new StringBuilder(protocol.toString());
            builder.append(getHost()).append(SEPARATOR);

            Object[] p1 = pathParams;
            builder.append(String.format(getUnformatedPath(), p1));

            if (queryStringParams != null) {
                Object[] p2 = queryStringParams;
                builder.append(QUERY_SYMBOL).append(String.format(getUnformatedQuerystring(), p2));
            }

            String ret = trim(builder);
            log(ret);
            return ret;
        }


        protected void log(String s) {
            DLog.d(getClass(), "format url:" + s);
        }

        protected String trim(StringBuilder builder) {
            return builder.toString().trim();
        }

        public RequestParams newRequestParams() {
            RequestParams params = new RequestParams();
            params.add("lang", "zh-CN");
            return params;
        }
    }

    abstract class AbsReqRestApi extends AbsRestApiBase {
        private static final String _HOST = "req.vsochina.com";
        private static final String KEY_AUTH_USER = "auth_username";

        private static final String KEY_AUTH_TOKEN = "auth_token";

        @Override
        public String getHost() {
            return _HOST;
        }

        public RequestParams newRequestParams() {
            RequestParams params = new RequestParams();
            params.add("lang", "zh-CN");
            params.add(KEY_AUTH_USER, StringUtil.nullStrToEmpty(IVerifyHolder.mLoginInfo
                    .getUsername()));
            params.add(KEY_AUTH_TOKEN, StringUtil.nullStrToEmpty(IVerifyHolder.mLoginInfo
                    .getAccessToken()));
            return params;
        }
    }

    /**
     * <p><b>Package</b> com.lht.cloudjob.interfaces.net
     * <p><b>Project</b> Chuangyiyun
     * <p><b>Classname</b> AbsRestApi
     * <p><b>Description</b>: 抽象restful api，主要作用：
     * 格式化rest api，特殊情况下同时格式化queryString，
     * <p/>
     * 2016-9-8 09:47:26：因为使用curl原因，不再支持格式化queryString
     * <p/>
     * Created by leobert on 2016/6/30.
     */
    abstract class AbsRestApi implements IRestfulApi {

        private static final String CURL = "api/curl";

        private static final String KEY_PATH = "url";

        private static final String KEY_AUTH_USER = "auth_username";

        private static final String KEY_AUTH_TOKEN = "auth_token";

        private static final String HOST_CURL = "req.vsochina.com"; // 正式环境
//                "req.vsochina.com:8443";//测试环境  已弃用


        @Override
        public String getHost() {
            return HOST_CURL;
        }

        protected abstract String getUnformatedPath();

        /**
         * @return null if do not need to format qs
         */
        protected abstract String getUnformatedQuerystring();

        @Override
        public String formatUrl(String[] pathParams) {
            return this.formatUrl(pathParams, null);
        }

        @Override
        public String formatUrl(String[] pathParams, String[] queryStringParams) {
            return this.formatUrl(DEFAULT_PROTOCAL, pathParams, queryStringParams);
        }

        @Override
        public String formatUrl(Protocol protocol, String[] pathParams) {
            return this.formatUrl(protocol, pathParams, null);
        }

        private String[] pathParams;
        private String[] queryStringParams;

        @Override
        public String formatUrl(Protocol protocol, String[] pathParams, String[]
                queryStringParams) {

            this.pathParams = pathParams;
            this.queryStringParams = queryStringParams;

            StringBuilder builder = new StringBuilder(Protocol.HTTPS.toString());
            builder.append(getHost()).append(SEPARATOR);
            builder.append(CURL);

            String ret = trim(builder);
            log(ret);
            return ret;
        }

//        private boolean isCurlComplete = false;

        protected void log(String s) {
            DLog.d(getClass(), "format url:" + s);
        }

        protected String trim(StringBuilder builder) {
            return builder.toString().trim();
        }

        public RequestParams newRequestParams() {
            RequestParams params = new RequestParams();
            params.add("lang", "zh-CN");

            Object[] p1 = pathParams;
            String path = String.format(getUnformatedPath(), p1);
            params.add(KEY_PATH, path);
            params.add(KEY_AUTH_USER, StringUtil.nullStrToEmpty(IVerifyHolder.mLoginInfo
                    .getUsername()));
            params.add(KEY_AUTH_TOKEN, StringUtil.nullStrToEmpty(IVerifyHolder.mLoginInfo
                    .getAccessToken()));

            return params;
        }
    }

    /**
     * 新版-用户登录
     * POST
     * https://api.vsochina.com/user/login/index
     * 参数：
     * <p/>
     * <li>appid	String	必须，全局通用appid
     * <li>token	String	必须，全局通用token
     * <li>name	String	必须，账号（用户名/手机号/邮箱）
     * <li>password	String	必须，密码
     * 参数格式:<br>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "name" => "XXX",
     * "password" => 'xxx'
     * ]
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.LoginResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.LoginModel}
     */
    class LoginApi extends AbsRestApi {

        public static final String PATH = "user/login/index";

        private static final String KEY_NAME = "name";

        private static final String KEY_PASSWORD = "password";

        @Override
        protected String getUnformatedPath() {
            return PATH;
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String usr, String pwd) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_NAME, usr);
            params.add(KEY_PASSWORD, pwd);
            return params;
        }
    }

    /**
     * <br> Device - 设备登录</br>
     * <br>POST</br>
     * <br>device/user/login</br>
     * <br>参数</br>
     * <p>
     * <li> username	String必须，用户名</li>
     * <li> registration_id	String 必须，极光推送用户ID</li>
     * <li> device_id	String	必须，设备号</>
     * <li>type	Number 可选，设备类型（1=>ios，2=>android，3=>Web），默认1</li>
     */
    class DeviceBindApi extends AbsRestApi {

        public static final String PATH = "device/user/login";

        private static final String KEY_USERNAME = "username";

        private static final String KEY_REGISTRATION_ID = "registration_id";

        private static final String KEY_TYPE = "type";

        private static final String KEY_DEVICE_ID = "device_id";

        private static final String VALUE_TYPE_ANDROID = "2";

        @Override
        protected String getUnformatedPath() {
            return PATH;
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String username, String registrationId) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, username);
            params.add(KEY_REGISTRATION_ID, registrationId);
            params.add(KEY_TYPE, VALUE_TYPE_ANDROID);
            params.add(KEY_DEVICE_ID, SettingUtil.getDeviceId(MainApplication.getOurInstance()));
            return params;
        }
    }

    /**
     * https://api.vsochina.com/user/login/logout
     * POST
     * 参数
     * appid	String
     * 必须，全局通用
     * <p/>
     * token	String
     * 必须，全局通用
     * <p/>
     * vso_uid	String
     * 必须，用户编号（UC）
     * <p/>
     * vso_sess	String
     * 必须，session，存在cookie中
     * <p/>
     * vso_token	String
     * 必须，用户登录后的token，存在cookie中
     * <p/>
     * 参数格式:
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "vso_uid" => "1747",
     * "vso_sess" => "0a1358648cf8aaf971919cb7abe4fa58",
     * "vso_token" => "a1168285eb062b2453de845b52fec460"
     * ]
     */
    class LogoutApi extends AbsRestApi {

        private static final String KEY_UID = "vso_uid";

        private static final String KEY_SESS = "vso_sess";

        private static final String KEY_TOKEN = "vso_token";


        @Override
        protected String getUnformatedPath() {
            return "user/login/logout";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String uid, String sess, String token) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_UID, uid);
            params.add(KEY_SESS, sess);
            params.add(KEY_TOKEN, token);
            return params;
        }
    }

    /**
     * 获取用户认证状态列表<br>
     * GET<br>
     * https://api.vsochina.com/auth/record/get-user-auth-status-list<br>
     * 参数:<br>
     * <li> appid	String	必须，通用
     * <li> token	String	必须，通用
     * <li> username	String	必须，用户名
     * <p/>
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.VerifyStatusResBean}
     * to see model at{@link com.lht.cloudjob.mvp.model.AuthStatusModel}
     */
    class AuthStatusApi extends AbsRestApi {
        @Override
        protected String getUnformatedPath() {
            return "auth/record/get-user-auth-status-list";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        private static final String KEY_USERNAME = "username";

        public RequestParams newQuery(String usr) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, usr);
            return params;
        }
    }


    /**
     * ## 三方通用登录，不存在时注册 ##
     * > 需要三方账号</br>
     * > POST</br>
     * > https://api.vsochina.com/user/login/oauth
     * <p/>
     * 参数:
     * <p/>
     * <li>via	String 必须，三方通道 （qq；weibo；weixin；facebook）
     * <li>openid	String 必须，身份验证
     * <p/>
     * 参数格式:
     * <p/>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "via" => "qq",
     * "openid" => ""
     * ]
     * to see ResBean at{@link TpLoginResBean}
     * to see Model at{@link TpLoginModel}
     */
    class TpLoginApi extends AbsRestApi {

        @Override
        protected String getUnformatedPath() {
            return "user/login/oauth";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        private static final String KEY_VIA = "via";

        private static final String KEY_OPENID = "openid";

        private static final String STATIC_PARAM_VIAQQ = "qq";

        private static final String STATIC_PARAM_VIAWECHAT = "weixin";

        private static final String STATIC_PARAM_VIASINA = "weibo";


        public RequestParams newRequestParams(int platform, String openId) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_OPENID, openId);
            String via;
            switch (platform) {
                case TPLoginVerifyBean.TYPE_QQ:
                    via = STATIC_PARAM_VIAQQ;
                    break;
                case TPLoginVerifyBean.TYPE_SINA:
                    via = STATIC_PARAM_VIASINA;
                    break;
                case TPLoginVerifyBean.TYPE_WECHAT:
                    via = STATIC_PARAM_VIAWECHAT;
                    break;
                default:
                    throw new IllegalArgumentException("第三方登录平台参数错了");
            }
            params.add(KEY_VIA, via);
            return params;
        }
    }


    /**
     * User - 重置登录密码，不需要原密码，支持用户名
     * <p/>
     * post
     * https://api.vsochina.com/user/safe/reset-password
     * 参数
     * <li>username	String 必须，用户名
     * <li>password	String 必须，新密码，明码
     * <p/>
     * 参数格式：
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "username" => "admin",
     * "password" => "123456",
     * ]
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.ResetPwdByUsernameModel}
     */
    class ResetPwdByUsernameApi extends AbsRestApi {

        private static final String KEY_USERNAME = "username";

        private static final String KEY_PASSWORD = "password";

        @Override
        protected String getUnformatedPath() {
            return "user/safe/reset-password";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String usr, String newPwd) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, usr);
            params.add(KEY_PASSWORD, newPwd);
            return params;
        }
    }

    /**
     * ## 重置登录密码，不需要原密码，支持账号 ##
     * > POST
     * > https://api.vsochina.com/user/safe/reset-password-by-name
     * <p/>
     * 参数：
     * <p/>
     * - **name	String 必须，账号（用户名/手机号/邮箱）**
     * - **password	String 必须，新密码，明码**
     * <p/>
     * 参数格式：
     * <p/>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "name" => "admin",
     * "password" => "123456",
     * ]
     * <p/>
     * 返回码表： 13280 成功
     * <p/>
     * 错误码 => 错误信息：
     * "13005" => "缺少账号",
     * "13003" => "账号不存在",
     * "9012" => "缺少用户名",
     * "13282" => "缺少新登录密码",
     * "9013" => "用户名不存在",
     * "13280" => "登录密码修改成功",
     * "13286" => "密码修改失败"
     * <p/>
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.ResetPwdByAccountModel}
     */
    class ResetPwdByAccountApi extends AbsRestApi {

        private static final String KEY_NAME = "name";
        private static final String KEY_PWD = "password";

        @Override
        protected String getUnformatedPath() {
            return "user/safe/reset-password-by-name";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String account, String newPwd) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_NAME, account);
            params.add(KEY_PWD, newPwd);
            return params;
        }
    }

    /**
     * post
     * https://api.vsochina.com/message/mobile/send-message-by-mobile
     * <p/>
     * 参数:</br>
     * <li>appid String 必须，全局通用appid
     * <li>token String 必须，全局通用token
     * <li>mobile  String 必须，手机号
     * <li>action  String 必须，操作类型
     * <p/>
     * valid_code_register => 注册验证码（使用紧急通道，每个手机号每天最多发送5条，此为服务商限制）
     * valid_code => 发送验证码，通用，非注册
     * reset_password => 重置密码
     * change_password => 修改登录密码
     * <p/>
     * 参数格式:
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "mobile" => "13800138000",
     * "action" => "valid_code"
     * ]
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.SendSmsModel}
     */
    class SendSmsApi extends AbsRestApi {

        private static final String KEY_MOBILE = "mobile";
        private static final String KEY_ACTION = "action";

        private static final String STATIC_PARAM_VALID_CODE_REGISTER = "valid_code_register";

        private static final String STATIC_PARAM_VALID_CODE = "valid_code";

        private static final String STATIC_PARAM_RESET_PASSWORD = "reset_password";

        private static final String STATIC_PARAM_CHANGE_PASSWORD = "change_password";

        @Override
        protected String getUnformatedPath() {
            return "message/mobile/send-message-by-mobile";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        /**
         * @param phone
         * @param type  当前实际生效的只有注册，注意！注意！
         *              TODO
         * @return
         */
        public RequestParams newRequestParams(String phone, SendSmsModel.SmsRequestType type) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_MOBILE, phone);
            params.add(KEY_ACTION, getAction(type));
            return params;
        }

        private String getAction(SendSmsModel.SmsRequestType type) {
            switch (type) {
                case Register:
                    return STATIC_PARAM_VALID_CODE_REGISTER;
                case ResetPwd:
                    return STATIC_PARAM_RESET_PASSWORD;
                case BindPhone://未投入使用
                    return null;
                case Normal:
                    return STATIC_PARAM_VALID_CODE;
                default:
                    return STATIC_PARAM_VALID_CODE;
            }
        }
    }

    /**
     * post
     * https://api.vsochina.com/message/mobile/check-valid-code-by-mobile
     * 参数
     * <p/>
     * appid String 必须，全局通用appid
     * token String 必须，全局通用token
     * mobile  Number 必须，手机号
     * valid_code  Number 必须，验证码
     * <p/>
     * 参数格式：
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "mobile" => "13800138000",
     * "valid_code" => 123456
     * ]
     * <p/>
     * to see ResBean at{@link BaseVsoApiResBean}
     * to see Model at{@link VerifyCodeCheckModel}
     */
    class CheckVerifyCodeApi extends AbsRestApi {

        private static final String KEY_MOBILE = "mobile";
        private static final String KEY_CODE = "valid_code";

        @Override
        protected String getUnformatedPath() {
            return "message/mobile/check-valid-code-by-mobile";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String phone, String code) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_MOBILE, phone);
            params.add(KEY_CODE, code);
            return params;
        }
    }

    /**
     * Message - 验证码校验，username + valid_code
     * 校验用户名与验证码
     * POST
     * /message/mobile/check-mobile-valid-code
     * 参数
     * username	String 必须，用户名
     * valid_code	String 必须，验证码，6位数字
     * <p/>
     * 参数格式:
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "username" => "admin",
     * "valid_code" => 123456,
     * ]
     */
    class CheckVerifyCodeWithUserApi extends AbsRestApi {

        private static final String KEY_USER = "username";
        private static final String KEY_CODE = "valid_code";

        @Override
        protected String getUnformatedPath() {
            return "message/mobile/check-mobile-valid-code";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String username, String code) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USER, username);
            params.add(KEY_CODE, code);
            return params;
        }

    }

    /**
     * ## 手机号注册用户 ##
     * <p/>
     * > 新版</br>
     * > POST</br>
     * > https://api.vsochina.com/user/register/mobile
     * <p/>
     * 参数：
     * <p/>
     * - **appid	String 必须，全局通用appid**
     * - **token	String 必须，全局通用token**
     * - **mobile	Number 必须，手机号，必须是没注册过的**
     * - **password	String 必须，登录密码，明文**
     * <p/>
     * 参数格式:
     * <p/>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "mobile" => 13800138000,
     * "password" => 'dell_456',
     * ]
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.RegisterResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.RegisterModel}
     */
    class RegisterApi extends AbsRestApi {

        private static final String KEY_MOBILE = "mobile";
        private static final String KEY_PASSWORD = "password";

        @Override
        protected String getUnformatedPath() {
            return "user/register/mobile";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String phone, String password) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_MOBILE, phone);
            params.add(KEY_PASSWORD, password);
//            params.add(KEY_ANALYZE_DOMAIN,VALUE_ANALYZE_DOMAIN_CLOUDJOB_ANDROID);
            return params;
        }
    }

    /**
     * ## 【注册】手机号是否可用 ##
     * > GET</br>
     * > https://api.vsochina.com/user/auth/is-available-mobile
     * <p/>
     * 参数：
     * <p/>
     * - appid
     * - token
     * - mobile 手机号
     * <p/>
     * 返回码对照表：
     * <p/>
     * 错误码 => 错误信息：
     * "13181" => "缺少手机号",
     * "13182" => "手机号不合法",
     * "13183" => "手机号被占用",
     * "0" => "成功"
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.CheckPhoneModel}
     */
    class CheckPhoneApi extends AbsRestApi {

        private static final String KEY_MOBILE = "mobile";

        @Override
        protected String getUnformatedPath() {
            return "user/auth/is-available-mobile";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String phone) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_MOBILE, phone);
            return params;
        }
    }

    /**
     * ## 修改用户基本信息 ##
     * POST</br>
     * https://api.vsochina.com/user/info/update-user-basic-info
     * <p/>
     * 参数
     * <p/>
     * appid String 必须，全局通用appid
     * <p/>
     * token String 必须，全局通用token
     * <p/>
     * username  String 必须，用户帐号
     * <p/>
     * nickname  String 可选，昵称，最长20字符
     * <p/>
     * avatar  String 可选，用户头像，图片路径
     * <p/>
     * truename  String 可选，真实姓名
     * <p/>
     * mobile  Number 可选，手机号
     * <p/>
     * email String 可选，邮箱
     * <p/>
     * user_type Number 可选，用户类型（1=>个人，2=>企业，3=>工作室，6=>校园）
     * <p/>
     * status  Number 可选，用户状态（1=>激活，2=>禁用）
     * <p/>
     * qq  Number 可选，QQ
     * <p/>
     * indus_pid Number 可选，用户所属行业，旧版行业编号
     * <p/>
     * lable_name  String 可选，人才标签（,分隔的字符串）
     * <p/>
     * sex Number 可选，性别（1=>男性，2=>女性）
     * <p/>
     * 参数格式:
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "username" => "admin",
     * "nickname" => "test",
     * "avatar" => ""
     * ]
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.InfoModifyModel}
     */
    class ModifyInfoApi extends AbsRestApi {

        private static final String KEY_USERNAME = "username";//username  String 必须，用户帐号
        private static final String KEY_NICKNAME = "nickname";//  * nickname  String 可选，昵称，最长20字符
        private static final String KEY_AVATAR = "avatar";//   * avatar  String 可选，用户头像，图片路径

        private static final String KEY_MOBILE = "mobile";//   * mobile  Number 可选，手机号

        private static final String KEY_INDUSTRY = "indus_pid";//   * indus_pid Number
        // 可选，用户所属行业，旧版行业编号
        private static final String KEY_FIELD = "lable_name";//   * lable_name  String 可选，人才标签（,
        // 分隔的字符串）
        private static final String KEY_SEX = "sex";//  * sex Number 可选，性别（1=>男性，2=>女性）

        @Override
        protected String getUnformatedPath() {
            return "user/info/update-user-basic-info";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public IRequestParamsBuilder newRequestParamsBuilder(String username) {
            return new RequestParamsBuilder(username);
        }

        /**
         * 参数情况复杂，使用builder模式。
         * 暴露该接口，不暴露实现类
         */
        public interface IRequestParamsBuilder {
            /**
             * 设置昵称
             *
             * @param nickname 昵称，length<=20
             * @return builder
             */
            RequestParamsBuilder setNickname(String nickname);

            /**
             * 设置头像
             *
             * @param avatarPath 服务器上的头像路径
             * @return builder
             */
            RequestParamsBuilder setAvatar(String avatarPath);

            /**
             * 设置绑定手机
             *
             * @param mobile 手机号
             * @return builder
             */
            @Deprecated
            RequestParamsBuilder setMobile(String mobile);

            /**
             * 设置行业
             *
             * @param industryId 行业编号
             * @return builder
             */
            RequestParamsBuilder setIndustry(int industryId);

            /**
             * 设置个人领域
             *
             * @param fieldLableIds 领域标签,旧版二级行业编号,数组，需格式化成","隔开的字符串
             * @return builder
             */
            RequestParamsBuilder setField(int[] fieldLableIds);

            /**
             * 设置性别为男
             *
             * @return builder
             */
            RequestParamsBuilder male();

            /**
             * 设置性别为女
             *
             * @return builder
             */
            RequestParamsBuilder female();

            RequestParams build();

        }

        private class RequestParamsBuilder implements IRequestParamsBuilder {

            private RequestParams params;

            private String nickname;

            private String avatar;

            private String mobile;

            private int industryId;

            private int[] fieldLables;

            private boolean isMale;

            private boolean isSexSet;

            RequestParamsBuilder(String username) {
                params = newRequestParams();//必须
                params.add(KEY_USERNAME, username);
            }

            @Override
            public RequestParamsBuilder setNickname(String nickname) {
                this.nickname = nickname;
                return this;
            }

            @Override
            public RequestParamsBuilder setAvatar(String avatarPath) {
                this.avatar = avatarPath;
                return this;
            }

            @Override
            public RequestParamsBuilder setMobile(String mobile) {
                this.mobile = mobile;
                return this;
            }

            @Override
            public RequestParamsBuilder setIndustry(int industryId) {
                this.industryId = industryId;
                return this;
            }

            /**
             * 设置个人领域
             *
             * @param fieldLableIds 领域标签,旧版二级行业编号,数组，需格式化成","隔开的字符串
             * @return builder
             */
            @Override
            public RequestParamsBuilder setField(int[] fieldLableIds) {
                this.fieldLables = fieldLableIds;
                return this;
            }


            @Override
            public RequestParamsBuilder male() {
                this.isSexSet = true;
                this.isMale = true;
                return this;
            }

            @Override
            public RequestParamsBuilder female() {
                this.isSexSet = true;
                this.isMale = false;
                return this;
            }

            @Override
            public RequestParams build() {
                if (needProcess(nickname)) {
                    params.add(KEY_NICKNAME, nickname);
                }

                if (needProcess(avatar)) {
                    params.add(KEY_AVATAR, avatar);
                }

                if (needProcess(mobile)) {
                    params.add(KEY_MOBILE, mobile);
                }

                if (needProcess(industryId)) {
                    params.add(KEY_INDUSTRY, String.valueOf(industryId));
                }

                if (needProcess(fieldLables)) {
                    StringBuilder sb = new StringBuilder();
                    for (int id : fieldLables) {
                        sb.append(id).append(",");
                    }
                    String labels = sb.substring(0, sb.length() - 1);
                    params.add(KEY_FIELD, labels);
                }

                if (isSexSet) {
                    if (isMale) {
                        params.add(KEY_SEX, "1");
                    } else {
                        params.add(KEY_SEX, "2");
                    }
                }
                return params;
            }

            private boolean needProcess(String s) {
                return !StringUtil.isEmpty(s);
            }

            private boolean needProcess(int i) {
                return i != 0;
            }

            private boolean needProcess(String[] s) {
                return s != null && s.length > 0;
            }

            private boolean needProcess(int[] s) {
                return s != null && s.length > 0;
            }
        }
    }

    /**
     * User - 用户基础信息，需求大厅app
     * 场景：任务大厅app专用
     * <p/>
     * GET<br>
     * https://api.vsochina.com/user/info/view-req-app
     * <p/>
     * 参数
     * appid	String 必须，全局通用appid
     * <p/>
     * token	String 必须，全局通用token
     * <p/>
     * username	String 必须，用户帐号
     * <p/>
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.BasicInfoModel}
     */
    class BasicInfoApi extends AbsRestApi {

        private static final String KEY_USERNAME = "username";

        @Override
        protected String getUnformatedPath() {
            return "user/info/view-req-app";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String username) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, username);
            return params;
        }
    }

    /**
     * 校验编辑用户信息时的昵称重复性<br>
     * GET<br>
     * https://api.vsochina.com/user/auth/is-available-nickname
     * 参数
     * <li>appid String 必须，全局通用
     * <li>token String 必须，全局通用
     * <li>nickname  String 必须，昵称，最长20
     * <li>username  String 可选，用户名，
     * <p/>
     * 错误码 => 错误信息：
     * "13482" => "缺少昵称",
     * "13481" => "昵称不合法，最长20个字符",
     * "13302" => "昵称被占用",
     * "13480" => "昵称可用"
     * <p/>
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.CheckNicknameModel}
     */
    class CheckNicknameApi extends AbsRestApi {

        private static final String KEY_NICKNAME = "nickname";

        @Override
        protected String getUnformatedPath() {
            return "user/auth/is-available-nickname";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String nickname) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_NICKNAME, nickname);
            return params;
        }
    }

    /**
     * ## 文件上传 ##
     * <br>POST</br>
     * <br>https://api.vsochina.com/file/index/upload</br>
     * <br>参数:</br>
     * <li>username	String 必须，用户名</li>
     * <li>attachment	String 必须，附件文件</li>
     * <li>objtype	String 必须，附件对象类型(参考本接口下面的对象说明)</li>
     * <p/>
     * - size	String 可选，需要生成的缩略图尺寸，数组形式
     * <p/>
     * <br>参数格式：</br>
     * {
     * "appid": "XXX",
     * "token": "XXX",
     * "username": "zhou88",
     * "attachment": "a.jpg",
     * "objtype": "task",
     * }<br>
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.UploadResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.UnreadMessageModel}
     */
    class UploadApi extends AbsRestApi {

        /**
         * 任务/需求
         */
        public static final String VALUE_TYPE_TASK = "task";
        /**
         * 服务
         */
        public static final String VALUE_TYPE_SERVICE = "service";

        /**
         * 作品、稿件
         */
        public static final String VALUE_TYPE_WORK = "work";

        /**
         * 用户认证
         */
        public static final String VALUE_TYPE_USER_CERT = "user_cert";

        /**
         * 用户资料
         */
        public static final String VALUE_TYPE_SPACE = "space";

        /**
         * 合同
         */
        public static final String VALUE_TYPE_AGREEMENT = "agreement";

        /**
         * 资源
         */
        public static final String VALUE_TYPE_RESOURCE = "resource";
        /**
         * 资源
         */
        public static final String VALUE_TYPE_RC = "rc";
        /**
         * 创客空间
         */
        public static final String VALUE_TYPE_MAKER = "maker";
        /**
         * 其他
         */
        public static final String VALUE_TYPE_DEFAULT = "default";

        private static final String KEY_USERNAME = "username";

        private static final String KEY_ATTACHMENT = "attachment";

        private static final String KEY_OBJTYPE = "objtype";

        @Override
        protected String getUnformatedPath() {
            return "file/index/upload";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String username, String attachmentPath, String type) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, username);
            params.add(KEY_OBJTYPE, type);
            try {
                params.put(KEY_ATTACHMENT, new File(attachmentPath), "application/octet-stream");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return params;
        }
    }

    /**
     * 媒体中心的上传
     * POST
     * https://req.vsochina.com/cloud-file/upload?createFolder=true&overwritePolicy=1
     * <br>参数:</br>
     * <li>folder	String 必须，存储路径，写死：/file_tmp/</li>
     * <li>textFile	String 必须，附件文件</li>
     */
    class MediaCenterUploadApi extends AbsRestApiBase {

        private static final String KEY_FOLDER = "folder";

        private static final String DEFAULT_FOLDER = "/file_tmp/";

        private static final String KEY_FILES = "files";

        @Override
        public String formatUrl(Protocol protocol, String[] pathParams, String[]
                queryStringParams) {
            //写死 https??
            return "https://req.vsochina.com/cloud-file/upload?createFolder=true&overwritePolicy=1";
        }

        @Override
        protected String getUnformatedPath() {
            return null;
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String filePath, String name) {
            RequestParams params = new RequestParams();
            params.setForceMultipartEntityContentType(true);
            params.add(KEY_FOLDER, DEFAULT_FOLDER);
            try {
//                params.put(KEY_FILES, new File(filePath),RequestParams.APPLICATION_OCTET_STREAM);
                InputStream inputStream = new FileInputStream(filePath);
                params.put(KEY_FILES, inputStream, name, RequestParams.APPLICATION_OCTET_STREAM,
                        true);
//                params.put(KEY_FILES, new File(filePath), "application/octet-stream");
            } catch (FileNotFoundException e) {
                DLog.e(getClass(), "file not found");
                e.printStackTrace();
            }
            return params;
        }
    }

    /**
     * Post
     * https://api.vsochina.com/industry/get-industry-list
     * 使用树遍历描述的数据，新接口将处理数据结构
     * to see Model at{@link com.lht.cloudjob.mvp.model.CategoryModel1}
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.CategoryResBean}
     */
    class CategoryApi1 extends AbsRestApi {

        @Override
        protected String getUnformatedPath() {
            return "industry/get-industry-list";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }
    }

    /**
     * User - 登录密码 -- 修改
     * <p/>
     * 修改登录密码，需要原登录密码，不需要发送验证码短信
     * 新密码不能与原密码相同
     * POST</br>
     * https://api.vsochina.com/user/safe/change-password</br>
     * 参数</br>
     * <li>appid	String 必须，全局通用
     * <li>token	String 必须，全局通用
     * <li>username	String 必须，用户名
     * <li>old_password	String 必须，原登录密码
     * <li>new_password	String 必须，新密码
     * <p/>
     * to see Model at{@link com.lht.cloudjob.mvp.model.ChangePwdModel}
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean}
     */
    class ChangePwdApi extends AbsRestApi {

        private static final String KEY_USERNAME = "username";

        private static final String KEY_OLDPWD = "old_password";

        private static final String KEY_NEWPWD = "new_password";

        @Override
        protected String getUnformatedPath() {
            return "user/safe/change-password";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String usr, String oldPwd, String newPwd) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, usr);
            params.add(KEY_OLDPWD, oldPwd);
            params.add(KEY_NEWPWD, newPwd);
            return params;
        }
    }


    /**
     * Auth - 实名认证 -- 查询
     * <p/>
     * GET</br>
     * https://api.vsochina.com/auth/realname/view </br>
     * 参数
     * <p/>
     * appid	String 必须，全局通用
     * <p/>
     * token	String 必须，全局通用
     * <p/>
     * username	String 必须，用户名
     * <p/>
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.PAuthenticQueryResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.PAuthenticQueryModel}
     */
    class QueryPAuthenticApi extends AbsRestApi {

        private static final String KEY_USERNAME = "username";

        @Override
        protected String getUnformatedPath() {
            return "auth/realname/view";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String usr) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, usr);
            return params;
        }
    }


    /**
     * Favor - 我关注的人才列表<br>
     * 获取用户关注人才列表，需要分页<br>
     * POST<br>
     * https://api.vsochina.com/favor/talent/get-user-favor-talent-list<br>
     * 参数
     * <li>appid	String 必须，全局通用
     * <li>token	String 必须，全局通用
     * <li>username	String 必须，用户名
     * <li>offset	Number 可选，分页查询偏移量
     * <li>limit	Number 可选，每页限制数据条数
     * 参数格式:
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "username" => "admin",
     * "offset" => 0,
     * "limit" => 10
     * ]
     */
    class FavorListApi extends AbsRestApi {

        private static final String KEY_USERNAME = "username";
        private static final String KEY_OFFSET = "offset";
        private static final String KEY_LIMIT = "limit";

        private static final int DEFAULT_LIMIT = 20;

        @Override
        protected String getUnformatedPath() {
            return "favor/talent/get-user-favor-talent-list";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String usr, int offset) {
            return this.newRequestParams(usr, offset, DEFAULT_LIMIT);
        }

        public RequestParams newRequestParams(String usr, int offset, int limit) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, usr);
            params.add(KEY_OFFSET, String.valueOf(offset));
            params.add(KEY_LIMIT, String.valueOf(limit));
            return params;
        }
    }

    /**
     * Favor - 关注用户<br>
     * POST<br>
     * https://api.vsochina.com/favor/talent/create-favor-talent<br>
     * 参数<br>
     * <li> appid	String	必须，全局通用
     * <li> token	String	必须，全局通用
     * <li> username	String	必须，关注人用户名
     * <li> obj_name	String	被关注人用户名
     * <p/>
     * 参数格式:<br>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "username" => "admin",
     * "obj_name" => "test"
     * ]
     */
    class FollowApi extends AbsRestApi {

        /**
         * 自身
         */
        private static final String KEY_USERNAME = "username";

        /**
         * 欲取关对象
         */
        private static final String KEY_FOLLOWUSER = "obj_name";

        @Override
        protected String getUnformatedPath() {
            return "favor/talent/create-favor-talent";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String usr, String fUser) {
            RequestParams params = newRequestParams();
            params.add(KEY_USERNAME, usr);
            params.add(KEY_FOLLOWUSER, fUser);
            return params;
        }
    }

    /**
     * Favor - 取消关注用户</br>
     * <br>POST</br>
     * https://api.vsochina.com/favor/talent/delete-favor-talent<br>
     * 参数<br>
     * <li> appid	String	必须，全局通用
     * <li> token	String	必须，全局通用
     * <li> username	String	必须，关注人用户名
     * <li> obj_name	String	被关注人用户名
     * <p/>
     * 参数格式:
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "username" => "admin",
     * "obj_name" => "test"
     * ]
     * to see ResBean at{@link BaseVsoApiResBean}
     * to see Model at{@link UnfollowModel}
     */
    class UnfollowApi extends AbsRestApi {

        /**
         * 自身
         */
        private static final String KEY_USERNAME = "username";

        /**
         * 欲取关对象
         */
        private static final String KEY_FOLLOWUSER = "obj_name";

        @Override
        protected String getUnformatedPath() {
            return "favor/talent/delete-favor-talent";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String usr, String fUser) {
            RequestParams params = newRequestParams();
            params.add(KEY_USERNAME, usr);
            params.add(KEY_FOLLOWUSER, fUser);
            return params;
        }
    }

    /**
     * Req - 需求检索，新版<br>
     * 目前使用搜索引擎，开发环境半小时更新一次数据
     * 也可以手动更新，地址：http://192.168.2.108/reqindex/refresh<br>
     * POST<br>
     * https://api.vsochina.com/req/search/list<br>
     * 参数 <br>
     * <li> appid	String  必须，全局通用
     * <li> token	String  必须，全局通用
     * <li> keyword	String  可选，搜索关键词，搜索范围：需求名称or描述
     * <li> indus_id	Number   可选，新版行业编号，用户选一级就传一级的，选二级就传二级的
     * <li> model	Number  可选，需求类型（1=>悬赏，2=>招标）
     * <li> mark	Number  可选，招标类型（1=>明标，2=>暗标），这个参数传了就不要传model参数
     * <li> minCash	Number  可选，赏金额度最小值
     * <li> maxCash	Number  可选，赏金额度最大值
     * <li> endTime	Number 可选，交稿截止时间，秒级时间戳
     * <li> status	Number     可选，需求状态（1=>进行中，2=>已选标，3=>已结束）
     * //     进行中对应投稿阶段
     * //     已选标包含如下阶段：选稿，公示，制作
     * <li> order	String 可选，排序字段，默认endTime
     * //     createTime => 发布时间
     * //     endTime => 交稿截止时间
     * //     totalBids => 投稿数
     * //     maxCash => 金额
     * <li> direction	String  可选，排序规则（ASC或DESC，不区分大小写），默认ASC
     * <li> pno	Number  可选，当前搜索第几页，从0开始，默认0
     * <li> pageSize	Number 可选，每页返回多少条数据，默认10
     * <p/>
     * 参数格式:<br>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "keyword" => "搜索"
     * ]
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.SearchResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.SearchModel}
     */
    class SearchTaskApi extends AbsRestApi {
        private static final String KEY_KEYWORD = "keyword";
        private static final String KEY_INDUS_ID = "indus_id";
        private static final String KEY_MODEL = "model";
        private static final String KEY_ORDER = "order";
        private static final String KEY_DIRECTION = "direction";

        private static final String KEY_PAGE = "pno";
        private static final String KEY_PAGESIZE = "pageSize";
        private static final String DEFAULT_PAGESIZE = "20";
        private static final String KEY_DELEGATE = "hosted_fee";

        @Override
        protected String getUnformatedPath() {
            return "req/search/list";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String keyword, CategoryResBean industry,
                                              FilterSheet.FilterSelectedItems filters, SortSheet
                                                      .SortRules sortRules, int offset) {
            RequestParams params = newRequestParams();
            params.add(KEY_KEYWORD, keyword);

            if (industry != null) {
                params.add(KEY_INDUS_ID, String.valueOf(industry.getId()));
            }

            if (filters != null) {

                //index started from zero;

                //0=>全部 但是不要传， 1=>悬赏，2=>招标
                int i = filters.getfTypeIndex();
                if (i > 0 && i < 3) {
                    params.add(KEY_MODEL, String.valueOf(i));
                }

                //0=>未托管，1=>已托管 app中index0是全部，不需要传
                int delegateIndex = filters.getfDelegateIndex();
                if (delegateIndex > 0) {
                    params.add(KEY_DELEGATE, String.valueOf(delegateIndex));
                }
            }
            if (sortRules != null) {
                params.add(KEY_ORDER, sortRules.getOrderName());
                params.add(KEY_DIRECTION, sortRules.getDirection());
            } else { //默认排序 临近截稿 asc
                params.add(KEY_ORDER, SortSheet.SortRules.deadline.getOrderName());
                params.add(KEY_DIRECTION, SortSheet.SortRules.deadline.getDirection());
            }

            double f = ((double) offset) / 20.0;
            int page = (int) Math.ceil(f);
            params.add(KEY_PAGE, String.valueOf(page));
            params.add(KEY_PAGESIZE, DEFAULT_PAGESIZE);

            return params;
        }

    }

    /**
     * Req - 所有用户热门搜索<br>
     * 读取搜索历史中搜索次数总计前8个
     * 场景：目前用于需求大厅app中<br>
     * GET<br>
     * https://api.vsochina.com/req/search/list-search-history<br>
     * 参数:<br>
     * <li> appid	String 必须，全局通用
     * <li> token	String	必须，全局通用
     * <li> keyword	String	可选，搜索关键词，模糊查询
     * <li> limit	Number	可选，数据条数，默认8
     * <p/>
     * 参数格式:
     * [
     * "appid" => "XXX",
     * "token" => "XXX"
     * ]
     * to see Model at {@link com.lht.cloudjob.mvp.model.HotSearchModel}
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.HotSearchResBean}
     */
    class HotSearchApi extends AbsRestApi {

        private static final String KEY_LIMIT = "limit";

        @Override
        protected String getUnformatedPath() {
            return "req/search/list-search-history";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        @Override
        public RequestParams newRequestParams() {
            RequestParams params = super.newRequestParams();
            params.add(KEY_LIMIT, "12");
            return params;
        }
    }

    /**
     * Req - 最火需求列表<br>
     * 需求状态为投稿中，排除暗标，redis缓存5分钟<br>
     * 排序：投稿数倒序，发布时间倒序<br>
     * 场景：目前用于需求大厅app中<br>
     * GET<br>
     * https://api.vsochina.com/req/search/list-hot<br>
     * 参数<br>
     * <li> appid	String	必须，全局通用
     * <li> token	String	必须，全局通用
     * <li> limit	Number	可选，数据条数，默认10
     * <p/>
     * 参数格式:
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "limit" => 10
     * ]
     * to see ResBean at {@link com.lht.cloudjob.mvp.model.bean.HotTaskResBean}
     * to see Model at {@link com.lht.cloudjob.mvp.model.HotTaskListModel}
     */
    class HotTaskListApi extends AbsRestApi {
        private static final String KEY_LIMIT = "limit";

        private static final String DEFAULT_LIMIT = "10";

        @Override
        protected String getUnformatedPath() {
            return "req/search/list-hot";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        @Override
        public RequestParams newRequestParams() {
            RequestParams params = super.newRequestParams();
            params.add(KEY_LIMIT, DEFAULT_LIMIT);
            return params;
        }
    }

    /**
     * Req - 最豪需求列表
     * 需求状态为投稿中，排除暗标，redis缓存5分钟<br>
     * 排序：悬赏金额倒序，发布时间倒序<br>
     * 场景：目前用于需求大厅app中<br>
     * GET<br>
     * https://api.vsochina.com/req/search/list-expensive<br>
     * 参数<br>
     * <li> appid	String	必须，全局通用
     * <li> token	String	必须，全局通用
     * <li> limit	Number	可选，数据条数，默认10
     * <p/>
     * 参数格式:
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "limit" => 10
     * ]
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.RichTaskResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.RichTaskListModel}
     */
    class RichTaskListApi extends AbsRestApi {

        private static final String KEY_LIMIT = "limit";

        private static final String DEFAULT_LIMIT = "10";

        @Override
        protected String getUnformatedPath() {
            return "req/search/list-expensive";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        @Override
        public RequestParams newRequestParams() {
            RequestParams params = super.newRequestParams();
            params.add(KEY_LIMIT, DEFAULT_LIMIT);
            return params;
        }
    }

    /**
     * Req - 需求 -- 我收藏的需求列表<br>
     * 按照需求截止时间顺序排列，临近截稿的排在前面<br>
     * 其次为选稿中，公示中，交付中，已结束<br>
     * GET<br>
     * https://api.vsochina.com/req/favor/list<br>
     * 参数:<br>
     * <li> appid	String 必须，全局通用
     * <li> token	String 必须，全局通用
     * <li>  username	String 必须，用户名
     * <li>  offset	Number 可选，分页查询偏移量，默认0
     * <li> limit	Number 可选，每页数据限制条数，默认10
     * 参数格式：<br>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "username" => "admin"
     * ]
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.CollectedTaskResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.CollectedTaskListModel}
     */
    class CollectedTaskListApi extends AbsRestApi {

        private static final String KEY_USERNAME = "username";
        private static final String KEY_OFFSET = "offset";
        private static final String KEY_LIMIT = "limit";

        private static final int DEFAULT_LIMIT = 20;

        @Override
        protected String getUnformatedPath() {
            return "req/favor/list";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String usr, int offset) {
            return this.newRequestParams(usr, offset, DEFAULT_LIMIT);
        }

        public RequestParams newRequestParams(String usr, int offset, int limit) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, usr);
            params.add(KEY_OFFSET, String.valueOf(offset));
            params.add(KEY_LIMIT, String.valueOf(limit));
            return params;
        }
    }

    /**
     * Req - 收藏 -- 添加
     * POST<br>
     * https://api.vsochina.com/req/favor/create<br>
     * 参数<br>
     * <li>appid	String	必须，全局通用
     * <li>token	String	必须，全局通用
     * <li>username	String	必须，用户名
     * <li>task_bn	Number	必须，需求编号（新版），支持批量（使用,分隔）
     * 参数格式：<br>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "username" => "admin",
     * "task_bn" => "123456"
     * ]
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.CollectTaskModel}
     */
    class CollectTaskApi extends AbsRestApi {

        private static final String KEY_USERNAME = "username";
        private static final String KEY_TASK_BN = "task_bn";

        @Override
        protected String getUnformatedPath() {
            return "req/favor/create";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String user, String taskId) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, user);
            params.add(KEY_TASK_BN, taskId);
            return params;
        }
    }

    /**
     * Req - 收藏 -- 取消收藏<br>
     * 支持批量操作，id使用,分隔<br>
     * GET<br>
     * https://api.vsochina.com/req/favor/delete<br>
     * 参数<br>
     * <li>appid	String	必须，全局通用
     * <li>token	String	必须，全局通用
     * <li>username	String	必须，用户名
     * <li>task_bn	String	必须，需求编号（新版），支持批量（使用,分隔）
     * 参数格式：<br>
     * ?appid=XXX&token=XXX&task_bn=
     * <p/>
     * to see model at {@link com.lht.cloudjob.mvp.model.DiscollectTaskModel}
     * to see ResBean at {@link com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean}
     */
    class DiscollectTaskApi extends AbsRestApi {
        private static final String KEY_USERNAME = "username";
        private static final String KEY_TASK_BN = "task_bn";

        @Override
        protected String getUnformatedPath() {
            return "req/favor/delete";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String user, ArrayList<String> tasks) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, user);
            if (tasks == null) {
                return params;
            }
            StringBuilder builder = new StringBuilder();
            for (String s : tasks) {
                builder.append(s).append(",");
            }
            String task_bns = builder.substring(0, builder.length() - 1);
            params.add(KEY_TASK_BN, task_bns);
            return params;
        }

        public RequestParams newRequestParams(String user, String taskId) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, user);
            params.add(KEY_TASK_BN, taskId);
            return params;
        }
    }

    /**
     * Req - 需求 -- 我承接的需求列表<br>
     * Get<br>
     * https://api.vsochina.com/req/undertake/list<br>
     * 参数:<br>
     * <p/>
     * <li>username	String	必须，用户名</li>
     * <li>type	String	<br>
     * 可选，类型，默认all
     * （all=>全部，bidding=>已投标，selected=>已中标，deliver=>交付中，to_evaluate=>待评价）</br></li>
     * <li>offset	Number	可选，偏移量，默认0</li>
     * <li>limit	Number	可选，数据条数限制，默认10</li>
     */
    class OrderedTaskListApi extends AbsRestApi {
        private static final String KEY_USERNAME = "username";
        private static final String KEY_OFFSET = "offset";
        private static final String KEY_LIMIT = "limit";
        private static final String KEY_TYPE = "type";

        private static final int DEFAULT_LIMIT = 20;

        @Override
        protected String getUnformatedPath() {
            return "req/undertake/list";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String usr, Type type, int offset) {
            return this.newRequestParams(usr, type, offset, DEFAULT_LIMIT);
        }

        public RequestParams newRequestParams(String usr, Type type, int offset, int limit) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, usr);
            params.add(KEY_OFFSET, String.valueOf(offset));
            params.add(KEY_LIMIT, String.valueOf(limit));
            params.add(KEY_TYPE, type.getValue());
            return params;
        }

        public enum Type {
            /**
             * all=>全部
             */
            All("all"),

            /**
             * bidding=>已投标
             */
            Bidding("bidding"),

            /**
             * selected=>已中标
             */
            Selected("selected"),

            /**
             * deliver=>交付中
             */
            Deliver("deliver"),

            /**
             * to_evaluate=>待评价
             */
            Unevaluated("to_evaluate");

            private final String value;

            Type(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }
        }
    }

    /**
     * Message - 消息列表，按类型分组<br>
     * 消息首页显示内容<br>
     * GET<br>
     * https://api.vsochina.com/message/message/list-by-type<br>
     * 参数<br>
     * <li> appid	String 必须，全局通用
     * <li> token	String 必须，全局通用
     * <li> username	String 必须，用户名
     * <p/>
     * 参数格式:<br>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "username" => "admin"
     * ]
     * to see ResBean at{@link UnreadMsgResBean}
     * to see Model at{@link UnreadMessageModel}
     */
    class UnreadMessageApi extends AbsRestApi {

        private static final String KEY_USERNAME = "username";
        private static final String KEY_SITEDOMAIN = "sitedomain";
        private static final String VALUE_SITE_DOMAIN_CLOUDJOB = "1002";

        @Override
        protected String getUnformatedPath() {
            return "message/message/list-by-type";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String usr) {
            RequestParams params = newRequestParams();
            params.add(KEY_USERNAME, usr);
            params.add(KEY_SITEDOMAIN, VALUE_SITE_DOMAIN_CLOUDJOB);
            return params;
        }
    }

    /**
     * Message - 消息阅读<br>
     * 消息阅读，支持批量，用于批量修改消息为已读<br>
     * GET<br>
     * https://api.vsochina.com/message/message/read<br>
     * 参数<br>
     * <li> appid	String	必须，全局通用
     * <li> token	String	必须，全局通用
     * <li> id	String	必须，消息编号，支持批量（使用,分隔）
     * <p/>
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.MarkMsgReadModel}
     */
    class MarkMsgReadApi extends AbsRestApi {

        private static final String KEY_ID = "id";

        @Override
        protected String getUnformatedPath() {
            return "message/message/read";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(ArrayList<String> ids) {
            RequestParams params = super.newRequestParams();
            if (ids == null) {
                ids = new ArrayList<>();
            }
            StringBuilder sb = new StringBuilder();
            for (String s : ids) {
                sb.append(s).append(",");
            }
            String _ids = sb.substring(0, sb.length() - 1);
            params.add(KEY_ID, _ids);
            return params;
        }
    }

    /**
     * 支持批量操作，id使用,分隔<br>
     * GET<br>
     * https://api.vsochina.com/message/message/delete<br>
     * 参数<br>
     * <li> appid	String	必须，全局通用
     * <li> token	String	必须，全局通用
     * <li> id	String	必须，消息编号，支持批量（使用,分隔）
     * <p/>
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.DeleteMsgModel}
     */
    class DeleteMsgApi extends AbsRestApi {

        private static final String KEY_ID = "id";

        @Override
        protected String getUnformatedPath() {
            return "message/message/delete";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(ArrayList<String> ids) {
            RequestParams params = super.newRequestParams();
            if (ids == null) {
                ids = new ArrayList<>();
            }
            StringBuilder sb = new StringBuilder();
            for (String s : ids) {
                sb.append(s).append(",");
            }
            String _ids = sb.substring(0, sb.length() - 1);
            params.add(KEY_ID, _ids);
            return params;
        }
    }


    /**
     * Message - 消息列表，通用，不区分类型<br>
     * 获取用户的消息列表<br>
     * POST<br>
     * https://api.vsochina.com/message/message/get-user-message-list<br>
     * 参数<br>
     * <li> appid	String	必须，全局通用
     * <li> token	String	必须，全局通用
     * <li> username	String	必须，用户名
     * <li> limit	Number	 可选，分页单页条目数上限
     * <li> offset	Number	可选，分页起始条目索引
     * <li> msg_type	String	可选，消息类型，默认system（system=>系统消息，appmsg=>应用消息，inbox=>私人消息）
     * <p/>
     * 参数格式:<br>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "username" => "admin",
     * "offset" => 0,
     * "limit" => 10,
     * "msg_type" => "system"
     * ]
     * to see Model at{@link com.lht.cloudjob.mvp.model.MessageListModel}
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.MessageListItemResBean}
     */
    class MessageListApi extends AbsRestApi {

        private static final String KEY_USERNAME = "username";

        private static final String KEY_LIMIT = "limit";

        private static final String KEY_OFFSET = "offset";

        private static final String KEY_MSG_TYPE = "msg_type";
        private static final String KEY_SITEDOMAIN = "sitedomain";

        private static final String VALUE_SITE_DOMAIN_CLOUDJOB = "1002";

        public enum MessageType {
            SYSTEM("system"), NOTIFY("appmsg"), PRIVATE("inbox");
            private final String value;

            MessageType(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }
        }

        public RequestParams newRequestParams(String user, int offset, MessageType type) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, user);
            params.add(KEY_LIMIT, "10");
            params.add(KEY_OFFSET, String.valueOf(offset)); //offset 是取得本地整体数量，不是按照页数算的
            params.add(KEY_MSG_TYPE, type.getValue());
            params.add(KEY_SITEDOMAIN, VALUE_SITE_DOMAIN_CLOUDJOB);
            return params;
        }

        @Override
        protected String getUnformatedPath() {
            return "message/message/get-user-message-list";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }
    }

    /**
     * Req - 需求 -- 详情<br>
     * GET<br>
     * https://api.vsochina.com/req/info/view<br>
     * 参数<br>
     * <li> appid	String	必须，全局通用
     * <li> token	String	必须，全局通用
     * <li> task_bn	Number	必须，需求编号
     * <p/>
     * <li> username	String	可选，当前访问者用户名，用于关注状态的查询（发布者，需求）
     * <p/>
     * to see ResBean at {@link com.lht.cloudjob.mvp.model.bean.DemandInfoResBean}
     * to see Model at {@link com.lht.cloudjob.mvp.model.DemandInfoModel}
     */
    class DemandInfoApi extends AbsRestApi {

        private static final String KEY_USERNAME = "username";
        private static final String KEY_TASK_BN = "task_bn";
        private static final String KEY_TRANSHTML = "html2text";

        @Override
        protected String getUnformatedPath() {
            return "req/info/view";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String task_bn, String user) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, user);
            params.add(KEY_TASK_BN, task_bn);
            params.add(KEY_TRANSHTML, "1");
            return params;
        }

        public RequestParams newRequestParams(String task_bn) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_TASK_BN, task_bn);
            params.add(KEY_TRANSHTML, "1");
            return params;
        }
    }

    /**
     * Req - 需求 -- 稿件列表<br>
     * 中标者的稿件排第一位<br>
     * 自己投稿的默认在第二个位置<br>
     * 可重复投稿，多次投稿的，按照投稿时间倒序排列<br>
     * 其他人的投稿按照投稿时间倒序排列，且其他用户投稿的默认不显示<br>
     * 需求发布者可以看到所有稿件，包括隐藏的<br>
     * 投稿者可以看到自己投稿的隐藏稿件<br>
     * 访客or其他用户无法看到隐藏稿件<br>
     * GET<br>
     * https://api.vsochina.com/req/work/list<br>
     * 参数<br>
     * <li> appid	String	必须，全局通用
     * <li> token	String	必须，全局通用
     * <li> task_bn	Number	必须，需求编号
     * <li> username	String	可选，当前访问者用户名，用于稿件是否隐藏的处理
     * <li> offset	Number	可选，数据偏移量，默认0
     * <li> limit	Number	可选，数据条数，默认10
     */
    class DemandWorksListApi extends AbsRestApi {

        private static final String KEY_USERNAME = "username";
        private static final String KEY_OFFSET = "offset";
        private static final String KEY_LIMIT = "limit";

        private static final String KEY_TASK_BN = "task_bn";

        private static final int DEFAULT_LIMIT = 20;


        @Override
        protected String getUnformatedPath() {
            return "req/work/list";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String taskBn, int offset) {
            return this.newRequestParams(null, taskBn, offset, DEFAULT_LIMIT);
        }

        public RequestParams newRequestParams(String usr, String taskBn, int offset) {
            return this.newRequestParams(usr, taskBn, offset, DEFAULT_LIMIT);
        }

//        public RequestParams newRequestParams(String taskBn, int offset, int limit) {
//            return this.newRequestParams(null, taskBn, offset, limit);
//        }

        public RequestParams newRequestParams(String usr, String taskBn, int offset, int limit) {
            RequestParams params = super.newRequestParams();
            if (!StringUtil.isEmpty(usr)) {
                params.add(KEY_USERNAME, usr);
            }
            params.add(KEY_TASK_BN, taskBn);
            params.add(KEY_OFFSET, String.valueOf(offset));
            params.add(KEY_LIMIT, String.valueOf(limit));
            return params;
        }
    }

    /**
     * Req - 需求推荐，猜你喜欢<br>
     * 使用场景：需求大厅app<br>
     * 已经选稿，公示，交付，已结束的不做推荐<br>
     * GET<br>
     * https://api.vsochina.com/req/recom/list-prefer<br>
     * 参数: <br>
     * <li>appid	String  必须，全局通用
     * <li>token	String 必须，全局通用
     * <li>username	String 可选，用户名，给该用户推荐
     * <li>offset	Number 可选，数据偏移量，默认0
     * <li>limit	Number 可选，数据条数，默认10
     * <p/>
     * 参数格式:<br>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "username" => "admin"
     * ]
     * <p/>
     * to see ResBean at{@link com.lht.cloudjob.mvp.model.bean.RecommendTaskResBean}
     * to see Model at{@link com.lht.cloudjob.mvp.model.RecommendTaskListModel}
     */
    class RecommendTaskListApi extends AbsRestApi {

        private static final String KEY_USERNAME = "username";
        private static final String KEY_OFFSET = "offset";
        private static final String KEY_LIMIT = "limit";

        private static final int DEFAULT_LIMIT = 20;

        @Override
        protected String getUnformatedPath() {
            return "req/recom/list-prefer";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }


        public RequestParams newRequestParams(String usr, int offset) {
            return this.newRequestParams(usr, offset, DEFAULT_LIMIT);
        }

        public RequestParams newRequestParams(String usr, int offset, int limit) {
            RequestParams params = super.newRequestParams();
            if (!StringUtil.isEmpty(usr)) {
                params.add(KEY_USERNAME, usr);
            }
            params.add(KEY_OFFSET, String.valueOf(offset));
            params.add(KEY_LIMIT, String.valueOf(limit));
            return params;
        }
    }


    /**
     * <br>Message - 发送短信，username + mobile</br>
     * <br>redis中，存储username和valid_code，因为用户手机号存在更换的情况</br>
     * 专用于绑定新手机
     * <br>POST</br>
     * <br>/message/mobile/send-mobile-valid-code</br>
     * <br> 参数</br>
     * <p/>
     * <li>username	String 必须，用户名</li>
     * <li> mobile	String 必须，手机号</li>
     * <p/>
     * <br>参数格式:</br>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "username" => "admin",
     * "mobile" => 13800138000,
     * ]
     */
    class SendSmsOnBindPhoneApi extends AbsRestApi {

        private static final String KEY_USERNAME = "username";

        private static final String KEY_PHONE = "mobile";

        @Override
        protected String getUnformatedPath() {
            return "message/mobile/send-mobile-valid-code";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String phone, String user) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_PHONE, phone);
            params.add(KEY_USERNAME, user);
            return params;
        }
    }

    /**
     * Auth - 手机认证 -- 更新  绑定新手机<br>
     * 前置条件：<br>
     * 1、发送验证码短信（旧手机号），接口地址：/message/mobile/send-mobile-valid-code<br>
     * 2、验证短信验证码，接口地址：/message/mobile/check-valid-code-by-mobile<br>
     * 3、验证手机号是否可用（新手机号），接口地址：/user/auth/is-available-mobile<br>
     * 4、发送验证码短信（新手机号），接口地址：/message/mobile/send-mobile-valid-code<br>
     * 5、验证短信验证码，接口地址：/message/mobile/check-valid-code-by-mobile<br>
     * POST<br>
     * https://api.vsochina.com/auth/mobile/update<br>
     * 参数<br>
     * <li> appid	String 必须，全局通用
     * <li> token	String 必须，全局通用
     * <li> username	String 必须，用户名
     * <li> mobile	String 必须，手机号
     * <p/>
     * 参数格式: <br>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "username" => "xx",
     * "mobile" => "xx"
     * ]
     * to see Model at{@link UpdateBindPhoneModel}
     * to see ResBean at {@link com.lht.cloudjob.mvp.model.bean.BaseVsoApiResBean}
     */
    class UpdateBindPhoneApi extends AbsRestApi {

        private static final String KEY_PHONE = "mobile";

        private static final String KEY_USERNAME = "username";

        @Override
        protected String getUnformatedPath() {
            return "auth/mobile/update";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String user, String phone) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_PHONE, phone);
            params.add(KEY_USERNAME, user);
            return params;
        }
    }

    /**
     * Auth - 手机认证 -- 添加
     * POST
     * 前置条件：
     * 1、验证手机号是否可用，接口地址：/user/auth/is-available-mobile
     * 2、发送验证码短信，接口地址：/message/mobile/send-message-by-mobile
     * 3、验证短信验证码，接口地址：/message/mobile/check-valid-code-by-mobile
     * <p/>
     * /auth/mobile/create
     * 参数<br>
     * <li> username	String 必须，用户名
     * <li> mobile	String 必须，手机号
     * <p/>
     * 参数格式: <br>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "username" => "admin",
     * "mobile" => "1391857799"
     * ]
     */
    class CreateBindPhoneApi extends AbsRestApi {

        private static final String KEY_PHONE = "mobile";

        private static final String KEY_USERNAME = "username";

        @Override
        protected String getUnformatedPath() {
            return "auth/mobile/create";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String user, String phone) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_PHONE, phone);
            params.add(KEY_USERNAME, user);
            return params;
        }

    }

    /**
     * 检查登录状态
     * https://api.vsochina.com/user/user/login-status
     * <p/>
     * to see Model at{@link CheckLoginStateModel}
     */
    class CheckLoginState extends AbsRestApi {
        private static final String KEY_USERNAME = "username";
        private static final String KEY_TOKEN = "vso_token";

        @Override
        protected String getUnformatedPath() {
            return "user/user/login-status";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String userName, String token) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, userName);
            params.add(KEY_TOKEN, token);
            return params;
        }
    }

    /**
     * Req - 需求承接，权限校验
     * <p/>
     * 0.0.0
     * https://api.vsochina.com/req/undertake/check-permission
     * 参数
     * 字段	类型	描述
     * appid	String  必须，全局通用
     * <p/>
     * token	String 必须，全局通用
     * <p/>
     * username	String 必须，用户名
     * <p/>
     * lang	String 可选，语言，默认zh-CN
     * zh-CN => 简体中文
     * zh-TW => 繁体中文
     * en => 英文
     */
    class UndertakeCheckPermission extends AbsRestApi {
        private static final String KEY_USERNAME = "username";

        @Override
        protected String getUnformatedPath() {
            return "req/undertake/check-permission";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String username) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, username);
            return params;
        }
    }


    /**
     * Req - 需求 -- 承接
     * POST
     * https://api.vsochina.com/req/undertake/create
     * 参数
     * <p> 字段	类型	描述
     * <li><li/> appid	String   必须，全局通用     <li/>
     * <li>token	String   必须，全局通用    <li/>
     * <li>task_bn	String   必须，需求编号    <li/>
     * <li>username	String    必须，用户名    <li/>
     * <li>description	String  必须，投稿描述    <li/>
     * <li>attachments	String   必须，附件    <li/>
     * <li>attachment_name	String  必须，附件原始文件名    <li/>
     * <li>days	Number  预计完成天数，按任务类型区分（悬赏可选，招标必须）    <li/>
     * <li>price	Number  报价，按任务类型区分（暗标必须）    <li/>
     * <li>sms_flag	Boolean  可选，投稿成功后是否发送站内信，默认false    <li/>
     * <li>lang	String  可选，语言，默认zh-CN    <li/>
     * zh-CN => 简体中文
     * zh-TW => 繁体中文
     * en => 英文
     * 参数格式:
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "task_bn" => "",
     * "username" => ""
     * ]
     * to see ResBean at{@link BaseVsoApiResBean}
     * to see Model at{@link UndertakeModel}
     */
    class UndertakeApi extends AbsRestApi {
        private static final String KEY_TASK_BN = "task_bn";
        private static final String KEY_USERNAME = "username";
        private static final String KEY_DESCRIPTION = "description";
        private static final String KEY_ATTACHMENTS = "attachments";
        private static final String KEY_ATTACHMENTS_NAME = "attachment_name";
        private static final String KEY_DAYS = "days";
        private static final String KEY_PRICE = "price";
        private static final String KEY_IS_MARK = "is_mark";
        private static final String KEY_SMS_FLAG = "sms_flag";

        private static final String VALUE_UNHIDDEN = "1";
        private static final String VALUE_HIDDEN = "2";

        @Override
        protected String getUnformatedPath() {
            return "req/undertake/create";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(LetterBean letterBean) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, letterBean.getUsername());
            params.add(KEY_DESCRIPTION, letterBean.getDescription());
            params.add(KEY_TASK_BN, letterBean.getTask_bn());
//            params.add(KEY_ANALYZE_DOMAIN,VALUE_ANALYZE_DOMAIN_CLOUDJOB_ANDROID);

            //不按照业务区分了，统一放，哪怕null
            params.add(KEY_ATTACHMENTS, letterBean.getAttachments());


            params.add(KEY_ATTACHMENTS_NAME, letterBean.getAttachment_name());
            params.add(KEY_DAYS, letterBean.getDays());
            params.add(KEY_PRICE, letterBean.getPrice());
            if (letterBean.is_mark()) {
                params.add(KEY_IS_MARK, VALUE_UNHIDDEN);
            } else {
                params.add(KEY_IS_MARK, VALUE_HIDDEN);
            }
            params.add(KEY_SMS_FLAG, String.valueOf(letterBean.isSms_flag()));
            return params;
        }
    }

    /**
     * Auth - 企业认证 -- 查询
     * <p/>
     * GET
     * https://api.vsochina.com/auth/enterprise/view
     * 参数
     * <p/>
     * 字段	类型	描述
     * appid	String 必须，全局通用
     * <p/>
     * token	String 必须，全局通用
     * <p/>
     * username	String 必须，用户名
     * <p/>
     * lang	String 可选，语言，默认zh-CN
     * zh-CN => 简体中文
     * zh-TW => 繁体中文
     * en => 英文
     * <p/>
     * to see Model at{@link EAuthenticQueryModel}
     * to see ResBean at{@link EAuthenticQueryResBean}
     */
    class QueryEAuthenticApi extends AbsRestApi {
        private static final String KEY_USERNAME = "username";

        @Override
        protected String getUnformatedPath() {
            return "auth/enterprise/view";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String username) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, username);

            return params;
        }
    }

    /**
     * Req - 需求列表 -- 最新成交
     * redis缓存5分钟
     * 排序：中标时间倒序
     * 场景：需求大厅app
     * post
     * https://api.vsochina.com/req/search/list-bid-latest
     * 参数
     * appid	String	必须，全局通用
     * token	String	必须，全局通用
     * offset	Number	可选，数据偏移量
     * limit	Number	可选，数据条数，默认10
     * 参数格式:
     * [
     * "appid" => "XXX",
     * "token" => "XXX"
     * ]
     */
    class GlobalNotifyApi extends AbsRestApi {
        @Override
        protected String getUnformatedPath() {
            return "req/search/list-bid-latest";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }
    }


    /**
     * Auth - 省市区 -- 认证管理
     * 使用redis缓存，缓存时间一个月
     * GET
     * https://api.vsochina.com/auth/area/list
     */
    class LocationsApi extends AbsRestApi {

        @Override
        protected String getUnformatedPath() {
            return "auth/area/list";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }


    }

    /**
     * Req - 评价 -- 发表评价
     * <p/>
     * 0.0.0
     * 前置条件：  获取评价配置项：/req/mark-config/view
     * <p/>
     * https://api.vsochina.com/req/mark/create
     * 参数
     * <p/>
     * 字段	类型	描述
     * appid	String
     * 必须，全局通用
     * <p/>
     * token	String
     * 必须，全局通用
     * <p/>
     * task_bn	Number
     * 必须，需求编号
     * <p/>
     * username	String
     * 必须，评价人
     * <p/>
     * items	Array
     * 必须，评分项
     * key => value，key为aid编号，value为评分（5分制）
     * 根据前置条件中返回的评价项给分，具体参见评价配置项
     * <p/>
     * content	String
     * 必须，评价内容
     */
    class Comment extends AbsRestApi {
        private static final String KEY_TASK_BN = "task_bn";
        private static final String KEY_USERNAME = "username";
        private static final String KEY_CONTENT = "content";


        @Override
        protected String getUnformatedPath() {
            return "req/mark/create";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String task_bn, String username,
                                              HashMap<String, Integer> map, String content) {
            RequestParams params = super.newRequestParams();

            params.add(KEY_TASK_BN, task_bn);
            params.add(KEY_USERNAME, username);
            params.add(KEY_CONTENT, content);

            params.put("items", map);
//            Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry entry = (Map.Entry) iterator.next();
//                String key = (String) entry.getKey();
//                Integer value = (Integer) entry.getValue();
//                String format = "items[%s]";
//                String paramKey = String.format(format, key);
//                Log.e("paramKey", paramKey + "======" + value);
//                params.add(paramKey, String.valueOf(value));
//
//            }
            return params;
        }
    }

    /**
     * Req - 评价 -- 获取评价配置项
     * <p/>
     * <p/>  https://api.vsochina.com/req/mark-config/view
     * 参数
     * <p/> 字段	类型	描述
     * appid	String 必须，全局通用
     * <p/>
     * token	String  必须，全局通用
     * <p/>
     * task_bn	Number  必须，需求编号
     * <p/>
     * username	String  必须，评价人
     * <p/>
     * lang	String 可选，语言，默认zh-CN
     * zh-CN => 简体中文
     * zh-TW => 繁体中文
     * en => 英文
     */
    class GetCommentConfig extends AbsRestApi {
        private final String KEY_TASK_BN = "task_bn";
        private final String KEY_USERNAME = "username";

        @Override
        protected String getUnformatedPath() {
            return "req/mark-config/view";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newnewRequestParams(String task_bn, String username) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_TASK_BN, task_bn);
            params.add(KEY_USERNAME, username);
            return params;
        }
    }

    /**
     * <br> Req - 发现首页，banner推荐</br>
     * <br>GET</br>
     * <br>后台运营人员配置</br>
     * <br>接口中心redis缓存半小时</br>
     * <br>https://api.vsochina.com/req/recom/list-banner</br>
     * <br>参数:</br>
     * <li>limit	String 数据条数限制，默认3</li>
     * 参数格式:
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "keyword" => "搜索"
     * ]
     */
    class BannerDataApi extends AbsRestApi {

        private static final String KEY_LIMIT = "limit";
        private static final String DEFAULT_LIMIT = "4";

        @Override
        protected String getUnformatedPath() {
            return "req/recom/list-banner";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams() {
            RequestParams params = super.newRequestParams();
            params.add(KEY_LIMIT, DEFAULT_LIMIT);
            return params;
        }
    }


    /**
     * <br>Tools</br>
     * <br>Tools - 提交意见反馈</br>
     * <br>目前用于任务大厅app中提交意见反馈</br>
     * <br>POST</br>
     * <br>https://api.vsochina.com/tools/feedback/create</br>
     * <br>参数</br>
     * <p/>
     * <li>content	String	必须，反馈内容，意见or建议</li>
     * <li> mobile	String	必填，手机号，联系方式</li>
     * <li>username	String	可选，反馈人用户名</li>
     * <li>file	String	可选，附件编号，逗号分隔的字符串，附件请走接口上传</li>
     * <li>phone_model	String	可选，手机型号</li>
     * <li>system_model	String	可选，系统型号</li>
     * <li> version	String	可选，版本</li>
     * <li>site	Number	可选，渠道来源 1009 => app</li>
     */
    class FeedbackApi extends AbsRestApi {
        private static final String KEY_CONTENT = "content";
        private static final String KEY_MOBILE = "mobile";
        private static final String KEY_USERNAME = "username";
        private static final String KEY_FILE = "file";
        private static final String KEY_VERSION = "version";
        private static final String KEY_SITE = "site";


        @Override
        protected String getUnformatedPath() {
            return "tools/feedback/create";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String username, String content, String contact,
                                              String files) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, username);
            params.add(KEY_CONTENT, content);
            params.add(KEY_MOBILE, contact);
            params.add(KEY_VERSION, VersionUtil.getVersion(MainApplication.getOurInstance()));
            params.add(KEY_SITE, "1009");
            if (!StringUtil.isEmpty(files)) {
                params.add(KEY_FILE, files);
            }
            return params;
        }
    }

    /**
     * <br>User - 用户基本信息，订阅查询</br>
     * <br>GET</br>
     * <br>/user/info/view-req-indus</br>
     * <br>参数</br>
     * <li>username	String	必须，用户帐号</li>
     */
    class QuerySubscribeApi extends AbsRestApi {

        private static final String KEY_USERNAME = "username";

        @Override
        protected String getUnformatedPath() {
            return "user/info/view-req-indus";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String usr) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, usr);
            return params;
        }
    }

    /**
     * <br>User - 用户基本信息，修改需求大厅所属行业</br>
     * <br>POST<br>
     * <br>/user/info/update-req-indus<br>
     * <p/>
     * <li>username	String	必须，用户帐号</li>
     * <li>indus_id	String	必须，新版二级行业编号（逗号,分隔，不限制行业个数）</li>
     * <p/>
     * <br>参数格式:</br>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "username" => "admin",
     * "indus_id" => "8,2"
     * ]
     */
    class SubscribeApi extends AbsRestApi {
        private static final String KEY_USERNAME = "username";
        private static final String KEY_INDUSTRYS = "indus_id";

        @Override
        protected String getUnformatedPath() {
            return "user/info/update-req-indus";
        }

        /**
         * @return null if do not need to format qs
         */
        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String usr, String ids) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, usr);
            params.add(KEY_INDUSTRYS, ids);
            return params;
        }
    }

    /**
     * Req - 协议 -- 签署
     * <p/>
     * 0.0.0
     * 目前签署协议只针对乙方
     * <p/> https://api.vsochina.com/req/protocol/sign
     * 参数
     * 字段	类型	描述
     * appid	String  必须，全局通用
     * <p/>
     * token	String  必须，全局通用
     * <p/>
     * task_bn	Number  必须，需求编号
     * <p/>
     * username	String  必须，用户名
     * <p/>
     * flag_agree	Boolean  可选，是否同意，默认false
     * <p/>
     * lang	String
     * 可选，语言，默认zh-CN
     * zh-CN => 简体中文
     * zh-TW => 繁体中文
     * en => 英文
     * to see model at{@link SignAgreementModel}
     */
    class SignAgreementApi extends AbsRestApi {

        private static final String KEY_TASK_BN = "task_bn";
        private static final String KEY_USERNAME = "username";
        private static final String KEY_FLAG_AGREE = "flag_agree";

        @Override
        protected String getUnformatedPath() {
            return "req/protocol/sign";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String task_bn, String username, boolean flag_agree) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_TASK_BN, task_bn);
            params.add(KEY_USERNAME, username);
//            params.add(KEY_FLAG_AGREE, String.valueOf(flag_agree));
            params.add(KEY_FLAG_AGREE, getFlagAlias(flag_agree));
            return params;
        }

        private String getFlagAlias(boolean flag) {
            return flag ? "1" : "0";
        }
    }

    /**
     * 精彩活动
     * GET
     */
    class WonderfulActivitiesApi extends AbsRestApi {

        private static final String DEFAULT_PAGESIZE = "20";
        private static final String KEY_OFFSET = "offset";
        private static final String KEY_LIMIT = "limit";

        @Override
        protected String getUnformatedPath() {
            return "message/message/get-activity-push-list";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(int offset) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_OFFSET, String.valueOf(offset));
            params.add(KEY_LIMIT, DEFAULT_PAGESIZE);
            return params;
        }

        public RequestParams newRequestParams(int offset, int pageSize) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_OFFSET, String.valueOf(offset));
            params.add(KEY_LIMIT, String.valueOf(pageSize));
            return params;
        }
    }

    /**
     * Req - 协议 -- 查看
     * <p>
     * 0.0.0
     * https://api.vsochina.com/req/protocol/view
     * 参数
     * <p>
     * 字段	类型	描述
     * appid	String 必须，全局通用
     * <p>
     * token	String  必须，全局通用
     * <p>
     * task_bn	Number 必须，需求编号
     * <p>
     * username	String  必须，当前访问者用户名
     * <p>
     * lang	String
     * 可选，语言，默认zh-CN
     * zh-CN => 简体中文
     * zh-TW => 繁体中文
     * en => 英文
     * <p>
     * to see model at{@link ReadAgreementModel}
     * to see bean at{@link ReadAgreementResBean}
     */
    class ReadAgreementApi extends AbsRestApi {

        private static final String KEY_TASK_BN = "task_bn";
        private static final String KEY_USERNAME = "username";

        @Override
        protected String getUnformatedPath() {
            return "req/protocol/view";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String task_bn, String username) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_TASK_BN, task_bn);
            params.add(KEY_USERNAME, username);
            return params;
        }
    }

    /**
     * 发布需求
     * v1/short_message_demand
     */
    class PublishDemandApi extends AbsReqRestApi {

        private static final String KEY_DEMAND_DESCRIBE = "short_msg";
        private static final String KEY_DEMAND_PRICE = "task_cash";
        private static final String KEY_DEMAND_CONTACT = "mobile";
//        private static final String KEY_DEMAND_SOURCEID = "source_id";
        private static final String KEY_DEMAND_FILE_PATH = "file_path";
        private static final String KEY_DEMAND_FILE_NAME = "file_name";
        private static final String KEY_DEMAND_FILE_EXT = "file_ext";
        private static final String KEY_DEMAND_FILE_SIZE = "file_size";

        @Override
        protected String getUnformatedPath() {
            return "v1/short_message_demand";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String demandDescribe, String price, String contact,
                                              ArrayList<DemandAttachFile> attachFiles) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_DEMAND_DESCRIBE, demandDescribe);
            params.add(KEY_DEMAND_PRICE, price);
            params.add(KEY_DEMAND_CONTACT, contact);
//            params.add(KEY_DEMAND_SOURCEID, "3");//app
            if (attachFiles != null && !attachFiles.isEmpty()) {
                StringBuilder builderPath = new StringBuilder();
                StringBuilder builderName = new StringBuilder();
                StringBuilder builderExt = new StringBuilder();
                StringBuilder builderSize = new StringBuilder();
                for (DemandAttachFile file : attachFiles) {
                    builderPath.append(file.getFile_path()).append(",");
                    builderName.append(file.getFile_name()).append(",");
                    builderExt.append(file.getFile_ext()).append(",");
                    builderSize.append(file.getFile_size()).append(",");
                }
                params.add(KEY_DEMAND_FILE_NAME, removeRedundantEnding(builderName));
                params.add(KEY_DEMAND_FILE_EXT, removeRedundantEnding(builderExt));
                params.add(KEY_DEMAND_FILE_PATH, removeRedundantEnding(builderPath));
                params.add(KEY_DEMAND_FILE_SIZE, removeRedundantEnding(builderSize));
            }
            return params;
        }

        private String removeRedundantEnding(StringBuilder stringBuilder) {
            if (stringBuilder == null || stringBuilder.length() == 0) {
                return "";
            } else {
                return stringBuilder.substring(0, stringBuilder.length() - 1);
            }
        }
    }
}
