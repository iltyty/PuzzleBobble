package control;

import view.scene.MainScene;
import view.scene.PlayScene;
import view.scene.Scene;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @program: PuzzleBobble
 * @description: 负责游戏的事件响应
 * @author: Qiu
 * @create: 2019-04-15 13:49
 */
public class Control {
	private GameInstance gameInstance;
	public Control(GameInstance gi){
		gameInstance = gi;
	}

	/**
	 * @description: 处理游戏中按钮被点击的事件
	 * @params: [button]
	 * @return: void
	 * @date: 2019/4/15
	 */
	public void buttonPressed(JButton button){
		// 被点击按钮的action command，用于标识不同的文本
		String name = button.getActionCommand();
		if(gameInstance.isPlaySound() && !name.equals("Clock") && !name.equals("Laser") && !name.equals("Pause")) {
			playSoundEffect("resource//music//click.wav");
		}
		switch (name){
			case "Low":
				// 进入低难度关卡的按钮
				gameInstance.setScene(new PlayScene(gameInstance, 3));
				break;
			case "Middle":
				// 进入中难度关卡的按钮
				gameInstance.setScene(new PlayScene(gameInstance, 4));
				break;
			case "High":
				// 进入高难度关卡的按钮
				gameInstance.setScene(new PlayScene(gameInstance, 5));
				break;
			case "Import":
				// 导入配置文件
				startByImporting();
				break;
			case "Music":
				// 音乐开关按钮
				if(gameInstance.isPlaySound()){
					// 当前有声音，设置为静音
					button.setIcon(new ImageIcon("resource//image//btnMusicClicked.png"));
					gameInstance.setPlaySound(false);
					gameInstance.stopBgm();
				} else{
					// 当前为静音，设置为有声音
					button.setIcon(new ImageIcon("resource//image//btnMusic.png"));
					gameInstance.setPlaySound(true);
					gameInstance.beginBgm();
				}
				break;
			case "Exit":
				// 退出游戏按钮
				System.exit(0);
				break;
			case "Pause":
				// 暂停游戏
				PlayScene scene = (PlayScene) gameInstance.getScene();
				if(scene.isPlaying()){
					playSoundEffect("resource//music//pause.wav");
					scene.setPlaying(false);
					gameInstance.setPlaySound(false);
					gameInstance.stopBgm();
					button.setIcon(new ImageIcon("resource//image//btnResume.png"));
				}else{
					playSoundEffect("resource//music//pause.wav");
					scene.setPlaying(true);
					gameInstance.setPlaySound(true);
					gameInstance.beginBgm();
					button.setIcon(new ImageIcon("resource//image//btnPause.png"));
				}
				break;
			case "Back":
				PlayScene scene1 = (PlayScene) gameInstance.getScene();
				scene1.clear();
				gameInstance.setScene(new MainScene(gameInstance));
				break;
			case "Bomb":
				PlayScene scene2 = (PlayScene) gameInstance.getScene();
				scene2.setImageForCurrentBobble(6);
				break;
			case "Rainbow":
				PlayScene scene3 = (PlayScene) gameInstance.getScene();
				scene3.setImageForCurrentBobble(7);
				break;
			case "Clock":
				PlayScene scene4 = (PlayScene) gameInstance.getScene();
				int newTimeLeft = scene4.getTimeLeft() + 10;
				if(newTimeLeft > 60){
					newTimeLeft = 60;
				}
				if(gameInstance.isPlaySound())
					playSoundEffect("resource//music//timeIncrease.wav");
				scene4.setTimeLeft(newTimeLeft);
				break;
			case "Laser":
				PlayScene scene5 = (PlayScene) gameInstance.getScene();
				if(gameInstance.isPlaySound())
					playSoundEffect("resource//music//laser.wav");
				scene5.emitLaser();
				break;
			case "Home":
				gameInstance.setScene(new MainScene(gameInstance));
				gameInstance.beginBgm();
				break;
		}
	}

	public void mousePressed(JLabel label, MouseEvent e){
		// 点击了鼠标
		if(gameInstance.isPlaySound()) {
			playSoundEffect("resource//music//bobbleEmit.wav");
		}
		String name = label.getText();
		if ("bg".equals(name)) {
			// 发射泡泡
			PlayScene scene = (PlayScene) gameInstance.getScene();
			if (scene.isPlaying()) {
				scene.emitBobble(gameInstance.getContentPanel(), e.getX(), e.getY());
			}
		}
	}

	public void mouseMoved(int x, int y){
		// 鼠标移动，更改提示虚线的位置
		if(y > 570)
			return;
		Graphics2D g = (Graphics2D)gameInstance.getContentPanel().getGraphics();
		Stroke dash = new BasicStroke(2.5f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,
				3.5f,new float[]{15,10,},0f);
		g.setStroke(dash);
		g.setColor(Color.CYAN);
		g.drawLine(250, 570, x, y);
		// gameInstance.getContentPanel().repaint();
	}

	public void mouseEntered(JButton btn){
		// 鼠标悬停，更改图片
		String name = btn.getActionCommand();
		if(!name.equals("Music") && !name.equals("Pause")) {
			String btnName = btn.getActionCommand();
			btn.setIcon(new ImageIcon("resource//image//btn" + btnName + "Hover.png"));
		}
	}


	public void mouseExited(JButton btn){
		// 鼠标离开，更改图片
		String name = btn.getActionCommand();
		if(!name.equals("Music") && !name.equals("Pause")) {
			String btnName = btn.getActionCommand();
			btn.setIcon(new ImageIcon("resource//image//btn" + btnName + ".png"));
		}
	}

	public static void playSoundEffect(String fileName){
		try {
			// 播放按钮点击音效
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new File(fileName)));
			clip.start();
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}

	public void startByImporting(){
		// 导入配置文件开始游戏
		JFileChooser jfc = new JFileChooser();
		jfc.setCurrentDirectory(new File("."));
		jfc.setDialogTitle("Open file");
		jfc.showOpenDialog(gameInstance);
		jfc.setVisible(true);

		File file = jfc.getSelectedFile();
		if(file != null){
			gameInstance.setScene(new PlayScene(gameInstance, file));
		}
	}
}
