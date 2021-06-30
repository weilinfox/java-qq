package me.weilinfox.javaqq;

public class javaqq {
	private static MainFrame frame;
	
	public static void main(String[] args) {
		frame = new MainFrame();
		//frame.setMainWindow();

		frame.init();
		//frame.lanch();

		/*
		frame.getOutputBox().newMsg(OutputBox.ALIGN.RIGHT, "aabb", 0);
		frame.getOutputBox().newMsg(OutputBox.ALIGN.LEFT, "aab", 0);
		frame.getOutputBox().newMsg(OutputBox.ALIGN.LEFT, "aab", 0);
		frame.getOutputBox().newMsg(OutputBox.ALIGN.LEFT, "aab", 0);
		frame.getOutputBox().newMsg(OutputBox.ALIGN.LEFT, "aab", 0);
		frame.getOutputBox().newMsg(OutputBox.ALIGN.LEFT, "aab", 0);
		frame.getOutputBox().newMsg(OutputBox.ALIGN.LEFT, "aab", 0);
		frame.getOutputBox().newMsg(OutputBox.ALIGN.LEFT, "aab", 0);
		frame.getOutputBox().newMsg(OutputBox.ALIGN.LEFT, "aab", 0);
		*/
		/*
		LeftPanel.InitPanel();
		MessagePanel.InitPanel();
		frame.add(LeftPanel.getPanel(), BorderLayout.WEST);
		frame.add(MessagePanel.getPanel(), BorderLayout.EAST);
		frame.lanch();
		*/
	}
}
