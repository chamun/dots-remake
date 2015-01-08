package gui.animations;

import model.Circle;


public class StubAnimationHandler implements AnimationHandler {

	@Override
	public void newFadeAnimation(Circle c) { }

	@Override
	public void newFallingAnimation(int column, int currentRow, int newRow,
			Circle circle) { }

	@Override
	public void newShrinkAnimation(Circle c) { }

}
