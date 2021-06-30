package me.weilinfox.javaqq;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.undo.UndoManager;

public class InputBox extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private EditArea editArea;
	private JTextArea inBox;
	//private JPanel inPanel;
	private UndoManager undoManager;
	private Long uid = null;
	public InputBox() {
		super();
		undoManager = new UndoManager();
		//inPanel = new JPanel();
		inBox = new JTextArea();
		inBox.setEditable(true);
		inBox.setLineWrap(true);
		inBox.setAutoscrolls(true);
		inBox.setRows(6);
		//inBox.setFont(new Font("", Font.PLAIN, 20));
		//inBox.setMinimumSize(new Dimension(200, 200));
		inBox.getDocument().addUndoableEditListener(undoManager);
		//inBox.setWrapStyleWord(true);
		//inPanel.setMinimumSize(new Dimension(400, 200));
		//this.setMinimumSize(new Dimension(300, 200));
		//inPanel.setPreferredSize(new Dimension(400, 200));
		///inPanel.setLayout(new GridLayout(1, 1));
		//inPanel.add(inBox);
		this.setViewportView(inBox);
		
		inBox.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) { }

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {
					if (undoManager.canUndo()) {
						undoManager.undo();
					}
				}
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {
					if (undoManager.canRedo()) {
						undoManager.redo();
					}
				}
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
					editArea.sendMsg();
					// 重新获得焦点
					regainFouces();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) { }
		});
	}
	
	public void regainFouces() {
		inBox.requestFocusInWindow();
	}

	public String getText() {
		String msg = this.inBox.getText();
		this.inBox.setText("");
		undoManager.discardAllEdits();
		return msg;
	}
	public void init(EditArea _editArea, Long _uid) {
		this.editArea = _editArea;
		this.uid = _uid;
	}
	
	public void setUid(Long _uid) {
		this.uid = _uid;
	}
}
