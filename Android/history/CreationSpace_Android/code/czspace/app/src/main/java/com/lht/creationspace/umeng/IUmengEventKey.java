package com.lht.creationspace.umeng;

/**
 * @package com.lht.vsocyy.interfaces.umeng
 * @project AndroidBase
 * @classname IUmengEventKey
 * @description: 友盟统计事件Key常量定义
 * Created by leobert on 2016/4/1.
 */
public interface IUmengEventKey {
    /**
     * UMAuthComp	企业认证	计数事件
     */
    String KEY_AUTH_ENTERPRISE = "UMAuthComp";

    /**
     * UMAuthPerso	个人认证	计数事件
     */
    String KEY_AUTH_PERSONAL = "UMAuthPerso";

    /**
     * UMHomeSearchBar	主页搜索框	计数事件
     */
    String KEY_SEARCH_ATHOME = "UMHomeSearchBar";

    /**
     * UMHomeTaskHouse	主页需求大厅	计数事件
     */
    String KEY_TASKCENTER_ATHOME = "UMHomeTaskHouse";

    /**
     * UMMgrTaskMenu	需求管理菜单	计算事件
     */
    String KEY_TABSWITCH_TASKMANAGER = "UMMgrTaskMenu";

    /**
     * UMNavSlide	导航栏侧滑	计数事件
     * 计算打开
     */
    String KEY_OPEN_SLIDING_MENU = "UMNavSlide";

    /**
     * UMSearchCondition	搜索－条件	计算事件
     */
    String KEY_SECRCH_CONDITION = "UMSearchCondition";

    /**
     * UMSearchHistory	搜索－历史关键词	计数事件
     * 根据历史进行搜索
     */
    String KEY_SECRCH_BY_HISTORY= "UMSearchHistory";

    /**
     * UMSearchPropose	搜索－推荐	计数事件
     * 通过推荐进行搜索
     */
    String KEY_SEARCH_BY_RECOMMEND = "UMSearchPropose";

    /**
     * UMSubscrFromHome	主页订阅	计数事件
     */
    String KEY_SUBSCRIBE_ATHOME = "UMSubscrFromHome";

    /**
     * UMSubscrFromMine	个人菜单订阅	计数事件
     */
    String KEY_SUBSCRIBE_ATMINE = "UMSubscrFromMine";

    /**
     * UMSubscrFromReg	注册后订阅	计数事件
     */
    @Deprecated
    String KEY_SUBSCRIBE_ATREG = "UMSubscrFromReg";

    /**
     * UMSubscrLeave	离开订阅	计数事件
     * 统计没有进行订阅的
     */
    String KEY_SUBSCRIBE_SKIP = "UMSubscrLeave";

    /**
     * UMSubscrSuccess	成功订阅	计算事件
     */
    String KEY_SUBSCRIBE_SUCCESS = "UMSubscrSuccess";

    /**
     * UMTaskComment	需求评价	计数事件
     */
    String KEY_TASK_COMMENT = "UMTaskComment";

    /**
     * UMTaskDetailPage	需求详情页面	计数事件
     */
    String KEY_VIEW_TASKDETAIL = "UMTaskDetailPage";

    /**
     * UMTaskFavo	需求收藏	计数事件
     * 通过tab切换需求收藏进行查看
     */
    String KEY_VIEW_TASK_FAVO = "UMTaskFavo";

    /**
     * UMTaskFavoList	需求收藏列表	计数事件
     * 点击需求收藏进入详情
     */
    String KEY_CLICK_TASKFAVO_ITEM = "UMTaskFavoList";

    /**
     * UMTaskHot	需求最火	计数事件
     */
    String KEY_VIEW_TASK_HOT = "UMTaskHot";

    /**
     * UMTaskRich	需求最壕	计数事件
     */
    String KEY_VIEW_TASK_RICH = "UMTaskRich";

    /**
     * UMTaskShare	需求分享	计算事件
     */
    String KEY_TASK_SHARE = "UMTaskShare";

    /**
     * UMTaskSubmisIncompl	需求投稿没填完整	计数事件
     */
    String KEY_TASK_SUBMIS_INCOMPL = "UMTaskSubmisIncompl";

    /**
     * UMTaskSubmisPage	需求投稿页面	计数事件
     */
    String KEY_TASK_SUBMIS_PAGE = "UMTaskSubmisPage";

    /**
     * UMTaskSubmission	需求投稿	计算事件
     */
    String KEY_TASK_SUBMISSION = "UMTaskSubmission";

    /**
     * UMUserLogin	登录	计算事件
     */
    String KEY_USER_LOGIN = "UMUserLogin";

    /**
     * UMUserLoginPage	登录页面	计数事件
     */
    String KEY_USER_LOGIN_PAGE = "UMUserLoginPage";

    /**
     * UMUserLoginQQ	QQ登录	计数事件
     */
    String KEY_USER_LOGIN_BYQQ = "UMUserLoginQQ";

    /**
     * UMUserLoginWB	微博登录	计数事件
     */
    String KEY_USER_LOGIN_BYWB = "UMUserLoginWB";

    /**
     * UMUserLoginWX	微信登录	计数事件
     */
    String KEY_USER_LOGIN_BYWX = "UMUserLoginWX";

    /**
     * UMUserRegist	注册是否成功	计算事件
     */
    String KEY_USER_REGIST_ISSUCCESS = "UMUserRegist";

    /**
     * UMUserTelVerfPage	验证手机页面	计数事件
     */
    String KEY_USER_TEL_VERF_PAGE = "UMUserTelVerfPage";

    /**
     * UMUserUpdateAvatar	用户更新头像	计数事件
     */
    String KEY_USER_UPDATE_AVATAR = "UMUserUpdateAvatar";

}
