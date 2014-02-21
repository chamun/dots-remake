package main;

import java.util.ArrayList;
import java.util.List;

import model.Animation;
import model.AnimationHandler;
import model.Circle;
import model.Fade;
import processing.core.PApplet;
import processing.core.PVector;
import control.CircleManager;


@SuppressWarnings("serial")
public class Main 
        extends PApplet
        implements AnimationHandler {

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
	private List<Animation> animations = new ArrayList<Animation>();

	public static void main(final String args[]) {
		PApplet.main(new String[] {"--bgcolor=#DFDFDF", PACKAGENAME+"."+CLASSNAME });
	}

	public void setup() {
		size(SCREEN_WIDTH, SCREEN_HEIGHT);		
		frameRate(FPS);
		if (frame != null)
			frame.setTitle("Dotz");
		circleManager.setAnimationHandler(this);
	}

	public void draw() {
		background(BACKGROUND_COLOR);

		for (int i = animations.size() - 1; i >= 0; i--)
			if (animations.get(i).isFinished())
				animations.remove(i);
		
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
		
		for(Animation a: animations)
			a.draw(this);

		for (int row = 0; row < ROWS; row++)
			for (int col = 0; col < COLUMNS; col++)
				drawCircle(circleManager.getCircle(row, col));
		
		for(Animation a: animations)
			a.step();
	}

	public void mousePressed() { action(); }

	public void mouseDragged() {
		if (!circleManager.hasSelection())
			return;
		action();
	}
	
	public void mouseReleased() { circleManager.flush();}
	
	/* Not Processing methods */

	private void action() {
		int row = mouseToRow();
		int col = mouseToCol();
		Circle c = circleManager.getCircle(row, col);
		if(mouseInside(c) && circleManager.actionAt(row, col))
			newFadeAnimation(c);
	}
	
	private void drawCircle(Circle b) {
		if (b == null)
			return;
		pushMatrix();
		translate(getX(b), getY(b));
		b.display(this);
		popMatrix();
	}

	private float getX(Circle c) {
		int cellW = SCREEN_WIDTH / COLUMNS;
		return c.getColumn() * cellW + cellW/2; 
	}
	
	private float getY(Circle c) {
		int cellH = SCREEN_HEIGHT / ROWS;
		return c.getRow() * cellH + cellH/2; 
	}
	
	private int mouseToRow() {
		int row = (int) (mouseY / (SCREEN_HEIGHT / ROWS));
		return constrain(row, 0, ROWS); 
	}
	
	private int mouseToCol() {
		int col = (int) (mouseX / (SCREEN_WIDTH / COLUMNS));
		return constrain(col, 0, COLUMNS);
	}

	private boolean mouseInside(Circle c) {
		if (c == null)
			return false;
		PVector center = new PVector(getX(c), getY(c));
		PVector mouse = new PVector(mouseX, mouseY);
		return PVector.sub(center, mouse).mag() <= c.getDiameter() / 2;
	}

	@Override
	public void newFadeAnimation(Circle c) {
		animations.add(
				new Fade(getX(c), getY(c), c.getBorder(), BALLDIAMETER + 20));
	}
}
