package view.scene;

import control.GameInstance;
import control.event.MouseEventHandler;
import sun.audio.AudioPlayer;
import view.panel.MyContentPanel;

import javax.swing.*;

/**
 * @program: PuzzleBobble
 * @description: 游戏选择难度的主界面
 * @author: Qiu
 * @create: 2019-04-15 14:08
 */
public class MainScene extends Scene{
	private JLabel lblTitle;  // 标题label
	private JButton btnLow;  // 低难度
	private JButton btnMiddle;  // 中难度
	private JButton btnHigh;  // 高难度
	private JButton btnImport;  // 导入配置文件按钮
	private JButton btnExit;  // 退出按钮

	public MainScene(GameInstance gi) {
		bg = new JLabel(new ImageIcon("resource//image//mainBG.png"));
		lblTitle = new JLabel(new ImageIcon("resource//image//title.png"));

		// 按钮的显示文本
		btnLow = new JButton(new ImageIcon("resource//image//btnLow.png"));
		btnMiddle = new JButton(new ImageIcon("resource//image//btnMiddle.png"));
		btnHigh = new JButton(new ImageIcon("resource//image//btnHigh.png"));
		btnImport = new JButton(new ImageIcon("resource//image//btnImport.png"));
		btnExit = new JButton(new ImageIcon("resource//image//btnExit.png"));

		// 按钮的ID，便于事件处理
		btnLow.setActionCommand("Low");
		btnMiddle.setActionCommand("Middle");
		btnHigh.setActionCommand("High");
		btnImport.setActionCommand("Import");
		btnExit.setActionCommand("Exit");

		btnLow.setBorderPainted(false);
		btnLow.setContentAreaFilled(false);
		btnMiddle.setBorderPainted(false);
		btnMiddle.setContentAreaFilled(false);
		btnHigh.setBorderPainted(false);
		btnHigh.setContentAreaFilled(false);
		btnImport.setBorderPainted(false);
		btnImport.setContentAreaFilled(false);
		btnExit.setBorderPainted(false);
		btnExit.setContentAreaFilled(false);

		MouseEventHandler mouseEventHandler = gi.getMouseEventHandler();
		btnLow.addMouseListener(mouseEventHandler);
		btnMiddle.addMouseListener(mouseEventHandler);
		btnHigh.addMouseListener(mouseEventHandler);
		btnImport.addMouseListener(mouseEventHandler);
		btnExit.addMouseListener(mouseEventHandler);
	}

	/**
	 * @description: 绘制游戏主界面
	 * @params: [gi, myContentPanel]
	 * @return: void
	 * @date: 2019/4/15
	 */
	@Override
	public void paintContent(GameInstance gi, MyContentPanel mcp) {
		super.paintContent(gi, mcp);
		int x = 170;
		int y = 170;
		mcp.setLayout(null);

		lblTitle.setBounds(25, 40, lblTitle.getIcon().getIconWidth(), lblTitle.getIcon().getIconHeight());
		bg.setBounds(0, 0, gi.getWidth(), gi.getHeight());
		btnLow.setBounds(x, y, btnLow.getIcon().getIconWidth(), btnLow.getIcon().getIconHeight());
		btnMiddle.setBounds(x, y + 100, btnMiddle.getIcon().getIconWidth(), btnMiddle.getIcon().getIconHeight());
		btnHigh.setBounds(x, y + 200, btnHigh.getIcon().getIconWidth(), btnHigh.getIcon().getIconHeight());
		btnImport.setBounds(x, y + 300, btnImport.getIcon().getIconWidth(), btnImport.getIcon().getIconHeight());
		btnExit.setBounds(x, y + 400, btnExit.getIcon().getIconWidth(), btnExit.getIcon().getIconHeight());

		mcp.add(lblTitle);
		mcp.add(btnLow);
		mcp.add(btnMiddle);
		mcp.add(btnHigh);
		mcp.add(btnImport);
		mcp.add(btnExit);
		mcp.add(bg);
	}
}