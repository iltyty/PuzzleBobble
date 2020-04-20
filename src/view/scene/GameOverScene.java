package view.scene;


import control.GameInstance;
import control.event.MouseEventHandler;
import view.panel.MyContentPanel;

import javax.swing.*;

/**
 * @program: PuzzleBobble
 * @description: 游戏结束的场景类
 * @author: Qiu
 * @create: 2019-06-13 02:13
 */
public class GameOverScene extends Scene{
	private int state;        // 记录游戏失败还是胜利
	private JLabel lblState;  // 显示游戏失败还是胜利
	private JButton btnHome;  // 返回首页的按钮

	public GameOverScene(GameInstance gi, int state){
		// state记录游戏失败还是成功，0表示失败，1表示成功
		this.state = state;
		bg = new JLabel(new ImageIcon("resource//image//gameover_bg.png"));
		btnHome = new JButton(new ImageIcon("resource//image//btnHome.png"));
		if(state == 0){
			lblState = new JLabel(new ImageIcon("resource//image//lose.png"));
		}else if(state == 1){
			lblState = new JLabel(new ImageIcon("resource//image//win.png"));
		}

		MouseEventHandler mouseEventHandler = gi.getMouseEventHandler();
		btnHome.addMouseListener(mouseEventHandler);
	}

	@Override
	public void paintContent(GameInstance gi, MyContentPanel mcp) {
		super.paintContent(gi, mcp);
		mcp.setLayout(null);

		bg.setBounds(0, 0, gi.getWidth(), gi.getHeight());
		if(state == 0) {
			lblState.setBounds(110, 90, lblState.getIcon().getIconWidth(), lblState.getIcon().getIconHeight());
		}else if(state == 1){
			lblState.setBounds(20, 55, lblState.getIcon().getIconWidth(), lblState.getIcon().getIconHeight());
		}
		btnHome.setBounds(180, 500, btnHome.getIcon().getIconWidth(), btnHome.getIcon().getIconHeight());

		btnHome.setActionCommand("Home");
		btnHome.setBorderPainted(false);
		btnHome.setContentAreaFilled(false);

		mcp.add(lblState);
		mcp.add(btnHome);
		mcp.add(bg);
	}
}
