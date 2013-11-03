package net.max.starassault.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bob {
	
	public enum State {
		IDLE, WALKING, JUMPING, DYING
	}
	
	public static final float SPEED = 2f;
	public static final float JUMP_VELOCITY = 1f;
	public static final float SIZE = 0.5f;
	
	Vector2 position = new Vector2();
	Vector2 acceleration = new Vector2();
	Vector2 velocity = new Vector2();
	Rectangle bounds = new Rectangle();
	State stat = State.IDLE;
	boolean facingLeft = true;
	
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
}
