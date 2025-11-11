package kfs.arkanoid.sys;

import com.badlogic.gdx.math.Vector2;
import kfs.arkanoid.World;
import kfs.arkanoid.comp.PositionComponent;
import kfs.arkanoid.comp.VelocityComponent;
import kfs.arkanoid.ecs.Entity;
import kfs.arkanoid.ecs.KfsSystem;

public class MovementSystem implements KfsSystem {

    private final World world;

    public MovementSystem(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        for (Entity e : world.getEntitiesWith(PositionComponent.class, VelocityComponent.class)) {
            PositionComponent pos = world.getComponent(e, PositionComponent.class);
            VelocityComponent vel = world.getComponent(e, VelocityComponent.class);

            pos.position.mulAdd(vel.velocity, new Vector2(delta, delta));
        }
    }
}
