package com.lht.pan_android.util.dir;

import com.lht.pan_android.Interface.IAsyncWithProgressbar;

import android.content.Context;

/**
 * @ClassName: CopyDataDirectionUtil
 * @Description: 从我的分享里面复制到网盘
 * @date 2016年1月5日 上午9:46:12
 * 
 * @author zhangbin
 * @version 1.0
 * @since JDK 1.6
 */
public class CopyDataDirectionUtil extends MoveDataDirectionUtil {

	public CopyDataDirectionUtil(Context ctx, IAsyncWithProgressbar iView) {
		super(ctx, iView);
	}

}
