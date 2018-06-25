package cz.zcu.students.kiwi.popApp.jfx;

import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.logging.Logger;

public abstract class PopScene<TContent extends Parent> extends Scene {

    protected final TContent content;

    protected Logger log;

    public PopScene(TContent root, double width, double height) {
        super(root, width, height);
        this.log = Logger.getLogger(getClass().getSimpleName());
        this.build(this.content = root);
    }

    protected abstract void build(TContent root);
}
