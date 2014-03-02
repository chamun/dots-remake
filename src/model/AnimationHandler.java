package model;

public interface AnimationHandler {

	void newFadeAnimation(Circle c);
	void newFallingAnimation(int column, int currentRow, 
			int newRow, Circle circle);
	void newShrinkAnimation(Circle c);
}
