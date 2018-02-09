package com.lht.lhtwebviewlib;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

/** 
 * @ClassName: JFParams 
 * @Description: 暂只考虑JsonObject，不处理嵌套
 * @date 2016年4月25日 下午1:06:25
 *  
 * @author leobert.lan
 * @version 1.0
 */
public class JFParams extends HashMap<String, Object>{
	
	/**
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么). 
	 */
	private static final long serialVersionUID = 1L;

	public String toJsonString() {
		JSONObject jObj = new JSONObject();
		for (String key:this.keySet()) {
			jObj.put(key, this.get(key));
		}
		return JSON.toJSONString(jObj);
	}

}
