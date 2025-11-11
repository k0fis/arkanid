package kfs.arkanoid;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class KfsMain extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }
}
