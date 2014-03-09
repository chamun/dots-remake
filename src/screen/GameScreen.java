package screen;

import gui.CircleBoard;
import gui.Scoreboard;
import model.Rectangle;
import processing.core.PApplet;

public class GameScreen
		extends Rectangle
        implements Screen {
	
	private PApplet p;
	
	private Scoreboard sb;
	private CircleBoard cb;
	
	public GameScreen(PApplet p, float x, float y, 
			float width, float height) {
		super(x, y, width, height);
		
		this.p = p;
		cb = new CircleBoard(0, 50, width, height - 50);
		sb =  new Scoreboard(0, 0, width, 50);
	}

	public void mousePressed() { cb.action(false); }
	public void mouseDragged() { cb.action(true); }
	
	public void mouseReleased() { 
		int flushedCircles = cb.flush();
		sb.addScore(flushedCircles);
	}

	@Override
	public void draw() {
		cb.draw(p);
		sb.draw(p);
	}
}
