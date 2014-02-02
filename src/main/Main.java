package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;


@SuppressWarnings("serial")
public class Main extends PApplet {

	/* Configs */
	private static final String PACKAGENAME = "main";
	private static final String CLASSNAME   = "Main";
	private static final String RENDERER    = P2D;

	private static final int SCREEN_WIDTH  = 500;
	private static final int SCREEN_HEIGHT = 500;
	private static final int BACKGROUND_COLOR = 0xffffffff;
	private static final int FPS = 30;

	public static int COLUMNS = 6;
	public static int ROWS = 6;
	public static int BALLDIAMETER = SCREEN_WIDTH / ROWS / 2;

	Ball balls[][] = new Ball[ROWS][COLUMNS];
	List<Ball> selectedBalls = new LinkedList<Ball>();

	public static void main(final String args[]) {
		PApplet.main(new String[] {"--bgcolor=#DFDFDF", PACKAGENAME+"."+CLASSNAME });
	}

	public void setup() {
		size(SCREEN_WIDTH, SCREEN_HEIGHT, RENDERER);		
		frameRate(FPS);
		createBalls();
	}

	public void draw() {
		background(BACKGROUND_COLOR);

		if (selectedBalls.size() != 0) {
			Ball b = selectedBalls.get(selectedBalls.size() - 1);
			strokeWeight(2);
			stroke(b.border);
			line(b.x, b.y, mouseX, mouseY);
		}

		for (int i = 0; i < selectedBalls.size() - 1; i++) {
			Ball a = selectedBalls.get(i);
			Ball b = selectedBalls.get(i + 1);
			strokeWeight(2);
			stroke(b.border);
			line(a.x, a.y, b.x, b.y);
		}

		for (int i = 0; i < balls[0].length; i++)
			for (Ball b: balls[i])
				if (b != null)
					b.draw();
	}

	public void keyPressed() {
		switch (key) {
		default:
			break;
		}
	}


	int[] getRandomColor() {
		int fill[]   = { 
				0xffff0000, 0xff00ff00, 0xff0000ff, 0xff00ffff, 0xffffff00
		};
		int border[]   = { 
				0xffaf0000, 0xff00af00, 0xff0000af, 0xff00afff, 0xffafff00
		};
		int ret[] = {
				0, 0
		};
		int n = (int) random(fill.length);
		ret[0] = fill[n];
		ret[1] = border[n];

		return ret;
	}

	public void mousePressed() {
		// Comeca a uniao
		Ball b = getBallUnderMouse();
		if (b == null)
			return;

		b.select();
		selectedBalls.add(b);
	}

	boolean xor(boolean a, boolean b) {
		return (a && !b) || (!a && b);
	}

	public void mouseDragged() {
		if (selectedBalls.size() == 0)
			return;
		Ball b = getBallUnderMouse();
		if (b == null)
			return;

		Ball lb = selectedBalls.get(selectedBalls.size() - 1);

		if (!b.isSelected() && b.isNeighbor(lb) && b.fill == lb.fill) {
			b.select();
			selectedBalls.add(b);
		}
	}
	public void mouseReleased() {
		if (selectedBalls.size() == 1) {
			selectedBalls.get(0).unselect();
			selectedBalls.clear();
			return;
		}
		// Termina a uniao
		for (Ball b: selectedBalls) { /* temporario */
			int i = numToCol(b.x);
			int j = numToRow(b.y);
			balls[j][i] = null;
		}

		for (int i = 0; i < COLUMNS; i++) {
			List<Ball> columnBalls = new ArrayList<Ball>(ROWS);
			for (int j = ROWS - 1; j >= 0; j--) {
				if (balls[j][i] != null)
					columnBalls.add(balls[j][i]);
			}
			int columnIndex = ROWS - 1;
			for (int j = 0; j < columnBalls.size(); j++) {
				balls[columnIndex][i] = columnBalls.get(j);
				balls[columnIndex][i].y = columnIndex * (SCREEN_HEIGHT / ROWS) + (SCREEN_HEIGHT / ROWS) / 2; 
				columnIndex--;
			}
			while (columnIndex >= 0) {
				newBall(i, columnIndex);
				columnIndex--;
			}
		}
		selectedBalls.clear();
	}

	int numToCol(float x) { 
		return (int) (x / (SCREEN_WIDTH / COLUMNS));
	}
	int numToRow(float y) { 
		return (int) (y / (SCREEN_HEIGHT / ROWS));
	}

	public Ball getBallUnderMouse() {
		int col = numToCol(mouseX);
		int row = numToRow(mouseY);
		if (col >= balls[0].length || row >= balls[0].length || row < 0 || col < 0)
			return null;
		Ball b = balls[row][col];
		if (b.inside(mouseX, mouseY))
			return b;
		return null;
	}

	void newBall(int i, int j) {
		int y = j * (SCREEN_HEIGHT / ROWS) + (SCREEN_HEIGHT / ROWS) / 2;
		int x = i * (SCREEN_WIDTH / COLUMNS) + (SCREEN_WIDTH / COLUMNS) / 2;
		int colors[] = getRandomColor();
		balls[j][i] = new Ball(x, y, colors[0], colors[1]);
	}

	void createBalls() {
		stroke(color(0, 0, 0));
		for (int j = 0; j < ROWS; j ++)
			for (int i = 0; i < COLUMNS; i++)
				newBall(i, j);
	}

	class Ball {
		float x, y;
		float d = BALLDIAMETER;
		int fill, border;
		boolean selected = false;

		public Ball (float x, float y, int fill, int border) { 
			this.x = x; 
			this.y = y; 
			this.fill = fill; 
			this.border = border;
		}

		public void select() { 
			selected = true;
		}
		public void unselect() { 
			selected = false;
		}

		public boolean inside(float px, float py) {
			PVector center = new PVector(x, y);
			PVector point  = new PVector(px, py);
			return PVector.sub(point, center).mag() <= d / 2;
		}

		public boolean isNeighbor(Ball b) {
			int myCol = numToCol(x);
			int myRow = numToRow(y);
			int bCol = numToCol(b.x);
			int bRow = numToRow(b.y);

			return xor(myCol == bCol, myRow == bRow) &&
					xor(abs(myCol - bCol) == 1, abs(myRow - bRow) == 1);
		}

		public boolean isSelected() { 
			return selected;
		}

		public void draw() { 
			noStroke();
			if (selected) {
				strokeWeight(4);
				stroke(border);
			}
			fill(fill);
			ellipse (x, y, d, d);
		}
	}
}
