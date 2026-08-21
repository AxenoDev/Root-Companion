package me.axeno.root.client.dialogue;

import net.minecraft.client.Minecraft;

public final class DialogueManager {
    public static void open(Dialogue dialogue) {
        Minecraft.getInstance().setScreen(new DialogueScreen(dialogue));
    }

    public static void close() {
        Minecraft.getInstance().setScreen(null);
    }

}
