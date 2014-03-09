package gui;

import processing.core.PApplet;

public class Scoreboard {
	
	private float x, y, width, height;
	
	private int score;
	
	public Scoreboard(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void addScore(int ammount) { score += ammount; }
	
	public void draw(PApplet p) {
		String score = Integer.toString(this.score);
		p.textSize(40);
		float twidth = p.textWidth(score);
		
		p.pushMatrix();
		p.translate(x, y);
		p.fill(0xffffffff);
		p.noStroke();
		p.rect(x, y, width, height);
		p.fill(0xff000000);
		p.text(score, width / 2 - twidth / 2, p.textAscent());
		p.popMatrix();
	}

}
