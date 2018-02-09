package com.lht.creationspace.module.user.security.model.pojo;

/**
 * @ClassName: WeChatUserInfoBean
 * @Description: TODO
 * @date 2016年3月10日 上午11:05:32
 * 
 * @author zhangbin
 * @version 1.0
 */
public class WeChatUserInfoBean {

	private String openid;
	private String nickname;
	private String sex;
	private String province;
	private String city;
	private String headimgurl;
	private String[] privilege;
	//此为唯一id
	private String unionid;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String[] getPrivilege() {
		return privilege;
	}

	public void setPrivilege(String[] privilege) {
		this.privilege = privilege;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
}
