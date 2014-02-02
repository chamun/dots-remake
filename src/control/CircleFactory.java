package control;

import java.util.Random;

import main.Main;
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
		int colors[] = randomColor();
		return new Circle(row, col, colors[0], colors[1], Main.BALLDIAMETER);
	}

	
	private int[] randomColor() {
		Random r = new Random();
		
		int fill[]   = { 
				0xffff0000, 0xff00ff00, 0xff0000ff, 0xff00ffff, 0xffffff00
		};
		
		int border[]   = { 
				0xffaf0000, 0xff00af00, 0xff0000af, 0xff00afff, 0xffafff00
		};
		
		int ret[] = {
				0, 0
		};
		
		int n = r.nextInt(fill.length);
		ret[0] = fill[n];
		ret[1] = border[n];
		return ret;
	}
}
