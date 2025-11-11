package kfs.arkanoid.sys;

import com.badlogic.gdx.Gdx;
import kfs.arkanoid.World;
import kfs.arkanoid.comp.PaddleComponent;
import kfs.arkanoid.comp.PositionComponent;
import kfs.arkanoid.comp.SizeComponent;
import kfs.arkanoid.comp.VelocityComponent;
import kfs.arkanoid.ecs.Entity;
import kfs.arkanoid.ecs.KfsSystem;

public class PaddleInputSystem implements KfsSystem {

    private final World world;
    private final float speed = 300f;

    public PaddleInputSystem(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        for (Entity paddle : world.getEntitiesWith(PaddleComponent.class)) {
            PositionComponent pos = world.getComponent(paddle, PositionComponent.class);
            VelocityComponent vel = world.getComponent(paddle, VelocityComponent.class);
            SizeComponent size = world.getComponent(paddle, SizeComponent.class);

            vel.velocity.x = 0;

            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
                vel.velocity.x = -speed;
            } else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
                vel.velocity.x = speed;
            }

            pos.position.x = Math.max(0, Math.min(world.getWidth() - size.width, pos.position.x));
        }
    }
}
