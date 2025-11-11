package kfs.arkanoid.sys;

import kfs.arkanoid.World;
import kfs.arkanoid.comp.PositionComponent;
import kfs.arkanoid.comp.SizeComponent;
import kfs.arkanoid.comp.VelocityComponent;
import kfs.arkanoid.ecs.Entity;
import kfs.arkanoid.ecs.KfsSystem;
import kfs.arkanoid.ecs.KfsWorld;

public class WorldBoundsSystem implements KfsSystem {

    private final World world;

    public WorldBoundsSystem(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {

        float worldWidth = world.getWidth();
        float worldHeight = world.getHeight();

        for (Entity e : world.getEntitiesWith(PositionComponent.class, VelocityComponent.class, SizeComponent.class)) {
            PositionComponent pos = world.getComponent(e, PositionComponent.class);
            VelocityComponent vel = world.getComponent(e, VelocityComponent.class);
            SizeComponent size = world.getComponent(e, SizeComponent.class);

            // left / right
            if (pos.position.x < 0) {
                pos.position.x = 0;
                vel.velocity.x *= -1;
            } else if (pos.position.x + size.width > worldWidth) {
                pos.position.x = worldWidth - size.width;
                vel.velocity.x *= -1;
            }

            // top
            if (pos.position.y + size.height > worldHeight) {
                pos.position.y = worldHeight - size.height;
                vel.velocity.y *= -1;
            }

            // bottom: reset ball or emit event
            if (pos.position.y < 0) {
                pos.position.y = worldHeight / 2f;
                pos.position.x = worldWidth / 2f;
                vel.velocity.set(150, -150);
            }

        }
    }
}
