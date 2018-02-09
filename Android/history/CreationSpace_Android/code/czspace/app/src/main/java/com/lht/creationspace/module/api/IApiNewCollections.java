package com.lht.creationspace.module.api;

import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.base.model.apimodel.BaseVsoApiResBean;
import com.lht.creationspace.module.setting.model.FeedbackModel;
import com.lht.creationspace.module.user.info.model.CreateBindPhoneModel;
import com.lht.creationspace.module.user.info.model.InfoModifyModel;
import com.lht.creationspace.module.user.info.model.QueryBasicInfoModel;
import com.lht.creationspace.module.user.info.model.UpdateBindPhoneModel;
import com.lht.creationspace.module.user.login.model.CheckLoginStateModel;
import com.lht.creationspace.module.user.login.model.LoginModel;
import com.lht.creationspace.module.user.login.model.TpLoginModel;
import com.lht.creationspace.module.user.login.model.pojo.LoginResBean;
import com.lht.creationspace.module.user.login.model.pojo.TpLoginResBean;
import com.lht.creationspace.module.user.register.model.RegisterModel;
import com.lht.creationspace.module.user.register.model.RoleChooseModel;
import com.lht.creationspace.module.user.register.model.TpRegWithOauthBindModel;
import com.lht.creationspace.module.user.register.model.pojo.RegisterResBean;
import com.lht.creationspace.module.user.security.model.ChangePwdModel;
import com.lht.creationspace.module.user.security.model.CheckNicknameModel;
import com.lht.creationspace.module.user.security.model.CheckPhoneModel;
import com.lht.creationspace.module.user.security.model.ResetPwdByAccountModel;
import com.lht.creationspace.module.user.security.model.ResetPwdByUsernameModel;
import com.lht.creationspace.module.user.security.model.SendSmsByUserModel;
import com.lht.creationspace.module.user.security.model.SendSmsModel;
import com.lht.creationspace.module.user.security.model.TpOauthCheckModel;
import com.lht.creationspace.module.user.security.model.VerifyCodeCheckModel;
import com.lht.creationspace.module.user.security.model.VerifyCodeCheckWithUserModel;
import com.lht.creationspace.social.oauth.TPOauthUserBean;
import com.lht.creationspace.util.phonebasic.SettingUtil;
import com.lht.creationspace.util.string.StringUtil;
import com.loopj.android.http.RequestParams;

/**
 * <p><b>Package:</b> com.lht.creationspace.interfaces.net </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> IApiNewCollections </p>
 * <p><b>Description:</b> 收集采用了auth的apinew公用接口 </p>
 * Created by leobert on 2017/3/20.
 */

public interface IApiNewCollections {


    /**
     * Auth - 省市区 -- 认证管理
     * 使用redis缓存，缓存时间一个月
     * GET
     * https://api.vsochina.com/auth/area/list
     */
    class LocationsApi extends IRestfulApi.ApiNewRestOauthApi {


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
     * <br> Device - 设备登录</br>
     * <br>POST</br>
     * <br>device/user/login</br>
     * <br>参数</br>
     * <p>
     * <li> username	String必须，用户名</li>
     * <li> registration_id	String 必须，极光推送用户ID</li>
     * <li> device_id	String	必须，设备号</li>
     * <li>type	Number 可选，设备类型（1=>ios，2=>android，3=>Web），默认1</li>
     */
    class DeviceBindApi extends IRestfulApi.ApiNewRestOauthApi {

        private static final String PATH = "device/user/login";

        private static final String KEY_USERNAME = "username";

        private static final String KEY_REGISTRATION_ID = "registration_id";

        private static final String KEY_TYPE = "type";

        private static final String KEY_DEVICE_ID = "device_id";


        /**
         * （1=>ios，2=>android，3=>Web）
         */
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
     * post
     * message/mobile/check-valid-code-by-mobile
     * 参数
     * <p/>
     * mobile  Number 必须，手机号
     * valid_code  Number 必须，验证码
     * <p/>
     * 参数格式：
     * [
     * "mobile" => "13800138000",
     * "valid_code" => 123456
     * ]
     * <p/>
     */
    class CheckVerifyCodeApi extends IRestfulApi.ApiNewRestOauthApi {

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

        public RequestParams newRequestParams(VerifyCodeCheckModel.VerifyCodeCheckData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_MOBILE, data.getPhone());
            params.add(KEY_CODE, data.getCode());
            return params;
        }
    }


