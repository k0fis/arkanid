package kfs.arkanoid.comp;

import com.badlogic.gdx.math.Vector2;
import kfs.arkanoid.ecs.KfsComp;

public class VelocityComponent implements KfsComp {
    public final Vector2 velocity = new Vector2();

    public VelocityComponent() {

    }

    public VelocityComponent(float x, float y) {
        velocity.set(x, y);
    }
}
