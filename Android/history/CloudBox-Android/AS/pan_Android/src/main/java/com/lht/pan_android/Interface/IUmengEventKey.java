package com.lht.pan_android.Interface;

/**
 * @ClassName: IUmengEventKey
 * @Description: TODO
 * @date 2016年1月22日 下午1:00:18
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface IUmengEventKey {

	/**
	 * COUNT_CB_ADD_FOLDER: 添加－新建文件夹 计数事件
	 */
	public String COUNT_CB_ADD_FOLDER = "cb_add_folder";

	/**
	 * COUNT_CB_ADD_PHOTOS:添加－选择照片 计数事件
	 */
	public String COUNT_CB_ADD_PHOTOS = "cb_add_photos";

	/**
	 * COUNT_CB_ADD_SHOW:添加－弹出 计数事件
	 */
	public String COUNT_CB_ADD_SHOW = "cb_add_show";

	/**
	 * COUNT_CB_ADD_VIDEOS:添加－视频 计数事件
	 */
	public String COUNT_CB_ADD_VIDEOS = "cb_add_videos";

	/**
	 * COUNT_CB_BOX:云盘－进入首页 计数事件
	 */
	public String COUNT_CB_BOX = "cb_box";

	/**
	 * COUNT_CB_LOGIN_LOGIN:登录界面－登录 计数事件
	 */
	public String COUNT_CB_LOGIN_LOGIN = "cb_login_login";

	/**
	 * COUNT_CB_LOGIN_SHOW: 登录界面－出现 计数事件
	 */
	public String COUNT_CB_LOGIN_SHOW = "cb_login_show";

	/**
	 * COUNT_CB_LOGIN_SUCCESS:登录界面－登录成功 计数事件
	 */
	public String COUNT_CB_LOGIN_SUCCESS = "cb_login_success";

	/**
	 * COUNT_CB_MINE:我的－主页 计数事件
	 */
	public String COUNT_CB_MINE = "cb_mine";

	/**
	 * COUNT_CB_OPEN_AUDIO: 播放音频 计数事件
	 */
	public String COUNT_CB_OPEN_AUDIO = "cb_open_audio";

	/**
	 * COUNT_CB_OPEN_DOC:打开文件 计数事件
	 */
	public String COUNT_CB_OPEN_DOC = "cb_open_doc";

	/**
	 * COUNT_CB_OPEN_IMAGE:打开照片 计数事件
	 */
	public String COUNT_CB_OPEN_IMAGE = "cb_open_image";

	/**
	 * COUNT_CB_OPEN_IMAGE_ALBUM:打开相册照片（上传列表） 计数事件
	 */
	public String COUNT_CB_OPEN_IMAGE_ALBUM = "cb_open_image_album";

	/**
	 * COUNT_CB_OPEN_IMAGE_URL:打开云照片 计数事件
	 */
	public String COUNT_CB_OPEN_IMAGE_URL = "cb_open_image_url";

	/**
	 * COUNT_CB_OPEN_VIDEO: 播放视频 计数事件
	 */
	public String COUNT_CB_OPEN_VIDEO = "cb_open_video";

	/**
	 * COUNT_CB_OPEN_EXTRA:除去：文本、音频、视频、图片 以外的打开尝试，统计获取mine后的type
	 */
	public String COUNT_CB_OPEN_EXTRA = "cb_open_extra";

	/**
	 * COUNT_CB_KEY_MINETYPE:COUNT_CB_OPEN_EXTRA事件的key 文件类型.
	 */
	public String COUNT_CB_KEY_MINETYPE = "cb_open_extra_key_mimetype";

	/**
	 * COUNT_CB_QD_SHOW:启动页-出现 计数事件
	 */
	public String COUNT_CB_QD_SHOW = "cb_qd_show";

	/**
	 * COUNT_CB_QUIT:退出账户 计数事件
	 */
	public String COUNT_CB_QUIT = "cb_quit";

	/**
	 * COUNT_CB_SEARCH:搜索－主页 计数事件
	 */
	public String COUNT_CB_SEARCH = "cb_search";

	/**
	 * COUNT_CB_SEARCH_USER:搜索－用户 计数事件
	 */
	public String COUNT_CB_SEARCH_USER = "cb_search_user";

	/**
	 * COUNT_CB_SHARE:分享－主页 计数事件
	 */
	public String COUNT_CB_SHARE = "cb_share";

	/**
	 * COUNT_CB_TOKEN_FAIL:登录失效 计数事件
	 */
	public String COUNT_CB_TOKEN_FAIL = "cb_token_fail";

	/**
	 * COUNT_CB_TRANS:传输－主页 计数事件
	 */
	public String COUNT_CB_TRANS = "cb_trans";

	/**
	 * CALC_CB_BG: 进后台 计算事件
	 */
	public String CALC_CB_BG = "cb_bg";

	/**
	 * CALC_CB_DEL:删除 计算事件
	 */
	public String CALC_CB_DEL = "cb_del";

	/**
	 * CALC_CB_SHARE:分享计算事件
	 */
	public String CALC_CB_SHARE = "cb_share";
	/**
	 * CALC_CB_DOWNLOAD:下载 计算事件
	 */
	public String CALC_CB_DOWNLOAD = "cb_download";

	/**
	 * CALC_CB_FOREGROUND:返回前台 计算事件
	 */
	public String CALC_CB_FOREGROUND = "cb_Foreground";

	/**
	 * CALC_CB_LOGIN_FAIL:登录界面－登录失败 计算事件
	 */
	public String CALC_CB_LOGIN_FAIL = "cb_login_fail";

	/**
	 * CALC_CB_MOVE:移动 计算事件
	 */
	public String CALC_CB_MOVE = "cb_move";

	/**
	 * CALC_CB_RENAME:重命名 计算事件
	 */
	public String CALC_CB_RENAME = "cb_rename";

	/**
	 * CALC_CB_SEARCH_CLOUD:搜索－数据 计算事件
	 */
	public String CALC_CB_SEARCH_CLOUD = "cb_search_cloud";

	/**
	 * CALC_CB_SEARCHUSER: 搜索－用户主页 计算事件
	 */
	public String CALC_CB_SEARCHUSER = "cb_search_user";

	/**
	 * CALC_CB_TERMINATE:杀进程 计算事件
	 */
	public String CALC_CB_TERMINATE = "cb_Terminate";

	/**
	 * CALC_CB_TYPE:分类选择 计算事件
	 */
	public String CALC_CB_TYPE = "cb_type";

	/**
	 * CALC_CB_UPLOAD:上传 计算事件
	 */
	public String CALC_CB_UPLOAD = "cb_upload";

	/**
	 * CALC_CB_SHARE_CLOUDBOX:分享转存
	 */
	public String CALC_CB_SHARE_CLOUDBOX = "cb_share_cloudbox";

	/**
	 * CALC_CB_SHARE_COPYLINK:分享拷贝链接（公开和私密）
	 */
	public String CALC_CB_SHARE_COPYLINK = "cb_share_copylink";

	/**
	 * CALC_CB_SHARE_DOWNLOAD:分享-下载
	 */
	public String CALC_CB_SHARE_DOWNLOAD = "cb_share_download";

	/**
	 * CALC_CB_SHARE_GOT:分享给我的
	 */
	public String CALC_CB_SHARE_GOT = "cb_share_got";

	/**
	 * CALC_CB_SHARE_IGNOREGOT:忽略给我的分享
	 */
	public String CALC_CB_SHARE_IGNOREGOT = "cb_share_ignoreGot";

	/**
	 * CALC_CB_SHARE_IGNORESEND:忽略我的分享
	 */
	public String CALC_CB_SHARE_IGNORESEND = "cb_share_ignoreSend";

	/**
	 * CALC_CB_SHARE_LOOKUSER:分享查看用户
	 */
	public String CALC_CB_SHARE_LOOKUSER = "cb_share_lookusers";

	/**
	 * CALC_CB_SHARE_PRIVATE:私密分享
	 */
	public String CALC_CB_SHARE_PRIVATE = "cb_share_private";
	/**
	 * CALC_CB_SHARE_PUBLIC:公开分享
	 */
	public String CALC_CB_SHARE_PUBLIC = "cb_share_public";

	/**
	 * CALC_CB_SHARE_SEND:分享-我的分享
	 */
	public String CALC_CB_SHARE_SEND = "cb_share_send";

	/**
	 * CALC_CB_SHARE_USERS:分享——用户
	 */
	public String CALC_CB_SHARE_USERS = "cb_share_users";

	/**
	 * CALC_CB_SHARE_PWDMODIFY:分享密码修改
	 */
	public String CALC_CB_SHARE_PWDMODIFY = "cb_share_pwdmodify";
	/**
	 * QQ分享统计
	 */
	public String CALC_CB_SHARE_QQ = "cb_share_qq";
	/**
	 * QQ空间分享统计
	 */
	public String CALC_CB_SHARE_QQ_ZONE = "cb_share_qqzone";
	/**
	 * Sina分享统计
	 */
	public String CALC_CB_SHARE_SINA = "cb_share_sina";
	/**
	 * 微信分享统计
	 */
	public String CALC_CB_SHARE_WX = "cb_share_wx";
	/**
	 * 微信朋友圈分享统计
	 */
	public String CALC_CB_SHARE_WXFRIEND = "cb_share_wxfriend";
	/**
	 * 虚拟应用打开
	 */
	public String CALC_CB_OPEN_VIRTUAL = "cb_open_virtual";
	/**
	 * 文本打开
	 */
	public String CALC_CB_OPEN_DOCUMENT = "cb_open_document";

	/**
	 * 清除缓存
	 */
	public String CALC_CB_CLEAN_CACHE = "cb_clean_cache";

	/**
	 * 清除下载
	 */
	public String CALC_CB_CLEAN_DOWNLOAD = "cb_clean_download";
}
