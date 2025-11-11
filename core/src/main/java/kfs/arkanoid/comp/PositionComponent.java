package kfs.arkanoid.comp;

import com.badlogic.gdx.math.Vector2;
import kfs.arkanoid.ecs.KfsComp;

public class PositionComponent implements KfsComp {
    public final Vector2 position = new Vector2();

    public PositionComponent(float x, float y) {
        this.position.set(x, y);
    }
}
