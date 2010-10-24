
package com.badlogic.gdx.tests;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputListener;
import com.badlogic.gdx.RenderListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Font;
import com.badlogic.gdx.graphics.Font.FontStyle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.SpriteBatch;
import com.badlogic.gdx.graphics.loaders.md5.MD5Animation;
import com.badlogic.gdx.graphics.loaders.md5.MD5AnimationInfo;
import com.badlogic.gdx.graphics.loaders.md5.MD5Joints;
import com.badlogic.gdx.graphics.loaders.md5.MD5Loader;
import com.badlogic.gdx.graphics.loaders.md5.MD5Model;
import com.badlogic.gdx.graphics.loaders.md5.MD5Renderer;

public class MD5Test implements RenderListener, InputListener {
	PerspectiveCamera camera;
	MD5Model model;
	MD5Animation anim;
	MD5AnimationInfo animInfo;
	MD5Joints skeleton;
	MD5Renderer renderer;
	SpriteBatch batch;
	Font font;

	@Override public void surfaceCreated () {
		if (model == null) {

			Gdx.app.log("MD5 Test", "created");
			model = MD5Loader.loadModel(Gdx.files.readFile("data/zfat.md5mesh", FileType.Internal));
			anim = MD5Loader.loadAnimation(Gdx.files.readFile("data/walk1.md5anim", FileType.Internal));
			skeleton = new MD5Joints();
			skeleton.joints = new float[anim.frames[0].joints.length];
			animInfo = new MD5AnimationInfo(anim.frames.length, anim.secondsPerFrame);
			renderer = new MD5Renderer(model, true);
			renderer.setSkeleton(model.baseSkeleton);

			// long start = System.nanoTime();
			// for( int i = 0; i < 100000; i++ )
			// renderer.setSkeleton( model.baseSkeleton );
			// app.log( "MD5 Test", "took: " + (System.nanoTime() - start ) / 1000000000.0 );

			camera = new PerspectiveCamera();
			camera.getPosition().set(0, 25, 100);
			camera.setFov(60);
			camera.setNear(1);
			camera.setFar(1000);
			camera.setViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

			batch = new SpriteBatch();
			font = Gdx.graphics.newFont("Arial", 12, FontStyle.Plain);
			Gdx.graphics.getGL10().glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			Gdx.input.addInputListener(this);
		}
	}

	@Override public void surfaceChanged (int width, int height) {

	}

	float angle = 0;

	@Override @SuppressWarnings("unused") public void render () {
		GL10 gl = Gdx.graphics.getGL10();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		camera.setMatrices();
		angle += Gdx.graphics.getDeltaTime() * 20;
		animInfo.update(Gdx.graphics.getDeltaTime());

		gl.glEnable(GL10.GL_DEPTH_TEST);
// gl.glPolygonMode( GL10.GL_FRONT_AND_BACK, GL10.GL_LINE );

		long start = 0;
		float renderTime = 0;
		float skinTime = 0;

		for (int z = 0; z < 50; z += 50) {
			gl.glLoadIdentity();
			gl.glTranslatef(0, 0, -z);
			gl.glRotatef(angle, 0, 1, 0);
			gl.glRotatef(-90, 1, 0, 0);

			start = System.nanoTime();
			MD5Animation.interpolate(anim.frames[animInfo.getCurrentFrame()], anim.frames[animInfo.getNextFrame()], skeleton,
				animInfo.getInterpolation());
			renderer.setSkeleton(skeleton);
			skinTime = (System.nanoTime() - start) / 1000000000.0f;

			start = System.nanoTime();
			renderer.render();
			renderTime = (System.nanoTime() - start) / 1000000000.0f;
		}

		gl.glDisable(GL10.GL_DEPTH_TEST);
// gl.glPolygonMode( GL10.GL_FRONT_AND_BACK, GL10.GL_FILL );
		batch.begin();
		batch.drawText(font, "fps: " + Gdx.graphics.getFramesPerSecond() + (renderer.isJniUsed() ? ", jni" : ", java"), 10, 20,
			Color.WHITE);
		batch.end();
	}

	@Override public void dispose () {
		batch.dispose();
		renderer.dispose();
		font.dispose();

		batch = null;
		renderer = null;
		font = null;

		System.gc();

		Gdx.input.removeInputListener(this);
	}

	@Override public boolean keyDown (int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override public boolean keyTyped (char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override public boolean keyUp (int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override public boolean touchDown (int x, int y, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override public boolean touchDragged (int x, int y, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override public boolean touchUp (int x, int y, int pointer) {
		renderer.setUseJni(!renderer.isJniUsed());
		return false;
	}

}
