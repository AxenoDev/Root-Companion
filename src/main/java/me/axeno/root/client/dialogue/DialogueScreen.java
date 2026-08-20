package me.axeno.root.client.dialogue;

import me.axeno.noctisui.client.NoctisUIClient;
import me.axeno.noctisui.client.api.system.render.font.FontAtlas;
import me.axeno.noctisui.client.component.Button;
import me.axeno.noctisui.client.component.DivComponent;
import me.axeno.noctisui.client.component.ImageComponent;
import me.axeno.noctisui.client.component.TextComponent;
import me.axeno.noctisui.client.utils.Color;
import me.axeno.root.client.RootClient;
import me.axeno.root.client.ui.UiMetrics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class DialogueScreen extends Screen {

    private static final long TYPEWRITER_CHAR_DELAY_MS = 25L;
    private static final long FADE_DURATION_MS = 250L;

    private final DialogueUI dialogueUI;
    private UiMetrics metrics;
    private DivComponent panel;
    private DivComponent textContainer;
    private Button continueButton;
    private ImageComponent portrait;
    private FontAtlas regularFont;
    private FontAtlas boldFont;

    private long lineStartTime;
    private boolean lineFullyRevealed;
    private final List<AnimatedLine> animatedLines = new ArrayList<>();

    private record AnimatedLine(TextComponent component, String fullText, int charOffset) {
    }

    public DialogueScreen(Dialogue dialogue) {
        super(Component.literal(dialogue.getId()));
        this.dialogueUI = new DialogueUI(dialogue);
    }

    @Override
    protected void init() {
        this.metrics = new UiMetrics(this.width, this.height);
        this.regularFont = NoctisUIClient.getInstance().getFonts().getInterMedium();
        this.boldFont = RootClient.getFonts().getPixelNes();
        buildUI();
    }

    private void buildUI() {
        float panelWidth = metrics.sf(DialogueLayout.PANEL_WIDTH);
        float panelHeight = metrics.sf(DialogueLayout.PANEL_HEIGHT);
        float panelX = metrics.centerX(panelWidth);
        float panelY = this.height - metrics.sf(DialogueLayout.PANEL_BOTTOM) - panelHeight;

        panel = new DivComponent(
                panelX,
                panelY,
                panelWidth,
                panelHeight
        );
        panel.setBackgroundColor(
                new Color(
                        DialogueLayout.PANEL_RED,
                        DialogueLayout.PANEL_GREEN,
                        DialogueLayout.PANEL_BLUE,
                        DialogueLayout.PANEL_ALPHA
                )
        );
        panel.setCornerRadius(metrics.sf(DialogueLayout.PANEL_RADIUS));
        panel.setOutline(
                new Color(
                        DialogueLayout.OUTLINE_RED,
                        DialogueLayout.OUTLINE_GREEN,
                        DialogueLayout.OUTLINE_BLUE,
                        DialogueLayout.OUTLINE_ALPHA
                ),
                metrics.sf(1.5f)
        );
        panel.enableBlur(18f, 1.0f);

        DialoguePortrait dialoguePortrait = dialogueUI.dialogue().getPortrait();
        if (dialoguePortrait != null) {
            float maxPortraitWidth = metrics.sf(330f);
            float maxPortraitHeight = metrics.sf(330f);

            float originalWidth = dialoguePortrait.width();
            float originalHeight = dialoguePortrait.height();

            float scale = Math.min(
                    maxPortraitWidth / originalWidth,
                    maxPortraitHeight / originalHeight
            );

            float portraitWidth = originalWidth * scale;
            float portraitHeight = originalHeight * scale;

            float portraitX = metrics.sf(DialogueLayout.PORTRAIT_X);
            float portraitY = metrics.sf(DialogueLayout.PORTRAIT_Y);

            portrait = new ImageComponent(
                    (int) portraitX,
                    (int) portraitY,
                    (int) portraitWidth,
                    (int) portraitHeight,
                    dialoguePortrait.texture()
            );

            panel.addChild(portrait);
        }

        textContainer = new DivComponent(
                0,
                0,
                panelWidth,
                panelHeight
        );

        panel.addChild(textContainer);

        float buttonWidth = metrics.sf(DialogueLayout.BUTTON_WIDTH);
        float buttonHeight = metrics.sf(DialogueLayout.BUTTON_HEIGHT);
        float buttonX = panelWidth - buttonWidth - metrics.sf(DialogueLayout.BUTTON_RIGHT);
        float buttonY = panelHeight - buttonHeight - metrics.sf(DialogueLayout.BUTTON_BOTTOM);
        continueButton = new Button(
                buttonX,
                buttonY,
                buttonWidth,
                buttonHeight,
                "CONTINUER",
                new Color(
                        DialogueLayout.BUTTON_RED,
                        DialogueLayout.BUTTON_GREEN,
                        DialogueLayout.BUTTON_BLUE,
                        235
                ),
                Color.WHITE
        );

        continueButton.setRadius(metrics.s(6));

        continueButton.setOutline(
                new Color(153, 42, 6, 255),
                metrics.sf(1f)
        );

        continueButton.setFont(boldFont);

        continueButton.setFontSize(
                metrics.s(DialogueLayout.BUTTON_FONT_SIZE)
        );

        continueButton.hover(
                120,
                new Color(
                        DialogueLayout.BUTTON_HOVER_RED,
                        DialogueLayout.BUTTON_HOVER_GREEN,
                        DialogueLayout.BUTTON_HOVER_BLUE,
                        245
                ),
                Color.WHITE
        );

        continueButton.setOnClick(button -> next());

        panel.addChild(continueButton);

        refreshText();
    }

    private void refreshText() {
        textContainer.clearChildren();
        animatedLines.clear();
        lineStartTime = System.currentTimeMillis();
        lineFullyRevealed = false;

        DialogueLine line = dialogueUI.currentLine();

        TextComponent speaker = new TextComponent(
                metrics.s(DialogueLayout.SPEAKER_X), metrics.s(DialogueLayout.SPEAKER_Y),
                line.speaker(), metrics.sf(DialogueLayout.SPEAKER_FONT_SIZE),
                new Color(DialogueLayout.SPEAKER_RED, DialogueLayout.SPEAKER_GREEN, DialogueLayout.SPEAKER_BLUE),
                boldFont);
        textContainer.addChild(speaker);

        List<String> wrappedLines = wrapText(
                line.text(), metrics.sf(DialogueLayout.CONTENT_WIDTH), metrics.sf(DialogueLayout.TEXT_FONT_SIZE));

        float y = metrics.sf(DialogueLayout.TEXT_Y);
        float lineHeight = regularFont.getLineHeight(metrics.sf(DialogueLayout.TEXT_FONT_SIZE));
        float spacing = metrics.sf(DialogueLayout.TEXT_LINE_SPACING);

        DialogueAnimation animation = dialogueUI.dialogue().getAnimation();
        int charOffset = 0;

        for (String textLine : wrappedLines) {
            TextComponent text = new TextComponent(
                    metrics.sf(DialogueLayout.CONTENT_X), y,
                    animation == DialogueAnimation.TYPEWRITER ? "" : textLine,
                    metrics.sf(DialogueLayout.TEXT_FONT_SIZE),
                    new Color(DialogueLayout.TEXT_RED, DialogueLayout.TEXT_GREEN, DialogueLayout.TEXT_BLUE),
                    regularFont);

            if (animation == DialogueAnimation.FADE) {
                text.setColor(withAlpha(text.getColor(), 0));
            }

            textContainer.addChild(text);
            animatedLines.add(new AnimatedLine(text, textLine, charOffset));

            charOffset += textLine.length() + 1;
            y += lineHeight + spacing;
        }

        if (animation == DialogueAnimation.NONE) {
            lineFullyRevealed = true;
        }
    }

    private void updateAnimation() {
        if (lineFullyRevealed) return;

        DialogueAnimation animation = dialogueUI.dialogue().getAnimation();
        long elapsed = System.currentTimeMillis() - lineStartTime;

        if (animation == DialogueAnimation.TYPEWRITER) {
            int totalVisibleChars = (int) (elapsed / TYPEWRITER_CHAR_DELAY_MS);
            boolean allRevealed = true;

            for (AnimatedLine animatedLine : animatedLines) {
                int localVisible = totalVisibleChars - animatedLine.charOffset();
                localVisible = Math.max(0, Math.min(localVisible, animatedLine.fullText().length()));

                if (localVisible < animatedLine.fullText().length()) {
                    allRevealed = false;
                }

                animatedLine.component().setText(animatedLine.fullText().substring(0, localVisible));
            }

            if (allRevealed) lineFullyRevealed = true;

        } else if (animation == DialogueAnimation.FADE) {
            float progress = Math.min(1f, elapsed / (float) FADE_DURATION_MS);
            int alpha = (int) (progress * 255f);

            for (AnimatedLine animatedLine : animatedLines) {
                animatedLine.component().setColor(withAlpha(animatedLine.component().getColor(), alpha));
            }

            if (progress >= 1f) lineFullyRevealed = true;
        }
    }

    private void skipAnimation() {
        for (AnimatedLine animatedLine : animatedLines) {
            animatedLine.component().setText(animatedLine.fullText());
            animatedLine.component().setColor(withAlpha(animatedLine.component().getColor(), 255));
        }
        lineFullyRevealed = true;
    }

    private static Color withAlpha(Color color, int alpha) {
        int packed = color.getValue();
        int r = (packed >> 16) & 0xFF;
        int g = (packed >> 8) & 0xFF;
        int b = packed & 0xFF;
        return new Color(r, g, b, alpha);
    }

    private List<String> wrapText(String text, float maxWidth, float fontSize) {
        List<String> result = new ArrayList<>();
        String[] paragraphs = text.split("\\n");

        for (String paragraph : paragraphs) {
            StringBuilder current = new StringBuilder();
            String[] words = paragraph.split(" ");

            for (String word : words) {
                String candidate = current.isEmpty() ? word : current + " " + word;
                float width = regularFont.getWidth(candidate, fontSize);

                if (width <= maxWidth) {
                    current = new StringBuilder(candidate);
                } else {
                    if (!current.isEmpty()) result.add(current.toString());
                    current = new StringBuilder(word);
                }
            }
            if (!current.isEmpty()) result.add(current.toString());
        }

        return result;
    }

    private void next() {
        if (!lineFullyRevealed) {
            skipAnimation();
            return;
        }
        boolean finished = dialogueUI.next();
        if (finished) {
            DialogueManager.close();
            return;
        }
        refreshText();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        updateAnimation();
        if (panel != null) {
            panel.render(graphics, mouseX, mouseY, partialTick);
        }
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (panel != null) panel.mouseClicked(mouseX, mouseY, button);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            DialogueManager.close();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_ENTER) {
            next();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}