    /**
     * post
     * api/vso/sms/send
     * message/mobile/send-message-by-mobile
     * <p/>
     * 参数:</br>
     * <li>appid String 必须，全局通用appid
     * <li>mobile  String 必须，手机号
     * <li>action  String 必须，操作类型
     * <p/>
     * valid_code_register => 注册验证码（使用紧急通道，每个手机号每天最多发送5条，此为服务商限制）
     * valid_code => 发送验证码，通用，非注册
     * reset_password => 重置密码
     * change_password => 修改登录密码
     * <p/>
     * to see ResBean at{@link BaseVsoApiResBean}
     * to see Model at{@link SendSmsModel}
     */
    class SendSmsApi extends IRestfulApi.ApiNewRestOauthApi {

        private static final String KEY_MOBILE = "mobile";
        private static final String KEY_ACTION = "action";

        private static final String STATIC_PARAM_VALID_CODE_REGISTER = "valid_code_register";

        private static final String STATIC_PARAM_BIND_PHONE = "valid_code_bind_mobile";

        private static final String STATIC_PARAM_RESET_PASSWORD = "reset_password";

//        private static final String STATIC_PARAM_CHANGE_PASSWORD = "change_password";

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
                case BindPhone:
                    return STATIC_PARAM_BIND_PHONE;
//                case Normal:
//                    return STATIC_PARAM_VALID_CODE;
                default:
                    return "error_action";
            }
        }
    }

    /**
     * VSO - 提交意见反馈
     * tools/feedback/create
     * 参数
     * 字段	类型	描述
     * content	String 必须，反馈内容，意见or建议
     * <p>
     * mobile_required	Boolean 标识位，手机号是否必填，默认true
     * <p>
     * mobile	String 手机号，联系方式（mobile_required为true时必填）
     * <p>
     * email	String 可选，手机号，联系方式
     * <p>
     * username	String 可选，反馈人用户名
     * <p>
     * file	String 可选，附件编号，逗号分隔的字符串，附件请走接口上传
     * <p>
     * phone_model	String  可选，手机型号
     * <p>
     * system_model	String 可选，系统型号
     * <p>
     * version	String 可选，版本
     * <p>
     * site	Number 可选，渠道来源（1=>Web，2=>App，3=>M站，4=>微站，其余参见渠道来源）
     * <p>
     * type	Number 可选，问题类型（1=>Bug反馈，2=>功能建议，3=>界面建议，4=>其他问题）
     * <p>
     * url	String 反馈页面路径
     * <p>
     * lang	String
     * 可选，语言，默认zh-CN
     * zh-CN => 简体中文
     * zh-TW => 繁体中文
     * en => 英文
     */
    class FeedbackApi extends IRestfulApi.ApiNewRestOauthApi {
        private static final String KEY_CONTENT = "content";
        private static final String KEY_MOBILE = "mobile";
        private static final String KEY_MOBILE_REQUIRED = "mobile_required";
        private static final String KEY_FILE = "file";
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

        public RequestParams newRequestParams(FeedbackModel.FeedbackData data,
                                              String files) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_CONTENT, data.getContent());
            params.add(KEY_MOBILE_REQUIRED, String.valueOf(data.isMobile_required()));
            params.add(KEY_MOBILE, data.getContact());
            // TODO: 2017/3/20 确认site
            params.add(KEY_SITE, "2");
            if (!StringUtil.isEmpty(files)) {
                params.add(KEY_FILE, files);
            }
            return params;
        }
    }


    /**
     * ## 【注册】手机号是否可用 ##
     * GET</br>
     * user/auth/is-available-mobile
     * <p/>
     * 参数：
     * - mobile 手机号
     * 返回码对照表：
     * 错误码 => 错误信息：
     * "13181" => "缺少手机号",
     * "13182" => "手机号不合法",
     * "13183" => "手机号被占用",
     * "0" => "成功"
     * to see ResBean at{@link BaseVsoApiResBean}
     * to see Model at{@link CheckPhoneModel}
     */
    class CheckPhoneApi extends IRestfulApi.ApiNewRestOauthApi {

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
     * User - 校验 -- 三方信息是否存在
     * GET
     * /api/vso/oauth/check
     * user/auth/is-available-oauth
     * 参数
     * 字段	类型	描述
     * via	String	必须，三方通道
     * openid	String	必须，身份验证
     */
    class TpOauthCheckApi extends IRestfulApi.ApiNewRestOauthApi {

        private static final String KEY_OPENID = "openid";

        private static final String KEY_VIA = "via";

        private static final String STATIC_PARAM_VIAQQ = "qq";

        private static final String STATIC_PARAM_VIAWECHAT = "weixin";

        private static final String STATIC_PARAM_VIASINA = "weibo";

        @Override
        protected String getUnformatedPath() {
            return "user/auth/is-available-oauth";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(TpOauthCheckModel.TpOauthCheckData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_OPENID, data.getUniqueId());
            String via;
            switch (data.getType()) {
                case TPOauthUserBean.TYPE_QQ:
                    via = STATIC_PARAM_VIAQQ;
                    break;
                case TPOauthUserBean.TYPE_SINA:
                    via = STATIC_PARAM_VIASINA;
                    break;
                case TPOauthUserBean.TYPE_WECHAT:
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
     * ## 修改用户基本信息 ##
     * POST</br>
     * user/info/update-user-basic-info
     * <p/>
     * 参数
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
     * to see ResBean at{@link BaseVsoApiResBean}
     * to see Model at{@link InfoModifyModel}
     */
    class ModifyInfoApi extends IRestfulApi.ApiNewRestOauthApi {

        private static final String KEY_USERNAME = "username";//username  String 必须，用户帐号
        private static final String KEY_NICKNAME = "nickname";//  * nickname  String 可选，昵称，最长20字符
        private static final String KEY_AVATAR = "avatar";//   * avatar  String 可选，用户头像，图片路径

        //        private static final String KEY_MOBILE = "mobile";//   * mobile  Number 可选，手机号
//
//        private static final String KEY_INDUSTRY = "indus_pid";//   * indus_pid Number
//        // 可选，用户所属行业，旧版行业编号
//        private static final String KEY_FIELD = "lable_name";//   * lable_name  String 可选，人才标签（,
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

//            /**
//             * 设置绑定手机
//             *
//             * @param mobile 手机号
//             * @return builder
//             */
//            @Deprecated
//            RequestParamsBuilder setMobile(String mobile);

//            /**
//             * 设置行业
//             *
//             * @param industryId 行业编号
//             * @return builder
//             */
//            RequestParamsBuilder setIndustry(int industryId);

//            /**
//             * 设置个人领域
//             *
//             * @param fieldLableIds 领域标签,旧版二级行业编号,数组，需格式化成","隔开的字符串
//             * @return builder
//             */
//            RequestParamsBuilder setField(int[] fieldLableIds);

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

//            private String mobile;
//
//            private int industryId;
//
//            private int[] fieldLables;

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

//            @Override
//            public RequestParamsBuilder setMobile(String mobile) {
//                this.mobile = mobile;
//                return this;
//            }
//
//            @Override
//            public RequestParamsBuilder setIndustry(int industryId) {
//                this.industryId = industryId;
//                return this;
//            }
//
//            /**
//             * 设置个人领域
//             *
//             * @param fieldLableIds 领域标签,旧版二级行业编号,数组，需格式化成","隔开的字符串
//             * @return builder
//             */
//            @Override
//            public RequestParamsBuilder setField(int[] fieldLableIds) {
//                this.fieldLables = fieldLableIds;
//                return this;
//            }


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

//                if (needProcess(mobile)) {
//                    params.add(KEY_MOBILE, mobile);
//                }
//
//                if (needProcess(industryId)) {
//                    params.add(KEY_INDUSTRY, String.valueOf(industryId));
//                }
//
//                if (needProcess(fieldLables)) {
//                    StringBuilder sb = new StringBuilder();
//                    for (int id : fieldLables) {
//                        sb.append(id).append(",");
//                    }
//                    String labels = sb.substring(0, sb.length() - 1);
//                    params.add(KEY_FIELD, labels);
//                }

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
     * User - 用户基础信息
     * GET<br>
     * user/info/view
     * <p/>
     * 参数
     * appid	String 必须，全局通用appid
     * <p/>
     * token	String 必须，全局通用token
     * <p/>
     * username	String 必须，用户帐号
     * <p/>
     * to see ResBean at{@link BaseVsoApiResBean}
     * to see Model at{@link QueryBasicInfoModel}
     */
    class BasicInfoApi extends IRestfulApi.ApiNewRestOauthApi {

        private static final String KEY_USERNAME = "username";

        @Override
        protected String getUnformatedPath() {
            return "user/info/view";
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

        public RequestParams newRequestParams(String username, String token) {
            final String KEY_AUTH_USER = "auth_username";
            final String KEY_AUTH_TOKEN = "auth_token";
            RequestParams params = new RequestParams();
            params.add(KEY_USERNAME, username);
            params.add(KEY_AUTH_USER, username);
            params.add(KEY_AUTH_TOKEN, token);

            return params;
        }
    }


    /**
     * 新版-用户登录
     * POST
     * user/login/index
     * 参数：
     * <p/>
     * <li>name	String	必须，账号（用户名/手机号/邮箱）
     * <li> password	String	必须，密码
     * <li>prom_profit	Boolean	可选，推广上线分钱（默认false）
     * <li> ip	String	用户ip地址
     * 参数格式:<br>
     * [
     * "appid" => "XXX",
     * "token" => "XXX",
     * "name" => "XXX",
     * "password" => 'xxx'
     * ]
     * to see ResBean at{@link LoginResBean}
     * to see Model at{@link LoginModel}
     */
    class LoginApi extends IRestfulApi.ApiNewRestOauthApi {

        private static final String PATH = "user/login/index";

        private static final String KEY_NAME = "name";

        private static final String KEY_PASSWORD = "password";

        private static final String KEY_CLIENT_ID = "client_id";
        private static final String VALUE_CLIENT_ID = "flag";

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
            params.add(KEY_CLIENT_ID, VALUE_CLIENT_ID);
            params.add("auto_login", "1");
            return params;
        }
    }


    /**
     * /user/login/logout
     * POST
     * 参数
     * vso_uid	String 必须，用户编号（UC）
     * vso_sess	String 必须，session，存在cookie中
     * vso_token	String 必须，用户登录后的token，存在cookie中
     * <p/>
     */
    class LogoutApi extends IRestfulApi.ApiNewRestOauthApi {

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

    }

    /**
     * VSO - 用户登录 -- 三方
     * POST
     * user/login/oauth
     * 参数
     * 字段	类型	描述
     * via	String
     * 必须，三方通道（facebook，google，qq，twitter，weibo，weixin）
     * <p>
     * openid	String
     * 必须，身份验证
     * to see ResBean at{@link TpLoginResBean}
     * to see Model at{@link TpLoginModel}
     */
    class TpLoginApi extends IRestfulApi.ApiNewRestOauthApi {

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

        private static final String KEY_CLIENT_ID = "client_id";
        private static final String VALUE_CLIENT_ID = "flag";
        private static final String KEY_AUTO_LOGIN = "auto_login";
        private static final String VALUE_TRUE = "1";


        public RequestParams newRequestParams(TpLoginModel.TpLoginData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_OPENID, data.getOpenId());
            params.add(KEY_AUTO_LOGIN,VALUE_TRUE);
            String via;
            switch (data.getPlatform()) {
                case TPOauthUserBean.TYPE_QQ:
                    via = STATIC_PARAM_VIAQQ;
                    break;
                case TPOauthUserBean.TYPE_SINA:
                    via = STATIC_PARAM_VIASINA;
                    break;
                case TPOauthUserBean.TYPE_WECHAT:
                    via = STATIC_PARAM_VIAWECHAT;
                    break;
                default:
                    throw new IllegalArgumentException("第三方登录平台参数错了");
            }
            params.add(KEY_VIA, via);
            params.add(KEY_CLIENT_ID, VALUE_CLIENT_ID);
            return params;
        }
    }


    /**
     * 检查登录状态
     * POST
     * user/login/status
     * <p/>
     * to see Model at{@link CheckLoginStateModel}
     */
    class CheckLoginState extends IRestfulApi.ApiNewRestOauthApi {
        private static final String KEY_USERNAME = "username";
        private static final String KEY_TOKEN = "vso_token";

        @Override
        protected String getUnformatedPath() {
            return "user/login/status";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(CheckLoginStateModel.CheckLoginStateData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, data.getUsername());
            params.add(KEY_TOKEN, data.getAccesstoken());
            return params;
        }
    }


    /**
     * VSO - 三方绑定
     * <p>
     * 0.0.0
     * 新用户中心改版使用
     * <p>
     * user/oauth/bind
     * 参数
     * <p>
     * 字段	类型	描述
     * appid	String 必须，全局通用
     * <p>
     * token	String 必须，全局通用
     * <p>
     * username	String 必须，用户名
     * <p>
     * via	String 必须，三方通道（facebook，google，qq，twitter，weibo，weixin）
     * <p>
     * openid	String 必须，身份验证
     * <p>
     * screen_name	String 可选，昵称，最长20
     * <p>
     * image	String 可选，头像，与平台头像不通用
     * <p>
     * access_token	String 可选，第三方返回的code
     * <p>
     * location	String 地理位置，如：江苏 苏州
     * <p>
     * lang	String 可选，语言，默认zh-CN
     * zh-CN => 简体中文
     * zh-TW => 繁体中文
     * en => 英文
     */
    class ThirdAccountBindApi extends IRestfulApi.ApiNewRestOauthApi {
        private static final String KEY_USERNAME = "username";
        private static final String KEY_VIA = "via";
        private static final String KEY_OPENID = "openid";

        private static String VALUE_VIA_QQ = "qq";
        private static String VALUE_VIA_SINA = "weibo";
        private static String VALUE_VIA_WEICHAT = "weixin";

        @Override
        protected String getUnformatedPath() {
            return "user/oauth/bind";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String username, int platform, String openid) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, username);
            params.add(KEY_OPENID, openid);
            String valueVia = "";
            switch (platform) {
                case TPOauthUserBean.TYPE_QQ:
                    valueVia = VALUE_VIA_QQ;
                    break;
                case TPOauthUserBean.TYPE_WECHAT:
                    valueVia = VALUE_VIA_WEICHAT;
                    break;
                case TPOauthUserBean.TYPE_SINA:
                    valueVia = VALUE_VIA_SINA;
                    break;
            }
            params.add(KEY_VIA, valueVia);

            return params;
        }

        public RequestParams newRequestParams(String username, int platform, String openid, LoginResBean authInfo) {
            final String KEY_AUTH_USER = "auth_username";
            final String KEY_AUTH_TOKEN = "auth_token";

            RequestParams params = new RequestParams();
            params.add(KEY_USERNAME, username);
            params.add(KEY_OPENID, openid);
            String valueVia = "";
            switch (platform) {
                case TPOauthUserBean.TYPE_QQ:
                    valueVia = VALUE_VIA_QQ;
                    break;
                case TPOauthUserBean.TYPE_WECHAT:
                    valueVia = VALUE_VIA_WEICHAT;
                    break;
                case TPOauthUserBean.TYPE_SINA:
                    valueVia = VALUE_VIA_SINA;
                    break;
            }
            params.add(KEY_VIA, valueVia);
            params.add(KEY_AUTH_USER, StringUtil.nullStrToEmpty(authInfo
                    .getUsername()));
            params.add(KEY_AUTH_TOKEN, StringUtil.nullStrToEmpty(authInfo.getVso_token()));

            return params;
        }
    }

    /**
     * 查询用户三方信息接口
     * user/oauth/list
     */
    class ThirdinfoListApi extends IRestfulApi.ApiNewRestOauthApi {

        private static final String KEY_USERNAME = "username";

        @Override
        protected String getUnformatedPath() {
            return "user/oauth/list";
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
     * ## 手机号注册用户 ##
     * <p/>
     * > 新版</br>
     * > POST</br>
     * > user/register/mobile
     * <p/>
     * 参数：
     * <p/>
     * - **mobile	Number 必须，手机号，必须是没注册过的**
     * - **password	String 必须，登录密码，明文**
     * <p/>
     * to see ResBean at{@link RegisterResBean}
     * to see Model at{@link RegisterModel}
     */
    class RegisterApi extends IRestfulApi.ApiNewRestOauthApi {

        private static final String KEY_MOBILE = "mobile";
        private static final String KEY_PASSWORD = "password";
        private static final String KEY_VALIDCODE = "valid_code";

        @Override
        protected String getUnformatedPath() {
            return "user/register/mobile";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(RegisterModel.ModelRequestData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_MOBILE, data.getMobile());
            params.add(KEY_PASSWORD, data.getPwd());
            params.add(KEY_VALIDCODE, data.getValidCode());
            return params;
        }
    }

    /**
     * User - 登录密码 -- 修改
     * <p/>
     * 修改登录密码，需要原登录密码，不需要发送验证码短信
     * 新密码不能与原密码相同
     * PUT</br>
     * user/safe/change-password
     * 参数</br>
     * <li>appid	String 必须，全局通用
     * <li>token	String 必须，全局通用
     * <li>username	String 必须，用户名
     * <li>old_password	String 必须，原登录密码
     * <li>new_password	String 必须，新密码
     * <p/>
     * to see Model at{@link ChangePwdModel}
     * to see ResBean at{@link BaseVsoApiResBean}
     */
    class ChangePwdApi extends IRestfulApi.ApiNewRestOauthApi {

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

        public RequestParams newRequestParams(ChangePwdModel.ChangePwdData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, data.getUsername());
            params.add(KEY_OLDPWD, data.getOldPwd());
            params.add(KEY_NEWPWD, data.getNewPwd());
            return params;
        }
    }


    /**
     * ## 重置登录密码，不需要原密码，支持账号 ##
     * > POST
     * user/safe/reset-password-by-name
     * <p/>
     * 参数：
     * <p/>
     * - **name	String 必须，账号（用户名/手机号/邮箱）**
     * - **password	String 必须，新密码，明码**
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
     * to see ResBean at{@link BaseVsoApiResBean}
     * to see Model at{@link ResetPwdByAccountModel}
     */
    class ResetPwdByAccountApi extends IRestfulApi.ApiNewRestOauthApi {

        private static final String KEY_NAME = "name";
        private static final String KEY_PWD = "password";
        private static final String KEY_VALID_CODE = "valid_code";

        @Override
        protected String getUnformatedPath() {
            return "user/safe/reset-password-by-name";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(ResetPwdByAccountModel.ModelRequestData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_NAME, data.getMobile());
            params.add(KEY_PWD, data.getPwd());
            params.add(KEY_VALID_CODE, data.getValidCode());
            return params;
        }
    }

    /**
     * VSO - 保存用户分流所选角色
     * post
     * user/roles/create
     * <p>
     * 参数
     * <p>
     * 字段	类型	描述
     * username	String 必须，用户名
     * <p>
     * source	String 可选 分流来源 默认为2 1 => 注册时 2 => 登录后 3 => 后台
     * <p>
     * roles	String 所选角色，多个用英文逗号隔开 1 => "发需求", 2 => "接需求", 3 => "孵化项目",
     * 4 => "投资项目", 5 => "渲染服务", 6 => "选购素材",
     * 7 => "出售素材", 8 => "版权保护",
     */
    class RoleCreateApi extends IRestfulApi.ApiNewRestOauthApi {

        private static final String KEY_USERNAME = "username";

        private static final String KEY_SOURCE = "source";

        private static final String KEY_ROLES = "roles";

        public static final int VALUE_SOURCE_LOGIN = 1;

        public static final int VALUE_SOURCE_REGISTER = 2;

        @Override
        protected String getUnformatedPath() {
            return "user/roles/create";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(RoleChooseModel.RoleChooseData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, data.getUsr());
            params.add(KEY_SOURCE, String.valueOf(data.getSource()));
            StringBuilder stringBuilder = new StringBuilder();
            for (Integer role : data.getRoles()) {
                stringBuilder.append(role).append(",");
            }
            String valueRoles = stringBuilder.substring(0, stringBuilder.length() - 1);
            params.add(KEY_ROLES, valueRoles);
            return params;
        }
    }


    /**
     * VSO - 第三方帐号注册带绑定三方信息
     * post
     * 0.0.0
     * 第三方帐号注册带绑定三方信息 前置条件：
     * 1、验证手机号是否可用
     * 2、发送验证码短信
     * 3、验证短信验证码
     * <p>
     * user/register/oauth
     * 参数
     * 字段	类型	描述
     * mobile	Number	必须，手机号，必须是没注册过的
     * password	String	必须，登录密码，明文
     * via	String	 必须，三方通道（facebook，google，qq，twitter，weibo，weixin）
     * openid	String	必须，身份验证
     * screen_name	String	可选，昵称，最长20
     * image	String	可选，头像，与平台头像不通用
     * <p>
     * <p>
     * user/register/oauth 三方注册，先注册后登录（支持手机号绑定，手机号为空时仅注册）
     */
    class TpRegWithOauthBindApi extends IRestfulApi.ApiNewRestOauthApi {

        private static final String KEY_MOBILE = "mobile";
        private static final String KEY_PWD = "password";
        private static final String KEY_VIA = "via";
        private static final String KEY_UNIQUEID = "openid";
        private static final String KEY_NICK = "screen_name";
        private static final String KEY_AVATAR = "image";


        private static final String STATIC_PARAM_VIAQQ = "qq";

        private static final String STATIC_PARAM_VIAWECHAT = "weixin";

        private static final String STATIC_PARAM_VIASINA = "weibo";

        private static final String KEY_VALID_CODE = "valid_code";

        @Override
        protected String getUnformatedPath() {
            return "user/register/oauth";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(TpRegWithOauthBindModel.TpRegWithOauthBindData data) {
            RequestParams params = this.newRequestParams(data.getBean());
            params.add(KEY_MOBILE, data.getMobile());
            params.add(KEY_PWD, data.getPwd());
            params.add(KEY_VALID_CODE, data.getValidCode());

            return params;
        }

        public RequestParams newRequestParams(TPOauthUserBean bean) {
            RequestParams params = super.newRequestParams();

            params.add(KEY_NICK, bean.getNickname());
            params.add(KEY_AVATAR, bean.getAvatar());
            params.add(KEY_UNIQUEID, bean.getUniqueId());
            String via;
            switch (bean.getType()) {
                case TPOauthUserBean.TYPE_QQ:
                    via = STATIC_PARAM_VIAQQ;
                    break;
                case TPOauthUserBean.TYPE_SINA:
                    via = STATIC_PARAM_VIASINA;
                    break;
                case TPOauthUserBean.TYPE_WECHAT:
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
    class CreateBindPhoneApi extends IRestfulApi.ApiNewRestOauthApi {

        private static final String KEY_PHONE = "mobile";

        private static final String KEY_USERNAME = "username";

        private static final String KEY_VALID_CODE = "valid_code";

        @Override
        protected String getUnformatedPath() {
            return "auth/mobile/create";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(CreateBindPhoneModel.CreateBindPhoneData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_PHONE, data.getPhone());
            params.add(KEY_USERNAME, data.getUsername());
            params.add(KEY_VALID_CODE, data.getValidCod());

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
     * to see ResBean at {@link BaseVsoApiResBean}
     */
    class UpdateBindPhoneApi extends IRestfulApi.ApiNewRestOauthApi {

        private static final String KEY_PHONE = "mobile";

        private static final String KEY_USERNAME = "username";

        private static final String KEY_OLD_VALID_CODE = "old_valid_code";

        private static final String KEY_VALID_CODE = "valid_code";

        @Override
        protected String getUnformatedPath() {
            return "auth/mobile/update";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(UpdateBindPhoneModel.ModelRequestData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_PHONE, data.getPhone());
            params.add(KEY_USERNAME, data.getUsername());
            params.add(KEY_VALID_CODE, data.getValidCode());
            params.add(KEY_OLD_VALID_CODE, data.getOldValidCode());
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
    class SendSmsByUserApi extends IRestfulApi.ApiNewRestOauthApi {

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

        public RequestParams newRequestParams(SendSmsByUserModel.SendSmsByUserData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_PHONE, data.getTarget());
            params.add(KEY_USERNAME, data.getUser());
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
     * to see ResBean at{@link BaseVsoApiResBean}
     * to see Model at{@link CheckNicknameModel}
     */
    class CheckNicknameApi extends IRestfulApi.ApiNewRestOauthApi {

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


    // TODO: 2017/3/20 确认使用地点

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
     * to see ResBean at{@link BaseVsoApiResBean}
     * to see Model at{@link ResetPwdByUsernameModel}
     */
    class ResetPwdByUsernameApi extends IRestfulApi.ApiNewRestOauthApi {

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

        public RequestParams newRequestParams(ResetPwdByUsernameModel.ResetPwdByUsernameData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USERNAME, data.getUsername());
            params.add(KEY_PASSWORD, data.getPassword());
            return params;
        }
    }

    //// TODO: 2017/3/20  确认使用地点

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
    class CheckVerifyCodeWithUserApi extends IRestfulApi.ApiNewRestOauthApi {

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

        public RequestParams newRequestParams(VerifyCodeCheckWithUserModel.VerifyCodeCheckWithUserData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USER, data.getUsername());
            params.add(KEY_CODE, data.getCode());
            return params;
        }

    }

}
