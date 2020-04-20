package control.event;

import control.Control;
import control.GameInstance;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @program: PuzzleBobble
 * @description: 处理游戏的鼠标点击事件
 * @author: Qiu
 * @create: 2019-04-15 22:25
 */
public class MouseEventHandler extends MouseAdapter {
	private Control control;
	public MouseEventHandler(Control ct){
		control = ct;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			// 仅处理按下鼠标左键的事件
			Object source = e.getSource();
			if(source instanceof JButton){
				// 点击的是JButton
				if(control != null) {
					control.buttonPressed((JButton)source);
				}
			} else if(source instanceof JLabel){
				// 发射泡泡
				if(control != null){
					if(e.getY() < 550) {
						// 鼠标点击550像素以下的地方不发射泡泡
						control.mousePressed((JLabel) source, e);
					}
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		Object source = e.getSource();
		if(source instanceof JButton){
			control.mouseEntered((JButton) source);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
		Object source = e.getSource();
		if(source instanceof JButton){
			control.mouseExited((JButton) source);
		}
	}
}
