package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.print.attribute.standard.Media;

import sun.rmi.runtime.Log;

import static com.badlogic.gdx.Application.ApplicationType.Android;
import static com.badlogic.gdx.Input.Keys.MEDIA_PLAY_PAUSE;
import static com.badlogic.gdx.Input.Keys.R;

public class Mario extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manState = 0;
	int pause = 0;
	float gravity = 1.5f;
	float velocity = 0;
	int manY = 0;
	Rectangle manRectangle;
	BitmapFont font;
	BitmapFont font1;
	Texture dizzy;
	String instruct;


	int score = 0;
	int gameState = 0;

	Random random;

	ArrayList <Integer> coinXs = new ArrayList <>();
	ArrayList<Integer> coinYs = new ArrayList <>();
	ArrayList<Rectangle> coinRectangles = new ArrayList <>();
	Texture coin;
	int coinCount;

	ArrayList<Integer> bombXs = new ArrayList <>();
	ArrayList<Integer> bombYs = new ArrayList <>();
	ArrayList<Rectangle> bombRectangles = new ArrayList <>();
	Texture bomb;
	int bombCount;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		manY = Gdx.graphics.getHeight() / 2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();
		dizzy = new Texture("dizzy-1.png");

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		font1 = new BitmapFont();
		font1.setColor(Color.WHITE);
		font1.getData().setScale(10);

	}

	public void makeCoin(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int) height);
		coinXs.add(Gdx.graphics.getWidth());
	}
	public void makeBomb(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int) height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		if(gameState == 1){
			// Game is live

			// bomb
			if(bombCount < 250){
				bombCount++;
			}else {
				bombCount = 0;
				makeBomb();
			}
			bombRectangles.clear();
			for (int i = 0;i < bombXs.size(); i++){
				batch.draw(bomb,bombXs.get(i),bombYs.get(i));
				bombXs.set(i,bombXs.get(i) -8);
				bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			//coins
			if(coinCount < 100){
				coinCount++;
			}else {
				coinCount = 0;
				makeCoin();
			}
			coinRectangles.clear();
			for (int i = 0;i < coinXs.size(); i++){
				batch.draw(coin,coinXs.get(i),coinYs.get(i));
				coinXs.set(i,coinXs.get(i) -4);
				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			if(Gdx.input.justTouched()){
				velocity = -35;
			}

			if(pause < 5){
				pause++;
			}else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			velocity += gravity;
			manY -= velocity;
			if(manY <= 100){
				manY = 90;
			}

		}else if(gameState == 0){
			// Game is dead
            font1.draw(batch,"Start",Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2,Gdx.graphics.getHeight() / 2 );
			if(Gdx.input.justTouched()){
				gameState =1;

			}
		}else if(gameState == 2){
			//Game Over
			font1.draw(batch,"Retry",Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2,Gdx.graphics.getHeight() / 2 );
			if(Gdx.input.justTouched()){
				gameState =1;
			}
			//	manY = Gdx.graphics.getHeight() / 2;
			score = 0;
			velocity = 0;
			coinXs.clear();
			coinYs.clear();
			coinRectangles.clear();
			coinCount = 0;

			bombXs.clear();
			bombYs.clear();
			bombRectangles.clear();
			bombCount = 0;


		}

		if(gameState == 2){
			batch.draw(dizzy,Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2,manY);
		}else{
			batch.draw(man[manState],Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2,manY);
		}


		manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2,manY,man[manState].getWidth(),man[manState].getHeight());

		for(int i = 0; i < coinRectangles.size(); i++) {
			if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
				//Gdx.app.log("Coin !","Collision!");
				score++;
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);

				/*URL url = null;
				try {
					File file = new File("coineffect.wav");
					if (file.canRead())
						url = file.toURI().toURL();
				} catch (MalformedURLException e) {
					throw new IllegalArgumentException(e);
				}

				// URL url = StdAudio.class.getResource(filename);
				if (url == null) {
					throw new IllegalArgumentException();
				}

				AudioClip clip = Applet.newAudioClip(url);
				clip.play();*/
			}
		}
		for(int i = 0; i < bombRectangles.size(); i++) {
			if(Intersector.overlaps(manRectangle,bombRectangles.get(i))){
				Gdx.app.log("Bombs!","Collision!");
				gameState = 2;
			}
		}

		font.draw(batch,String.valueOf(score),100,200);

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}

}
