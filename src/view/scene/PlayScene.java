package view.scene;

import control.Control;
import control.GameInstance;
import control.event.MouseEventHandler;
import item.Bobble;
import view.panel.MyContentPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Timer;

/**
 * @program: PuzzleBobble
 * @description: 游戏界面
 * @author: Qiu
 * @create: 2019-04-15 14:08
 */
public class PlayScene extends Scene{
	private static int WINDOW_WIDTH;  // 窗口宽度
	private static int WINDOW_HEIGHT;  // 窗口高度
	private static int IMAGE_SIZE;  // 泡泡图片宽度
	private int BOBBLE_MAX_COL = 10;  // 每行的泡泡个数
	private int BOBBLE_MAX_ROW = 9;   // 最大的泡泡行数
	private static int CENTER_X;  // 发射泡泡时的中心位置x坐标
	private static int CENTER_Y;  // 发射泡泡时的中心位置y坐标
	private static int GAME_TIME = 60; // 一局游戏的限制时间
	private int difficulty;  // 难度
	private LinkedList<Bobble> bobbles;  // 当亲场景中的所有泡泡
	private Bobble currentBobble;  // 当前泡泡
	private Bobble nextBobble;     // 下一个泡泡
	private int timeLeft;  // 剩余时间
	private int[][] type;  // 此二元数组记录每个单元格当前泡泡的类型，0表示没有泡泡
	private boolean isPlaying;  // 游戏是否暂停

	private JButton btnPause;  // 暂停按钮
	private JButton btnBack;   // 返回主菜单按钮
	private JButton btnMusic;  // 静音按钮
	private JButton btnBomb;  // 炸弹
	private JButton btnRainbow; // 彩虹
	private JButton btnLaser;  // 激光
	private JButton btnClock;  // 沙漏
	private JLabel topBar;  // 顶部显示栏
	private JLabel lblTimeLeft; // 显示剩余时间
	private JLabel lblTimeFrame; // 时间框
	private JLabel lblGridFull;  // 黄色（满）时间条
	private JLabel lblGridEmpty; // 红色（空）时间条
	private JLabel lblLifeLine;  // 生命线
	private JLabel lblLifeLineVerti; // 右边的竖直边界线
	private JLabel bottomBar;  // 底部显示栏
	private JLabel ejector;  // 发射器
	private BufferedImage ejectorImage;  // 包含所有帧的发射器图片

	private Timer timerUpdate;  // 用于更新发射的泡泡的位置的定时器
	private Timer timerTime;    // 用于计时的定时器

