package com.lht.pan_android.bean;

/**
 * @ClassName: ImageBean
 * @Description: TODO 选择文件列表第一层数据模型，改名
 * @date 2015年12月1日 下午5:36:56
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class ImageBean {
	/**
	 * 文件夹的第一张图片路径
	 */
	private String topImagePath;
	/**
	 * 文件夹名
	 */
	private String folderName;
	/**
	 * 文件夹中的图片数
	 */
	private int imageCounts;

	public String getTopImagePath() {
		return topImagePath;
	}

	public void setTopImagePath(String topImagePath) {
		this.topImagePath = topImagePath;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public int getImageCounts() {
		return imageCounts;
	}

	public void setImageCounts(int imageCounts) {
		this.imageCounts = imageCounts;
	}

}
