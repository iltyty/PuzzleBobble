package main;

import control.GameInstance;


/**
 * @program: PuzzleBobble
 * @description: 游戏入口
 * @author: Qiu
 * @create: 2019-04-14 17:51
 */
public class MainWindow {
	private static final int WINDOW_WIDTH = 500;
	private static final int WINDOW_HEIGHT = 600;

	private GameInstance gi;

	public MainWindow(){
		gi = new GameInstance();
	}

	public static void main(String[] args) { new MainWindow(); }
}
