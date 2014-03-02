package model;

import processing.core.PApplet;

public class ShrinkAnimation implements Animation {
	
	private float x, y;
	private Circle c;
	private float scale = 1;
	
	public ShrinkAnimation(float x, float y, Circle c) {
		this.x = x;
		this.y = y;
		this.c = c;
	}

	@Override
	public void step() { scale *= 0.8; }

	@Override
	public boolean isFinished() {
		return scale  <= 0.01;
	}

	@Override
	public void draw(PApplet p) {
		p.pushMatrix();
		p.translate(x, y);
		p.scale(scale);
		c.display(p);
		p.popMatrix();
	}

}
