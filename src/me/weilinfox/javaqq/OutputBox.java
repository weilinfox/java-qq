package me.weilinfox.javaqq;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** 
 * <p> 消息输出框类 </p>
 * @author weilinfox
 *
 */
public class OutputBox extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private EditArea editArea;
	private JPanel listPanel;
	private Box listBox;
	private Map<Long, Vector<JPanel>> listMsg;
	private int width;
	private Long uid;
	public static enum ALIGN {
		LEFT, RIGHT;
	}

	public OutputBox() {
		super();

		listPanel = new JPanel();
		listBox = Box.createVerticalBox();
		listBox.setAlignmentX(LEFT_ALIGNMENT);
		listPanel.add(listBox);
		listMsg = new HashMap<Long, Vector<JPanel>>();
		width = 560;
		uid = null;
		//listBox.setBackground(Color.blue);
		listBox.removeAll();
		this.setViewportView(listPanel);
		listPanel.setBackground(Color.white);
		//this.setLayout(new ScrollPaneLayout());
		//this.setAutoscrolls(true);
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

	void refresh() {
		int wid = this.getViewport().getSize().width;
		if (wid > 200) {
			Vector<JPanel> vec;
			Dimension tmpDimension;
			width = wid - 20;
			// tmpDimension = new Dimension(wid-20, preDimension.height);
			if (listBox == null || uid == null) return;
			vec = listMsg.get(this.uid);
			if (vec == null) return;
			listBox.removeAll();
			for (int i = 0; i < vec.size(); i++) {
				tmpDimension = vec.get(i).getPreferredSize();
				tmpDimension = new Dimension(width, tmpDimension.height);
				vec.get(i).setPreferredSize(tmpDimension);
				listBox.add(Box.createVerticalStrut(50));
				listBox.add(vec.get(i));
				//((msgPanel)vec.get(i)).print();
				//System.out.println(vec.get(i).getVisibleRect());
			}
			listBox.revalidate();
		}
	}
	
	void delPanel(JPanel panel) {
		listMsg.get(uid).remove(panel);
	}

	public void newMsg(ALIGN align, MsgInfo nMsg) {
		msgPanel newPanel = new msgPanel(this, align, nMsg);

		if (listMsg.keySet().contains(nMsg.superId))
			listMsg.get(nMsg.superId).add(newPanel);
		else {
			listMsg.put(nMsg.superId, new Vector<JPanel>());
			listMsg.get(nMsg.superId).add(newPanel);
		}
		
		if (nMsg.superId == uid.longValue()) {
			listBox.add(Box.createVerticalStrut(50));
			listBox.add(newPanel);
			listBox.revalidate();
		}
		
		//System.out.println(listMsg.keySet());
		//System.out.println(listMsg.values());

		//newPanel.print();
		//System.out.println(listBox.getSize());

		//reSize();

		if (this.getVerticalScrollBar().isShowing()) {
			scroll();
		}
		
		//newPanel.print();
		//this.getViewport().doLayout();
	}
	
	public void init(EditArea _editArea, Long _uid) {
		this.uid = _uid;
		this.editArea = _editArea;
		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) { refresh(); }
			@Override
			public void componentShown(ComponentEvent e) { }
			@Override
			public void componentMoved(ComponentEvent e) { }
			@Override
			public void componentHidden(ComponentEvent e) { }
		});
	}
	
	public void setUid (Long _uid) {
		this.uid = _uid;
		this.refresh();
	}
	
	/**
	 * <p>Message panel in OutputBox</p>
	 * <p>print messages and images</p>
	 * <p>show pop up menu when right click</p>
	 * <p>support copy, recall</p>
	 * @author weilinfox
	 *
	 */
	class msgPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private OutputBox superBox;
		private JPanel newPanel;
		//private JButton newButton;
		private MouseListener newTextRclick;
		private MouseListener newImageRclick;
		private String rawMsg;
		private MsgInfo msgInfo;

		public msgPanel (OutputBox listBox, ALIGN align, MsgInfo nMsg) {
			super();
			// 初始化对象
			rawMsg = nMsg.rawMessage;
			newPanel = new JPanel();
			//newButton = new JButton("撤回");
			msgInfo = nMsg;

			superBox = listBox;
			// 右键菜单
			newTextRclick = new MouseListener() {
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
					if (e.isMetaDown()) {
						showTextPopupMenu(e.getComponent(),
											e.getX(), e.getY());
					}
				}
			};
			// 图片右键 复制图片本地/远程地址
			newImageRclick = new MouseListener() {
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
					if (e.isMetaDown()) {
						showImagePopupMenu(e.getComponent(),
											e.getX(), e.getY());
					}
				}
			};
			this.setBackground(Color.white);
			this.addMouseListener(newTextRclick);
			
			// 文本域大小
			JTextArea newText = new JTextArea(rawMsg);
			int lineHeight, lineNum, index, tmp;
			newText.setEditable(false);
			newText.setTabSize(1);
			if (rawMsg.length() > 30) {
				newText.setColumns(30);
				newText.setLineWrap(true);
			}
			
			Font font = newText.getFont();
			FontMetrics fontMetrics = newText.getFontMetrics(font);
			lineHeight = fontMetrics.getHeight();
			//System.out.println(lineHeight);
			index = 0; lineNum = 1;
			while ((index = rawMsg.indexOf("\n", index)) >= 0) {
				lineNum ++;
				index ++;
			}
			//System.out.println(lineNum);
			for (String s : rawMsg.split("\n")) {
				if (s.length() == 0) continue;
				index = tmp = 0;
				while ((index = s.indexOf("\t", index)) >= 0) {
					tmp ++;
					index ++;
				}
				lineNum += (s.getBytes().length+tmp) / 59.0;
			}
			//System.out.println(lineNum);

			// 添加文本域
			FlowLayout flowLayout;
			BorderLayout bdrLayout;
			if (align == ALIGN.LEFT) flowLayout =
										new FlowLayout(FlowLayout.LEFT);
			else flowLayout = new FlowLayout(FlowLayout.RIGHT);
			bdrLayout = new BorderLayout();
			newPanel.setLayout(bdrLayout);
			//newPanel.add(newButton, BorderLayout.PAGE_END);
			newPanel.add(newText, BorderLayout.CENTER);
			this.setLayout(flowLayout);
			this.setPreferredSize(
						new Dimension(width, lineNum * lineHeight + 5));
			newText.addMouseListener(newTextRclick);
			newPanel.addMouseListener(newTextRclick);
			this.add(newPanel);
		}
		
		private void showTextPopupMenu (Component com, int _x, int _y) {
			JPopupMenu newMenu = new JPopupMenu();
			
			JMenuItem copyMenuItem = new JMenuItem("复制");
			JMenuItem recallMenuItem = new JMenuItem("撤回");
			JMenuItem replyMenuItem = new JMenuItem("回复");
			
			copyMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Clipboard clipboard =
							Toolkit.getDefaultToolkit().getSystemClipboard();
					Transferable trans = new StringSelection(getCopyMsg());
					clipboard.setContents(trans, null);
				}
			});
			recallMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					recallMsg(msgInfo.messageId);
				}
			});
			replyMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			
			newMenu.add(replyMenuItem);
			newMenu.add(recallMenuItem);
			newMenu.add(new JPopupMenu.Separator());
			newMenu.add(copyMenuItem);
			
			newMenu.show(com, _x, _y);
		}
		
		private void showImagePopupMenu (Component com, int _x, int _y) {
			JPopupMenu newMenu = new JPopupMenu();
			
			newMenu.show(com, _x, _y);
		}
		
		private String getCopyMsg() {
			return this.rawMsg;
		}
		
		private int recallMsg(Long msgId) {
			boolean flag = true;
			// 撤回逻辑 失败 flag = false;
			if (flag) {
				this.superBox.listBox.remove(this);
				// this.superBox.listBox.revalidate();
				this.superBox.delPanel(this);
				this.superBox.refresh();
			}
			return 0;
		}

		/*
		public void print() {
			System.out.println(newText.getSize());
			System.out.println(newButton.getSize());
			System.out.println(this.getSize());
		}  */
		
		
	}
}




