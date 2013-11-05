package net.max.starassault.utils;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

public class TextureSetup {
	public static void main(String[] args) {
		TexturePacker2.process(
				"/home/max/work/ligdx/star-assault/star-assault-android/assets/images",
				"/home/max/work/ligdx/star-assault/star-assault-android/assets/images/textures",
				"texture.pack"
		);
	}
}
