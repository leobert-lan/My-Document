package com.lht.pan_android.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Default;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;
import com.litesuits.orm.db.enums.AssignType;

/**
 * @ClassName: AdsTable
 * @Description: TODO
 * @date 2016年5月26日 下午1:58:37
 * 
 * @author leobert.lan
 * @version 1.0
 */
@Table("ads_model")
public class AdsBean {

	// id:广告的Id
	// jumpLink:点击广告跳转的链接
	// title:广告标题
	// imageUrl:广告的图片的url
	// startTime:广告生效开始时间戳
	// endTime: 失效时间
	// postTime: 发布时间

	@NotNull
	@Unique
	@Column("adId")
	@PrimaryKey(AssignType.BY_MYSELF)
	private String aid;

	@Default("null")
	@NotNull
	private String jumpLink;

	@NotNull
	private String imageUrl;

	@NotNull
	private Long startTime;

	@NotNull
	private String title;

	@NotNull
	private Long endTime;

	@Column("imgRes")
	private String imgRes;

	@Default("false")
	@Column("isDownload")
	private Boolean isDownload;

	@NotNull
	private Long postTime;

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getJumpLink() {
		return jumpLink;
	}

	public void setJumpLink(String jumpLink) {
		this.jumpLink = jumpLink;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public String getImgRes() {
		return imgRes;
	}

	public void setImgRes(String imgRes) {
		this.imgRes = imgRes;
	}

	public Boolean getIsDownload() {
		return isDownload;
	}

	public void setIsDownload(Boolean isDownload) {
		this.isDownload = isDownload;
	}

	public Long getPostTime() {
		return postTime;
	}

	public void setPostTime(Long postTime) {
		this.postTime = postTime;
	}

}
