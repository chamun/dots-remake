package model;

import processing.core.PApplet;
import processing.core.PVector;

public class Circle {
	
		private float x, y;
		private float d;
		private int fill, border;
		private boolean selected = false;

		public Circle (float x, float y, int fill, 
				int border, int diameter) {
			this.x = x; 
			this.y = y; 
			this.fill = fill; 
			this.border = border;
			this.d = diameter;
		}
		
		
		public float getX() { return x; }
		public float getY() { return y; }
		public float getDiameter() { return d; }
		public int getFill() { return fill; }
		public int getBorder() { return border; }
		
		public void select() { selected = true; }
		public void unselect() { selected = false; }
		public void setY(int y) { this.y = y; }

		public boolean inside(float px, float py) {
			PVector center = new PVector(x, y);
			PVector point  = new PVector(px, py);
			return PVector.sub(point, center).mag() <= d / 2;
		}



		public boolean isSelected() { 
			return selected;
		}

		public void draw(PApplet p) { 
			p.pushMatrix();
			p.noStroke();
			if (selected) {
				p.strokeWeight(4);
				p.stroke(border);
			}
			p.fill(fill);
			p.ellipse (x, y, d, d);
			p.popMatrix();
		}	
}
