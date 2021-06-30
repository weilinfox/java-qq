package me.weilinfox.javaqq;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.hjson.ParseException;
import org.hjson.Stringify;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import me.weilinfox.cqhttpApi.CqhttpApi;
import me.weilinfox.cqhttpApi.HttpConfigure;

/** 
 * <p> 主窗体类 </p>
 * @author weilinfox
 *
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private UserList usrList;
	private EditArea editArea;
	private ConfigArea cfgArea;
	private HttpServer miniServer;
	private long uid;
	
	private HeartBeatInfo heartInfo;
	
	public MainFrame() {
		super();

		this.setTitle("Java qq v0.0.1 - weilinfox 2021");
		
		this.setLocationRelativeTo(null);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Configure.endProcess();
				try {
					if (miniServer != null)
						miniServer.stop(0);
				} catch (Exception ee) {
					ee.printStackTrace();
				}
				System.exit(0);
			}
		});
		
		/*
		Color c1 = new Color(255, 0, 255);
		Color c2 = new Color(255, 0, 25);
		Color c3 = new Color(25, 0, 255);
		usrList.setBackground(c3);
		inBox.setBackground(c2);
		outBox.setBackground(c1);
		 */
		uid = new Long(0);
		miniServer = null;
		heartInfo = null;
		
		usrList = new UserList();
		editArea = new EditArea();
		cfgArea = new ConfigArea();
		Configure.setConfigure(cfgArea.getLogArea(), this);

		usrList.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		usrList.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		usrList.setMinimumSize(new Dimension(240, 700));
	}
	public void init() {
		/** 
		 * <p> 启动预配置和登录界面 </p>
		 */
		boolean autoInitFlag = 
			Configure.initConfigure() && Configure.readConfigFile();
		//System.out.println(Configure.cfgType);
		setConfigWindow(autoInitFlag);
		HttpConfigure.setHttpConfigure(
				Configure.httpPostHost,
				Configure.httpPostPort,
				Configure.token
				);
		CqhttpApi.initCqhttpApi();
		this.setVisible(true);
		// System.out.println(autoInitFlag);
	}
	
	public boolean initServer() {
		miniServer = null;
		try {
			miniServer = HttpServer.create(
					new InetSocketAddress(
							Configure.httpListenHost,
							Configure.httpListenPort
							),
					0
					);
			miniServer.createContext("/", new miniHttpHandler());
			miniServer.start();
			//System.out.println("started");
		} catch (IOException e) {
			Configure.logArea.append("Http服务器建立失败\n" + e + "\n");
			if (miniServer != null) {
				miniServer.stop(0);
				miniServer = null;
			}
			return false;
		}
		// 初始化消息处理界面
		MessageHandler.initMsgHandler(usrList, editArea, this);

		return true;
	}
	public void lanch() {
		/**
		 * <p> 启动主聊天完成配置 </p>
		 */
		this.setMainWindow();
		this.editArea.init(this, uid);
		this.usrList.init(this, this.editArea);
		this.setVisible(true);
	}
	public void hide() {
		this.setVisible(false);
	}
	
	public void newHeartBeat(HeartBeatInfo nBeat) {
		if (heartInfo != null && this.heartInfo.postTime > nBeat.postTime)
			return;
		this.heartInfo = nBeat;
	}
	public HeartBeatInfo getHeartBeat() {
		return this.heartInfo;
	}
	
	/**
	 * <p>清空窗体并绘制配置界面</p>
	 */
	private void setConfigWindow(boolean cfgSuccess) {
		GridBagConstraints gridBagConstraints =
							new GridBagConstraints();

		this.setSize(490, 400);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		
		//this.setMinimumSize(new Dimension(850, 700));
		
		this.getContentPane().removeAll();
		this.setLayout(new GridBagLayout());
		
		// 未填满显示区域的右对齐
		gridBagConstraints.anchor = GridBagConstraints.LINE_END;
		// 设置控件间隔
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);

		// 使组件完全填满显示区域
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.weightx = 10;
		gridBagConstraints.weighty = 10;
		this.add(cfgArea.getLogPane(), gridBagConstraints);

		gridBagConstraints.weighty = 1;
		JPanel[] tmp = cfgArea.getPanels();
		for (int i = 0; i < tmp.length; i++) {
			gridBagConstraints.gridy = i + 1;
			this.add(tmp[i], gridBagConstraints);
		}
		
		//System.out.println(Configure.cfgType);
		
		if (cfgSuccess) {
			cfgArea.setExecText(Configure.cqhttpPath + 
										Configure.cqhttpName);
			cfgArea.setUserText(Configure.myId);
			cfgArea.setPasswdText(Configure.myPasswd);
			cfgArea.setSendAddrText(Configure.httpPostHost);
			cfgArea.setSendPortText(Configure.httpPostPort);
			cfgArea.setServerAddrText(Configure.httpListenHost);
			cfgArea.setServerPortText(Configure.httpListenPort);
			cfgArea.setConfigureType(Configure.cfgType);
		} else {
			cfgArea.setInitable(true);
		}
	}


	/**
	 * <p>清空窗体并绘制主聊天窗口</p>
	 */
	private void setMainWindow() {
		GridBagConstraints gridBagConstraints =
							new GridBagConstraints();

		this.setSize(850, 700);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.setMinimumSize(new Dimension(850, 700));
		
		this.getContentPane().removeAll();
		this.setLayout(new GridBagLayout());
		// 未填满显示区域的右对齐
		gridBagConstraints.anchor = GridBagConstraints.LINE_END;
		// 设置控件间隔
		gridBagConstraints.insets = new Insets(2, 2, 2, 2);

		// 使组件完全填满显示区域
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridheight = 4;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.weighty = 32;
		this.add(usrList, gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.weightx = 10;
		gridBagConstraints.weighty = 10;
		this.add(editArea.getOutBox(), gridBagConstraints);
		
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.weighty = 0;
		this.add(editArea.getViewPanel(), gridBagConstraints);

		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.weightx = 10;
		gridBagConstraints.weighty = 4;
		this.add(editArea.getInBox(), gridBagConstraints);
		
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.weighty = 0;
		this.add(editArea.getMsgPanel(), gridBagConstraints);
	}

	/**
	 * <p>加载配置文件</p>
	 * @author weilinfox
	 *
	 */
	static class Configure {
		static JTextArea logArea;
		static MainFrame mainFrame;
		static String cqhttpPath;
		static String osName;
		static String archName;
		static String cqhttpName;
		static final String token = 
				"classWeilinfox:PrivateInuyasha";
		static CONTYPE cfgType;

		static JsonObject jsObject;
		static Long myId;
		static String myPasswd;
		static String httpPostHost;
		static int httpPostPort;
		static String httpListenUrl;
		static String httpListenHost;
		static int httpListenPort;
		static String httpListenName;
		static String charSet;
		
		static Process cqhttpProc;
		static Thread dataProc;
		static OutputStreamWriter cqhttpOut;
		
		static enum CONTYPE {
			HJSON, YAML, NONE
		}
		public static void setConfigure(JTextArea areaa, MainFrame mainn) {
			logArea = areaa;
			mainFrame = mainn;
		}
		/*
		 * 自动初始化配置
		 */
		public static boolean initConfigure() {
			cfgType = CONTYPE.NONE;
			cqhttpName = null;
			// 获取可执行文件名
			cqhttpPath = "./bin/go-cqhttp/";
			osName = System.getProperty("os.name");
			archName = System.getProperty("os.arch");
			try {
				// 转换 java 环境描述为 go 环境描述
				// 运行的二进制为 go 二进制
				archName = archName.toLowerCase().trim();
				if (archName.equals("x86"))
					archName = "386";
				osName = osName.split(" ")[0].trim().toLowerCase();
				if (osName.equals("mips64el"))
					osName = "mips64le";
			} catch (Exception e) {
				archName = null;
				osName = null;
			}
			//cqhttpName = "go-cqhttp-" + osName + "-" + archName;
			if (osName.equals("windows")) {
				charSet = "GBK";
				//cqhttpName = cqhttpName + ".exe";
				// 未知原因 无法运行64bit go-cqhttp
				// 但是似乎可以运行其他64bit 二进制
				cqhttpName = "go-cqhttp-windows-386.exe";
			} else if (osName.equals("linux")) {
				// 386 amd64 mips64el loongarch64
				charSet = "UTF-8";
				cqhttpName = "go-cqhttp-linux-" + archName;
			} else {
				charSet = "UTF-8";
				// 没有机子不知道
				cqhttpName = "go-cqhttp-darwin-" + archName;
			}
			//	charSet = "UTF-8";
			// 是否存在
			File execFile = new File(cqhttpPath + cqhttpName);
			if (!execFile.exists() || !execFile.canExecute()) {
				cqhttpName = null;
				cqhttpPath = null;
				archName = null;
				osName = null;
				return false;
			}
			// 配置文件类型
			setConfigType(cqhttpPath);
			if (cfgType == CONTYPE.NONE)
				return false;

			//System.out.println(cfgType);
			return true;
		}
		public static void setConfigType(String path) {
			if (path == null) {
				cfgType = CONTYPE.NONE;
				return;
			}
			File cfg = new File(path + "config.yaml");
			if (cfg.exists()) {
				cfgType = CONTYPE.YAML;
				return;
			}
			cfg = new File(path + "config.hjson");
			if (cfg.exists()) {
				cfgType = CONTYPE.HJSON;
				return;
			}
			cfgType = CONTYPE.NONE;
		}
		public static boolean readConfigFile() {
			String errorMsg = "";
			boolean errorFlag = false;
			if (cqhttpPath == null || cfgType == CONTYPE.NONE)
				return false;
			long configLen;
			byte[] configBytes;
			String configFile = cqhttpPath;
			String constring;
			File confile;
			FileInputStream constream;

			try {
				switch (cfgType) {
				case YAML:
					configFile = configFile + "config.yaml";
					break;
				case HJSON:
					configFile = configFile + "config.hjson";
					break;
				default:
					return false;
				}
				
				confile = new File(configFile);
				configLen = confile.length();
				configBytes = new byte[(int) configLen];
				constream = new FileInputStream(confile);
				constream.read(configBytes);
				constring = new String(configBytes);
				
				try {
					constream.close();
				} catch (IOException e) {
					errorMsg = errorMsg + "\n配置文件关闭出错\n" + e;
				}
				
				switch (cfgType) {
				case YAML:
					
					break;
				case HJSON:
					boolean autoSet = false;
					jsObject = JsonValue.readHjson(constring).asObject();
					JsonValue httpValue = jsObject.get("http_config");
					if (!jsObject.getString("access_token","").equals(token)) {
						jsObject.set("access_token", token);
						autoSet = true;
					}
					if (httpValue.asObject().getBoolean("enabled", false)) {
						httpValue.asObject().set("enabled", true);
						jsObject.set("http_config", httpValue);
						autoSet = true;
					}

					//System.out.println(jsObject.toString(Stringify.HJSON));

					myId = jsObject.getLong("uin", 0);
					myPasswd = jsObject.getString("password", "");
					httpPostHost = httpValue.asObject().getString("host", "");
					httpPostPort = httpValue.asObject().getInt("port", 5700);
					if (!httpPostHost.equals("127.0.0.1")) {
						httpPostHost = "127.0.0.1";
						httpValue.asObject().set("host", httpPostHost);
						jsObject.set("http_config", httpValue);
						autoSet = true;
					}
					try {
						httpListenUrl = httpValue.asObject().get("post_urls")
													.asObject().names().get(0);
						httpListenHost = httpListenUrl.split(":")[0];
						httpListenPort =
								Integer.parseInt(httpListenUrl.split(":")[1]);
						httpListenName = httpValue.asObject().get("post_urls")
									.asObject().getString(httpListenUrl, "");
						if (!httpListenHost.equals("127.0.0.1") ||
														httpListenPort < 1024) {
							httpListenHost = "127.0.0.1";
							if (httpListenPort < 1024) httpListenPort = 5701;
							httpListenUrl = "127.0.0.1:" + httpListenPort;
							JsonObject tmpObj = new JsonObject();
							tmpObj.set(httpListenUrl, httpListenName);
							httpValue.asObject().get("post_urls").asObject()
													.set("post_urls", tmpObj);
							jsObject.set("http_config", httpValue);
							autoSet = true;
						}
					} catch (Exception e) {
						httpListenUrl = "";
						httpListenHost = "";
						httpListenPort = -1;
						errorMsg = errorMsg + "\n配置文件解析错误\n" + e;
						errorFlag = true;
					}
					//System.out.println(myId);
					//System.out.println();
					
					if (!errorFlag && autoSet) {
						try {
							writeConfigFile(jsObject, confile);
						} catch (IOException e) {
							errorMsg = errorMsg + "\n配置文件写入错误\n" + e;
							errorFlag = true;
						}
					}
					
					break;
				default:
					return false;
				}

			} catch (ParseException | IOException e) {
				errorMsg = errorMsg + "\n配置文件处理出错\n" + e;
				errorFlag = true;
			}
			if (errorFlag) {
				logArea.setText(errorMsg);
				// System.out.println(errorMsg);
				// 显示错误
				return false;
			} else {
				logArea.setText("配置读取完成");
				return true;
			}
		}
		public static void saveConfigure() {
			try {
				writeConfigFile(jsObject, new File(cqhttpPath + cqhttpName));
			} catch (IOException e) {
				logArea.setText("配置文件写入错误\n" + e);
				return;
			}
			logArea.setText("配置写入完成");
		}
		public static void writeConfigFile(JsonObject obj, File confile)
															throws IOException {
			FileOutputStream outstream = new FileOutputStream(confile);
			outstream.write(
					obj.toString(Stringify.HJSON).getBytes()
					);
			outstream.close();
		}
		
		public static boolean login() {
			String runPath = cqhttpPath + cqhttpName;
			File runFile = new File(runPath);
			ProcessBuilder procBuilder = null;
			Process proc = null;
			InputStreamReader input = null;
			BufferedInputStream inputStream = null;
			OutputStreamWriter output = null;
			
			try {
				procBuilder = new ProcessBuilder(runPath);
				procBuilder.redirectErrorStream(true);
				procBuilder.directory(runFile.getParentFile());
				proc = procBuilder.start();
				inputStream = new BufferedInputStream(proc.getInputStream());
				input = new InputStreamReader(inputStream, "UTF-8");
				output = new OutputStreamWriter(proc.getOutputStream(), "UTF-8");
				
				cqhttpProc = proc;
				cqhttpOut = output;
			} catch (Exception e) {
				logArea.setText("go-cqhttp运行出错\n" + e);
				if (proc != null)
					proc.destroy();
			} finally {
				if (inputStream == null || input == null || output == null) {
					return false;
				}
			}
			
			try {
				dataProc = null;
				dataProc = new rundataThread(input, output, logArea, mainFrame);
				dataProc.start();
			} catch (Exception e) {
				logArea.setText("go-cqhttp监视线程运行出错\n" + e);
			} finally {
				if (dataProc == null) {
					return false;
				}
			}
			
			return true;
		}
		
		public static void endProcess() {
			if (dataProc != null && dataProc.isAlive())
				dataProc.interrupt();
			if (cqhttpProc != null && cqhttpProc.isAlive())
				cqhttpProc.destroy();
		}
	}
}