	public PlayScene(GameInstance gi){
		WINDOW_WIDTH = gi.getWindowWidth();
		WINDOW_HEIGHT = gi.getWindowHeight();
		IMAGE_SIZE = 45;
		CENTER_X = (WINDOW_WIDTH - IMAGE_SIZE) / 2;
		CENTER_Y = 570;
		bobbles = new LinkedList<>();
		isPlaying = true;
		timerUpdate = new Timer();
		timerTime = new Timer();

		lblTimeLeft = new JLabel(String.valueOf(timeLeft));
		lblTimeFrame = new JLabel(new ImageIcon("resource//image//lblGrid.png"));
		lblGridFull = new JLabel(new ImageIcon("resource//image//gridFull.png"));
		lblGridEmpty = new JLabel(new ImageIcon("resource//image//gridEmpty.png"));
		lblLifeLine = new JLabel(new ImageIcon("resource//image//lblLifeLine.png"));
		lblLifeLineVerti = new JLabel(new ImageIcon("resource//image//lblLifeLineVerti.png"));
		btnBomb = new JButton(new ImageIcon("resource//image//btnBomb.png"));
		btnRainbow = new JButton(new ImageIcon("resource//image//btnRainbow.png"));
		btnClock = new JButton(new ImageIcon("resource//image//btnClock.png"));
		btnLaser = new JButton(new ImageIcon("resource//image//btnLaser.png"));
		bg = new JLabel(new ImageIcon("resource//image//playBG.png"));
		topBar = new JLabel(new ImageIcon("resource//image//top.png"));
		bottomBar = new JLabel(new ImageIcon("resource//image//bottom.png"));
		try {
			ejectorImage = ImageIO.read(new File("resource//image//ejector.png"));
			ejector = new JLabel();
			ejector.setIcon(new ImageIcon(ejectorImage.getSubimage(0, 0, ejectorImage.getWidth(), 130)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		btnPause = new JButton(new ImageIcon("resource//image//btnPause.png"));
		btnBack = new JButton(new ImageIcon("resource//image//btnBack.png"));
		if(gi.isPlaySound()) {
			btnMusic = new JButton(new ImageIcon("resource//image//btnMusic.png"));
		}else{
			btnMusic = new JButton(new ImageIcon("resource//image//btnMusicClicked.png"));
		}

		timerTime.schedule(new TimerTask() {
			@Override
			public void run() {
				if(isPlaying) {
					timeLeft--;
					if(timeLeft <= 5){
						Control.playSoundEffect("resource//music//tick.wav");
					}
					lblTimeLeft.setText(String.valueOf(timeLeft));
					if (timeLeft == 0) {
						timerTime.cancel();
					}
				}
			}
		}, 50, 1000);
		timerUpdate.schedule(new TimerTask() {
			@Override
			public void run() {
				if(isPlaying) {
					int x = MouseInfo.getPointerInfo().getLocation().x - gi.getX();
					int y = MouseInfo.getPointerInfo().getLocation().y - gi.getY();
					Graphics2D g = (Graphics2D) gi.getContentPanel().getGraphics();
					Stroke dash = new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
							3.5f, new float[]{15, 10,}, 0f);
					g.setStroke(dash);
					g.setColor(Color.CYAN);
					g.drawLine(WINDOW_WIDTH / 2, CENTER_Y, x, y);
					setImageForEjector(x, y, ejector);
					for (Bobble bobble : bobbles) {
						if (bobble.getVx() != 0 || bobble.getVy() != 0) {
							updateBobble(bobble);
						}
					}
					for (Bobble bobble : bobbles) {
						if (bobble.isToBeDown()) {
							bobble.setVy(bobble.getV());
							bobble.setVx(0);
						}
					}
					if(bobbles.size() == 0){
						Control.playSoundEffect("resource//music//win.wav");
						win(gi);
					}
					for(Bobble bobble : bobbles){
						if(bobble.getRow() >= BOBBLE_MAX_ROW){
							Control.playSoundEffect("resource//music//lose.wav");
							lose(gi);
						}
					}
					if(timeLeft == 0){
						if(bobbles.size() <= 5){
							Control.playSoundEffect("resource//music//win.wav");
							win(gi);
						}else{
							Control.playSoundEffect("resource//music//timeover.wav");
							lose(gi);
						}
					}
					gi.getContentPanel().repaint();
				}
			}
		}, 50, 20);

		lblTimeLeft.setFont(new Font("Consolas", Font.BOLD, 30));
		lblTimeLeft.setForeground(Color.WHITE);
		btnPause.setActionCommand("Pause");
		btnPause.setBorderPainted(false);
		btnPause.setContentAreaFilled(false);
		btnBack.setActionCommand("Back");
		btnBack.setBorderPainted(false);
		btnBack.setContentAreaFilled(false);
		btnMusic.setActionCommand("Music");
		btnMusic.setBorderPainted(false);
		btnMusic.setContentAreaFilled(false);
		btnBomb.setActionCommand("Bomb");
		btnBomb.setBorderPainted(false);
		btnBomb.setContentAreaFilled(false);
		btnRainbow.setActionCommand("Rainbow");
		btnRainbow.setBorderPainted(false);
		btnRainbow.setContentAreaFilled(false);
		btnClock.setActionCommand("Clock");
		btnClock.setBorderPainted(false);
		btnClock.setContentAreaFilled(false);
		btnLaser.setActionCommand("Laser");
		btnLaser.setBorderPainted(false);
		btnLaser.setContentAreaFilled(false);
		bg.setText("bg");

		MouseEventHandler mouseEventHandler = gi.getMouseEventHandler();
		bg.addMouseListener(mouseEventHandler);
		bg.addMouseMotionListener(mouseEventHandler);
		btnMusic.addMouseListener(mouseEventHandler);
		btnPause.addMouseListener(mouseEventHandler);
		btnBack.addMouseListener(mouseEventHandler);
		btnBomb.addMouseListener(mouseEventHandler);
		btnRainbow.addMouseListener(mouseEventHandler);
		btnClock.addMouseListener(mouseEventHandler);
		btnLaser.addMouseListener(mouseEventHandler);
	}

	public PlayScene(GameInstance gi, int hardness){
		this(gi);
		timeLeft = GAME_TIME;
		type = new int[BOBBLE_MAX_ROW][BOBBLE_MAX_COL];  // 默认初始化为0
		difficulty = hardness;
		initBobblesRandomly();
	}

	public PlayScene(GameInstance gi, File file){
		this(gi);
		Scanner scanner;
		try {
			scanner = new Scanner(new FileReader(file));
			char[][] types;
			int currentRow = 0;
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				int beginIndex = line.indexOf(':');
				if(line.length() == 0){
					continue;
				}
				if(line.charAt(line.length()-1) == 's'){
					GAME_TIME = Integer.valueOf(line.substring(beginIndex+1, line.length()-1).trim());
				}else if(line.substring(0, 4).equals("Row:")){
					BOBBLE_MAX_ROW = Integer.valueOf(line.substring(beginIndex+1).trim());
				}else if(line.substring(0, 7).equals("Column:")){
					BOBBLE_MAX_COL = Integer.valueOf(line.substring(beginIndex+1).trim());
				}else if(line.substring(0, 6).equals("Color:")){
					difficulty = Integer.valueOf(line.substring(beginIndex+1).trim());
					break;
				}
			}
			types = new char[BOBBLE_MAX_ROW][BOBBLE_MAX_COL];
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				if(line.length() != 0) {
					for(int i = 0; i < BOBBLE_MAX_COL; i++) {
						types[currentRow][i] = line.charAt(i);
					}
					currentRow++;
				}
			}
			timeLeft = GAME_TIME;
			type = new int[BOBBLE_MAX_ROW][BOBBLE_MAX_COL];
			initBobblesFixed(types);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paintContent(GameInstance gi, MyContentPanel mcp) {
		mcp.setLayout(null);
		mcp.removeAll();

		lblTimeLeft.setBounds(200, 34, 50, 50);
		lblTimeFrame.setBounds(58, 25, lblTimeFrame.getIcon().getIconWidth(), lblTimeFrame.getIcon().getIconHeight());
		lblGridFull.setBounds(111, 44, (int) (timeLeft / 60.0 * lblGridFull.getIcon().getIconWidth()), lblGridFull.getIcon().getIconHeight());
		lblGridEmpty.setBounds(111, 44, lblGridEmpty.getIcon().getIconWidth(), lblGridEmpty.getIcon().getIconHeight());
		lblLifeLine.setBounds(0, (BOBBLE_MAX_ROW + 1)* IMAGE_SIZE, lblLifeLine.getIcon().getIconWidth(), lblLifeLine.getIcon().getIconHeight());
		lblLifeLineVerti.setBounds(BOBBLE_MAX_COL * IMAGE_SIZE + 30,  0, lblLifeLineVerti.getIcon().getIconWidth(), lblLifeLineVerti.getIcon().getIconHeight());
		ejector.setBounds(165, 500, 176, 130);
		bottomBar.setBounds(0, WINDOW_HEIGHT - bottomBar.getIcon().getIconHeight(), bottomBar.getIcon().getIconWidth(), bottomBar.getIcon().getIconHeight());
		bg.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		topBar.setBounds(0, 0, topBar.getIcon().getIconWidth(), topBar.getIcon().getIconHeight());
		btnPause.setBounds(430, 30, btnPause.getIcon().getIconWidth(), btnPause.getIcon().getIconHeight());
		btnBack.setBounds(360, 30, btnBack.getIcon().getIconWidth(), btnBack.getIcon().getIconHeight());
		btnMusic.setBounds(5, 30, btnMusic.getIcon().getIconWidth(), btnMusic.getIcon().getIconHeight());
		btnBomb.setBounds(CENTER_X - 200, 557, btnBomb.getIcon().getIconWidth(), btnBomb.getIcon().getIconHeight());
		btnRainbow.setBounds(CENTER_X - 105, 557, btnRainbow.getIcon().getIconWidth(), btnRainbow.getIcon().getIconHeight());
		btnClock.setBounds(CENTER_X + 90, 557, btnClock.getIcon().getIconWidth(), btnClock.getIcon().getIconHeight());
		btnLaser.setBounds(CENTER_X + 185, 557, btnLaser.getIcon().getIconWidth(), btnLaser.getIcon().getIconHeight());
		currentBobble.setBounds(currentBobble.getX(), currentBobble.getY(), IMAGE_SIZE, IMAGE_SIZE);
		nextBobble.setBounds(nextBobble.getX(), nextBobble.getY(), IMAGE_SIZE, IMAGE_SIZE);
		mcp.add(currentBobble);
		mcp.add(nextBobble);
		// 删除相邻的同颜色的泡泡
		bobbles.removeIf(Bobble::isToBeRemoved);
		for(Bobble bobble : bobbles){
			bobble.setBounds(bobble.getX(), bobble.getY(), IMAGE_SIZE, IMAGE_SIZE);
			mcp.add(bobble);
		}
		mcp.add(lblTimeLeft);
		mcp.add(lblGridFull);
		mcp.add(lblGridEmpty);
		mcp.add(lblTimeFrame);
		mcp.add(lblLifeLine);
		mcp.add(lblLifeLineVerti);
		mcp.add(btnMusic);
		mcp.add(btnBack);
		mcp.add(btnPause);
		mcp.add(btnBomb);
		mcp.add(btnRainbow);
		mcp.add(btnClock);
		mcp.add(btnLaser);
		mcp.add(topBar);
		mcp.add(ejector);
		mcp.add(bottomBar);
		mcp.add(bg);
	}

	public void initBobblesRandomly(){
		// 根据不同的难度初始化不同排数、种数的泡泡墙
		Random random = new Random();
		int ty;  // 泡泡的种类
		for(int i = 0; i < BOBBLE_MAX_COL * difficulty; i++){
			ty = random.nextInt(difficulty) + 1;
			int row = i / BOBBLE_MAX_COL + 1;
			int col = i % BOBBLE_MAX_COL + 1;
			Bobble bobble = new Bobble(ty, (row % 2 == 0 ? 30 : 5) + IMAGE_SIZE * (col - 1), IMAGE_SIZE * row + 45);
			bobble.setRow(row);
			bobble.setCol(col);
			type[row-1][col-1] = ty;
			bobbles.add(bobble);
		}
		ty = random.nextInt(difficulty) + 1;
		currentBobble = new Bobble(ty, CENTER_X, CENTER_Y);
		ty = random.nextInt(difficulty) + 1;
		nextBobble = new Bobble(ty, CENTER_X, CENTER_Y + 50);
	}

	public void initBobblesFixed(char[][] types){
		// 根据types数组初始化泡泡墙
		Random random = new Random();
		int ty;
		for(int i = 0;i < types.length;i++){
			for(int j = 0;j < types[i].length;j++){
				Bobble bobble;
				ty = (int)types[i][j] - 48;
				if(ty >= 1 && ty <= 5){
					System.out.print(types[i][j] + " ");
					ty = (int) types[i][j] - 48;
					bobble = new Bobble(ty, ((i+1) % 2 == 0 ? 30 : 5) + IMAGE_SIZE * j, IMAGE_SIZE * (i+1) + 45);
					bobble.setRow(i+1);
					bobble.setCol(j+1);
					type[i][j] = ty;
					bobbles.add(bobble);
				}
			}
			System.out.println();
		}
		ty = random.nextInt(difficulty) + 1;
		currentBobble = new Bobble(ty, CENTER_X, CENTER_Y);
		ty = random.nextInt(difficulty) + 1;
		nextBobble = new Bobble(ty, CENTER_X, CENTER_Y + 50);
	}

	public boolean isPlaying() { return isPlaying; }

	public void setPlaying(boolean playing) { isPlaying = playing; }

	public int getTimeLeft() { return timeLeft; }

	public void setTimeLeft(int timeLeft) { this.timeLeft = timeLeft; }

	public void emitBobble(MyContentPanel contentPanel, int x, int y){
		// 发射泡泡
		Random random = new Random();
		Bobble bobble = new Bobble(currentBobble);
		currentBobble.setBgImage(nextBobble.getType());
		nextBobble.setBgImage(random.nextInt(difficulty) + 1);
		double dist = Math.sqrt(Math.pow(x - 250, 2) + Math.pow(y - CENTER_Y, 2));
		double cos = (x - 250) / dist;
		double sin = (y - CENTER_Y) / dist;
		double v = bobble.getV();
		bobble.setVx(cos * v);
		bobble.setVy(sin * v);
		bobbles.add(bobble);
		contentPanel.repaint();
	}

	public void emitLaser(){
		// 发射激光，第二行及以下的泡泡都下落并消失
		for(Bobble bobble : bobbles){
			if(bobble.getRow() > 1){
				type[bobble.getRow()-1][bobble.getCol()-1] = 0;
				bobble.setToBeDown(true);
			}
		}
	}

	public void updateBobble(Bobble bobble){
		// 更新泡泡位置
		double vx = bobble.getVx();
		double vy = bobble.getVy();
		int newX  = bobble.getX() + (int) vx;
		int newY  = bobble.getY() + (int) vy;
		// 边界检测
		if (newX < 0) {
			bobble.setX(0);
			bobble.setVx(vx * (-1));
		} else if (newX > (IMAGE_SIZE * (BOBBLE_MAX_COL) + 30) - IMAGE_SIZE) {
			bobble.setX((IMAGE_SIZE * (BOBBLE_MAX_COL) + 30) - IMAGE_SIZE);
			bobble.setVx(vx * (-1));
		} else if (newY > WINDOW_HEIGHT - IMAGE_SIZE) {
			// 下落的泡泡即将被消除
			bobble.setToBeRemoved(true);
		} else {
			bobble.setX(newX);
			bobble.setY(newY);
		}
		if(bobble.isToBeDown()){
			return;
		}
		//碰撞检测
		for(Bobble bobble1 : bobbles){
			if(bobble != bobble1 && bobble1.getRow() != 0 && bobble.isCollided(bobble1)){
				bobble.setVx(0);
				bobble.setVy(0);
				bobble.findNearestCell(bobble1, type);
				int r = bobble.getRow();
				int c = bobble.getCol();
				Point point = bobble.getPointByRowAndCol(r, c);
				bobble.setX((int) point.getX());
				bobble.setY((int) point.getY());
				type[r-1][c-1] = bobble.getType();
				checkTheSameBobbles(bobble, r, c);
				return;
			}
		}
		if (newY < 90) {
			int c = bobble.getColByPoint(newX, BOBBLE_MAX_COL);
			bobble.setVx(0);
			bobble.setVy(0);
			Point point = bobble.getPointByRowAndCol(1, c);
			bobble.setX((int) point.getX());
			bobble.setY((int) point.getY());
			bobble.setRow(1);
			bobble.setCol(c);
			type[0][bobble.getCol()-1] = bobble.getType();
			checkTheSameBobbles(bobble, 1, c);
		}
	}

	/**
	 *
	 * @param x  当前鼠标的x坐标
 	 * @param y  当前鼠标的y坐标
	 * @param eject  要设置背景图片的发射器，JLabel
	 */
	public void setImageForEjector(int x, int y, JLabel eject) {
		// 鼠标与发射器中心点的距离
		if (y >= 550) {
			if (x <= 250) {
				eject.setIcon(new ImageIcon(ejectorImage.getSubimage(0, 0, ejectorImage.getWidth(), 130)));
			} else {
				eject.setIcon(new ImageIcon(ejectorImage.getSubimage(0, ejectorImage.getHeight() - 130, ejectorImage.getWidth(), 130)));
			}
			return;
		}
		double dist = Math.sqrt(Math.pow(x - 250, 2) + Math.pow(y - 590, 2));
		// 鼠标与发射器连线与竖直方向的夹角（度）
		double angle = 180 / Math.PI * Math.acos((x - 250) / dist);
		int num = (int) angle / 15;
		int sourceY;
		if(num > 10){
			sourceY = 1300;
		}else{
			sourceY = 130 * (10 - (num != 0 ? num : 1));
		}
		eject.setIcon(new ImageIcon(ejectorImage.getSubimage(0, sourceY, ejectorImage.getWidth(), sourceY + 130 <= ejectorImage.getHeight() ? 130 : ejectorImage.getHeight() - sourceY)));
	}

	public void setImageForCurrentBobble(int ty){
		currentBobble.setBgImage(ty);
	}

	public void checkTheSameBobbles(Bobble bobble, int r, int c){
		LinkedList<Bobble> rmBobbles = new LinkedList<>();
		boolean[][] visited = createBoolArray(BOBBLE_MAX_ROW, BOBBLE_MAX_COL, false);
		bobble.findTheSameBobbles(bobbles, rmBobbles, r, c, type, visited);
		if(rmBobbles.size() >= 3 || bobble.getType() == 6){
			// 有3个及以上相同颜色的泡泡连在一起
			setRemoveState(rmBobbles, bobble);
		}
	}

	public void checkTheDownBobbles(boolean[][] isHanging, LinkedList<Bobble> hangBobbles){
		// 遍历悬空的泡泡，从第2行开始遍历（第1行不可能悬空）
		int maxRow = isHanging.length;
		int maxCol = isHanging[0].length;
		for(int i = 0;i < maxCol;i++){
			isHanging[0][i] = false;
		}
		for(int i = 0; i < maxRow;i++){
			for(int j = 0; j < maxCol;j++){
				// 从上至下判断是否悬空
				if(type[i][j] != 0 && !isHanging[i][j]){
					// 当前位置有泡泡
					if(type[i+1][j] != 0){
						isHanging[i+1][j] = false;
						if(j - 1 >= 0 && type[i+1][j-1] != 0){
							// 将下一行左边的泡泡也设为不悬空
							isHanging[i+1][j-1] = false;
						}
						if(j + 1 < maxCol && type[i+1][j+1] != 0){
							// 将下一行右边的泡泡也设为不悬空
							isHanging[i+1][j+1] = false;
						}
					}
					if(i % 2 == 0){
						if(j > 0 && type[i+1][j-1] != 0){
							isHanging[i+1][j-1] = false;
							if(j - 2  >= 0 && type[i+1][j-2] != 0){
								// 将下一行左边的泡泡也设为不悬空
								isHanging[i+1][j-2] = false;
							}
							if(type[i+1][j] != 0){
								// 将下一行右边的泡泡也设为不悬空
								isHanging[i+1][j] = false;
							}
						}
					}else{
						if(j < maxCol- 1 && type[i+1][j+1] != 0){
							isHanging[i+1][j+1] = false;
							if(type[i+1][j] != 0){
								// 将下一行左边的泡泡也设为不悬空
								isHanging[i+1][j] = false;
							}
							if(j + 2 < maxCol && type[i+1][j+2] != 0){
								// 将下一行右边的泡泡也设为不悬空
								isHanging[i+1][j+2] = false;
							}
						}
					}
				}
			}
		}
		for(int i = maxRow - 1;i > 0;i--){
			for(int j = maxCol - 1;j > 0;j--) {
				// 从下至上判断是否悬空
				if(type[i][j] != 0 && !isHanging[i][j]){
					// 当前位置有泡泡
					if(type[i-1][j] != 0){
						isHanging[i-1][j] = false;
						if(type[i-1][j-1] != 0){
							// 将上一行左边的泡泡也设为不悬空
							isHanging[i-1][j-1] = false;
						}
						if(j + 1 < maxCol && type[i-1][j+1] != 0){
							// 将上一行右边的泡泡也设为不悬空
							isHanging[i-1][j+1] = false;
						}
					}
					if(i % 2 == 0){
						if(type[i-1][j-1] != 0){
							isHanging[i-1][j-1] = false;
							if(j - 2  >= 0 && type[i-1][j-2] != 0){
								// 将上一行左边的泡泡也设为不悬空
								isHanging[i-1][j-2] = false;
							}
							if(j < maxCol && type[i-1][j] != 0){
								// 将上一行右边的泡泡也设为不悬空
								isHanging[i-1][j] = false;
							}
						}
					}else{
						if(j < maxCol - 1 && type[i-1][j+1] != 0){
							isHanging[i-1][j+1] = false;
							if(type[i-1][j] != 0){
								// 将上一行左边的泡泡也设为不悬空
								isHanging[i-1][j] = false;
							}
							if(j + 2 < maxCol && type[i=1][j+2] != 0){
								// 将上一行右边的泡泡也设为不悬空
								isHanging[i-1][j+2] = false;
							}
						}
					}
				}
			}
		}
		for(int i = 0;i < maxRow;i++){
			for(int j = 0;j < maxCol;j++){
				if(type[i][j] != 0 && isHanging[i][j]){
					int index = getIndex(i+1, j+1);
					if(index != -1){
						hangBobbles.add(bobbles.get(index));
					}
				}
			}
		}
	}

	public int getIndex(int i, int j){
		// 获取第i行第j列的泡泡的索引，行列索引从1开始
		for(int index = 0;index < bobbles.size();index++){
			if(bobbles.get(index).getRow() == i && bobbles.get(index).getCol() == j){
				return index;
			}
		}
		return -1;
	}

	public void setRemoveState(LinkedList<Bobble> sameBobbles, Bobble bobble1){
		// 给相同且相邻的泡泡设置状态为即将删除
		for(Bobble bobble : sameBobbles){
			type[bobble.getRow()-1][bobble.getCol()-1] = 0;
			bobble.setToBeRemoved(true);
		}
		if(bobble1.getType() != 6){
			Control.playSoundEffect("resource//music//bobbleRemove.wav");
		}
		if(bobble1.getType() == 6){
			Control.playSoundEffect("resource//music//bomb.wav");
		}
		// 记录每一个泡泡是否悬空
		boolean[][] isHanging = createBoolArray(BOBBLE_MAX_ROW, BOBBLE_MAX_COL, true);
		LinkedList<Bobble> hangBobbles = new LinkedList<>();
		checkTheDownBobbles(isHanging, hangBobbles);
		// 给悬空的泡泡设置状态为即将下落，以便实现下落动画效果
		for(Bobble b : hangBobbles){
			type[b.getRow()-1][b.getCol()-1] = 0;
			b.setToBeDown(true);
		}
	}

	public boolean[][] createBoolArray(int maxRow, int maxCol, boolean state){
		// 创建一个count*count的全是state的二维数组
		boolean[][] array = new boolean[maxRow][maxCol];
		for(int i = 0;i < maxRow;i++){
			for(int j = 0;j < maxCol;j++){
				array[i][j] = state;
			}
		}
		return array;
	}

	public void clear(){
		// 退出后进行清理工作
		isPlaying = false;
		timerUpdate.cancel();
		timerTime.cancel();
		timerUpdate = null;
		timerTime = null;
	}

	public void win(GameInstance gi){
		// 游戏胜利
		gi.stopBgm();
		gi.setPlaySound(false);
		clear();
		gi.setScene(new GameOverScene(gi, 1));
	}

	public void lose(GameInstance gi){
		// 游戏失败
		gi.stopBgm();
		gi.setPlaySound(false);
		clear();
		gi.setScene(new GameOverScene(gi, 0));
	}
}
