package kfs.arkanoid.sys;

import kfs.arkanoid.World;
import kfs.arkanoid.comp.ParticleComponent;
import kfs.arkanoid.comp.PositionComponent;
import kfs.arkanoid.comp.VelocityComponent;
import kfs.arkanoid.ecs.Entity;
import kfs.arkanoid.ecs.KfsSystem;

public class ParticleSystem implements KfsSystem {

    private final World world;

    public ParticleSystem(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        for (Entity e : world.getEntitiesWith(ParticleComponent.class)) {
            ParticleComponent pc = world.getComponent(e, ParticleComponent.class);
            PositionComponent pos = world.getComponent(e, PositionComponent.class);
            VelocityComponent vel = world.getComponent(e, VelocityComponent.class);

            pc.age += delta;
            if (pc.age >= pc.lifetime) {
                world.deleteEntity(e);
                continue;
            }

            pos.position.mulAdd(vel.velocity, delta);
        }
    }
}
