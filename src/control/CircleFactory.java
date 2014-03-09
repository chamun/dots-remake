package control;

import gui.CircleBoard;

import java.util.Random;

import model.Circle;

public class CircleFactory {
	
	private static CircleFactory instance;
	
	private CircleFactory() {}
	
	public static CircleFactory instance() {
		if (instance == null)
			instance = new CircleFactory();
		return instance;
	}
	
	
	public Circle newCircle(int row, int col) {
		int color = randomColor();
		return new Circle(row, col, color, CircleBoard.BALL_DIAMETER);
	}

	
	private int randomColor() {
		Random r = new Random();	
		int fill[]   = { 
				0xffff0000, 0xff00ff00, 0xff0000ff, 0xff00ffff, 0xffffff00
		};
		
		int n = r.nextInt(fill.length);
		return fill[n];
	}
}
