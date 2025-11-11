package kfs.arkanoid.comp;

import kfs.arkanoid.ecs.KfsComp;

public class RenderComponent implements KfsComp {
    public String textureName;

    public RenderComponent(String textureName) {
        this.textureName = textureName;
    }
}
