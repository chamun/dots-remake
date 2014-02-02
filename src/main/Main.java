package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.Circle;
import processing.core.PApplet;


@SuppressWarnings("serial")
public class Main extends PApplet {

	/* Configs */
	private static final String PACKAGENAME = "main";
	private static final String CLASSNAME   = "Main";

	private static final int SCREEN_WIDTH  = 500;
	private static final int SCREEN_HEIGHT = 500;
	private static final int BACKGROUND_COLOR = 0xffffffff;
	private static final int FPS = 30;

	public static int COLUMNS = 6;
	public static int ROWS = 6;
	public static int BALLDIAMETER = SCREEN_WIDTH / ROWS / 2;

	Circle circles[][] = new Circle[ROWS][COLUMNS];
	List<Circle> selectedCircles = new LinkedList<Circle>();

	public static void main(final String args[]) {
		PApplet.main(new String[] {"--bgcolor=#DFDFDF", PACKAGENAME+"."+CLASSNAME });
	}

	public void setup() {
		size(SCREEN_WIDTH, SCREEN_HEIGHT);		
		frameRate(FPS);
		if (frame != null)
			frame.setTitle("Dotz");
		createBalls();
	}

	public void draw() {
		background(BACKGROUND_COLOR);

		if (selectedCircles.size() != 0) {
			Circle b = selectedCircles.get(selectedCircles.size() - 1);
			strokeWeight(2);
			stroke(b.getBorder());
			line(b.getX(), b.getY(), mouseX, mouseY);
		}

		for (int i = 0; i < selectedCircles.size() - 1; i++) {
			Circle a = selectedCircles.get(i);
			Circle b = selectedCircles.get(i + 1);
			strokeWeight(2);
			stroke(b.getBorder());
			line(a.getX(), a.getY(), b.getX(), b.getY());
		}

		for (int i = 0; i < circles[0].length; i++)
			for (Circle b: circles[i])
				if (b != null)
					b.draw(this);
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
		Circle b = getBallUnderMouse();
		if (b == null)
			return;

		b.select();
		selectedCircles.add(b);
	}

	boolean xor(boolean a, boolean b) {
		return (a && !b) || (!a && b);
	}

	public void mouseDragged() {
		if (selectedCircles.size() == 0)
			return;
		Circle b = getBallUnderMouse();
		if (b == null)
			return;

		Circle lb = selectedCircles.get(selectedCircles.size() - 1);

		if (!b.isSelected() && areNeighbor(b, lb) && b.getFill() == lb.getFill()) {
			b.select();
			selectedCircles.add(b);
		}
	}
	public void mouseReleased() {
		if (selectedCircles.size() == 1) {
			selectedCircles.get(0).unselect();
			selectedCircles.clear();
			return;
		}
		// Termina a uniao
		for (Circle b: selectedCircles) { /* temporario */
			int i = numToCol(b.getX());
			int j = numToRow(b.getY());
			circles[j][i] = null;
		}

		for (int i = 0; i < COLUMNS; i++) {
			List<Circle> columnBalls = new ArrayList<Circle>(ROWS);
			for (int j = ROWS - 1; j >= 0; j--) {
				if (circles[j][i] != null)
					columnBalls.add(circles[j][i]);
			}
			int columnIndex = ROWS - 1;
			for (int j = 0; j < columnBalls.size(); j++) {
				circles[columnIndex][i] = columnBalls.get(j);
				circles[columnIndex][i].setY(columnIndex * (SCREEN_HEIGHT / ROWS) + (SCREEN_HEIGHT / ROWS) / 2); 
				columnIndex--;
			}
			while (columnIndex >= 0) {
				newBall(i, columnIndex);
				columnIndex--;
			}
		}
		selectedCircles.clear();
	}

	int numToCol(float x) { 
		return (int) (x / (SCREEN_WIDTH / COLUMNS));
	}
	int numToRow(float y) { 
		return (int) (y / (SCREEN_HEIGHT / ROWS));
	}

	public Circle getBallUnderMouse() {
		int col = numToCol(mouseX);
		int row = numToRow(mouseY);
		if (col >= circles[0].length || row >= circles[0].length || row < 0 || col < 0)
			return null;
		Circle b = circles[row][col];
		if (b.inside(mouseX, mouseY))
			return b;
		return null;
	}

	void newBall(int i, int j) {
		int y = j * (SCREEN_HEIGHT / ROWS) + (SCREEN_HEIGHT / ROWS) / 2;
		int x = i * (SCREEN_WIDTH / COLUMNS) + (SCREEN_WIDTH / COLUMNS) / 2;
		int colors[] = getRandomColor();
		circles[j][i] = new Circle(x, y, colors[0], colors[1], BALLDIAMETER);
	}

	void createBalls() {
		stroke(color(0, 0, 0));
		for (int j = 0; j < ROWS; j ++)
			for (int i = 0; i < COLUMNS; i++)
				newBall(i, j);
	}
	
	public boolean areNeighbor(Circle a, Circle b) {
		int myCol = numToCol(a.getX());
		int myRow = numToRow(a.getY());
		int bCol = numToCol(b.getX());
		int bRow = numToRow(b.getY());

		return xor(myCol == bCol, myRow == bRow) &&
				xor(abs(myCol - bCol) == 1, abs(myRow - bRow) == 1);
	}

}
