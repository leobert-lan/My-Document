package com.lht.creationspace.hybrid;

/**
 * <p><b>Package</b> com.lht.vsocyy.interfaces.net
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> IHybridPagesCollection
 * <p><b>Description</b>: TODO
 * <p>Created by leobert on 2017/2/16.
 */

public interface IHybridPagesCollection {

    /**
     * 首页-推荐
     */
    class HybridHomeIndexRecommend extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/index.html";
        }
    }

    /**
     * 首页-关注：/maker/module/info.html
     */
    class HybridHomeIndexFavor extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/info.html";
        }
    }

    /**
     * 首页-广场：/maker/module/square.html
     * 改为“项目更新” ：/maker/module/proUpdate.html
     */
    class HybridHomeIndexSquare extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/proUpdate.html";
        }
    }

    class HybridTest extends IHybridPageConfig.AbsHybridPageConfig {

        //https://m.vsochina.com/maker/module/test.html
        @Override
        protected String getPagePath() {
            return "maker/module/test.html";
        }
    }

    /**
     * 我的项目
     */
    class HybridMyProject extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/myproject.html";
        }
    }

    /**
     * 我的文章
     */
    class HybridMyArticle extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/myarticle.html";
        }
    }

    /**
     * 我的圈子
     */
    class HybridMyCircle extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/mycircle.html";
        }
    }

    /**
     * 我的关注
     */
    class HybridMyAttention extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/myfocus.html";
        }
    }

    /**
     * 我的粉丝
     */
    class HybridMyFans extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/myfans.html";
        }
    }

    /**
     * 我的收藏--项目
     */
    class HybridMyCollectionProject extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/likepro.html";
        }
    }

//    /**
//     * 项目详情
//     */
//    @Deprecated
//    class HybridProjectDetail extends IHybridPageConfig.AbsHybridPageConfig {
//
//        @Override
//        protected String getPagePath() {
//            return "maker/module/prodetail.html";
//        }
//
//        @Override
//        protected String getQueryStringFormat() {
//            return "project_id=%s";
//        }
//    }

    /**
     * 用户主页
     */
    class HybridUserSpace extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/uspace.html";
        }

        @Override
        protected String getQueryStringFormat() {
            return "target_user=%s";
        }
    }

    /**
     * 我的收藏--文章
     */
    class HybridMyCollectionArticle extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/likeart.html";
        }
    }

//    /**
//     * 文章详情  使用全路径链接
//     */
//    @Deprecated
//    class HybridArticleDetail extends IHybridPageConfig.AbsHybridPageConfig {
//
//        @Override
//        protected String getPagePath() {
//            return "maker/module/artdetail.html";
//        }
//
//        @Override
//        protected String getQueryStringFormat() {
//            return "article_id=%s";
//        }
//    }

//    @Deprecated
//    class HybridCircleDetailConfig extends IHybridPageConfig.AbsHybridPageConfig {
//
//        @Override
//        protected String getPagePath() {
//            return "maker/module/cirdetail.html";
//        }
//
//        @Override
//        protected String getQueryStringFormat() {
//            return "circle_id=%s";
//        }
//    }

    /**
     * 我的项目--待审核
     */
    class HybridMyProNeedAudit extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/likeart.html";
        }
    }

    /**
     * 我的项目--待完善
     */
    class HybridMyProNeedComplete extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "";
        }
    }

    /**
     * 我的项目--不通过
     */
    class HybridMyProRefuse extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/likeart.html";
        }
    }

    /**
     * 我的项目--已上线
     */
    class HybridMyProThrough extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "";
        }
    }

    /**
     * 首页--圈子
     */
    class HybridHomeTopic extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/circle.html";
        }
    }

    /**
     * 首页--项目
     */
    class HybridHomeProject extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/project.html";
        }

        @Override
        protected String getQueryStringFormat() {
            return "pid=%s";
        }
    }

    /**
     * 首页--活动
     */
    class HybridHomeVsoActivity extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/activity.html";
        }
    }


    /**
     * 各种提及我的评论列表
     */
    class HybridMentionMeList extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/msgcomment.html";
        }

        @Override
        protected String getQueryStringFormat() {
            return "username=%s";
        }
    }



    /**
     * 消息-提醒列表
     */
    class HybridRemindList extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/msgremind.html";
        }

        @Override
        protected String getQueryStringFormat() {
            return "username=%s";
        }
    }

    /**
     * 消息-通知列表
     */
    class HybridNotificationList extends IHybridPageConfig.AbsHybridPageConfig {

        @Override
        protected String getPagePath() {
            return "maker/module/notice.html";
        }

        @Override
        protected String getQueryStringFormat() {
            return "username=%s";
        }
    }


}
