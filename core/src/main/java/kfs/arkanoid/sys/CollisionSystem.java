package kfs.arkanoid.sys;

import com.badlogic.gdx.math.Rectangle;
import kfs.arkanoid.World;
import kfs.arkanoid.comp.*;
import kfs.arkanoid.ecs.Entity;
import kfs.arkanoid.ecs.KfsSystem;

public class CollisionSystem implements KfsSystem {

    private final World world;

    public CollisionSystem(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        for (Entity ball: world.getEntitiesWith(BallComponent.class)) {

            PositionComponent pos = world.getComponent(ball, PositionComponent.class);
            VelocityComponent vel = world.getComponent(ball, VelocityComponent.class);
            SizeComponent size = world.getComponent(ball, SizeComponent.class);

            Rectangle ballRect = new Rectangle(pos.position.x, pos.position.y, size.width, size.height);

            for (Entity brick : world.getEntitiesWith(BrickComponent.class)) {
                PositionComponent brickPos = world.getComponent(brick, PositionComponent.class);
                SizeComponent brickSize = world.getComponent(brick, SizeComponent.class);
                BrickComponent brickHP = world.getComponent(brick, BrickComponent.class);

                Rectangle brickRect = new Rectangle(brickPos.position.x, brickPos.position.y, brickSize.width, brickSize.height);

                if (ballRect.overlaps(brickRect)) {
                    vel.velocity.y *= -1;
                    brickHP.hitPoints -= 1;
                    if (brickHP.hitPoints <= 0) {
                        world.deleteEntity(brick);
                        world.spawnBurst(brickPos.position.x + brickSize.width/2f,
                            brickPos.position.y + brickSize.height/2f, 12);
                    }
                }
            }

            for (Entity paddle : world.getEntitiesWith(PaddleComponent.class)) {
                PositionComponent padPos = world.getComponent(paddle, PositionComponent.class);
                SizeComponent padSize = world.getComponent(paddle, SizeComponent.class);

                Rectangle padRect = new Rectangle(padPos.position.x, padPos.position.y, padSize.width, padSize.height);
                if (ballRect.overlaps(padRect)) {
                    vel.velocity.y *= -1;
                }
            }
        }
    }
}
