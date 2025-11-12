package kfs.arkanoid.sys;

import kfs.arkanoid.World;
import kfs.arkanoid.comp.BrickComponent;
import kfs.arkanoid.ecs.KfsSystem;

public class LevelCheckerSystem implements KfsSystem {

    private final World world;
    public LevelCheckerSystem(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        if (world.getEntitiesWith(BrickComponent.class).isEmpty()) {
            world.newLevel();
        }
    }
}
