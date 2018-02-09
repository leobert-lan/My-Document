package com.lht.pan_android.clazz;

import com.lht.pan_android.bean.ReportBean;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * @ClassName: Events
 * @date 2016年5月12日 下午2:58:31
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class Events {

	public static class RefuseOn4G {
		public static int postCount = 0;
	}

	public static class MultiUserQuery {
		public String apiRequest;

		public AsyncHttpResponseHandler handler;
	}

	public static class UploadSelectChange {
	}

	public static class SetTransOnlyOnWifi {
	}

	public static class TransSettingChanged {
		public boolean isOnlyOnWifi = true;
	}

	/**
	 * @ClassName: ShareReportEvent
	 * @Description: 举报事件
	 */
	public static class ShareReportEvent {
		private ReportBean bean;

		public ShareReportEvent(ReportBean bean) {
			super();
			this.bean = bean;
		}

		public ReportBean getBean() {
			return bean;
		}

		public void setBean(ReportBean bean) {
			this.bean = bean;
		}

	}

	public static class ReportItemClickEvent {
	}

	public static class CallFreshShareEvent {
	}

}