/**
 * <p>go-cqhttp监视线程</p>
 * @author weilinfox
 *
 */
class rundataThread extends Thread {
	InputStreamReader input;
	OutputStreamWriter output;
	JTextArea logArea;
	MainFrame mainFrame;
	char[] buffer;
	rundataThread(
			InputStreamReader _input,
			OutputStreamWriter _output,
			JTextArea _area,
			MainFrame _main
									) {
		super();

		input = _input;
		output = _output;
		logArea = _area;
		mainFrame = _main;
		buffer = new char[1024];
	}
	@Override
	public void run() {
		int getNum;
		logArea.setText("");
		boolean success = false;

		while (!this.isInterrupted()) {
			try {
				while (true) {
					if ((getNum = input.read(buffer)) == -1) break;
					logArea.append(new String(buffer, 0, getNum));
					//System.out.println(new String(buffer, 0, getNum));
					if (!success && getNum > 0) {
						if (logArea.getText().indexOf(
								new String("登录成功")
									) >= 0) {
							success = true;
							if (mainFrame.initServer())
								mainFrame.lanch();
							else
								MainFrame.Configure.logArea.append(
										"启动失败 请检查配置并重启以重试\n"
										);
							//System.out.println("Login Success~");
						}
					}
				}
			}catch (Exception e) {
				logArea.append("线程输出读取错误\n" + e);
				break;
			}
		}
	}
}

