package item;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * @program: PuzzleBobble
 * @description: 泡泡类
 * @author: Qiu
 * @create: 2019-06-08 22:45
 */
public class Bobble extends JLabel {
	private int x = 0;           // 泡泡的x坐标
	private int y = 0;           // 泡泡的y坐标
	private int row = 0;         // 泡泡所处的行数，索引从1开始
	private int col = 0;         // 泡泡所处的列数，索引从1开始
	private double v = 30;       // 总速度
	private double vx = 0;       // 水平速度
	private double vy = 0;       // 垂直速度
	private int type = 0;        // 泡泡类型
	private boolean toBeRemoved = false; // 判断此泡泡是否即将被移除
	private boolean toBeDown = false;    // 判断此泡泡是否即将因悬空而下落
	private ImageIcon bgImage;   // 泡泡图片
	private static HashMap<Integer, ImageIcon> map;  // 不同的type对应不同的图片

	static{
		map = new HashMap<>();
		map.put(1, new ImageIcon("resource//image//bobble_blue.png"));
		map.put(2, new ImageIcon("resource//image//bobble_green.png"));
		map.put(3, new ImageIcon("resource//image//bobble_white.png"));
		map.put(4, new ImageIcon("resource//image//bobble_red.png"));
		map.put(5, new ImageIcon("resource//image//bobble_yellow.png"));
		map.put(6, new ImageIcon("resource//image//bobble_bomb.png"));
		map.put(7, new ImageIcon("resource//image//bobble_rainbow.png"));
	}

	public Bobble(int type, int x, int y){
		this.type = type;
		this.x = x;
		this.y = y;
		bgImage = map.get(type);
		setIcon(bgImage);
	}

	public Bobble(Bobble bobble){ setNewBobble(bobble); }

	public void setNewBobble(Bobble bobble) {
		x = bobble.getX();
		y = bobble.getY();
		v = bobble.getV();
		vx = bobble.getVx();
		vy = bobble.getVy();
		row = bobble.getRow();
		col = bobble.getCol();
		type = bobble.getType();
		bgImage = bobble.getBgImage();
		setIcon(bgImage);
	}

	public int getImageSize() { return bgImage.getIconWidth(); }

	public void setX(int x) { this.x = x; }

	public void setY(int y) { this.y = y; }

	public void setRow(int row) { this.row = row; }

	public void setCol(int col) { this.col = col; }

	public void setVx(double vx) { this.vx = vx; }

	public void setVy(double vy) { this.vy = vy; }

	public void setBgImage(int type){
		setBgImage(map.get(type));
		setType(type);
	}

	public void setBgImage(ImageIcon image) {
		this.bgImage = image;
		setIcon(bgImage);
	}

	public void setType(int type) { this.type = type; }

	public void setToBeRemoved(boolean toBeRemoved) { this.toBeRemoved = toBeRemoved; }

	public void setToBeDown(boolean toBeDown) { this.toBeDown = toBeDown; }

	@Override
	public int getX() { return x; }

	@Override
	public int getY() { return y; }

	public int getRow() { return row; }

	public int getCol() { return col; }

	public double getV() { return v; }

	public double getVx() { return vx; }

	public double getVy() { return vy; }

	public ImageIcon getBgImage() { return bgImage; }

	public int getType() { return type; }

	public boolean isToBeRemoved() { return toBeRemoved; }

	public boolean isToBeDown() { return toBeDown; }

	public boolean isCollided(Bobble bobble){
		// 判断当前泡泡是否与bobble相碰
		return Math.sqrt(Math.pow(x - bobble.getX(), 2) + Math.pow(y - bobble.getY(), 2)) < getImageSize();
	}

	public Point getPointByRowAndCol(int r, int c){
		// 根据泡泡所处的行列返回其对应的x，y坐标值
		int size = getImageSize();
		int x = (r % 2 == 0 ? 30 : 5) + size * (c - 1);
		int y = size * r + 45;
		return new Point(x, y);
	}

	public int getColByPoint(int x, int maxCol){
		// 当发射的泡泡到达第一行且未碰撞时，获取其对应的列坐标
		int c = (x - 5) / getImageSize() + 1;
		return c > maxCol ? maxCol : c;
	}

	public void findNearestCell(Bobble bobble, int[][] isTaken){
		// 当前泡泡与bobble相碰，停下后为其找到最近的单元格
		int r = bobble.getRow();
		int c = bobble.getCol();
		if(r == 0 && c == 0){
			// 碰到正在移动的泡泡，不进行碰撞检测
			return;
		}
		int maxRow = isTaken.length;
		int maxCol = isTaken[0].length;

		double minDist = 999;  // 记录与当前泡泡最近的单元格的距离
		int rowMin = r == 1 ? 1 : (r - 1);
		int rowMax = r == maxRow ? maxRow: (r + 1);
		int colMin = c == 1 ? 1 : (c - 1);
		int colMax = c == maxCol ? maxCol : (c + 1);
		for(int i = rowMin;i <= rowMax;i++){
			for(int j = colMin;j <= colMax;j++){
				if(isTaken[i-1][j-1] == 0){
					// 此单元格没有泡泡，则判断其与当前泡泡的距离
					Point point = getPointByRowAndCol(i, j);
					double dist = Math.sqrt(Math.pow(point.getX() - x, 2) + Math.pow(point.getY() - y, 2));
					if(dist < minDist){
						minDist = dist;
						setRow(i);
						setCol(j);
					}
				}
			}
		}
	}


