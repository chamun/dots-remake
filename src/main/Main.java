package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.Circle;
import processing.core.PApplet;
import processing.core.PVector;


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
			line(getX(b), getY(b), mouseX, mouseY);
		}

		for (int i = 0; i < selectedCircles.size() - 1; i++) {
			Circle a = selectedCircles.get(i);
			Circle b = selectedCircles.get(i + 1);
			strokeWeight(2);
			stroke(b.getBorder());
			line(getX(a), getY(a), getX(b), getY(b));
		}

		for (int i = 0; i < circles[0].length; i++)
			for (Circle b: circles[i])
				if (b != null)
					drawCircle(b);
	}

	public void mousePressed() {
		Circle b = getBallUnderMouse();
		if (b == null)
			return;

		b.select();
		selectedCircles.add(b);
	}

	public void mouseDragged() {
		if (selectedCircles.size() == 0)
			return;
		Circle b = getBallUnderMouse();
		if (b == null)
			return;

		Circle lb = selectedCircles.get(selectedCircles.size() - 1);

		if (!b.isSelected() && areNeighbors(b, lb) && b.getFill() == lb.getFill()) {
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

		for (Circle b: selectedCircles) {
			int col = b.getColumn();
			int row = b.getRow();
			circles[row][col] = null;
		}

		for (int col = 0; col < COLUMNS; col++) {
			List<Circle> columnBalls = new ArrayList<Circle>(ROWS);
			for (int j = ROWS - 1; j >= 0; j--) {
				if (circles[j][col] != null)
					columnBalls.add(circles[j][col]);
			}
			
			int currentRow = ROWS - 1;
			for (int j = 0; j < columnBalls.size(); j++) {
				circles[currentRow][col] = columnBalls.get(j);
				circles[currentRow][col].setRow(currentRow); 
				currentRow--;
			}
			
			while (currentRow >= 0) {
				newBall(currentRow, col);
				currentRow--;
			}
		}
		selectedCircles.clear();
	}
	
	/* Not Processing methods */
	
	void newBall(int row, int col) {
		int colors[] = getRandomColor();
		circles[row][col] = new Circle(row, col, colors[0], colors[1], BALLDIAMETER);
	}

	void createBalls() {
		stroke(color(0, 0, 0));
		for (int row = 0; row < ROWS; row ++)
			for (int col = 0; col < COLUMNS; col++)
				newBall(row, col);
	}
	
	private void drawCircle(Circle b) {
		pushMatrix();
		noStroke();
		if (b.isSelected()) {
			strokeWeight(4);
			stroke(b.getBorder());
		}
		
		fill(b.getFill());
		ellipse (getX(b), getY(b), b.getDiameter(), b.getDiameter());
		popMatrix();
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
	
	float getX(Circle c) {
		int cellW = SCREEN_WIDTH / COLUMNS;
		return c.getColumn() * cellW + cellW/2; 
	}
	
	float getY(Circle c) {
		int cellH = SCREEN_HEIGHT / ROWS;
		return c.getRow() * cellH + cellH/2; 
	}

	public Circle getBallUnderMouse() {
		int row = (int) (mouseY / (SCREEN_HEIGHT / ROWS));
		int col = (int) (mouseX / (SCREEN_WIDTH / COLUMNS));

		if (col >= circles[0].length || row >= circles[0].length 
				|| row < 0 || col < 0)
			return null;
		
		Circle b = circles[row][col];
		if (mouseInside(b))
			return b;
		
		return null;
	}
	
	private boolean mouseInside(Circle c) {
		PVector center = new PVector(getX(c), getY(c));
		PVector mouse = new PVector(mouseX, mouseY);
		return PVector.sub(center, mouse).mag() <= c.getDiameter() / 2;
	}
	
	private boolean areNeighbors(Circle a, Circle b) {
		int myCol = a.getColumn();
		int myRow = a.getRow();
		int bCol = b.getColumn();
		int bRow = b.getRow();

		return xor(myCol == bCol, myRow == bRow) &&
				xor(abs(myCol - bCol) == 1, abs(myRow - bRow) == 1);
	}
	
	boolean xor(boolean a, boolean b) {
		return (a && !b) || (!a && b);
	}

}
