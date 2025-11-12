package kfs.arkanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import kfs.arkanoid.comp.*;
import kfs.arkanoid.ecs.Entity;
import kfs.arkanoid.ecs.KfsWorld;
import kfs.arkanoid.outp.MusicManager;
import kfs.arkanoid.sys.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class World extends KfsWorld {
    private static final String PARTICLE = "particle";
    private static final String BALL = "ball";
    private static final String BRICK = "brick";
    private static final String PADDLE = "paddle";
    private static final String SURPRISE_1 = "surprise-1";
    private static final String SURPRISE_2 = "surprise-2";
    private static final String SURPRISE_3 = "surprise-3";


    private static final int BRICK_TOP = 85;
    public static final int BRICK_WIDTH = 48;
    private static final int BRICK_HEIGHT = 20;
    private static final int BRICK_SPACE = 4;
    private static final int BALL_SIZE = 12;

    private final Random random = new Random();

    private final Map<String, Texture> textures;

    private final float height;
    private final float width;
    private final Consumer<String> gameOverAction;
    private final Consumer<String> setInfoAction;
    private final Runnable pauseAction;
    private boolean gameOver1 = false;
    private boolean level1 = false;
    private final BrickTextAnimationSystem brickTextAnimationSystem;

    public World(float width, float height, MusicManager music,
                 Consumer<String> gameOver, Consumer<String> setInfoAction, Runnable pauseAction) {
        this.textures = new HashMap<>();
        this.height = height;
        this.width = width;
        this.gameOverAction = gameOver;
        this.setInfoAction = setInfoAction;
        this.pauseAction = pauseAction;

        textures.put(PARTICLE, generateParticle(5));
        textures.put(BALL, new Texture(Gdx.files.internal("textures/ball.png")));
        textures.put(BRICK+"_1", new Texture(Gdx.files.internal("textures/brick-orange.png")));
        textures.put(BRICK+"_2", new Texture(Gdx.files.internal("textures/brick-blue.png")));
        textures.put(PADDLE, new Texture(Gdx.files.internal("textures/paddle.png")));
        textures.put(SURPRISE_1, new Texture(Gdx.files.internal("textures/surprise-1.png")));
        textures.put(SURPRISE_2, new Texture(Gdx.files.internal("textures/surprise-2.png")));
        textures.put(SURPRISE_3, new Texture(Gdx.files.internal("textures/surprise-3.png")));

        addSys(new CollisionSystem(this, music));
        addSys(new LevelCheckerSystem(this));
        addSys(new MovementSystem(this));
        addSys(new PaddleInputSystem(this));
        addSys(new ParticleSystem(this));
        addSys(new RenderSystem(this));
        addSys(new SurpriseActiveSystem(this));
        addSys(new WorldBoundsSystem(this));

        createPaddle();
        createBall(false);

        brickTextAnimationSystem = new BrickTextAnimationSystem(this);

        newWall();
    }

    @Override
    public void done() {
        super.done();
        textures.values().forEach(Texture::dispose);
    }

    public Texture getTexture(String name) {
        Texture out = textures.get(name);
        if (out == null) {
            Gdx.app.error("World", "Texture: " + name + " is not loaded!");
        }
        return out;
    }

    public void createPaddle() {
        for (Entity e : getEntitiesWith(PaddleComponent.class)) {
            deleteEntity(e);
        }
        Entity e = createEntity();
        addComponent(e, new PositionComponent(width / 2f - 40, 40));
        addComponent(e, new SizeComponent(80, 16));
        addComponent(e, new VelocityComponent());
        addComponent(e, new PaddleComponent());
        addComponent(e, new RenderComponent(PADDLE));
    }

    public void createBall(boolean removeOld) {
        if (removeOld) {
            for (Entity e : getEntitiesWith(BallComponent.class)) {
                deleteEntity(e);
            }
        }
        Entity e = createEntity();
        addComponent(e, new PositionComponent(width / 2f,height / 2f));
        addComponent(e, new SizeComponent(BALL_SIZE, BALL_SIZE));
        addComponent(e, new VelocityComponent(150, 150));
        addComponent(e, new BallComponent());
        addComponent(e, new RenderComponent(BALL));
    }

    public void createSurprise1(float x, float y) {
        createSurprise(x,y, 24, 19, SURPRISE_1, 1);
    }

    public void createSurprise2(float x, float y) {
        createSurprise(x,y, 24, 21, SURPRISE_2, 2);
    }

    public void createSurprise3(float x, float y) {
        createSurprise(x,y, 24, 21, SURPRISE_3, 3);
    }

    public void createSurprise(float x, float y, float width, float height, String texture, int inx) {
        Entity e = createEntity();
        addComponent(e, new PositionComponent(x,y));
        addComponent(e, new SizeComponent(width, height));
        addComponent(e, new VelocityComponent(0, -200));
        addComponent(e, new SurpriseComponent(inx));
        addComponent(e, new RenderComponent(texture));
    }

    public void createBrick(float x, float y, float w, float h, int hp, String renderName) {
        Entity e = createEntity();
        addComponent(e, new PositionComponent(x,y));
        addComponent(e, new SizeComponent(w, h));
        addComponent(e, new BrickComponent(hp, random.nextInt(20)));
        addComponent(e, new RenderComponent(renderName));
    }

    public void spawnBurst(float x, float y, int count) {
        for (int i = 0; i < count; i++) {
            Entity p = createEntity();
            addComponent(p, new PositionComponent(x, y));

            float angle = (float) (Math.random() * Math.PI * 2);
            float speed = 60 + (float) Math.random() * 60;

            addComponent(p, new VelocityComponent((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed));

            ParticleComponent pc = new ParticleComponent();
            pc.lifetime = 0.5f + (float) Math.random() * 0.3f;
            pc.startSpeed = speed;

            addComponent(p, pc);

            addComponent(p, new SizeComponent(4, 4));
            addComponent(p, new RenderComponent(PARTICLE));
        }
    }



    public static Texture generateParticle(int size) {
        Pixmap pm = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fillRectangle(0, 0, size, size);
        Texture tex = new Texture(pm);
        pm.dispose();
        return tex;
    }

    public void newWall() {
        int colCount = (int)(width / (BRICK_WIDTH  + BRICK_SPACE)) - 1;
        int rowCount = 3+random.nextInt(5);

        float startX = (width - colCount * (BRICK_WIDTH  + BRICK_SPACE)) /2f;
        float startY = height - BRICK_TOP;

        int brickInx = 0;
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                float x = startX + col * (BRICK_WIDTH + BRICK_SPACE);
                float y = startY - row * (BRICK_HEIGHT + BRICK_SPACE);
                createBrick(x, y, BRICK_WIDTH, BRICK_HEIGHT,
                    1+random.nextInt(4),
                    (brickInx%2==0)?BRICK+"_1":BRICK+"_2");
                brickInx++;
            }
        }
    }

    @Override
    public void update(float delta) {
        if (gameOver1 || level1) {
            brickTextAnimationSystem.update(delta);
        } else {
            super.update(delta);
        }
        if (brickTextAnimationSystem.isTimeout(6f)) {
            if (gameOver1) {
                for (Entity brick : getEntitiesWith(BrickComponent.class)) {
                    deleteEntity(brick);
                }
                createPaddle();
                createBall(true);
                newWall();
                gameOverAction.accept("Game Over");
            }
            if (level1) {
                createBall(true);
                for (Entity paddleEntity : getEntitiesWith(PaddleComponent.class)) {
                    PositionComponent pc = getComponent(paddleEntity, PositionComponent.class);
                    pc.position.x = width / 2f - 40;
                    pc.position.y = 40;
                }
                newWall();
                pauseAction.run();
            }
            gameOver1 = false;
            level1 = false;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (gameOver1 || level1) {
            brickTextAnimationSystem.render(batch);
        } else {
            super.render(batch);
        }
    }

    public void setInfo(String info) {
        setInfoAction.accept(info);
    }

    public void gameOver() {
        this.gameOver1 = true;
        brickTextAnimationSystem.setMessage("Game Over");
    }

    public void newLevel() {
        this.level1 = true;
        brickTextAnimationSystem.setMessage("Level done");
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
