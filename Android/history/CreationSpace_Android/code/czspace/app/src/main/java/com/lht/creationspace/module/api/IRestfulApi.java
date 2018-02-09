package com.lht.creationspace.module.api;


import com.lht.creationspace.base.IVerifyHolder;
import com.lht.creationspace.module.article.model.ArticleCollectModel;
import com.lht.creationspace.module.article.model.ArticlePublishModel;
import com.lht.creationspace.module.article.model.QueryArticleCollectStateModel;
import com.lht.creationspace.module.msg.model.UnreadMessageModel;
import com.lht.creationspace.module.msg.model.pojo.UnreadMsgResBean;
import com.lht.creationspace.module.projchapter.ui.model.ProjChapterPublishModel;
import com.lht.creationspace.module.proj.model.ProjectCollectModel;
import com.lht.creationspace.module.proj.model.ProjectPublishModel;
import com.lht.creationspace.module.proj.model.QueryProjectCollectStateModel;
import com.lht.creationspace.module.pub.model.MediaCenterUploadModel;
import com.lht.creationspace.module.topic.model.CircleJoinStateModel;
import com.lht.creationspace.module.topic.model.JoinCircleModel;
import com.lht.creationspace.module.topic.model.JoinedTopicListModel;
import com.lht.creationspace.module.user.info.model.IntroduceModifyModel;
import com.lht.creationspace.module.user.social.model.QueryUserSubscribeStateModel;
import com.lht.creationspace.module.user.social.model.UserSubscribeModel;
import com.lht.creationspace.util.debug.DLog;
import com.lht.creationspace.util.string.StringUtil;
import com.loopj.android.http.RequestParams;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * <p><b>Package</b> com.lht.vsocyy.interfaces.net
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> IRestfulApi
 * <p><b>Description</b>: Restful API 管理</br>
 * <li>CollectArticleApi,used to collect an article ,also see {@link IRestfulApi.CollectArticleApi}</li>
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

    Protocol DEFAULT_PROTOCOL = Protocol.HTTPS;

    String HOST = "maker.vsochina.com";

    String SEPARATOR = "/";

    String QUERY_SYMBOL = "?";

    String AND_SYMBOL = "&";

    /**
     * default schema is https
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

    abstract class AbsRestApiBase implements IRestfulApi {
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
            return this.formatUrl(DEFAULT_PROTOCOL, pathParams, queryStringParams);
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

        protected String getHost() {
            return HOST;
        }


        protected void log(String s) {
            DLog.d(getClass(), "format url:" + s);
        }

        String trim(StringBuilder builder) {
            return builder.toString().trim();
        }

        public RequestParams newRequestParams() {
            RequestParams params = new RequestParams();
            params.add("lang", "zh-CN");
            return params;
        }
    }


    /**
     * czspace专用接口
     */
    abstract class CyyRestOauthApi extends AbsRestApiBase {

        private static final String KEY_AUTH_USER = "auth_username";

        private static final String KEY_AUTH_TOKEN = "auth_token";

        @Override
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
     * 新版APINEW的接口
     */
    abstract class ApiNewRestOauthApi extends AbsRestApiBase {
        private static final String HOST = "apinew.vsochina.com";

        private static final String KEY_AUTH_USER = "auth_username";

        private static final String KEY_AUTH_TOKEN = "auth_token";

        @Override
        protected String getHost() {
            return HOST;
        }

        @Override
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


    //////////////////////////////////////////////////
    // 以下是实际接口
    //////////////////////////////////////////////////

    /**
     * 媒体中心的上传
     * POST
     * /api/vso/file/upload
     * <br>参数:</br>
     * <li>attachment	文件流
     * <li>insert_only	number 0覆盖 1不覆盖</li>
     */
    class MediaCenterUploadApi extends CyyRestOauthApi {

        private static final String KEY_ATTACHMENT = "attachment";

        private static final String KEY_INSERT_ONLY = "insert_only";

        private static final String KEY_TYPE = "object_type";

        private static final String VALUE_OVERRIDE_NEVER = "1";


        public RequestParams newRequestParams(MediaCenterUploadModel.
                                                      MediaCenterUploadData data,
                                              String name) {
            RequestParams params = newRequestParams();
            params.setForceMultipartEntityContentType(true);
            params.add(KEY_INSERT_ONLY, VALUE_OVERRIDE_NEVER);
            params.add(KEY_TYPE, data.getType());
            try {
//                params.put(KEY_FILES, new File(filePath),RequestParams.APPLICATION_OCTET_STREAM);
                InputStream inputStream = new FileInputStream(data.getFilePath());
                params.put(KEY_ATTACHMENT, inputStream,
                        name, RequestParams.APPLICATION_OCTET_STREAM,
                        true);
//                params.put(KEY_FILES, new File(filePath), "application/octet-stream");
            } catch (FileNotFoundException e) {
                DLog.e(getClass(), "file not found");
                e.printStackTrace();
            }
            return params;
        }

        @Override
        protected String getUnformatedPath() {
            return "api/vso/file/upload";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }
    }


//    /**
//     * Auth - 实名认证 -- 查询
//     * <p/>
//     * GET</br>
//     * https://api.vsochina.com/auth/realname/view </br>
//     * 参数
//     * <p/>
//     * appid	String 必须，全局通用
//     * <p/>
//     * token	String 必须，全局通用
//     * <p/>
//     * username	String 必须，用户名
//     * <p/>
//     * to see ResBean at{@link com.lht.creationspace.mvp.model.bean.PAuthenticQueryResBean}
//     * to see Model at{@link com.lht.creationspace.mvp.model.PAuthenticQueryModel}
//     */
//    @Deprecated
//    class QueryPAuthenticApi extends CyyRestOauthApi {
//
//        private static final String KEY_USERNAME = "username";
//
//        @Override
//        protected String getUnformatedPath() {
//            return "auth/realname/view";
//        }
//
//        @Override
//        protected String getUnformatedQuerystring() {
//            return null;
//        }
//
//        public RequestParams newRequestParams(String usr) {
//            RequestParams params = super.newRequestParams();
//            params.add(KEY_USERNAME, usr);
//            return params;
//        }
//    }

    // TODO: 2017/3/23 modify

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
    class UnreadMessageApi extends CyyRestOauthApi {

        private static final String KEY_USERNAME = "username";
        private static final String KEY_SITEDOMAIN = "sitedomain";
        // TODO: 2017/5/23 checkthis
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
     * 修改个性签名
     * PUT
     * /api/vso/user/brief
     * 参数
     * username	String 用户名
     * brief	String 个性签名
     */
    class IntroModifyApi extends CyyRestOauthApi {

        private static final String KEY_USER = "username";
        private static final String KEY_BRIEF = "brief";

        @Override
        protected String getUnformatedPath() {
            return "api/vso/user/brief";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(IntroduceModifyModel.IntroduceModifyData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USER, data.getUser());
            params.add(KEY_BRIEF, data.getIntroduce());
            return params;
        }
    }

    /**
     * 获取个性签名
     * get
     * /api/vso/user/brief
     * 参数
     * username	String 用户名
     */
    class IntroQueryApi extends CyyRestOauthApi {

        private static final String KEY_USER = "username";

        @Override
        protected String getUnformatedPath() {
            return "api/vso/user/brief";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String user) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USER, user);
            return params;
        }
    }


    /**
     * CIRCLE - 获取圈子列表
     * GET
     * 获取圈子列表
     * /api/frontend/circles
     * 参数
     * <p>
     * <li>offset 可选 列表分页起始点 默认值: 0
     * <li>limit 可选 分页每页数量 默认值: 10
     */
    class TopicListApi extends CyyRestOauthApi {
        private static final String KEY_OFFSET = "offset";
        private static final String KEY_LIMIT = "limit";

        @Override
        protected String getUnformatedPath() {
            return "api/frontend/circles";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(int offset) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_OFFSET, String.valueOf(offset));
            params.add(KEY_LIMIT, "12");
            return params;
        }
    }

    /**
     * CIRCLE - 获取圈子列表
     * GET
     * 获取圈子列表
     * /api/frontend/circles/joined
     * 参数
     * <p>
     * <li>offset 可选 列表分页起始点 默认值: 0
     * <li>limit 可选 分页每页数量 默认值: 10
     * <li>username </>
     */
    class JoinedTopicListApi extends CyyRestOauthApi {
        private static final String KEY_OFFSET = "offset";
        private static final String KEY_LIMIT = "limit";
        private static final String KEY_USERNAME = "username";

        @Override
        protected String getUnformatedPath() {
            return "api/frontend/circles/joined";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(JoinedTopicListModel.JoinedTopicListData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_OFFSET, String.valueOf(data.getOffset()));
            params.add(KEY_USERNAME, data.getUsername());
            params.add(KEY_LIMIT, "12");
            return params;
        }
    }

    /**
     * ARTICLE - 新建文章
     * 新建文章
     * /api/frontend/articles
     * 参数
     * 字段	类型	描述
     * id	Number 需要执行更新操作的文章记录id 应该用不着更新
     * circle_id	Number 圈子id
     * article_title	String 文章标题
     * article_content	String 文章内容
     * article_brief	String 文章简介
     * article_thumb	String 封面图片
     * article_author	String 作者
     * article_images   Array  配图id，cbs的附件id
     */
    class ArticlePublishApi extends CyyRestOauthApi {
        private static final String KEY_CIRCLE_ID = "circle_id";
        private static final String KEY_ARTICLE_TITLE = "article_title";
        private static final String KEY_ARTICLE_CONTENT = "article_content";
        private static final String KEY_ARTICLE_AUTHOR = "article_author";
        //        private static final String KEY_ARTICLE_BRIEF = "article_brief"; //deprecated
        private static final String KEY_ARTICLE_IMAGES = "article_images";

        @Override
        protected String getUnformatedPath() {
            return "api/frontend/articles";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(ArticlePublishModel.ArticleData
                                                      articleData) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_CIRCLE_ID, articleData.getCircleId());
            params.add(KEY_ARTICLE_TITLE, articleData.getTitle());
            params.add(KEY_ARTICLE_CONTENT, articleData.getContent());
            params.add(KEY_ARTICLE_AUTHOR, articleData.getAuthor());

            ArrayList<String> images = articleData.getImages();
            if (images == null || images.isEmpty())
                return params;

            params.put(KEY_ARTICLE_IMAGES, images);

            return params;
        }
    }


    /**
     * APP - 获取项目行业类型配置
     * <p>
     * 0.0.0
     * 获取项目行业类型配置
     * <p>
     * api/frontend/app/project/categories
     */
    class ProjectTypeListApi extends CyyRestOauthApi {

        @Override
        protected String getUnformatedPath() {
            return "api/frontend/app/project/categories";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams() {
            RequestParams params = super.newRequestParams();
            return params;
        }
    }

    /**
     * 项目状态列表
     */
    class ProjectStateListApi extends CyyRestOauthApi {

        @Override
        protected String getUnformatedPath() {
            return "api/frontend/app/project/phases";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams() {
            RequestParams params = super.newRequestParams();
            return params;
        }
    }

    /**
     * 发布项目
     * POST
     * /api/frontend/user-projects
     * 参数
     * 字段	类型	描述
     * name	String 项目名称
     * qq 可选	String 联系人qq
     * m_type 可选	String 一级分类
     * c_type 可选	String 二级分类 默认值: null
     * province	String 省份
     * city	String 城市
     * phase_id 可选	Number 阶段 默认值: 0
     * <p>
     * $username = Input::get('vso_uname' ,'');
     * $uid = Input::get('vso_uid', '');
     * $session = Input::get('vso_sess', '');
     */
    class ProjectPublishApi extends CyyRestOauthApi {
        private static final String KEY_PRO_NAME = "name";

        private static final String KEY_QQ = "qq";

        private static final String KEY_PRIMARY_TYPE_ID = "m_type";

        private static final String KEY_SECONDARY_TYPE_ID = "c_type";

        private static final String KEY_PROVINCE = "province";

        private static final String KEY_MOBILE = "mobile";

        private static final String KEY_CITY = "city";

        private static final String KEY_PHASE = "phase_id";

        @Override
        protected String getUnformatedPath() {
            return "api/frontend/user-projects";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(ProjectPublishModel.ProjectData bean) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_PRO_NAME, bean.getProjectName());
            params.add(KEY_QQ, bean.getQq());
            params.add(KEY_PRIMARY_TYPE_ID, String.valueOf(bean.getPrimaryProType()));
            params.add(KEY_MOBILE, bean.getMobile());
            if (bean.getSecondaryProType() > -1) {
                params.add(KEY_SECONDARY_TYPE_ID,
                        String.valueOf(bean.getSecondaryProType()));
            }
            params.add(KEY_PROVINCE, bean.getProvince());
            if (!StringUtil.isEmpty(bean.getCity())) {
                params.add(KEY_CITY, bean.getCity());
            }
            params.add(KEY_PHASE, bean.getProjectState());

            return params;
        }
    }

    /**
     * api/frontend/subscribes
     * POST
     * subscriber 发起者-用户
     * subscribe_to 对象
     */
    class SubscribeUserApi extends CyyRestOauthApi {

        private static final String KEY_ME = "subscriber";

        private static final String KEY_OBJ = "subscribe_to";

        @Override
        protected String getUnformatedPath() {
            return "api/frontend/subscribes";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(UserSubscribeModel.UserSubscribeData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_ME, data.getUser());
            params.add(KEY_OBJ, data.getTarget());
            return params;
        }
    }

//    //demo
//    class UserSubscribesApi extends CyyRestOauthApi {
//
//        static class PostParams {
//
//            private static final String KEY_ME = "subscriber";
//
//            private static final String KEY_OBJ = "subscribe_to";
//        }
//
//        static class DeleteParams {
//            private static final String KEY_ID = "id";
//        }
//
//        @Override
//        protected String getUnformatedPath() {
//            return "api/frontend/subscribes";
//        }
//
//        @Override
//        protected String getUnformatedQuerystring() {
//            return null;
//        }
//
//        public RequestParams newPostRequestParams(UserSubscribeModel.UserSubscribeData data) {
//            RequestParams params = super.newRequestParams();
//            params.add(PostParams.KEY_ME, data.getUser());
//            params.add(PostParams.KEY_OBJ, data.getTarget());
//            return params;
//        }
//
//        public RequestParams newDeleteRequestParams(String id) {
//            RequestParams params = super.newRequestParams();
//            params.add(DeleteParams.KEY_ID, id);
//            return params;
//        }
//    }

    /**
     * api/frontend/subscribes
     * DELETE
     * id
     */
    class UnSubscribeUserApi extends CyyRestOauthApi {

        private static final String KEY_ID = "id";


        @Override
        protected String getUnformatedPath() {
            return "api/frontend/subscribes";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String id) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_ID, id);
            return params;
        }
    }

    /**
     * api/frontend/is-subscribe
     * GET
     * subscriber 发起者-用户
     * subscribe_to 对象
     */
    class QueryUserSubscribeStateApi extends CyyRestOauthApi {

        private static final String KEY_ME = "username";

        private static final String KEY_OBJ = "subscribe_to";

        @Override
        protected String getUnformatedPath() {
            return "api/frontend/is-subscribe";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(QueryUserSubscribeStateModel.QueryUserSubscribeStateData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_ME, data.getUser());
            params.add(KEY_OBJ, data.getTarget());
            return params;
        }
    }

    /**
     * api/frontend/favorites/projects
     * POST
     * subscriber 发起者-用户
     * project_id 对象
     */
    class CollectProjectApi extends CyyRestOauthApi {

        private static final String KEY_ME = "subscriber";

        private static final String KEY_OBJ = "project_id";

        @Override
        protected String getUnformatedPath() {
            return "api/frontend/favorites/projects";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(ProjectCollectModel.ProjectCollectData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_ME, data.getUser());
            params.add(KEY_OBJ, data.getTarget());
            return params;
        }
    }

    /**
     * api/frontend/favorites/projects
     * DELETE
     * id
     */
    class DisCollectProjectApi extends CyyRestOauthApi {

        private static final String KEY_ID = "id";


        @Override
        protected String getUnformatedPath() {
            return "api/frontend/favorites/projects";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String id) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_ID, id);
            return params;
        }
    }


    /**
     * api/frontend/is-fav-project
     * GET
     * subscriber 发起者-用户
     * project_id 对象
     */
    class QueryProjectCollectStateApi extends CyyRestOauthApi {

        private static final String KEY_ME = "username";

        private static final String KEY_OBJ = "project_id";

        @Override
        protected String getUnformatedPath() {
            return "api/frontend/is-fav-project";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(QueryProjectCollectStateModel.QueryProjectCollectStateData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_ME, data.getUser());
            params.add(KEY_OBJ, data.getTarget());
            return params;
        }
    }


    /**
     * api/frontend/favorites/articles
     * POST
     * subscriber 发起者-用户
     * article_id 对象
     */
    class CollectArticleApi extends CyyRestOauthApi {

        private static final String KEY_ME = "subscriber";

        private static final String KEY_OBJ = "article_id";

        @Override
        protected String getUnformatedPath() {
            return "api/frontend/favorites/articles";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(ArticleCollectModel.ArticleCollectData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_ME, data.getUser());
            params.add(KEY_OBJ, data.getTarget());
            return params;
        }
    }

    /**
     * api/frontend/favorites/articles
     * DELETE
     * id
     */
    class DisCollectArticleApi extends CyyRestOauthApi {

        private static final String KEY_ID = "id";


        @Override
        protected String getUnformatedPath() {
            return "api/frontend/favorites/articles";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String id) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_ID, id);
            return params;
        }
    }


    /**
     * api/frontend/is-fav-article
     * GET
     * subscriber 发起者-用户
     * article_id 对象
     */
    class QueryArticleCollectStateApi extends CyyRestOauthApi {

        private static final String KEY_ME = "username";

        private static final String KEY_OBJ = "article_id";

        @Override
        protected String getUnformatedPath() {
            return "api/frontend/is-fav-article";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(QueryArticleCollectStateModel.QueryArticleCollectStateData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_ME, data.getUser());
            params.add(KEY_OBJ, data.getTarget());
            return params;
        }
    }


    /**
     * 查询关注等数量
     * api/frontend/app/user/counts
     * GET
     * username
     */
    class QuerySocialInfoApi extends CyyRestOauthApi {

        private static final String KEY_ME = "username";


        @Override
        protected String getUnformatedPath() {
            return "api/frontend/app/user/counts";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(String user) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_ME, user);
            return params;
        }
    }

    /**
     * FRONTEND - 检查指定用户是否是指定圈子的成员
     * <p>
     * 0.0.0
     * 检查指定用户是否是指定圈子的成员
     * <p>
     * /api/frontend/is-joined-circle
     * 参数
     * <p>
     * 字段	类型	描述
     * username	String
     * 用户名
     * <p>
     * circle_id	String
     * 圈子id
     */
    class CheckUserIsJoinedCircleApi extends IRestfulApi.CyyRestOauthApi {

        private static final String KEY_USER = "username";
        private static final String KEY_CIRCLE_ID = "circle_id";

        @Override
        protected String getUnformatedPath() {
            return "api/frontend/is-joined-circle";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(CircleJoinStateModel.CheckUserIsJoinedCircleData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USER, data.getUsername());
            params.add(KEY_CIRCLE_ID, data.getId());
            return params;
        }

    }

    /**
     * FRONTEND - 加入圈子
     * <p>
     * 0.0.0
     * 加入圈子
     * <p>
     * /api/frontend/circles/join
     * 参数
     * <p>
     * 字段	类型	描述
     * username	String
     * 用户名
     * <p>
     * circle_id	String
     * 圈子id
     */
    class JoinCircleApi extends IRestfulApi.CyyRestOauthApi {

        private static final String KEY_USER = "username";
        private static final String KEY_CIRCLE_ID = "circle_id";

        @Override
        protected String getUnformatedPath() {
            return "api/frontend/circles/join";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(JoinCircleModel.JoinCircleData data) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_USER, data.getUsername());
            params.add(KEY_CIRCLE_ID, data.getCircle_id());
            return params;
        }
    }

    /**
     * PROJECT_RENEWALS - 新建项目更新
     * <p>
     * 0.0.0 新建项目更新
     * <p>
     * POST
     * /api/frontend/project/{project_id}/renewals
     * 参数
     * <p>
     * 字段	类型	描述
     * id	Number 需要执行更新操作的文章记录id
     * <p>
     * project_id	Number 项目id
     * <p>
     * article_title	String 文章标题
     * <p>
     * article_content	String 文章内容
     * <p>
     * article_brief	String  文章简介
     * <p>
     * article_images	Array 文章配图id数组
     */
    class ProjContentPublishApi extends IRestfulApi.CyyRestOauthApi {

        private static final String KEY_PROJECT_ID = "project_id";
        private static final String KEY_ARTICLE_TITLE = "article_title";
        private static final String KEY_ARTICLE_CONTENT = "article_content";
        private static final String KEY_ARTICLE_IMAGES = "article_images";

        @Override
        protected String getUnformatedPath() {
            return "api/frontend/project/%s/renewals";
        }

        @Override
        protected String getUnformatedQuerystring() {
            return null;
        }

        public RequestParams newRequestParams(ProjChapterPublishModel.RequestData paramsDate) {
            RequestParams params = super.newRequestParams();
            params.add(KEY_PROJECT_ID, paramsDate.getProjectId());
            params.add(KEY_ARTICLE_TITLE, paramsDate.getTitle());
            params.add(KEY_ARTICLE_CONTENT, paramsDate.getContent());

            ArrayList<String> images = paramsDate.getImages();
            if (images == null || images.isEmpty())
                return params;
            params.put(KEY_ARTICLE_IMAGES, images);
            return params;
        }
    }
}
