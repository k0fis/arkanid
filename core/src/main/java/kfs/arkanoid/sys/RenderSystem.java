package kfs.arkanoid.sys;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import kfs.arkanoid.World;
import kfs.arkanoid.comp.PositionComponent;
import kfs.arkanoid.comp.RenderComponent;
import kfs.arkanoid.comp.SizeComponent;
import kfs.arkanoid.ecs.Entity;
import kfs.arkanoid.ecs.KfsSystem;

public class RenderSystem implements KfsSystem {

    private final World world;

    public RenderSystem(World world) {
        this.world = world;
    }

    @Override
    public void render(SpriteBatch batch) {
        for (Entity e : world.getEntitiesWith(RenderComponent.class, PositionComponent.class, SizeComponent.class)) {
            PositionComponent pos = world.getComponent(e, PositionComponent.class);
            RenderComponent ren = world.getComponent(e, RenderComponent.class);
            SizeComponent size = world.getComponent(e, SizeComponent.class);

            batch.draw(world.getTexture(ren.textureName), pos.position.x, pos.position.y, size.width, size.height);
        }
    }
}
