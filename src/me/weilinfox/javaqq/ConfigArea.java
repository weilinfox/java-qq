package me.weilinfox.javaqq;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import me.weilinfox.javaqq.MainFrame.Configure;

/**
 * <p>登录配置界面</p>
 * @author weilinfox
 *
 */
public class ConfigArea extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private JScrollPane logPane;
	private JTextArea logArea;

	private JPanel panel1;
	private JPanel panel2;
	private JPanel panel3;
	private JPanel panel4;
	private JPanel panel5;

	private JLabel execLabel;
	private JLabel userLabel;
	private JLabel passwdLabel;
	private JLabel sendAddrLabel;
	private JLabel sendPortLabel;
	private JLabel serverAddrLabel;
	private JLabel serverPortLabel;
	private JLabel cfgFileLabel;

	private JTextField execText;
	private JTextField userText;
	private JPasswordField passwdText;
	private JTextField sendAddrText;
	private JTextField sendPortText;
	private JTextField serverAddrText;
	private JTextField serverPortText;
	private JTextArea enterText;

	private JButton lanchButton;
	private JButton saveButton;
	private JButton initButton;
	private JButton pathChooseButton;
	private JButton enterButton;
	
	private JComboBox<String> cfgFileType;

	public ConfigArea() {
		super();

		logPane = new JScrollPane();
		logArea = new JTextArea();
		logPane.setViewportView(logArea);
		logPane.setVerticalScrollBarPolicy(
						JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		logPane.setHorizontalScrollBarPolicy(
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		logArea.setEditable(false);
		logArea.setTabSize(1);
		logArea.setLineWrap(true);

		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 10, 0);
		panel1 = new JPanel(flowLayout);
		panel2 = new JPanel(flowLayout);
		panel3 = new JPanel(flowLayout);
		panel4 = new JPanel(flowLayout);
		flowLayout = new FlowLayout(FlowLayout.RIGHT, 10, 0);
		panel5 = new JPanel(flowLayout);
		panel5.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		execLabel = new JLabel("go-cqhttp二进制");
		userLabel = new JLabel("账号");
		passwdLabel = new JLabel("密码");
		sendAddrLabel = new JLabel("发送地址");
		sendPortLabel = new JLabel("发送端口");
		serverAddrLabel = new JLabel("接收地址");
		serverPortLabel = new JLabel("接收端口");
		cfgFileLabel = new JLabel("配置文件类型");

		initButton = new JButton("初始化");
		initButton.setPreferredSize(new Dimension(90, 25));
		initButton.setEnabled(false);
		saveButton = new JButton("保存配置");
		saveButton.setPreferredSize(new Dimension(90, 25));
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainFrame.Configure.saveConfigure();
			}
		});
		pathChooseButton = new JButton("选择");
		pathChooseButton.setPreferredSize(new Dimension(60, 25));
		pathChooseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fchooser = new JFileChooser();
				File binFile;
				fchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fchooser.showDialog(new JLabel(), "选择go-cqhttp二进制");
				binFile = fchooser.getSelectedFile();
				logArea.setText("");
				if (binFile != null && binFile.canExecute())
					getAllConfigure(binFile.getAbsolutePath());
			}
		});
		enterButton = new JButton("输入");
		enterButton.setPreferredSize(new Dimension(60, 25));
		enterButton.setEnabled(false);
		enterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String tmp = enterText.getText();
				enterText.setText("");
				if (tmp.length() > 0) {
					try {
						MainFrame.Configure.cqhttpOut.write(tmp);
						MainFrame.Configure.cqhttpOut.flush();
						logArea.append(tmp);
					} catch (Exception ee) {
						if (!MainFrame.Configure.cqhttpProc.isAlive()) {
							logArea.append("cqhttp进程已退出");
							logArea.append("请重新启动");
						} else
							logArea.append("输入出错\n" + ee);
					}
				} else {
					
				}
			}
		});
		lanchButton = new JButton("登录");
		lanchButton.setPreferredSize(new Dimension(90, 25));
		lanchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logArea.setText("");
				if (MainFrame.Configure.login()) {
					initButton.setEnabled(false);
					saveButton.setEnabled(false);
					enterText.setEditable(true);
					enterButton.setEnabled(true);
					pathChooseButton.setEnabled(false);
					userText.setEditable(false);
					passwdText.setEditable(false);
					sendPortText.setEditable(false);
					serverPortText.setEditable(false);
					lanchButton.setEnabled(false);
				} else {
					logArea.append("\n登录出错 请查看之前的错误信息");
				}
			}
		});

		execText = new JTextField();
		execText.setColumns(25);
		execText.setEditable(false);
		userText = new JTextField();
		userText.setColumns(10);
		userText.setEditable(true);
		passwdText = new JPasswordField();
		passwdText.setColumns(10);
		passwdText.setEditable(true);
		sendAddrText = new JTextField();
		sendAddrText.setColumns(8);
		sendAddrText.setEditable(false);
		sendPortText = new JTextField();
		sendPortText.setColumns(5);
		sendPortText.setEditable(true);
		serverAddrText = new JTextField();
		serverAddrText.setColumns(8);
		serverAddrText.setEditable(false);
		serverPortText = new JTextField();
		serverPortText.setColumns(5);
		serverPortText.setEditable(true);
		enterText = new JTextArea();
		enterText.setColumns(35);
		enterText.setRows(2);
		enterText.setEditable(false);
		
		cfgFileType = new JComboBox<String>();
		cfgFileType.addItem("未知");
		cfgFileType.addItem("yaml");
		cfgFileType.addItem("hjson");
		cfgFileType.setPreferredSize(new Dimension(70, 25));
		cfgFileType.setEnabled(false);

		panel1.add(execLabel);
		panel1.add(execText);
		panel1.add(pathChooseButton);

		panel2.add(userLabel);
		panel2.add(userText);
		panel2.add(sendAddrLabel);
		panel2.add(sendAddrText);
		panel2.add(sendPortLabel);
		panel2.add(sendPortText);

		panel3.add(passwdLabel);
		panel3.add(passwdText);
		panel3.add(serverAddrLabel);
		panel3.add(serverAddrText);
		panel3.add(serverPortLabel);
		panel3.add(serverPortText);
		
		panel4.add(enterText);
		panel4.add(enterButton);
		
		panel5.add(lanchButton);
		panel5.add(saveButton);
		panel5.add(initButton);
		panel5.add(cfgFileType);
		panel5.add(cfgFileLabel);
	}
	
	public JPanel[] getPanels() {
		JPanel[] ans = {
				this.panel1,
				this.panel2,
				this.panel3,
				this.panel4,
				this.panel5
		};

		return ans;
	}

	public JScrollPane getLogPane() {
		return this.logPane;
	}

	public void getAllConfigure(String _exec) {
		File execFile = new File(_exec);
		this.execText.setText(_exec);
		MainFrame.Configure.cqhttpPath = execFile.getParent() + "/";
		MainFrame.Configure.cqhttpName = execFile.getName();
		MainFrame.Configure.setConfigType(execFile.getParent() + "/");
		
		String osName = System.getProperty("os.name");
		String archName = System.getProperty("os.arch");
		try {
			archName = archName.toLowerCase().trim();
			osName = osName.split(" ")[0].trim().toLowerCase();
		} catch (Exception e) {
			archName = null;
			osName = null;
		}
		// 在windows平台使用UTF-8也没有乱码
		// 而GBK出现乱码
		// 字符常量用于indexOf new 成String即可
		if (osName.equals("windows"))
			MainFrame.Configure.charSet = "GBK";
		else
			MainFrame.Configure.charSet = "UTF-8";
		
		setConfigureType(MainFrame.Configure.cfgType);
		boolean readFile = MainFrame.Configure.readConfigFile();
		if (!readFile) {
			// 错误处理
			this.initButton.setEnabled(true);
		} else {
			this.setExecText(Configure.cqhttpPath + 
					Configure.cqhttpName);
			this.setUserText(Configure.myId);
			this.setPasswdText(Configure.myPasswd);
			this.setSendAddrText(Configure.httpPostHost);
			this.setSendPortText(Configure.httpPostPort);
			this.setServerAddrText(Configure.httpListenHost);
			this.setServerPortText(Configure.httpListenPort);
			this.setConfigureType(Configure.cfgType);
			this.initButton.setEnabled(false);
		}
		//MainFrame.Configure.cqhttpName;
	}
	public void setExecText(String _exec) {
		this.execText.setText(_exec);
	}
	public void setUserText(Long _uid) {
		this.userText.setText(_uid.toString());
	}
	public void setPasswdText(String _passwd) {
		this.passwdText.setText(_passwd);
	}
	public void setSendAddrText(String _addr) {
		this.sendAddrText.setText(_addr);
	}
	public void setSendPortText(int _port) {
		this.sendPortText.setText(Integer.toString(_port));
	}
	public void setServerAddrText(String _addr) {
		this.serverAddrText.setText(_addr);
	}
	public void setServerPortText(int _port) {
		this.serverPortText.setText(Integer.toString(_port));
	}
	public void setConfigureType(MainFrame.Configure.CONTYPE type) {
		//System.out.println(type);
		switch (type) {
		case YAML:
			this.cfgFileType.setSelectedIndex(1);
			break;
		case HJSON:
			this.cfgFileType.setSelectedIndex(2);
			break;
		case NONE:
			this.cfgFileType.setSelectedIndex(0);
			this.initButton.setEnabled(true);
			break;
		}
	}
	
	public void setInitable(boolean init) {
		this.initButton.setEnabled(init);
		this.saveButton.setEnabled(!init);
	}
	
	public JTextArea getLogArea() {
		return this.logArea;
	}
}
