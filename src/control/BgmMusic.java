package control;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * @program: PuzzleBobble
 * @description: 背景音乐
 * @author: Qiu
 * @create: 2019-06-10 15:26
 */
public class BgmMusic extends Thread{
	private AudioInputStream ais;
	private Clip clip;

	public BgmMusic(String path){
		try {
			clip = AudioSystem.getClip();
			ais = AudioSystem.getAudioInputStream(new File(path));
			start();
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		super.run();
		try {
			clip.open(ais);
		} catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	public void stopPlaying(){
		clip.stop();
	}
}
