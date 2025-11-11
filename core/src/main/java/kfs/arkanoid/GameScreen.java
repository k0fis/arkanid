package kfs.arkanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen extends ScreenAdapter {

    private final Stage uiStage;
    private final Skin skin;
    private final World world;
    private final SpriteBatch batch;
    private boolean paused = false;
    private boolean musicOn = true;

    public GameScreen(float worldWidth, float worldHeight) {
        uiStage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(uiStage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        world = new World(worldWidth, worldHeight);
        //world.load("levels/level-1");

        createTopBar();
    }

    private void createTopBar() {
        Table root = new Table();
        root.setFillParent(true);
        uiStage.addActor(root);

        Table topBar = new Table();
        topBar.pad(6);
        topBar.defaults().pad(4);

        TextButton musicBtn = new TextButton("Music: ON", skin);
        musicBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                musicOn = !musicOn;
                musicBtn.setText(musicOn ? "Music: ON" : "Music: OFF");
            }
        });

        TextButton pauseBtn = new TextButton("Pause", skin);
        pauseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                paused = !paused;
                pauseBtn.setText(paused ? "Resume" : "Pause");
            }
        });

        TextButton exitBtn = new TextButton("Exit", skin);
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        topBar.add(musicBtn);
        topBar.add(pauseBtn);
        topBar.add(exitBtn);

        root.top();
        root.add(topBar).expandX().fillX();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!paused) {
            world.update(delta);
        }
        batch.begin();
        world.render(batch);
        batch.end();

        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
        world.setSize(width, height);
    }

    @Override
    public void dispose() {
        uiStage.dispose();
        world.done();
        batch.dispose();
    }
}
