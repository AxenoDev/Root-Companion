package me.axeno.root.client.dialogue;

import java.util.Objects;

public record DialogueLine(
        String speaker,
        String text,
        Runnable action
) {

    public DialogueLine {
        Objects.requireNonNull(speaker, "speaker");
        Objects.requireNonNull(text, "text");
    }

    public DialogueLine(String speaker, String text) {
        this(speaker, text, null);
    }

}
