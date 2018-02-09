package com.lht.pan_android.mpv.model;

import java.io.File;

import org.apache.http.Header;

import com.lht.pan_android.bean.AdsBean;
import com.lht.pan_android.util.Base64Utils;
import com.litesuits.orm.LiteOrm;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

/**
 * @ClassName: AdsImageHunterModel
 * @Description: TODO
 * @date 2016年5月26日 下午1:42:02
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class AdsImageHunterModel extends FileAsyncHttpResponseHandler {

	private LiteOrm liteOrm;

	private String adId;

	public AdsImageHunterModel(File file, LiteOrm liteOrm, String id) {
		super(file);
		this.liteOrm = liteOrm;
		this.adId = id;
	}

	@Override
	public void onFailure(int arg0, Header[] arg1, Throwable arg2, File arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(int arg0, Header[] arg1, File arg2) {

		String oldName = arg2.getName();
		String newName = Base64Utils.GetBASE64(oldName).replaceAll("\r|\n", "");

		File dest = new File(arg2.getParentFile(), newName);

		String path = arg2.renameTo(dest) ? dest.getAbsolutePath() : arg2.getAbsolutePath();

		AdsBean bean = liteOrm.queryById(adId, AdsBean.class);
		if (bean != null) {
			bean.setIsDownload(true);
			bean.setImgRes(path);

			// int i =
			liteOrm.update(bean);
			// Log.e("lmsg", "update:" + i);
			//
			// AdsBean bean2 = liteOrm.queryById(adId, AdsBean.class);
			// String s = JSONObject.toJSONString(bean2);
			// Log.e("lmsg", "check:\r\n" + s);
		}

	}

}
