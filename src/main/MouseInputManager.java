package main;


import model.Rectangle;
import processing.core.PApplet;

public class MouseInputManager {
	
	private static MouseInputManager instance;
	private PApplet applet;
	
	private MouseInputManager (PApplet applet) { this.applet = applet; }
	
	public static void init(PApplet applet) { 
		instance = new MouseInputManager(applet);
	}
	
	public static MouseInputManager instance() {
		if (instance == null)
			throw new RuntimeException(
					"MouseInputManager was not initialized");
		return instance;
	}
	
	/**
	 * Returns the current mouseX in the object's coordinate system
	 */
	public float getOCSx(Rectangle r) { return applet.mouseX - r.getX(); }
	
	/**
	 * Returns the current mouseY in the object's coordinate system
	 */
	public float getOCSy(Rectangle r) { return applet.mouseY - r.getY(); }
}
