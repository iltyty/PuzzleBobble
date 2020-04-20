package view.panel;

import control.GameInstance;
import item.Bobble;
import view.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * @program: PuzzleBobble
 * @description: 游戏自定义Panel类
 * @author: Qiu
 * @create: 2019-04-14 17:57
 */
public class MyContentPanel extends JPanel {
	private Scene scene;  // 当前游戏场景
	private GameInstance gameInstance;  // 游戏实例

	public MyContentPanel(GameInstance gi){
		gameInstance = gi;
		scene = gameInstance.getScene();
		setOpaque(false);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		scene = gameInstance.getScene();
		scene.paintContent(gameInstance, this);
	}

}
