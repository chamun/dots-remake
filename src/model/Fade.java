package model;

import processing.core.PApplet;

public class Fade implements Animation {

	private static final int MAX_FRAMES = 15;
	private int color;
	private float x, y;
	private int size;
	private int frames = 0;

	public Fade(float x, float y, int color, int size) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.size = size;
	}
	
	@Override
	public void step() { frames++; }

	@Override
	public boolean isFinished() {	return frames >= MAX_FRAMES; }

	@Override
	public void draw(PApplet p) {
		p.pushMatrix();
		p.noStroke();
		p.fill(color, 1 - frames * 255 / MAX_FRAMES);
		p.translate(x, y);
		p.ellipse(0, 0, size, size);
		p.popMatrix();
	}
}