/**
 * <p>http服务器handler</p>
 * @author weilinfox
 *
 */
class miniHttpHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) {
		String request = null;
		try {
			//String resp = "";
			byte[] buffer;
			InputStream inStream = null;
			if (exchange.getRequestHeaders().containsKey("Content-length")) {
				buffer = new byte[
	Integer.parseInt(exchange.getRequestHeaders().get("Content-length").get(0))
				                  ];
				inStream = exchange.getRequestBody();
	
				inStream.read(buffer);
				request = new String(buffer, "UTF-8");
				//System.out.println(request);
	
				inStream.close();
			}
		} catch (IOException e) {
			System.out.println("Http读取失败" + e);
		} catch (Exception e) {
			System.out.println("Http其他错误" + e);
		} finally {
			try {
				OutputStream outStream = null;
				exchange.sendResponseHeaders(200, 0);
				
				outStream = exchange.getResponseBody();
				outStream.write("".getBytes("UTF-8"));
				outStream.close();
			} catch (IOException e) {
				System.out.println("Http响应失败" + e);
			} catch (Exception e) {
				System.out.println("Http其他错误" + e);
			}
		}
		
		if (request != null && request.length() > 0) {
			// 请求处理
			MessageHandler handler = new MessageHandler(request);
			handler.start();
		}
	}
}

/**
 * <p>html弹出窗口</p>
 * @author weilinfox
 *
 */
class popDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public popDialog (JFrame father, String title, String htmlMsg, Dimension pred)  {
		super(father, title, false);
		JLabel label = new JLabel(htmlMsg);
		label.setBackground(Color.blue);
		this.setLayout(new GridLayout(1, 1));
		this.setSize(pred);
		this.add(label);
		this.setResizable(true);
		this.setLocationRelativeTo(null);
	}
}
