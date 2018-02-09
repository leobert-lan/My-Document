package com.lht.pan_android.Interface;

import java.io.File;

/**
 * @ClassName: IPreViewFile
 * @Description: TODO
 * @date 2016年3月11日 下午1:13:11
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface IPreViewFile {

	/**
	 * @Title: loadPreviewFile
	 * @Description: 下载文件
	 * @author: leobert.lan
	 */
	void loadPreviewFile(String url, String localPath);

	/**
	 * @Title: compareCheckSum
	 * @Description: 本地文件Md5与服务器文件Md5比较 服务端CheckSum无效
	 * @author: leobert.lan
	 * @param serverFileCheckSum
	 * @param localFilePath
	 * @return true if double file with the same Md5,false otherwise;
	 */
	boolean compareCheckSum(String serverFileCheckSum, String localFilePath);

	/**
	 * @Title: checkSupportedBy3rdParty
	 * @Description: 检查是否支持打开
	 * @author: leobert.lan
	 * @param contentType
	 * @return
	 */
	boolean checkSupportedBy3rdParty(String contentType);

	/**
	 * @Title: endJob
	 * @Description: 结束任务时回调
	 * @author: leobert.lan
	 */
	void endJob();

	/**
	 * @Title: getMIMETypeBySuffix
	 * @Description: 扩展名转换文件类型
	 * @author: leobert.lan
	 * @param suffix
	 * @return
	 */
	String getMIMETypeBySuffix(String suffix);

	/**
	 * @Title: getMIMEType
	 * @Description: 获取扩展名
	 * @author: leobert.lan
	 * @param f
	 * @return
	 */
	String getMIMEType(File f);

	/**
	 * @Title: getMIMEType
	 * @Description: 获取扩展名
	 * @author: leobert.lan
	 * @param filePath
	 * @return
	 */
	String getMIMEType(String filePath);

	/**
	 * @Title: openPreView
	 * @Description: 打开文件
	 * @author: leobert.lan
	 * @param localPath
	 * @param contentType
	 */
	void openPreView(String localPath, String contentType);

	/**
	 * @Title: cancelPreview
	 * @Description: 取消预览要求
	 * @author: leobert.lan
	 */
	void cancelPreview();

}
