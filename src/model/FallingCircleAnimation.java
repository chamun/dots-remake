package model;

import processing.core.PApplet;


public class FallingCircleAnimation implements Animation {

	private static final int MAX_FRAMES = 8;

	private float x, sy, speed;
	private int frames;
	private Circle circle;
	
	public FallingCircleAnimation(float x, float startingY, 
			float endingY, Circle circle) {

		this.x = x;
		this.circle = circle;
		
		sy = startingY;
		speed = (endingY - startingY) / MAX_FRAMES;
		circle.deactivate();
	}
	
	@Override
	public void step() {
		frames++;
		if (frames >= MAX_FRAMES)
			circle.activate();
		
	}
	@Override
	public boolean isFinished() { return frames >= MAX_FRAMES; }
	
	@Override
	public void draw(PApplet p) {
		p.pushMatrix();
		p.translate(x, sy + speed * frames);
		circle.display(p);
		p.popMatrix();
	}
}
