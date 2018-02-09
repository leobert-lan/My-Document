package com.lht.cloudjob.mvp.model.bean;

import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.cloudjob.mvp.model.bean
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> CheckUndertakePerssionResBean
 * <p><b>Description</b>: 定义投稿权限的错误码
 *
 * @since 1.0.50 企业、个人认证相关的统一提示去web做 13169
 * <p>Created by Administrator on 2016/9/21.
 */

public class CheckUndertakePermissionResBean {

  /*
  "15120" => "权限校验通过"
  //不通过的
"15121" => "实名认证未通过"
"15122" => "企业认证未通过"
"15123" => "团队认证未通过"
"15124" => "实名认证或企业认证未通过"
"15125" => "实名认证或团队认证未通过"
"15126" => "企业认证或团队认证未通过"
"15127" => "实名认证，企业认证或团队认证未通过"
"15128" => "手机绑定未通过"
"15129" => "邮箱绑定未通过"
"15130" => "银行卡绑定未通过"
"15131" => "手机绑定或邮箱绑定未通过"
"15132" => "手机绑定或银行卡绑定未通过"
"15133" => "邮箱绑定或银行卡绑定未通过"
"15134" => "手机绑定，邮箱绑定或银行卡绑定未通过"
"15135" => "手机绑定和邮箱绑定未通过"
"15136" => "手机绑定和银行卡绑定未通过"
"15137" => "邮箱绑定和银行卡绑定未通过"
"15138" => "手机绑定，邮箱绑定和银行卡绑定未通过"
"15139" => "投标次数限制未通过"

since 1.0.50:
13169 请升级认证（请至web平台升级认证）
*/

    public static final int NO_AUTH = 13169;

    /*
     *需要个人认证
     */
    public static final int NO_PAUTHENTICATE = 15121;

    /**
     * 需要进行企业认证
     */
    public static final int NO_EAUTHENTICATE_ONE = 15122;
    public static final int NO_EAUTHENTICATE_TWO = 15123;
    public static final int NO_EAUTHENTICATE_THREE = 15126;

    /**
     * 需要进行企业认证或个人认证
     */
    public static final int NO_EAU_AND_PAU_ONE = 15124;
    public static final int NO_EAU_AND_PAU_TWO = 15125;
    public static final int NO_EAU_AND_PAU_THREE = 15127;

    /**
     * 需要绑定手机号
     */
    public static final int NO_BINDPHONE_ONE = 15128;
    public static final int NO_BINDPHONE_TWO = 15131;
    public static final int NO_BINDPHONE_THREE = 15132;
    public static final int NO_BINDPHONE_FOUR = 15134;

    /**
     * 弹出绑定邮箱/银行卡，请到电脑上绑定的Toast弹窗
     */
    public static final int NO_BAND_EMAIL_OR_BANKCARD_ONE = 15129;
    public static final int NO_BAND_EMAIL_OR_BANKCARD_TWO = 15130;
    public static final int NO_BAND_EMAIL_OR_BANKCARD_THREE = 15133;
    public static final int NO_BAND_EMAIL_OR_BANKCARD_FOUR = 15135;
    public static final int NO_BAND_EMAIL_OR_BANKCARD_FIVE = 15136;
    public static final int NO_BAND_EMAIL_OR_BANKCARD_SIX = 15137;
    public static final int NO_BAND_EMAIL_OR_BANKCARD_SEVEN = 15138;

    /**
     * 投稿次数达到上限
     */
    public static final int UNDERTAKE_COUNT_FULLED = 15139;

    /**
     * 需要个人认证的rets
     */
    public static final ArrayList<Integer> RETS_NEEDAUTH_P = new ArrayList<>();

    static {
        RETS_NEEDAUTH_P.add(NO_PAUTHENTICATE);
        RETS_NEEDAUTH_P.add(NO_AUTH);
    }

    /**
     * 需要企业认证的rets
     */
    public static final ArrayList<Integer> RETS_NEEDAUTH_E = new ArrayList<>();

    static {
        RETS_NEEDAUTH_E.add(NO_EAUTHENTICATE_ONE);
        RETS_NEEDAUTH_E.add(NO_EAUTHENTICATE_TWO);
        RETS_NEEDAUTH_E.add(NO_EAUTHENTICATE_THREE);
        RETS_NEEDAUTH_E.add(NO_AUTH);
    }

    /**
     * 需要个人或企业认证的rets
     */
    public static final ArrayList<Integer> RETS_NEEDAUTH_PORE = new ArrayList<>();

    static {
        RETS_NEEDAUTH_PORE.add(NO_EAU_AND_PAU_ONE);
        RETS_NEEDAUTH_PORE.add(NO_EAU_AND_PAU_TWO);
        RETS_NEEDAUTH_PORE.add(NO_EAU_AND_PAU_THREE);
        RETS_NEEDAUTH_PORE.add(NO_AUTH);
    }

    /**
     * 需要手机认证的rets
     */
    public static final ArrayList<Integer> RETS_NEEDAUTH_MOBILE = new ArrayList<>();

    static {
//        public static final int NO_BINDPHONE_ONE = 15128;
//        public static final int NO_BINDPHONE_TWO = 15131;
//        public static final int NO_BINDPHONE_THREE = 15132;
//        public static final int NO_BINDPHONE_FOUR = 15134;
        RETS_NEEDAUTH_MOBILE.add(NO_BINDPHONE_ONE);
        RETS_NEEDAUTH_MOBILE.add(NO_BINDPHONE_TWO);
        RETS_NEEDAUTH_MOBILE.add(NO_BINDPHONE_THREE);
        RETS_NEEDAUTH_MOBILE.add(NO_BINDPHONE_FOUR);
    }

    /**
     * 需要绑定邮箱或银行卡的rets
     */
    public static final ArrayList<Integer> RETS_NEEDBIND_MAILORICCARD = new ArrayList<>();

    static {
//        public static final int NO_BAND_EMAIL_OR_BANKCARD_ONE = 15129;
//        public static final int NO_BAND_EMAIL_OR_BANKCARD_TWO = 15130;
//        public static final int NO_BAND_EMAIL_OR_BANKCARD_THREE = 15133;
//        public static final int NO_BAND_EMAIL_OR_BANKCARD_FOUR = 15135;
//        public static final int NO_BAND_EMAIL_OR_BANKCARD_FIVE = 15136;
//        public static final int NO_BAND_EMAIL_OR_BANKCARD_SIX = 15137;
//        public static final int NO_BAND_EMAIL_OR_BANKCARD_SEVEN = 15138;
        RETS_NEEDBIND_MAILORICCARD.add(NO_BAND_EMAIL_OR_BANKCARD_ONE);
        RETS_NEEDBIND_MAILORICCARD.add(NO_BAND_EMAIL_OR_BANKCARD_TWO);
        RETS_NEEDBIND_MAILORICCARD.add(NO_BAND_EMAIL_OR_BANKCARD_THREE);
        RETS_NEEDBIND_MAILORICCARD.add(NO_BAND_EMAIL_OR_BANKCARD_FOUR);
        RETS_NEEDBIND_MAILORICCARD.add(NO_BAND_EMAIL_OR_BANKCARD_FIVE);
        RETS_NEEDBIND_MAILORICCARD.add(NO_BAND_EMAIL_OR_BANKCARD_SIX);
        RETS_NEEDBIND_MAILORICCARD.add(NO_BAND_EMAIL_OR_BANKCARD_SEVEN);
    }


}
