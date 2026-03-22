package kfs.arkanoid.sys;

import com.badlogic.gdx.math.Rectangle;
import kfs.arkanoid.World;
import kfs.arkanoid.comp.*;
import kfs.arkanoid.ecs.Entity;
import kfs.arkanoid.ecs.KfsSystem;
import kfs.arkanoid.outp.MusicManager;

public class CollisionSystem implements KfsSystem {

    private final World world;
    private final MusicManager musicManager;

    public CollisionSystem(World world, MusicManager musicManager) {
        this.world = world;
        this.musicManager = musicManager;
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
                    int hpBefore = brickHP.hitPoints;
                    brickHP.hitPoints -= 1;
                    musicManager.playBrickBreakSound();
                    if (brickHP.hitPoints <= 0) {
                        // Score based on original HP: 100/200/300/500
                        int[] brickScores = {0, 100, 200, 300, 500};
                        int pts = hpBefore >= 1 && hpBefore <= 4 ? brickScores[hpBefore] : 100;
                        world.addScore(pts);
                        world.deleteEntity(brick);
                        world.spawnBurst(brickPos.position.x + brickSize.width/2f,
                            brickPos.position.y + brickSize.height/2f, 12);
                        if (brickHP.surpriseIndex == 1) {
                            world.createSurprise1(World.BRICK_WIDTH / 2f + brickPos.position.x, brickPos.position.y);
                        } else if (brickHP.surpriseIndex == 2) {
                            world.createSurprise2(World.BRICK_WIDTH / 2f + brickPos.position.x, brickPos.position.y);
                        } else if (brickHP.surpriseIndex == 3) {
                            world.createSurprise3(World.BRICK_WIDTH / 2f + brickPos.position.x, brickPos.position.y);
                        }

                    } else {
                        world.addScore(10);
                        world.spawnBurst(brickPos.position.x + brickSize.width/2f,
                            brickPos.position.y + brickSize.height/2f, 12/brickHP.hitPoints);

                    }
                }
            }

            for (Entity paddle : world.getEntitiesWith(PaddleComponent.class)) {
                PositionComponent padPos = world.getComponent(paddle, PositionComponent.class);
                SizeComponent padSize = world.getComponent(paddle, SizeComponent.class);

                Rectangle padRect = new Rectangle(padPos.position.x, padPos.position.y, padSize.width, padSize.height);
                if (ballRect.overlaps(padRect)) {
                    vel.velocity.y *= -1;
                    musicManager.playBounceSound();
                }
            }
        }
        for (Entity surprise: world.getEntitiesWith(SurpriseComponent.class)) {
            PositionComponent pos = world.getComponent(surprise, PositionComponent.class);
            SizeComponent size = world.getComponent(surprise, SizeComponent.class);
            Rectangle surpriseRect = new Rectangle(pos.position.x, pos.position.y, size.width, size.height);

            for (Entity paddle : world.getEntitiesWith(PaddleComponent.class)) {
                PositionComponent padPos = world.getComponent(paddle, PositionComponent.class);
                SizeComponent padSize = world.getComponent(paddle, SizeComponent.class);

                Rectangle padRect = new Rectangle(padPos.position.x, padPos.position.y, padSize.width, padSize.height);
                if (surpriseRect.overlaps(padRect)) {
                    musicManager.playSurpriseSound();
                    world.addScore(50);
                    int surpriseIndex = world.getComponent(surprise, SurpriseComponent.class).inx;
                    world.deleteEntity(surprise);
                    world.addComponent(world.createEntity(), new SurpriseActiveComponent(surpriseIndex));
                }
            }
        }
    }
}
