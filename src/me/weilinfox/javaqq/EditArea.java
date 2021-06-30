package me.weilinfox.javaqq;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import org.hjson.JsonObject;
import org.hjson.JsonValue;

import me.weilinfox.cqhttpApi.CqhttpApi;

public class EditArea {
	private MainFrame mainFrame;
	private InputBox inBox;
	private OutputBox outBox;
	private JPanel msgPanel;
	private JPanel viewPanel;
	private JButton sendButton;
	private JButton fileButton;
	private JButton historyButton;
	private CancelButton cancelButton;
	private JLabel helpLabel;
	// uid引用的对象为inBox和outBox共享
	// 更新时不应该new而是刷新其值
	private Long uid;


	public EditArea() {
		inBox = new InputBox();
		outBox = new OutputBox();
		sendButton = new JButton("Send");
		fileButton = new JButton("File");
		historyButton = new JButton("History");
		cancelButton = new CancelButton("Cancel");
		msgPanel = new JPanel();
		viewPanel = new JPanel();
		helpLabel = new JLabel("", JLabel.RIGHT);
		
		helpLabel.setText("准备就绪...右击此处显示菜单");
		
		fileButton.setEnabled(false);
		sendButton.setEnabled(false);
		historyButton.setEnabled(false);
		cancelButton.setEnabled(false);
		
		inBox.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		inBox.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		outBox.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		outBox.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		sendButton.setPreferredSize(new Dimension(90,30));
		fileButton.setPreferredSize(new Dimension(90,30));
		historyButton.setPreferredSize(new Dimension(90,30));
		cancelButton.setPreferredSize(new Dimension(90,30));
		msgPanel.setMinimumSize(new Dimension(400, 35));
		viewPanel.setMinimumSize(new Dimension(400, 35));
		
		FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT, 10, 0);
		msgPanel.setLayout(flowLayout);
		msgPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		viewPanel.setLayout(flowLayout);
		viewPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		viewPanel.add(historyButton);
		viewPanel.add(fileButton);
		msgPanel.add(sendButton);
		msgPanel.add(cancelButton);
		msgPanel.add(helpLabel);
		
		
		/*
		 * 事件
		 */
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMsg();
			}
		});
		helpLabel.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) { }
			@Override
			public void mousePressed(MouseEvent e) { }
			@Override
			public void mouseExited(MouseEvent e) { }
			@Override
			public void mouseEntered(MouseEvent e) { }
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.isMetaDown())
					showOptionPopupMenu(e.getComponent(), e.getX(), e.getY());
			}
		});
		sendButton.setEnabled(true);

	}
	
	public void init(MainFrame _mainframe, Long _uid) {
		this.mainFrame = _mainframe;
		this.inBox.init(this, _uid);
		this.outBox.init(this, _uid);
		uid = _uid;
	}
	
	public void setUid(Long _uid) {
		this.uid = _uid;
		this.inBox.setUid(_uid);
		this.outBox.setUid(_uid);
	}
	
	private void showOptionPopupMenu (Component com, int _x, int _y) {
		JPopupMenu newMenu = new JPopupMenu();
		
		JMenuItem groupMenuItem = new JMenuItem("搜索群");
		JMenuItem userMenuItem = new JMenuItem("搜索好友");
		JMenuItem aboutMenuItem = new JMenuItem("关于java-qq");
		
		groupMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		userMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		aboutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		newMenu.add(userMenuItem);
		newMenu.add(groupMenuItem);
		newMenu.add(new JPopupMenu.Separator());
		newMenu.add(aboutMenuItem);
		
		newMenu.show(com, _x, _y);
	}
	
	public void sendMsg() {
		String msg = inBox.getText();
		// msgId 需要发送后才知道
		if (msg.length() > 0) {
			MsgInfo nMsg = new MsgInfo(msg);
			String resp = null;
			if (uid > 0) {
				nMsg.userId = uid.longValue();
				resp = CqhttpApi.sendPrivateMessage(nMsg.userId, msg, false);
			} else {
				nMsg.groupId = - uid.longValue();
				resp = CqhttpApi.sendGroupMessage(nMsg.groupId, msg, false);
			}
			if (resp == null) {
				System.out.println("发送失败");
				return;
			} else {
				JsonObject jsObject = JsonValue.readHjson(resp).asObject();
				if (jsObject.getLong("retcode", -1) == 0) {
					nMsg.messageId = jsObject.getLong("message_id", 0);
					System.out.println("cqhttp消息发送成功\n" + jsObject);
				} else {
					System.out.println("cqhttp消息发送失败\n" + jsObject);
					return;
				}
			}
			nMsg.superId = uid.longValue();
			// nMsg.messageId;
			
			
			outBox.newMsg(OutputBox.ALIGN.RIGHT, nMsg);
		}
		inBox.regainFouces();
	}
	
	public void receiveMsg(MsgInfo nMsg) {
		outBox.newMsg(OutputBox.ALIGN.LEFT, nMsg);
	}
	
	public InputBox getInBox() {
		return inBox;
	}
	public OutputBox getOutBox() {
		return outBox;
	}
	public JPanel getMsgPanel() {
		return msgPanel;
	}
	public JPanel getViewPanel() {
		return viewPanel;
	}
	
	class CancelButton extends JButton {
		CancelButton(String msg) {
			super(msg);
		}
	}
}
