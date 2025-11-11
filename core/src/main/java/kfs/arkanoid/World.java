package kfs.arkanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import kfs.arkanoid.comp.*;
import kfs.arkanoid.ecs.Entity;
import kfs.arkanoid.ecs.KfsWorld;
import kfs.arkanoid.sys.*;

import java.util.HashMap;
import java.util.Map;

public class World extends KfsWorld {
    private static final String PARTICLE = "particle";
    private static final String BALL = "ball";
    private static final String BRICK = "brick";
    private static final String PADDLE = "paddle";

    private final Map<String, Texture> textures;

    private float height;
    private float width;

    public World(float width, float height) {
        this.textures = new HashMap<>();
        this.height = height;
        this.width = width;

        textures.put(PARTICLE, generateParticle(5));
        textures.put(BALL, generateBall(12));
        textures.put(BRICK, generateBrick(48, 20, Color.RED));
        textures.put(PADDLE, generatePaddle(80, 16));


        addSys(new CollisionSystem(this));
        addSys(new MovementSystem(this));
        addSys(new PaddleInputSystem(this));
        addSys(new ParticleSystem(this));
        addSys(new RenderSystem(this));
        addSys(new WorldBoundsSystem(this));

        createPaddle(width / 2f - 40, 40, 80, 16);
        createBall( width / 2f, height / 2f, 12);

        // bricks grid
        float brickW = 48;
        float brickH = 20;
        float startX = 30;
        float startY = height - 80;

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 10; col++) {
                float x = startX + col * (brickW + 4);
                float y = startY - row * (brickH + 4);
                createBrick(x, y, brickW, brickH, 1);
            }
        }
    }

    public void load(String jsonPath) {
        for (Entity e : getEntitiesWith(BrickComponent.class)) deleteEntity(e);

        FileHandle file = Gdx.files.internal(jsonPath);
        JsonValue root = new JsonReader().parse(file);

        float brickW = root.getFloat("brickWidth");
        float brickH = root.getFloat("brickHeight");
        float padding = root.getFloat("padding");
        float startX = root.getFloat("startX");
        float startY = root.getFloat("startY");

        JsonValue bricks = root.get("bricks");
        for (JsonValue brick : bricks) {
            int row = brick.getInt("row");
            int col = brick.getInt("col");
            int hp = brick.getInt("hp", 1);

            float x = startX + col * (brickW + padding);
            float y = startY - row * (brickH + padding);

            createBrick(x, y, brickW, brickH, hp);
        }
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

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void createPaddle(float x, float y, float w, float h) {
        Entity e = createEntity();
        addComponent(e, new PositionComponent(x,y));
        addComponent(e, new SizeComponent(w, h));
        addComponent(e, new VelocityComponent());
        addComponent(e, new PaddleComponent());
        addComponent(e, new RenderComponent("paddle"));
    }

    public void createBall(float x, float y, float size) {
        Entity e = createEntity();
        addComponent(e, new PositionComponent(x,y));
        addComponent(e, new SizeComponent(size, size));
        addComponent(e, new VelocityComponent(150, 150));
        addComponent(e, new BallComponent());
        addComponent(e, new BounceComponent());
        addComponent(e, new RenderComponent("ball"));
    }

    public void createBrick(float x, float y, float w, float h, int hp) {
        Entity e = createEntity();
        addComponent(e, new PositionComponent(x,y));
        addComponent(e, new SizeComponent(w, h));
        addComponent(e, new BrickComponent(hp));
        addComponent(e, new RenderComponent("brick"));
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


    Texture generateBall(int size) {
        Pixmap pm = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fillCircle(size/2, size/2, size/2);
        Texture tex = new Texture(pm);
        pm.dispose();
        return tex;
    }

    public static Texture generateBrick(int w, int h, Color color) {
        Pixmap pm = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pm.setColor(color);
        pm.fillRectangle(0, 0, w, h);
        pm.setColor(Color.BLACK);
        pm.drawRectangle(0, 0, w, h);
        Texture tex = new Texture(pm);
        pm.dispose();
        return tex;
    }

    public static Texture generatePaddle(int w, int h) {
        Pixmap pm = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pm.setColor(Color.GRAY);
        pm.fillRectangle(0, 0, w, h);
        pm.setColor(Color.DARK_GRAY);
        pm.drawRectangle(0, 0, w, h);
        Texture tex = new Texture(pm);
        pm.dispose();
        return tex;
    }

    public static Texture generateParticle(int size) {
        Pixmap pm = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fillRectangle(0, 0, size, size);
        Texture tex = new Texture(pm);
        pm.dispose();
        return tex;
    }
}
