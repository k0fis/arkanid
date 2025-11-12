package kfs.arkanoid.sys;

import kfs.arkanoid.World;
import kfs.arkanoid.comp.PaddleComponent;
import kfs.arkanoid.comp.SizeComponent;
import kfs.arkanoid.comp.SurpriseActiveComponent;
import kfs.arkanoid.ecs.Entity;
import kfs.arkanoid.ecs.KfsSystem;

public class SurpriseActiveSystem implements KfsSystem {

    private final World world;

    public SurpriseActiveSystem(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        for (Entity e : world.getEntitiesWith(SurpriseActiveComponent.class)) {
            SurpriseActiveComponent sac = world.getComponent(e, SurpriseActiveComponent.class);
            sac.time += delta;
            if (sac.time > sac.timeMax) {
                doneSac(e, sac.inx);
                world.deleteEntity(e);
                continue;
            }
            if (!sac.init) {
                initSac(e, sac.inx);
                sac.init = true;
            }
        }
    }

    private void initSac(Entity sacEntity, int inx) {
        if (inx == 1) {
            // double paddle size
            for (Entity e : world.getEntitiesWith(PaddleComponent.class)) {
                SizeComponent sc = world.getComponent(e, SizeComponent.class);
                sc.width += 20;
            }
        } else if (inx == 2) {
            // double paddle speed
            for (Entity e : world.getEntitiesWith(PaddleComponent.class)) {
                PaddleComponent pc = world.getComponent(e, PaddleComponent.class);
                pc.speed += 50;
            }
        } else if (inx == 3) {
            // add ball
            world.createBall(false);
            world.deleteEntity(sacEntity);
        }
    }

    private void doneSac(Entity sacEntity, int inx) {
        if (inx == 1) {
            // double paddle size
            for (Entity e : world.getEntitiesWith(PaddleComponent.class)) {
                SizeComponent sc = world.getComponent(e, SizeComponent.class);
                sc.width -= 20;
                if (sc.width < 20) sc.width = 20;
            }
        } else if (inx == 2) {
            // double paddle speed
            for (Entity e : world.getEntitiesWith(PaddleComponent.class)) {
                PaddleComponent pc = world.getComponent(e, PaddleComponent.class);
                pc.speed -= 50;
            }
        }

    }

}
