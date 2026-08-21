package me.axeno.root.entity.popup.impl;

import me.axeno.root.client.dialogue.DialogueManager;
import me.axeno.root.client.dialogue.RootDialogues;
import me.axeno.root.entity.RootEntity;
import me.axeno.root.entity.popup.RootPopup;

public class BonjourPopup extends RootPopup {
    public BonjourPopup() {
        super("oui-bonjour", "J'ai quelque chose a te dire...");
    }

    @Override
    public void onClientTrigger(RootEntity entity) {
        DialogueManager.open(RootDialogues.OUI_BONJOUR);
    }
}
