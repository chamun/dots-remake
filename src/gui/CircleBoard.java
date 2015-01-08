package gui;

import gui.animations.Animation;
import gui.animations.AnimationHandler;
import gui.animations.FadeAnimation;
import gui.animations.MoveAndBounceAnimation;
import gui.animations.ShrinkAnimation;

import java.util.ArrayList;
import java.util.List;

import main.MouseInputManager;
import model.Circle;
import model.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;
import control.CircleManager;

public class CircleBoard
		extends Rectangle
        implements AnimationHandler {
	
	private static final int COLUMNS = 6;
	private static final int ROWS = 6;
		
	private CircleManager circleManager;
	private List<Animation> animations = new ArrayList<Animation>();
	
	public CircleBoard(float x, float y, 
			float width, float height) {
		super(x, y, width, height);
		
		int circleDiameter = (int) width / ROWS / 2;
		circleManager = new CircleManager(ROWS, COLUMNS, circleDiameter);
		circleManager.setAnimationHandler(this);
	}

	public void draw(PApplet p) {
		p.pushMatrix();
		p.translate(getX(), getY());
		
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
				drawCircle(p, circleManager.getCircle(row, col));
				
		for(Animation a: animations)
			a.step();
		p.popMatrix();
	}

	public void dragAction() {
		if (circleManager.hasSelection())
			action();
	}
	
	public void action() {
		int row = mouseToRow();
		int col = mouseToCol();
		Circle c = circleManager.getCircle(row, col);
		if(mouseInside(c) && circleManager.actionAt(row, col))
			newFadeAnimation(c);
	}
	
	public int flush() { return circleManager.flush(); }
	
	@Override
	public void newFadeAnimation(Circle c) {
		animations.add(new FadeAnimation(getX(c), getY(c), c));
	}

	@Override
	public void newFallingAnimation(int column, int currentRow, 
			int newRow, Circle circle) {
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


	private void drawCircle(PApplet p, Circle b) {
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
		float cellW = getWidth() / COLUMNS;
		return column * cellW + cellW/2;
	}
	
	private float rowToY(float row) {
		float cellH = getHeight() / ROWS;
		return row * cellH + cellH/2; 
	}
	
	private int mouseToRow() {
		int row = (int) (getMouseY() / (getHeight() / ROWS));
		return PApplet.constrain(row, 0, ROWS); 
	}
	
	private int mouseToCol() {
		int col = (int) (getMouseX() / (getWidth() / COLUMNS));
		return PApplet.constrain(col, 0, COLUMNS);
	}

	private boolean mouseInside(Circle c) {
		if (c == null)
			return false;
		PVector center = new PVector(getX(c), getY(c));
		PVector mouse = new PVector(getMouseX(), getMouseY());
		return PVector.sub(center, mouse).mag() <= c.getDiameter() / 2;
	}
	
	private float getMouseX() { 
		return MouseInputManager.instance().getOCSx(this); 
	}
	
	private float getMouseY() {
		return MouseInputManager.instance().getOCSy(this);
	}	
}
