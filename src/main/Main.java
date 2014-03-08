package main;

import java.util.Stack;

import processing.core.PApplet;
import screen.GameManager;
import screen.GameScreen;
import screen.Screen;


@SuppressWarnings("serial")
public class Main 
        extends PApplet 
        implements GameManager {

	/* Configs */
	private static final String PACKAGENAME = "main";
	private static final String CLASSNAME   = "Main";

	private static final int SCREEN_WIDTH  = 500;
	private static final int SCREEN_HEIGHT = 500;
	private static final int BACKGROUND_COLOR = 0xffffffff;
	private static final int FPS = 30;
	
	private Stack<Screen> screens = new Stack<Screen>();

	public static void main(final String args[]) {
		PApplet.main(new String[] {"--bgcolor=#DFDFDF", PACKAGENAME+"."+CLASSNAME });
	}

	public void setup() {
		size(SCREEN_WIDTH, SCREEN_HEIGHT);		
		frameRate(FPS);
		if (frame != null)
			frame.setTitle("Dotz");
		
		// screens.push(new MenuScreen(this, this));
		startGame();
	}

	public void draw() {
		background(BACKGROUND_COLOR);
		pushMatrix();
			screens.peek().draw();
		popMatrix();
	}

	public void mousePressed()  { screens.peek().mousePressed();  }
	public void mouseDragged()  { screens.peek().mouseDragged();  }
	public void mouseReleased() { screens.peek().mouseReleased(); }

	@Override
	public void startGame() {	
		screens.push(new GameScreen(this, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT));
	}
}
