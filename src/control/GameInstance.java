package control;

import control.event.MouseEventHandler;
import view.panel.MyContentPanel;
import view.scene.MainScene;
import view.scene.Scene;

import javax.swing.*;

/**
 * @program: PuzzleBobble
 * @description: 游戏实例
 * @author: Qiu
 * @create: 2019-04-15 16:04
 */
public class GameInstance extends JFrame {
	private static final int WINDOW_WIDTH = 500;
	private static final int WINDOW_HEIGHT = 700;

	private Control control;
	private Scene scene;
	private MouseEventHandler mouseEventHandler;
	private MyContentPanel contentPanel;
	private BgmMusic bgm;  // 背景音乐
	private boolean playSound;  // 是否播放背景音乐和按钮点击音效

	public GameInstance(){
		setTitle("PuzzleBobble");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);

		control = new Control(this);
		mouseEventHandler = new MouseEventHandler(control);
		scene = new MainScene(this);
		contentPanel = new MyContentPanel(this);
		bgm = new BgmMusic("resource//music//bgm.wav");
		playSound = true;

		setContentPane(contentPanel);

		setVisible(true);
	}

	public Scene getScene() { return scene; }

	public Control getControl() { return control; }

	public boolean isPlaySound() { return playSound; }

	public MouseEventHandler getMouseEventHandler() { return mouseEventHandler; }

	public MyContentPanel getContentPanel() { return contentPanel; }

	public int getWindowWidth(){ return WINDOW_WIDTH; }

	public int getWindowHeight() { return WINDOW_HEIGHT; }

	public void setScene(Scene scene) {
		this.scene = scene;
		contentPanel.removeAll();
		contentPanel.repaint();
		contentPanel.revalidate();
	}

	public void setPlaySound(boolean playSound) { this.playSound = playSound; }

	public void beginBgm(){
		bgm = new BgmMusic("resource//music//bgm.wav");
	}

	public void stopBgm(){
		// 停止播放背景音乐
		bgm.stopPlaying();
	}
}