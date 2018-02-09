package com.lht.cloudjob.share;

/**
 * @ClassName: IShare
 * @Description: TODO
 * @date 2016年3月25日 下午2:59:09
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface IShare {

	void share2QQ(ShareBean bean);

	void share2QZone(ShareBean bean);

	void share2Wechat(ShareBean bean);

	void share2WechatFC(ShareBean bean);

	void share2Sina(ShareBean bean);

}
