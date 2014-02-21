package model;

import processing.core.PApplet;

public class Circle {
	private int row, column;
	private int fill, border;
	private float d;
	private boolean selected = false;
	private boolean active = true;

	public Circle (int row, int column,
			int fill, int border, int diameter) {
		this.row = row;
		this.column = column;
		this.fill = fill; 
		this.border = border;
		this.d = diameter;
	}

	public int getRow() { return row; }
	public int getColumn() { return column; }
	public int getFill() { return fill; }
	public int getBorder() { return border; }

	public float getDiameter() { return d; }

	public boolean isSelected() { return selected; }
	public boolean isActive() { return active; }
	
	public void select() { selected = true; }
	public void unselect() { selected = false; }
	public void setRow(int row) { this.row = row; }
	public void activate() { active = true; }
	public void deactivate() { active = false; }
	
	public void display(PApplet p) {
		p.pushMatrix();
		p.noStroke();
		if (isSelected()) {
			p.strokeWeight(4);
			p.stroke(getBorder());
		}
		
		p.fill(getFill());
		p.ellipse (0, 0, getDiameter(), getDiameter());
		p.popMatrix();
	}

}
