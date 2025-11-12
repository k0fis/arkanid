package kfs.arkanoid;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import kfs.arkanoid.comp.PaddleComponent;
import kfs.arkanoid.ecs.Entity;
import kfs.arkanoid.outp.MusicManager;


public class GameScreen extends ScreenAdapter {

    private final Stage uiStage;
    private final Skin skin;
    private final World world;
    private final BitmapFont font;
    private final SpriteBatch batch;
    private final MusicManager musicManager;
    private final Texture background;
    private boolean gameOver = false;
    private boolean paused = true;
    private boolean musicOn = false;
    private TextButton pauseBtn;
    private Label scoreLabel;

    public GameScreen(float worldWidth, float worldHeight) {
        uiStage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        musicManager = new MusicManager("music");
        Gdx.input.setInputProcessor(uiStage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        font = skin.getFont("font");
        background = new Texture(Gdx.files.internal("background.png"));

        world = new World(worldWidth, worldHeight, musicManager, this::gameOver, this::setInfo, this::pauseAction);

        createTopBar();

    }

    private void createTopBar() {
        Table root = new Table();
        root.setFillParent(true);
        uiStage.addActor(root);

        Table topBar = new Table();
        topBar.pad(6);
        topBar.defaults().pad(10);

        scoreLabel = new Label("Lives: " + PaddleComponent.DEFAULT_LIVES, skin);

        TextButton musicBtn = new TextButton("Music: OFF", skin);
        musicBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                musicOn = !musicOn;
                if (musicOn) {
                    musicManager.play();
                } else {
                    musicManager.stop();
                }
                musicBtn.setText(musicOn ? "Music: ON" : "Music: OFF");
            }
        });

        pauseBtn = new TextButton("Pause", skin);
        pauseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                pauseAction();
            }
        });

        topBar.add(scoreLabel);
        topBar.add(musicBtn);
        topBar.add(pauseBtn);

        if (Gdx.app.getType() != Application.ApplicationType.WebGL) {
            TextButton exitBtn = new TextButton("Exit", skin);
            exitBtn.addListener(new ClickListener() {
                @Override
                public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                    Gdx.app.exit();
                }
            });
            topBar.add(exitBtn);
        }

        root.top();
        root.add(topBar).expandX().fillX();
    }

    private float blinkTimer = 0f;

    @Override
    public void render(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            pauseAction();
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!paused) {
            world.update(delta);
        }
        batch.begin();
        batch.draw(background, 0, 0, world.getWidth(), world.getHeight());
        world.render(batch);

        if (paused){
            blinkTimer += delta;
            Color color = (blinkTimer > 0.5) ? Color.WHITE : Color.ORANGE;
            if (blinkTimer > 1) {
                blinkTimer = 0;
            }
            Color c = font.getColor();
            font.setColor(color);
            font.draw(batch, (gameOver?"Game OVER! ":"")+"Press SPACE to play", 30, world.getHeight() - 40);
            font.setColor(c);
        }

        batch.end();

        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        uiStage.dispose();
        world.done();
        batch.dispose();
        background.dispose();
        musicManager.dispose();
    }

    private void setInfo(String msg) {
        scoreLabel.setText(msg);
    }

    private void gameOver(String message) {
        setInfo(message);
        gameOver = true;
        paused = true;
    }


    public void pauseAction() {
        if (gameOver && paused) {
            int lives = 0;
            for (Entity e : world.getEntitiesWith(PaddleComponent.class)) {
                lives = world.getComponent(e, PaddleComponent.class).lives;
                break;
            }
            setInfo("Lives: " + lives);
        }
        paused = !paused;
        gameOver = false;
        pauseBtn.setText(paused ? "Resume" : "Pause");
    }
}
