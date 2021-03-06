package net.max.starassault.controller;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import net.max.starassault.model.Block;
import net.max.starassault.model.Bob;
import net.max.starassault.model.Bob.State;
import net.max.starassault.model.World;

public class BobController {
	
	enum Keys {
		LEFT, RIGHT, JUMP, FIRE
	}
	
	private static final long LONG_JUMP_PRESS = 1501;
	private static final float ACCELERATION = 20f;
	private static final float GRAVITY = -20f;
	private static final float MAX_JUMP_SPEED = 7f;
	private static final float DAMP = 0.90f;
	private static final float MAX_VEL = 4f;
	
	private static final float WIDTH = 10f;
	
	private World world;
	private Bob bob;
	private Array<Block> collidable = new Array<Block>();
	private long jumpPressedTime;
	private boolean jumpingPressed;
	private boolean grounded = false;
	
	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};
	
	static Map<Keys, Boolean> keys = new HashMap<BobController.Keys, Boolean>();
	static {
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.JUMP, false);
		keys.put(Keys.FIRE, false);
	};
	
	public BobController(World world) {
		this.world = world;
		this.bob = world.getBob();
	}
	
	public void leftPressed() {
		keys.get(keys.put(Keys.LEFT, true));
	}
	
	public void rightPressed() {
		keys.get(keys.put(Keys.RIGHT, true));
	}
	
	public void jumpPressed() {
		keys.get(keys.put(Keys.JUMP, true));
	}
	
	public void firePressed() {
		keys.get(keys.put(Keys.FIRE, true));
	}
	
	public void leftReleased() {
		keys.get(keys.put(Keys.LEFT, false));
	}
	
	public void rightReleased() {
		keys.get(keys.put(Keys.RIGHT, false));
	}
	
	public void jumpReleased() {
		keys.get(keys.put(Keys.JUMP, false));
		jumpingPressed = false;
	}
	
	public void fireReleased() {
		keys.get(keys.put(Keys.FIRE, false));
	}
	
	public void update(float delta) {
		processInput();
		
		if (grounded && bob.getState().equals(State.JUMPING)) {
			bob.setState(State.IDLE);
		}
		
		bob.getAcceleration().y = GRAVITY;
		bob.getAcceleration().mul(delta);
		bob.getVelocity().add(bob.getAcceleration().x, bob.getAcceleration().y);
		checkCollisionWithBlocks(delta);
		bob.getVelocity().x *= DAMP;
		if (bob.getVelocity().x > MAX_VEL) {
			bob.getVelocity().x = MAX_VEL;
		}
		if (bob.getVelocity().x < -MAX_VEL) {
			bob.getVelocity().x = -MAX_VEL;
		}
		
		bob.update(delta);
		
		if (bob.getPosition().y < 0) {
			bob.getPosition().y = 0f;
			bob.setPosition(bob.getPosition());
			if (bob.getState().equals(State.JUMPING)) {
				bob.setState(State.IDLE);
			}
		}
		if (bob.getPosition().x < 0) {
			bob.getPosition().x = 0f;
			bob.setPosition(bob.getPosition());
			if (bob.getState().equals(State.JUMPING)) {
				bob.setState(State.IDLE);
			}
		}
		if (bob.getPosition().x > WIDTH - bob.getBounds().width) {
			bob.getPosition().x = WIDTH - bob.getBounds().width;
			bob.setPosition(bob.getPosition());
			if (bob.getState().equals(State.JUMPING)) {
				bob.setState(State.IDLE);
			}
		}
	}
	
	private void checkCollisionWithBlocks(float delta) {
		bob.getVelocity().mul(delta);
		Rectangle bobRect = rectPool.obtain();
		bobRect.set(bob.getBounds().x, bob.getBounds().y,
				bob.getBounds().width, bob.getBounds().height);
		int startX, endX;
        int startY = (int) bob.getBounds().y;
        int endY = (int) (bob.getBounds().y + bob.getBounds().height);
        // if Bob is heading left then we check if he collides with the block on his left
        // we check the block on his right otherwise
        if (bob.getVelocity().x < 0) {
            startX = endX = (int) Math.floor(bob.getBounds().x + bob.getVelocity().x);
        } else {
            startX = endX = (int) Math.floor(bob.getBounds().x + bob.getBounds().width + bob.getVelocity().x);
        }

        // get the block(s) bob can collide with
        populateCollidableBlocks(startX, startY, endX, endY);

        // simulate bob's movement on the X
        bobRect.x += bob.getVelocity().x;
        
        // clear collision boxes in world
        world.getCollisionRects().clear();
        
        // if bob collides, make his horizontal velocity 0
        for (Block block : collidable) {
            if (block == null) continue;
            if (bobRect.overlaps(block.getBounds())) {
                bob.getVelocity().x = 0;
                world.getCollisionRects().add(block.getBounds());
                break;
            }
        }

        // reset the x position of the collision box
        bobRect.x = bob.getPosition().x;
        
        // the same thing but on the vertical Y axis
        startX = (int) bob.getBounds().x;
        endX = (int) (bob.getBounds().x + bob.getBounds().width);
        if (bob.getVelocity().y < 0) {
            startY = endY = (int) Math.floor(bob.getBounds().y + bob.getVelocity().y);
        } else {
            startY = endY = (int) Math.floor(bob.getBounds().y + bob.getBounds().height + bob.getVelocity().y);
        }
        
        populateCollidableBlocks(startX, startY, endX, endY);
        
        bobRect.y += bob.getVelocity().y;
        
        for (Block block : collidable) {
            if (block == null) continue;
            if (bobRect.overlaps(block.getBounds())) {
                if (bob.getVelocity().y < 0) {
                    grounded = true;
                }
                bob.getVelocity().y = 0;
                world.getCollisionRects().add(block.getBounds());
                break;
            }
        }
        // reset the collision box's position on Y
        bobRect.y = bob.getPosition().y;
        
        // update Bob's position
        bob.getPosition().add(bob.getVelocity());
        bob.getBounds().x = bob.getPosition().x;
        bob.getBounds().y = bob.getPosition().y;
        
        // un-scale velocity (not in frame time)
        bob.getVelocity().mul(1 / delta);
	}
	
	private void populateCollidableBlocks(int startX, int startY, int endX, int endY) {
	    collidable.clear();
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (x >= 0 && x < world.getLevel().getWidth() && y >=0 && y < world.getLevel().getHeight()) {
                    collidable.add(world.getLevel().get(x, y));
                }
            }
        }
}
	
	private boolean processInput() {
		if (keys.get(Keys.JUMP)) {
			if (!bob.getState().equals(State.JUMPING)) {
				jumpingPressed = true;
				jumpPressedTime = System.currentTimeMillis();
				bob.setState(State.JUMPING);
				bob.getVelocity().y = MAX_JUMP_SPEED;
			} else {
				if (jumpingPressed && ((System.currentTimeMillis() - jumpPressedTime) >= LONG_JUMP_PRESS)) {
					jumpingPressed = false;
				} else {
					if (jumpingPressed) {
						bob.getVelocity().y = MAX_JUMP_SPEED;
					}
				}
			}
		}
		if (keys.get(Keys.LEFT)) {
			bob.setFacingLeft(true);
			if (!bob.getState().equals(State.JUMPING)) {
				bob.setState(State.WALKING);
			}
			bob.getAcceleration().x = -ACCELERATION;
		} else if (keys.get(Keys.RIGHT)) {
			bob.setFacingLeft(false);
			if (!bob.getState().equals(State.JUMPING)) {
				bob.setState(State.WALKING);
			}
			bob.getAcceleration().x = ACCELERATION;
		} else {
			if (!bob.getState().equals(State.JUMPING)) {
				bob.setState(State.IDLE);
			}
			bob.getAcceleration().x = 0;
		}
		
		return false;
	}
}