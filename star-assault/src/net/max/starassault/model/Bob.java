package net.max.starassault.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bob {
	
	public enum State {
		IDLE, WALKING, JUMPING, DYING
	}
	
	public static final float SPEED = 4f;
	public static final float JUMP_VELOCITY = 1f;
	public static final float SIZE = 0.5f;
	float stateTime = 0;
	
	Vector2 position = new Vector2();
	Vector2 acceleration = new Vector2();
	Vector2 velocity = new Vector2();
	Rectangle bounds = new Rectangle();
	State state = State.IDLE;
	boolean facingLeft = true;
	private boolean longJump = false;
	
	public Bob(Vector2 position) {
		this.position = position;
		this.bounds.height = SIZE;
		this.bounds.width = SIZE;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public Vector2 getPosition() {
		return position;
	}
	
	public void setState(State newState) {
		this.state = newState;
	}

	public void update(float delta) {
		position.add(velocity.tmp().mul(delta));
		bounds.x = position.x;
		bounds.y = position.y;
		stateTime += delta;
	}

	public Vector2 getAcceleration() {
		return acceleration;
	}
	
	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
	}
	
	public boolean isLongJump() {
		return longJump;
	}
	
	public void setLongJump(boolean longJump) {
		this.longJump = longJump;
	}
	
	public Vector2 getVelocity() {
		return velocity;
	}

	public void setFacingLeft(boolean b) {
		facingLeft = b;
	}

	public boolean isFacingLeft() {
		return facingLeft;
	}

	public State getState() {
		return state;
	}

	public float getStateTime() {
		return stateTime;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
		this.bounds.setX(position.x);
		this.bounds.setY(position.y);
	}
}
