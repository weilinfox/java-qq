package me.weilinfox.javaqq;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.HashSet;
import java.util.Set;

public class UserList extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private MainFrame mainFrame;
	public EditArea editArea;
	private JPanel listPanel;
	private Box listBox;
	private JPanel myPanel;
	private Set<Long> users;
	//private JPanel helpPanel;
	//private JLabel helpLabel;
	private final Dimension preDemension = new Dimension(200, 60);
	private final Dimension miniDemension = new Dimension(200, 30);
	public UserList() {
		super();

		listPanel = new JPanel();
		listBox = Box.createVerticalBox();
		listBox.setAlignmentX(LEFT_ALIGNMENT);
		myPanel = null;
		this.setViewportView(listPanel);
		listPanel.add(listBox);
		users = new HashSet<Long>();
	}

	private void scroll() {
		JScrollBar bar = this.getVerticalScrollBar();
		AdjustmentListener listener = new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());
				// 回滚完毕移除监听器
				bar.removeAdjustmentListener(this);
			}
		};
		// 添加监听器
		bar.addAdjustmentListener(listener);
		// 事件触发
		this.getViewport().doLayout();
	}

	private void addUser(long sid, boolean ifscroll) {
		JPanel myPanel = new JPanel();
		JButton myButton = new JButton(Long.toString(sid));
		myButton.setPreferredSize(preDemension);
		myButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editArea.setUid(sid);
			}
		});
		myPanel.add(myButton);
		listBox.add(myPanel);
		listBox.revalidate();

		if (ifscroll) scroll();
	}
	
	public void newUser(long sId) {
		if (users.contains(sId)) return;
		users.add(sId);
		addUser(sId, false);
	}

	public void init(MainFrame _mainframe, EditArea _editArea) {
		this.mainFrame = _mainframe;
		myPanel = new JPanel();
		//helpPanel = new JPanel();
		
		JButton myButton = new JButton("运行状态");
		this.editArea = _editArea;
		
		//helpLabel.setPreferredSize(preDemension);
		myButton.setPreferredSize(miniDemension);
		myButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HeartBeatInfo info = mainFrame.getHeartBeat();
				popDialog dlg;
				String msg;
				if (info == null) {
					msg = "暂无数据";
					dlg = new popDialog(_mainframe, "运行信息",
								msg, new Dimension(100, 200));
				} else {
					msg = "<html><body>"
						+ "<p>&emsp;&emsp;&emsp;账号:&nbsp;" + info.selfId + "</p></br>"
						+ "</br>"
						+ "<p>&emsp;上报间隔:&nbsp;" + info.interval + "</p></br>"
						+ "<p>&emsp;上报时间:&nbsp;" + info.postTime + "</p></br>"
						+ "<p>&emsp;上次信息:&nbsp;" + info.lastMessageTime + "</p></br>"
						+ "</br>"
						+ "<p>&emsp;&emsp;接收包:&nbsp;" + info.packetReceived + "</p></br>"
						+ "<p>&emsp;&emsp;发送包:&nbsp;" + info.packetSent + "</p></br>"
						+ "<p>&emsp;&emsp;丢失包:&nbsp;" + info.packetLost + "</p></br>"
						+ "<p>&emsp;接收消息:&nbsp;" + info.messageReceived + "</p></br>"
						+ "<p>&emsp;发送消息:&nbsp;" + info.messageSent + "</p></br>"
						+ "<p>&emsp;断连次数:&nbsp;" + info.disconnectTimes + "</p></br>"
						+ "<p>&emsp;丢失次数:&nbsp;" + info.lostTimes + "</p></br>"
						+ "</br>"
						+ "<p>&emsp;&emsp;&emsp;在线:&nbsp;" + info.isOnline + "</p></br>"
						+ "<p>&emsp;&emsp;状态好:&nbsp;" + info.appGood + "</p></br>"
						+ "<p>&emsp;被初始化:&nbsp;" + info.appInitialized + "</p></br>"
						+ "<p>&emsp;&emsp;被启用:&nbsp;" + info.appEnabled + "</p></br>"
						+ "<p>&emsp;&emsp;是好的:&nbsp;" + info.isGood + "</p>"
						+ "</body></html>";
					
					dlg = new popDialog(_mainframe, "运行信息",
												msg, new Dimension(300, 400));
				}
				dlg.setVisible(true);
			}
		});
		//helpPanel.add(helpLabel);
		myPanel.add(myButton);
		//listBox.add(helpPanel);
		listBox.add(myPanel);
	}
}

