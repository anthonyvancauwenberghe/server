package org.hyperion.rs2.packet;

import java.util.HashMap;

public class ActionsManager {

    private static final ActionsManager manager = new ActionsManager();
    private final HashMap<Integer, ButtonAction> buttonActions = new HashMap<Integer, ButtonAction>();

    public static ActionsManager getManager() {
        return manager;
    }

    public void submit(final int button, final ButtonAction action) {
        buttonActions.put(button, action);
    }

    public ButtonAction getButtonAction(final int id) {
        return buttonActions.get(id);
    }

}
