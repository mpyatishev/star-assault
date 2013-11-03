/**
 * 
 */
package net.max.starassault;

import net.max.starassault.screens.GameScreen;

import com.badlogic.gdx.Game;

/**
 * @author max
 *
 */
public class StarAssault extends Game {

	@Override
	public void create() {
		setScreen(new GameScreen());
	}
}
