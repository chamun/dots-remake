package model;

import processing.core.PApplet;

public interface Animation {
	
	void step();
	boolean isFinished();
	void draw(PApplet p);
	
}
