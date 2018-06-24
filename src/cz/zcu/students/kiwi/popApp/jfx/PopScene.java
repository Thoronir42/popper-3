package cz.zcu.students.kiwi.popApp.jfx;

import javafx.scene.Parent;
import javafx.scene.Scene;

public abstract class PopScene<TContent extends Parent> extends Scene {

    protected final TContent content;

    public PopScene(TContent root, double width, double height) {
        super(root, width, height);

        this.build(this.content = root);
    }

    protected abstract void build(TContent root);
}
