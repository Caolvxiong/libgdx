/*
 * Copyright (c) 2008-2010, Matthias Mann
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution. * Neither the name of Matthias Mann nor
 * the names of its contributors may be used to endorse or promote products derived from this software without specific prior
 * written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.badlogic.gdx.twl.renderer;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.MouseCursor;
import de.matthiasmann.twl.renderer.Resource;
import de.matthiasmann.twl.renderer.Texture;

class GdxTexture implements Texture, Resource {
	public enum Filter {
		NEAREST(GL10.GL_NEAREST), LINEAR(GL10.GL_LINEAR);

		final int glValue;

		Filter (int value) {
			this.glValue = value;
		}
	}

	final TwlRenderer renderer;
	final com.badlogic.gdx.graphics.Texture texture;

	public GdxTexture (TwlRenderer renderer, String path) {
		this.renderer = renderer;
		if (path.charAt(0) == '/') path = path.substring(1);
		texture = Gdx.graphics.newTexture(Gdx.files.getFileHandle(path, FileType.Internal), TextureFilter.Linear,
			TextureFilter.Linear, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
	}

	public void destroy () {
		texture.dispose();
	}

	public int getWidth () {
		return texture.getWidth();
	}

	public int getHeight () {
		return texture.getHeight();
	}

	// BOZO
	boolean bind (Color color) {
		texture.bind();
		// renderer.tintStack.setColor(color);
		return true;
	}

	boolean bind () {
		texture.bind();
		return true;
	}

	public Image getImage (int x, int y, int width, int height, Color tintColor, boolean tiled) {
		if (x < 0 || x >= getWidth()) {
			throw new IllegalArgumentException("x");
		}
		if (y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException("y");
		}
		if (x + Math.abs(width) > getWidth()) {
			throw new IllegalArgumentException("width");
		}
		if (y + Math.abs(height) > getHeight()) {
			throw new IllegalArgumentException("height");
		}
		if (tiled && (width <= 0 || height <= 0)) {
			throw new IllegalArgumentException("Tiled rendering requires positive width & height");
		}
		return new TextureArea(this, x, y, width, height, tintColor, tiled);
	}

	public MouseCursor createCursor (int x, int y, int width, int height, int hotSpotX, int hotSpotY, Image imageRef) {
		return null;
	}

	public void themeLoadingDone () {
	}
}
