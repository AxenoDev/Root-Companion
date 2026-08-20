package me.axeno.root.client.dialogue;

import net.minecraft.resources.ResourceLocation;

public record DialoguePortrait(
        ResourceLocation texture,
        float width,
        float height
) {

    public DialoguePortrait(ResourceLocation texture) {
        this(texture, 769f, 864f);
    }
}