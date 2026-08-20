package me.axeno.root.client.dialogue;

public record DialogueButton(
        String label,
        DialogueButtonStyle style,
        IDialogueAction action
) {

    public static DialogueButton primary(String label, IDialogueAction action) {
        return new DialogueButton(label, DialogueButtonStyle.PRIMARY, action);
    }

    public static DialogueButton secondary(String label, IDialogueAction action) {
        return new DialogueButton(label, DialogueButtonStyle.SECONDARY, action);
    }

    public enum DialogueButtonStyle {
        PRIMARY,
        SECONDARY
    }

}
