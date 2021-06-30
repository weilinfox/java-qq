package me.weilinfox.cqhttpApi;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpConfigure {
	private static String protocol;
	private static String host;
	private static String token;
	private static int port;
	public static void setHttpConfigure(String _host, int _port, String _token) {
		protocol = "http";
		host = _host; port = _port; token = _token;
	}
	public static String sendGetRequest(String path, Map<String, String> parms) {
		URL getUrl = null;
		HttpURLConnection connect = null;
		int respCode = -1;
		String resp = "";
		try {
			String url = protocol + "://" + host + ":" + port;
			if (path.charAt(0) == '/') url += path;
			else url = url + "/" + path;
			url = url + "?access_token=" + token;
			for (String s : parms.keySet()) {
				url = url + "&" + s + "=" + parms.get(s);
			}
			getUrl = new URL(url);
			
			System.out.println("Get url: " + getUrl.toString());
			connect = (HttpURLConnection) getUrl.openConnection();
			
			connect.setRequestMethod("GET");
			connect.setConnectTimeout(1000);
			connect.setReadTimeout(1000);
			
			respCode = connect.getResponseCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (respCode == -1) {
				System.out.println("未知发送错误");
				return null;
			} else {
				byte[] buffer = new byte[1024];
				InputStream inStream = null;
				int length;
				try {
					inStream = connect.getInputStream();
					while ((length = inStream.read(buffer)) != -1) {
						resp += new String(buffer, 0, length);
					}
				} catch (IOException e) {
					System.out.println("响应接收失败\n" + e);
					return null;
				} finally {
					try {
						if (inStream != null)
							inStream.close();
					} catch (IOException ee) {
						ee.printStackTrace();
					}
				}
			}
		}
		return resp;
	}
}
