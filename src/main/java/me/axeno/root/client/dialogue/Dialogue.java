package me.axeno.root.client.dialogue;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Dialogue {

    @Getter
    private final String id;
    @Getter
    private final DialoguePortrait portrait;
    @Getter
    private final DialogueAnimation animation;
    @Getter
    private final List<DialogueLine> lines;

    private Dialogue(String id, DialoguePortrait portrait, DialogueAnimation animation, List<DialogueLine> lines) {
        this.id = id;
        this.portrait = portrait;
        this.animation = animation;
        this.lines = List.copyOf(lines);
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static final class Builder {
        private final String id;
        private DialoguePortrait portrait;
        private DialogueAnimation animation = DialogueAnimation.NONE;
        private final List<DialogueLine> lines = new ArrayList<>();

        private Builder(String id) {
            this.id = Objects.requireNonNull(id);
        }

        public Builder portrait(DialoguePortrait portrait) {
            this.portrait = portrait;
            return this;
        }

        public Builder portrait(ResourceLocation texture) {
            this.portrait = new DialoguePortrait(texture);
            return this;
        }

        public Builder animation(DialogueAnimation animation) {
            this.animation = animation;
            return this;
        }

        public Builder line(String speaker, String text) {
            lines.add(new DialogueLine(speaker, text));
            return this;
        }

        public Builder line(String speaker, String text, Runnable action) {
            lines.add(new DialogueLine(speaker, text, action));
            return this;
        }

        public Dialogue build() {
            if (lines.isEmpty()) {
                throw new IllegalStateException("Dialogue '" + id + "' has no lines.");
            }
            return new Dialogue(id, portrait, animation, lines);
        }
    }
}
