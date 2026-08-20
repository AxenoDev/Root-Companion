package me.axeno.root.entity.popup.impl;

import me.axeno.root.client.dialogue.DialogueManager;
import me.axeno.root.client.dialogue.RootDialogues;
import me.axeno.root.entity.RootEntity;
import me.axeno.root.entity.popup.RootPopup;

public class CadeauPopup extends RootPopup {
    public CadeauPopup() {
        super("root-gift", "Eh, j'ai un cadeau pour toi !", 0.2D);
    }

    @Override
    public void onClientTrigger(RootEntity entity) {
        DialogueManager.open(RootDialogues.GIVE_DIAMOND);
    }
}
