package model;

import processing.core.PApplet;

public class Circle {
	private int row, column;
	private int fill;
	private float d;
	private boolean active = true;

	public Circle (int row, int column,
			int fill, int diameter) {
		this.row = row;
		this.column = column;
		this.fill = fill; 
		this.d = diameter;
	}

	public int getRow() { return row; }
	public int getColumn() { return column; }
	public int getFill() { return fill; }

	public float getDiameter() { return d; }
	public boolean isActive() { return active; }
	
	public void setRow(int row) { this.row = row; }
	public void activate() { active = true; }
	public void deactivate() { active = false; }
	
	public void display(PApplet p) {
		p.pushMatrix();
		p.noStroke();		
		p.fill(getFill());
		p.ellipse (0, 0, getDiameter(), getDiameter());
		p.popMatrix();
	}

}
