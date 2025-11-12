package kfs.arkanoid.sys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import kfs.arkanoid.World;
import kfs.arkanoid.comp.PaddleComponent;
import kfs.arkanoid.comp.PositionComponent;
import kfs.arkanoid.comp.SizeComponent;
import kfs.arkanoid.comp.VelocityComponent;
import kfs.arkanoid.ecs.Entity;
import kfs.arkanoid.ecs.KfsSystem;

public class PaddleInputSystem implements KfsSystem {

    private final World world;
    private final Vector2 touchPos = new Vector2();

    public PaddleInputSystem(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        for (Entity paddle : world.getEntitiesWith(PaddleComponent.class)) {
            PaddleComponent pc = world.getComponent(paddle, PaddleComponent.class);
            PositionComponent pos = world.getComponent(paddle, PositionComponent.class);
            VelocityComponent vel = world.getComponent(paddle, VelocityComponent.class);
            SizeComponent size = world.getComponent(paddle, SizeComponent.class);

            vel.velocity.x = 0;

            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
                vel.velocity.x = -pc.speed;
            } else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
                vel.velocity.x = pc.speed;
            }

            // === Touch or Mouse drag ===
            if (Gdx.input.isTouched()) {
                touchPos.set(Gdx.input.getX(), Gdx.input.getY());
                float targetX = touchPos.x - size.width / 2f;
                float diff = targetX - pos.position.x;
                vel.velocity.x = diff * pc.speed/10;  // tweak multiplier for responsiveness
            }
            pos.position.x = Math.max(0, Math.min(world.getWidth() - size.width, pos.position.x));
        }
    }
}
