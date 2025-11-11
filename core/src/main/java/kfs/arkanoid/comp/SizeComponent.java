package kfs.arkanoid.comp;

import kfs.arkanoid.ecs.KfsComp;

public class SizeComponent implements KfsComp {
    public float width;
    public float height;

    public SizeComponent(float width, float height) {
        this.width = width;
        this.height = height;
    }
}
