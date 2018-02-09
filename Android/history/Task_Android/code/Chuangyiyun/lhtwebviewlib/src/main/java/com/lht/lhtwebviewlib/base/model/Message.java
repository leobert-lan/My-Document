package com.lht.lhtwebviewlib.base.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Message
 * @Description: bridge 数据封装类
 * @date 2016年2月17日 上午10:05:15
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class Message {

	/**
	 * callbackId:回调方法id
	 */
	private String callbackId;

	/**
	 * responseId:回传对象id
	 */
	private String responseId;

	/**
	 * responseData:回传数据
	 */
	private String responseData;

	/**
	 * data:data of message
	 */
	private String data;

	/**
	 * handlerName:name of handler
	 */
	private String handlerName;

	/**
	 * CALLBACK_ID_STR:json-key belongs to:callback_id
	 */
	private final static String CALLBACK_ID_STR = "callbackId";

	/**
	 * RESPONSE_ID_STR:json-key belongs to:response_id
	 */
	private final static String RESPONSE_ID_STR = "responseId";

	/**
	 * RESPONSE_DATA_STR:json-key belongs to:response_data
	 */
	private final static String RESPONSE_DATA_STR = "responseData";

	/**
	 * DATA_STR:json-key belongs to:data ,the data of the message
	 */
	private final static String DATA_STR = "data";

	/**
	 * HANDLER_NAME_STR:json-key belongs to:handler_name
	 */
	private final static String HANDLER_NAME_STR = "handlerName";

	// ==================================================
	// ===================== get and set of members ===

	public String getResponseId() {
		return responseId;
	}

	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}

	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}

	public String getCallbackId() {
		return callbackId;
	}

	public void setCallbackId(String callbackId) {
		this.callbackId = callbackId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

	/**
	 * @Title: toJson
	 * @Description: 获取序列化后的数据 JSON-formated
	 * @author: leobert.lan
	 * @return json格式： { $callbackId$:"", $data$:"", $handlerName$:"",
	 *         $responseData$:"", $resonseId$:"" }
	 */
	public String toJson() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(CALLBACK_ID_STR, getCallbackId());
			jsonObject.put(DATA_STR, getData());
			jsonObject.put(HANDLER_NAME_STR, getHandlerName());
			jsonObject.put(RESPONSE_DATA_STR, getResponseData());
			jsonObject.put(RESPONSE_ID_STR, getResponseId());
			return jsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Title: toObject
	 * @Description: 反序列化，获取Message对象
	 * @author: leobert.lan
	 * @param jsonStr
	 * @return 反序列化后的对象
	 */
	public static Message toObject(String jsonStr) {
		Message m = new Message();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			m.setHandlerName(jsonObject.has(HANDLER_NAME_STR) ? jsonObject
					.getString(HANDLER_NAME_STR) : null);
			m.setCallbackId(jsonObject.has(CALLBACK_ID_STR) ? jsonObject
					.getString(CALLBACK_ID_STR) : null);
			m.setResponseData(jsonObject.has(RESPONSE_DATA_STR) ? jsonObject
					.getString(RESPONSE_DATA_STR) : null);
			m.setResponseId(jsonObject.has(RESPONSE_ID_STR) ? jsonObject
					.getString(RESPONSE_ID_STR) : null);
			m.setData(jsonObject.has(DATA_STR) ? jsonObject.getString(DATA_STR)
					: null);
			return m;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return m;
	}

	/**
	 * @Title: toArrayList
	 * @Description: 反序列化 [{message1},{message2},....]
	 * @author: leobert.lan
	 * @param jsonStr
	 * @return List &lt Message &gt
	 */
	public static List<Message> toArrayList(String jsonStr) {
		List<Message> list = new ArrayList<Message>();
		try {
			JSONArray jsonArray = new JSONArray(jsonStr);
			for (int i = 0; i < jsonArray.length(); i++) {
				Message m = new Message();
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				m.setHandlerName(jsonObject.has(HANDLER_NAME_STR) ? jsonObject
						.getString(HANDLER_NAME_STR) : null);
				m.setCallbackId(jsonObject.has(CALLBACK_ID_STR) ? jsonObject
						.getString(CALLBACK_ID_STR) : null);
				m.setResponseData(jsonObject.has(RESPONSE_DATA_STR) ? jsonObject
						.getString(RESPONSE_DATA_STR) : null);
				m.setResponseId(jsonObject.has(RESPONSE_ID_STR) ? jsonObject
						.getString(RESPONSE_ID_STR) : null);
				m.setData(jsonObject.has(DATA_STR) ? jsonObject
						.getString(DATA_STR) : null);
				list.add(m);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
}
