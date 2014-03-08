package screen;

import processing.core.PApplet;

public class MenuScreen implements Screen {
	
	private PApplet p;
	private GameManager gm;
	
	public MenuScreen(PApplet p, GameManager gm){
		this.p = p;
		this.gm = gm;
	}

	@Override
	public void mousePressed() { gm.startGame(); }

	@Override
	public void mouseDragged() { }

	@Override
	public void mouseReleased() { }

	@Override
	public void draw() {
		p.fill(0xff000000);
		p.textSize(40);
		p.text("Dots-Remake", 0, p.height / 2);
		p.textSize(20);
		p.text("(click anywhere to play)", 0, p.height / 2 + 20);
	}

}
