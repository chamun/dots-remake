package model;

import processing.core.PApplet;
import processing.core.PVector;


public class MoveAndBounceAnimation implements Animation {

	boolean isFinished = false;
	private Circle circle;

	private PVector position;
	private PVector acceleration;
	private PVector velocity;

	private PVector target;
	private float thrust = 2f;
	
	public MoveAndBounceAnimation(float takeoffx, float takeoffy, 
			float targetx, float targety, Circle circle) {

		position = new PVector(takeoffx, takeoffy);
		target = new PVector(targetx, targety);
		velocity = new PVector();
		setAcceleration();
		
		this.circle = circle;
		circle.deactivate();
	}
	
	@Override
	public void step() {
		if (isFinished)
			return;
		float distance = PVector.sub(target, position).mag();
		if (distance <= 0.01) {
			circle.activate();
			isFinished = true;
		} else {
			velocity.add(acceleration);
			position.add(velocity);
			setAcceleration();
			float angle = PVector.angleBetween(velocity, acceleration);
			if (angle > PApplet.radians(0.5f)) {
				thrust /= 2;
				velocity.set(0, 0);
			}
		}
		
	}
	private void setAcceleration() {
		acceleration = PVector.sub(target, position);
		acceleration.normalize();
		acceleration.mult(thrust);
	}

	@Override
	public boolean isFinished() { return isFinished; }
	
	@Override
	public void draw(PApplet p) {
		p.pushMatrix();
		p.translate(position.x, position.y);
		circle.display(p);
		p.popMatrix();
	}
}
