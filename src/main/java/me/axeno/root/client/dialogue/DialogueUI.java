package me.axeno.root.client.dialogue;

public final class DialogueUI {

    private final Dialogue dialogue;

    private int currentLine;

    public DialogueUI(Dialogue dialogue) {
        this.dialogue = dialogue;
        this.currentLine = 0;
    }

    public Dialogue dialogue() {
        return dialogue;
    }

    public DialogueLine currentLine() {
        return dialogue.getLines().get(currentLine);
    }

    public int currentLineIndex() {
        return currentLine;
    }

    public boolean isLastLine() {
        return currentLine >= dialogue.getLines().size() - 1;
    }

    /**
     * Passe à la ligne suivante.
     *
     * @return true si le dialogue est terminé.
     */
    public boolean next() {

        DialogueLine line = currentLine();

        if (line.action() != null) {
            line.action().run();
        }

        if (isLastLine()) {
            return true;
        }

        currentLine++;

        return false;
    }
}