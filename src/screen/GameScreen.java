package screen;

import java.util.ArrayList;
import java.util.List;

import model.Animation;
import model.AnimationHandler;
import model.Circle;
import model.FadeAnimation;
import model.MoveAndBounceAnimation;
import model.ShrinkAnimation;
import control.CircleManager;
import processing.core.PApplet;
import processing.core.PVector;


public class GameScreen 
        implements Screen, AnimationHandler {
	
	private PApplet p;
	
	private static final int COLUMNS = 6;
	private static final int ROWS = 6;
	public  static int BALL_DIAMETER; /* FIXME: this should be here */
	
	private float x, y, width, height;
	
	private CircleManager circleManager;
	private List<Animation> animations = new ArrayList<Animation>();
	
	public GameScreen(PApplet p, float x, float y, 
			float width, float height) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		BALL_DIAMETER = (int) width / ROWS / 2;
		circleManager = new CircleManager(ROWS, COLUMNS);
		circleManager.setAnimationHandler(this);
	}

	public void mousePressed() { action(); }

	public void mouseDragged() {
		if (!circleManager.hasSelection())
			return;
		action();
	}
	
	public void mouseReleased() { circleManager.flush();}

	@Override
	public void draw() {
		p.translate(x, y);

		for (int i = animations.size() - 1; i >= 0; i--)
			if (animations.get(i).isFinished())
				animations.remove(i);
		
		Circle last = circleManager.lastSelectedCircle();
		if (last != null) {
			p.strokeWeight(2);
			p.stroke(last.getFill());
			p.line(getX(last), getY(last), getMouseX(), getMouseY());
		}

		List<Circle> selected = circleManager.getSelected();
		for (int i = 0; i < selected.size() - 1; i++) {
			Circle a = selected.get(i);
			Circle b = selected.get(i + 1);
			p.strokeWeight(2);
			p.stroke(b.getFill());
			p.line(getX(a), getY(a), getX(b), getY(b));
		}
		
		for(Animation a: animations)
			a.draw(p);

		for (int row = 0; row < ROWS; row++)
			for (int col = 0; col < COLUMNS; col++)
				drawCircle(circleManager.getCircle(row, col));
				
		for(Animation a: animations)
			a.step();
	}
	
	/* Not Processing methods */

	private void action() {
		int row = mouseToRow();
		int col = mouseToCol();
		Circle c = circleManager.getCircle(row, col);
		if(mouseInside(c) && circleManager.actionAt(row, col))
			newFadeAnimation(c);
	}
	
	private void drawCircle(Circle b) {
		if (b == null || !b.isActive())
			return;
		p.pushMatrix();
		p.translate(getX(b), getY(b));
		b.display(p);
		p.popMatrix();
	}

	private float getX(Circle c) {
		return columnToX(c.getColumn()); 
	}
	
	private float getY(Circle c) {
		return rowToY(c.getRow());
	}
	
	private float columnToX(float column) {
		float cellW = width / COLUMNS;
		return column * cellW + cellW/2;
	}
	
	private float rowToY(float row) {
		float cellH = height / ROWS;
		return row * cellH + cellH/2; 
	}
	
	private int mouseToRow() {
		int row = (int) (getMouseY() / (height / ROWS));
		return PApplet.constrain(row, 0, ROWS); 
	}
	
	private int mouseToCol() {
		int col = (int) (getMouseX() / (width / COLUMNS));
		return PApplet.constrain(col, 0, COLUMNS);
	}

	private boolean mouseInside(Circle c) {
		if (c == null)
			return false;
		PVector center = new PVector(getX(c), getY(c));
		PVector mouse = new PVector(getMouseX(), getMouseY());
		return PVector.sub(center, mouse).mag() <= c.getDiameter() / 2;
	}

	@Override
	public void newFadeAnimation(Circle c) {
		animations.add(new FadeAnimation(getX(c), getY(c), c));
	}

	@Override
	public void newFallingAnimation(int column, int currentRow, int newRow, Circle circle) {
		float y = rowToY(currentRow);
		float newY = rowToY(newRow);
		float x = columnToX(column);
		Animation a = new MoveAndBounceAnimation(x, y, x, newY, circle);
		animations.add(a);
	}

	@Override
	public void newShrinkAnimation(Circle c) {
		float x = columnToX(c.getColumn());
		float y = rowToY(c.getRow());
		Animation a = new ShrinkAnimation(x, y, c);
		animations.add(a);
	}
	
	private float getMouseX() { return p.mouseX - x; }
	private float getMouseY() { return p.mouseY - y; }

	
}
