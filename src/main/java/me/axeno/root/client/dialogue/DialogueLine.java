package me.axeno.root.client.dialogue;

import java.util.List;

public record DialogueLine(
        String speaker,
        String text,
        Runnable action,
        List<DialogueButton> buttons
) {

    public DialogueLine(String speaker, String text) {
        this(speaker, text, null, List.of());
    }

    public DialogueLine(String speaker, String text, Runnable action) {
        this(speaker, text, action, List.of());
    }

    public List<DialogueButton> resolvedButtons(IDialogueAction defaultNextAction) {
        if (!buttons.isEmpty()) {
            return buttons;
        }
        return List.of(DialogueButton.primary("CONTINUER", defaultNextAction));
    }
}
