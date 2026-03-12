package org.seal.mindustry.timeFactor.ui;

import arc.scene.Element;

public interface UIComponent<E extends Element> {
    E render();
}
