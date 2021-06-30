package me.weilinfox.javaqq;

import org.hjson.JsonObject;
import org.hjson.JsonValue;

public class MessageHandler extends Thread {
	private static UserList userList;
	private static EditArea editErea;
	private static MainFrame mainFrame;
	private String info;
	
	public MessageHandler(String jsMsg) {
		super();
		info = jsMsg;
	}
	public static void initMsgHandler(UserList _list, EditArea _erea, MainFrame _frame) {
		userList = _list;
		editErea = _erea;
		mainFrame = _frame;
	}
	@Override
	public void run() {
		JsonObject jsObject = JsonValue.readJSON(this.info).asObject();
		if (jsObject.getString("post_type", "").equals("meta_event") &&
				jsObject.getString("meta_event_type", "").equals("heartbeat")) {
			MessageHandler.mainFrame.newHeartBeat(new HeartBeatInfo(jsObject));
		} else if (jsObject.getString("post_type", "").equals("message")) {
			MsgInfo nMsg = new MsgInfo(jsObject);
			MessageHandler.editErea.receiveMsg(nMsg);
			MessageHandler.userList.newUser(nMsg.superId);
			System.out.println(nMsg);
		} else {
			System.out.println("不支持的消息" + jsObject);
		}
	}
}

class MsgInfo {
	long userId;
	long groupId;
	long messageId;
	long selfId;
	long postTime;
	// -groupId || userId
	long superId;
	MSGTYPE messageType;
	String rawMessage;
	String canCopyMessage;
	String[] canShowMessages;
	public static enum MSGTYPE {
		GROUP, PRIVATE
	}
	
	public MsgInfo(String msg) {
		rawMessage = msg;
		canCopyMessage = getCanCopyMessage(rawMessage);
		canShowMessages = getCanShowMessages(rawMessage);
	}

	public MsgInfo(JsonObject jsObject) {
		groupId = jsObject.getLong("group_id", -1);
		userId = jsObject.getLong("user_id", -1);
		messageId = jsObject.getLong("message_id", -1);
		rawMessage = jsObject.getString("message", "");
		selfId = jsObject.getLong("self_id", -1);
		postTime = jsObject.getLong("time", -1);
		if (jsObject.getString("message_type", "").equals("group")) {
			messageType = MsgInfo.MSGTYPE.GROUP;
			superId = -groupId;
		} else {
			messageType = MsgInfo.MSGTYPE.PRIVATE;
			superId = userId;
		}
		canCopyMessage = getCanCopyMessage(rawMessage);
		canShowMessages = getCanShowMessages(rawMessage);
	}
	private String getCanCopyMessage(String msg) {
		return msg;
	}
	private String[] getCanShowMessages(String msg) {
		return null;
	}
	
	@Override
	public String toString() {
		return messageType + " in " + groupId + " from " 
				+ userId + " to " + selfId + " : " + rawMessage;
	}
}

class HeartBeatInfo {
	long interval;
	long selfId;
	
	boolean appEnabled;
	boolean appGood;
	boolean appInitialized;
	boolean isGood;
	boolean isOnline;
	
	long packetReceived;
	long packetSent;
	long packetLost;
	long messageReceived;
	long messageSent;
	long disconnectTimes;
	long lostTimes;
	long lastMessageTime;
	long postTime;
	
	public HeartBeatInfo(JsonObject jsObject) {
		interval = jsObject.getLong("interval", -1);
		selfId = jsObject.getLong("self_id", -1);
		postTime = jsObject.getLong("time", -1);
		
		JsonValue jsValue = jsObject.get("status");
		if (jsValue == null) {
			appEnabled = appGood = appInitialized = isGood = isOnline = false;
		} else {
			appEnabled = jsValue.asObject().getBoolean("app_enabled", false);
			appGood = jsValue.asObject().getBoolean("app_good", false);
			appInitialized = jsValue.asObject().getBoolean("app_initialized", false);
			isGood = jsValue.asObject().getBoolean("good", false);
			isOnline = jsValue.asObject().getBoolean("online", false);
		}

		if (jsValue != null)
			jsValue = jsValue.asObject().get("stat");
		if (jsValue == null) {
			packetReceived = packetSent = packetLost = messageReceived = 
			messageSent = disconnectTimes = lostTimes = lastMessageTime = 
			postTime = -1;
		} else {
			packetReceived = jsValue.asObject().getLong("packet_received", -1);
			packetSent = jsValue.asObject().getLong("packet_sent", -1);
			packetLost = jsValue.asObject().getLong("packet_lost", -1);
			messageReceived = jsValue.asObject().getLong("message_received", -1);
			messageSent = jsValue.asObject().getLong("message_sent", -1);
			disconnectTimes = jsValue.asObject().getLong("disconnect_times", -1);
			lostTimes = jsValue.asObject().getLong("lost_times", -1);
			lastMessageTime = jsValue.asObject().getLong("last_message_time", -1);
		}
	}
	
	@Override
	public String toString() {
		return "interval is " + interval + ", selfId is " + selfId + 
				", good is " + isGood + ", time is " + postTime + 
				", lastMessageTime is " + lastMessageTime;
	}
}
