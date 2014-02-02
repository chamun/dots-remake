package main;

import java.util.List;

import model.Circle;
import processing.core.PApplet;
import processing.core.PVector;
import control.CircleManager;


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

	private CircleManager circleManager = new CircleManager(ROWS, COLUMNS);

	public static void main(final String args[]) {
		PApplet.main(new String[] {"--bgcolor=#DFDFDF", PACKAGENAME+"."+CLASSNAME });
	}

	public void setup() {
		size(SCREEN_WIDTH, SCREEN_HEIGHT);		
		frameRate(FPS);
		if (frame != null)
			frame.setTitle("Dotz");
	}

	public void draw() {
		background(BACKGROUND_COLOR);

		Circle last = circleManager.lastSelectedCircle();
		if (last != null) {
			strokeWeight(2);
			stroke(last.getBorder());
			line(getX(last), getY(last), mouseX, mouseY);
		}

		List<Circle> selected = circleManager.getSelected();
		for (int i = 0; i < selected.size() - 1; i++) {
			Circle a = selected.get(i);
			Circle b = selected.get(i + 1);
			strokeWeight(2);
			stroke(b.getBorder());
			line(getX(a), getY(a), getX(b), getY(b));
		}

		for (int row = 0; row < ROWS; row++)
			for (int col = 0; col < COLUMNS; col++)
				drawCircle(circleManager.getCircle(row, col));;
	}

	public void mousePressed() {
		int row = mouseToRow();
		int col = mouseToCol();
		if(mouseInside(circleManager.getCircle(row, col)))
			circleManager.select(row, col);
	}

	public void mouseDragged() {
		if (!circleManager.hasSelection())
			return;
		int row = mouseToRow();
		int col = mouseToCol();
		if(mouseInside(circleManager.getCircle(row, col)))
			circleManager.select(row, col);
	}
	
	public void mouseReleased() { circleManager.flush();}
	
	/* Not Processing methods */

	private void drawCircle(Circle b) {
		if (b == null)
			return;
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

	float getX(Circle c) {
		int cellW = SCREEN_WIDTH / COLUMNS;
		return c.getColumn() * cellW + cellW/2; 
	}
	
	float getY(Circle c) {
		int cellH = SCREEN_HEIGHT / ROWS;
		return c.getRow() * cellH + cellH/2; 
	}
	
	private int mouseToRow() {
		return (int) (mouseY / (SCREEN_HEIGHT / ROWS)); 
	}
	
	private int mouseToCol() {
		return (int) (mouseX / (SCREEN_WIDTH / COLUMNS));
	}

	private boolean mouseInside(Circle c) {
		PVector center = new PVector(getX(c), getY(c));
		PVector mouse = new PVector(mouseX, mouseY);
		return PVector.sub(center, mouse).mag() <= c.getDiameter() / 2;
	}
}
