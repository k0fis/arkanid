package kfs.arkanoid;

import com.badlogic.gdx.Game;
import kfs.arkanoid.outp.MusicManager;
import kfs.arkanoid.ui.MainScreen;

public class KfsMain extends Game {

    private MusicManager musicManager;

    @Override
    public void create() {
        musicManager = new MusicManager("music");
        setScreen(new MainScreen(this));
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (musicManager != null) {
            musicManager.dispose();
        }
    }
}
