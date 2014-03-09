package gui.animations;

import model.Circle;
import processing.core.PApplet;

public class FadeAnimation implements Animation {

	private static final int MAX_FRAMES = 20;
	private float x, y;
	private int frames = 0;
	private Circle c;

	public FadeAnimation(float x, float y, Circle c) {
		this.x = x;
		this.y = y;
		this.c = c;
	}
	
	@Override
	public void step() { frames++; }

	@Override
	public boolean isFinished() {	return frames >= MAX_FRAMES; }

	@Override
	public void draw(PApplet p) {
		p.pushMatrix();
		p.noStroke();
		p.fill(c.getFill(), getAlpha());
		p.translate(x, y);
		p.scale(getScale());
		p.ellipse(0, 0, c.getDiameter(), c.getDiameter());
		p.popMatrix();
	}

	private float getScale() {
		float angle = frames * PApplet.HALF_PI / MAX_FRAMES;
		float scale = 1.5f *  PApplet.sin(angle);
		return scale;
	}

	private int getAlpha() {
		float angle = frames * PApplet.PI / MAX_FRAMES;
		float color = 255 * PApplet.sin(angle);
		return (int) PApplet.map(color, 0, 255, 0, 140);
	}
}