	/**
	 *
	 * @param bobbles     所有泡泡
	 * @param rmBobbles   可能将要删除的泡泡，（若相同的泡泡数大于等于3）
	 * @param r           当前行坐标
	 * @param c           当前列坐标
	 * @param type        所有泡泡的种类
	 * @param visited     此泡泡是否被访问过，防止重复访问
	 */
	public void findTheSameBobbles(LinkedList<Bobble> bobbles, LinkedList<Bobble> rmBobbles, int r, int c, int[][] type, boolean[][] visited){
		// 泡泡碰撞检测后判断相同的泡泡个数
		int maxRow = type.length;
		int maxCol = type[0].length;
		if(this.type == 6){
			// 是炸弹，找到周围所有的泡泡
			rmBobbles.add(this);
			findNeighborBobbles(bobbles, rmBobbles, type);
			return;
		} else if(this.type == 7){
			// 彩虹，设置为周围任意相邻泡泡的颜色
			findNeighborBobbles(bobbles, rmBobbles, type);
			Random random = new Random();
			int index;
			if(rmBobbles.size() > 0){
				// 有相邻泡泡
				index = random.nextInt(rmBobbles.size());
			}else{
				// 没有相邻泡泡
				index = random.nextInt(5) + 1;
			}
			int ty = rmBobbles.get(index).getType();
			setBgImage(ty);
			type[row-1][col-1] = ty;
			rmBobbles.clear();
			findTheSameBobbles(bobbles, rmBobbles, r, c, type, visited);
		}
		if(r < 1 || r > maxRow || c < 1 || c > maxCol){
			// 索引超出范围
			return;
		}
		if (visited[r - 1][c - 1]) {
			// 当前泡泡已经被访问过
			return;
		}
		if(type[r-1][c-1] != this.type){
			// 与当前泡泡种类不同
			visited[r-1][c-1] = true;
			return;
		} else {
			// 与当前泡泡种类相同
			for (Bobble bobble : bobbles) {
				int i = bobble.getRow();
				int j = bobble.getCol();
				if (i == r && j == c) {
					rmBobbles.add(bobble);
					visited[i-1][j-1] = true;
					break;
				}
			}
		}
		// 从左上，右上，左，右，左下，右下六个方向寻找相同的泡泡
		if(r % 2 == 0) {
			// 左上泡泡的行列坐标为：r - 1, c
			findTheSameBobbles(bobbles, rmBobbles, r - 1, c, type, visited);
			// 右上泡泡的行列坐标为：r - 1, c + 1
			findTheSameBobbles(bobbles, rmBobbles, r - 1, c + 1, type, visited);
			// 左下泡泡的行列坐标为：r + 1, c
			findTheSameBobbles(bobbles, rmBobbles, r + 1, c, type, visited);
			// 右下泡泡的行列坐标为：r + 1, c + 1
			findTheSameBobbles(bobbles, rmBobbles, r + 1, c + 1, type, visited);
		}else{
			// 左上泡泡的坐标为：r - 1, c - 1
			findTheSameBobbles(bobbles, rmBobbles, r - 1, c - 1, type, visited);
			// 右上泡泡的行列坐标为：r - 1, c
			findTheSameBobbles(bobbles, rmBobbles, r - 1, c, type, visited);
			// 左下泡泡的行列坐标为：r + 1, c - 1
			findTheSameBobbles(bobbles, rmBobbles, r + 1, c - 1, type, visited);
			// 右下泡泡的行列坐标为：r + 1, c
			findTheSameBobbles(bobbles, rmBobbles, r + 1, c, type, visited);
		}
		// 左边泡泡的行列坐标为：r, c - 1
		findTheSameBobbles(bobbles, rmBobbles, r, c - 1, type, visited);
		// 右边泡泡的行列坐标为：r, c + 1
		findTheSameBobbles(bobbles, rmBobbles, r, c + 1, type, visited);
	}

	public void findNeighborBobbles(LinkedList<Bobble> bobbles, LinkedList<Bobble> neighborBobbles, int[][] type){
		// 记录哪些行列坐标的泡泡应该删除
		int maxRow = type.length;
		int maxCol = type[0].length;
		LinkedList<Point> toRemove = new LinkedList<>();
		if(col > 1 && type[row-1][col-2] != 0){
			// 左边
			toRemove.add(new Point(row, col-1));
		}
		if(col < maxCol && type[row-1][col] != 0){
			// 右边
			toRemove.add(new Point(row, col+1));
		}
		if(row > 1 && type[row-2][col-1] != 0){
			toRemove.add(new Point(row-1, col));
		}
		if(row < maxRow && type[row][col-1] != 0){
			toRemove.add(new Point(row+1, col));
		}
		if(row % 2 == 0){
			if(row > 1 && col < maxCol && type[row-2][col] != 0){
				toRemove.add(new Point(row-1, col+1));
			}
			if(row < maxRow && col < maxCol && type[row][col] != 0){
				toRemove.add(new Point(row+1, col+1));
			}
		}else{
			if(row > 1 && col > 1 && type[row-2][col-2] != 0){
				toRemove.add(new Point(row-1, col-1));
			}
			if(row < maxRow && col > 1 && type[row][col-2] != 0){
				toRemove.add(new Point(row+1, col-1));
			}
		}
		for(Point point : toRemove){
			int x = (int)point.getX();
			int y = (int)point.getY();
			for(Bobble bobble : bobbles){
				if(bobble.getRow() == x && bobble.getCol() == y){
					neighborBobbles.add(bobble);
				}
			}
		}
	}
}
