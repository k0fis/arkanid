package kfs.arkanoid.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import kfs.arkanoid.KfsConst;
import kfs.arkanoid.KfsMain;
import kfs.arkanoid.World;
import kfs.arkanoid.outp.MusicManager;

public class GameScreen extends BaseScreen {

    private final Stage uiStage;
    private final World world;
    private final SpriteBatch batch;
    private final MusicManager musicManager;
    private final Texture background;
    private boolean paused = true;
    private boolean musicOn;
    private TextButton pauseBtn;
    private Label scoreLabel;

    public GameScreen(KfsMain game) {
        super(game);
        uiStage = new Stage(new FitViewport(KfsConst.WORLD_WIDTH, KfsConst.WORLD_HEIGHT));
        batch = new SpriteBatch();
        musicManager = game.getMusicManager();
        musicOn = musicManager.isPlaying();
        background = new Texture(Gdx.files.internal("background.png"));

        world = new World(KfsConst.WORLD_WIDTH, KfsConst.WORLD_HEIGHT, musicManager,
            this::setInfo, this::pauseAction);
        world.setGameOverCallback(this::onGameOver);

        createTopBar();
    }

    private void createTopBar() {
        Table root = new Table();
        root.setFillParent(true);
        uiStage.addActor(root);

        Table topBar = new Table();
        topBar.pad(6);
        topBar.defaults().pad(10);

        scoreLabel = new Label("Score: 0  Lives: 3", skin);

        TextButton musicBtn = new TextButton(musicOn ? "Music: ON" : "Music: OFF", skin);
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
            world.update(Math.min(delta, 0.05f));
        }

        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        batch.begin();
        batch.draw(background, 0, 0, KfsConst.WORLD_WIDTH, KfsConst.WORLD_HEIGHT);
        world.render(batch);

        if (paused) {
            blinkTimer += delta;
            Color color = (blinkTimer > 0.5) ? Color.WHITE : Color.ORANGE;
            if (blinkTimer > 1) {
                blinkTimer = 0;
            }
            Color c = fontSmall.getColor();
            fontSmall.setColor(color);
            fontSmall.draw(batch, "Press SPACE to play", 150, KfsConst.WORLD_HEIGHT - 40);
            fontSmall.setColor(c);
        }

        batch.end();

        uiStage.act(delta);
        uiStage.draw();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        super.dispose();
        uiStage.dispose();
        world.done();
        batch.dispose();
        background.dispose();
    }

    private void setInfo(String msg) {
        scoreLabel.setText(msg);
    }

    private void onGameOver(int finalScore) {
        game.setScreen(new GameOverScreen(game, finalScore));
    }

    public void pauseAction() {
        paused = !paused;
        pauseBtn.setText(paused ? "Resume" : "Pause");
    }
}
