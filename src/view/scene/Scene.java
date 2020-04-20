package view.scene;

import control.GameInstance;
import javazoom.jl.decoder.JavaLayerException;
import sun.audio.*;
import view.panel.MyContentPanel;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import javazoom.jl.player.Player;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @program: PuzzleBobble
 * @description: 游戏的场景
 * @author: Qiu
 * @create: 2019-04-15 13:58
 */
public class Scene {
	protected JLabel bg;  // 背景图片

	public Scene(){}

	public void paintContent(GameInstance gi, MyContentPanel myContentPanel){}
}
