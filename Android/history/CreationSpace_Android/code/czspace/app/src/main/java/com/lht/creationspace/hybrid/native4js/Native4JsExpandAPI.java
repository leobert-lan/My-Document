package com.lht.creationspace.hybrid.native4js;


import com.lht.lhtwebviewlib.base.Interface.BridgeHandler;

/**
 * <p><b>Package</b> com.lht.vsocyy.native4js
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> Native4JsExtendAPI
 * <p><b>Description</b>: 额外提供给js的扩展的API收纳，一般都是特殊业务或者存在模块依赖
 * Created by leobert on 2016/6/12.
 */
public interface Native4JsExpandAPI {

    interface VsoAuthInfoHandler extends BridgeHandler {
        /**
         * API_NAME:获取vso验证信息方法
         */
        String API_NAME = "APP_N_PUBLIC_SIGN_AUTHINFO";
    }

    interface VsoLoginHandler extends BridgeHandler {
        /**
         * API_NAME:vso登录
         */
        String API_NAME = "APP_N_PUBLIC_SIGN_LOGIN";
    }

    interface InputIntentHandler extends BridgeHandler {
        /**
         * API_NAME:吊起全局输入键盘，处理输入意图
         */
        String API_NAME = "APP_N_PUBLIC_INPUT";
    }

    interface DownloadHandler extends BridgeHandler {
        /**
         * API_NAME:下载
         */
        String API_NAME = "APP_N_TOOLS_LOAD_FILE";
    }

    /**
     * 三方分享
     */
    interface TPShareHandler extends BridgeHandler {
        /**
         * API_NAME:三方分享
         */
        String API_NAME = "APP_N_TOOLS_SHARE";
    }

    /**
     * 用户中心-批量操作关闭
     */
    interface UcenterBatchOpCloseHandler extends BridgeHandler {
        /**
         * API_NAME:用户中心-批量操作关闭
         */
        String API_NAME = "APP_N_CZSPACE_UCENTER_BATCHOP_CLOSE";
    }

    interface ArticleDetailNavigateHandler extends BridgeHandler {
        /**
         * 打开、跳转到文章详情页
         */
        String API_NAME = "APP_N_CZSPACE_START_ARTICLEDETAIL";
    }

    interface ProjectDetailNavigateHandler extends BridgeHandler {
        /**
         * 打开、跳转到项目详情页
         */
        String API_NAME = "APP_N_CZSPACE_START_PROJECTDETAIL";
    }

    interface CircleDetailNavigateHandler extends BridgeHandler {
        /**
         * 打开、跳转到圈子详情页
         */
        String API_NAME = "APP_N_CZSPACE_START_CIRCLEDETAIL";
    }

    interface UCenterNavigateHandler extends BridgeHandler {
        /**
         * 打开、跳转到圈子详情页
         */
        String API_NAME = "APP_N_CZSPACE_START_UCENTER";
    }

    interface VsoAcNavigateHandler extends BridgeHandler {
        /**
         * 打开、跳转到活动详情
         */
        String API_NAME = "APP_N_CZSPACE_START_VSOACTIVITYDETAIL";
    }


    interface CustomToastHandler extends BridgeHandler {
        /**
         * 弹出自定义的toast
         */
        String API_NAME = "APP_N_PUBLIC_CUSTOM_TOAST";
    }

    interface CommentListNavigateHandler extends BridgeHandler {
        /**
         * 跳转到评论列表
         */
        String API_NAME = "APP_N_CZSPACE_START_COMMENTLIST";
    }

    interface GeneralNavigateHandler extends BridgeHandler {
        /**
         * 通用跳转
         */
        String API_NAME = "APP_N_PUBLIC_URL";
    }


    interface PublishProjectNavigateHandler extends BridgeHandler {
        /**
         * 跳转到发布项目
         */
        String API_NAME = "APP_N_CZSPACE_PUBLISHPROJ";
    }


    interface PublishArticleNavigateHandler extends BridgeHandler {
        /**
         * 跳转到发布文章
         */
        String API_NAME = "APP_N_CZSPACE_PUBLISHARTICLE";
    }

    interface ImagePreviewHandler extends BridgeHandler {
        /**
         * 进行图片预览
         */
        String API_NAME = "APP_N_TOOLS_PREVIEW_IMG";
    }

    /**
     * 跳转到项目更新编辑页
     */
    interface PublishProjChapterNavigateHandler extends BridgeHandler {
        String API_NAME = "APP_N_CZSPACE_PROJ_UPDATE_ARTICLE";
    }

    interface ProjChapterDetailNavigateHandler extends BridgeHandler {
        /**
         * 打开项目chapter更新的详情页
         */
        String API_NAME = "APP_N_CZSPACE_START_PROJCHAPTER";
    }


//    ### 跳转到评论列表 ###
//
//            `APP_N_CZSPACE_START_COMMENTLIST`
//
//    参数：
//
//            - url 列表页url
//
//    ### 弹出自定义toast ###
//
//            `APP_N_PUBLIC_CUSTOM_TOAST`
//
//    参数：
//
//            * type int类型 0代表显示勾，1代表显示叉
//    * content 通知的内容
}
