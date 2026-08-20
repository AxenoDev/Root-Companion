package me.axeno.root.client.ui;

public final class UiMetrics {

    public static final float REFERENCE_WIDTH = 1920f;
    public static final float REFERENCE_HEIGHT = 1080f;

    private final int screenWidth;
    private final int screenHeight;

    private final float scaleFactor;

    public UiMetrics(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.scaleFactor = Math.min(
                screenWidth / REFERENCE_WIDTH,
                screenHeight / REFERENCE_HEIGHT
        );
    }

    public int screenWidth() {
        return screenWidth;
    }

    public int screenHeight() {
        return screenHeight;
    }

    public float scaleFactor() {
        return scaleFactor;
    }

    public int s(float value) {
        return Math.round(value * scaleFactor);
    }

    public float sf(float value) {
        return value * scaleFactor;
    }

    public float centerX(float width) {
        return (screenWidth - width) / 2f;
    }

    public float centerY(float height) {
        return (screenHeight - height) / 2f;
    }

    public float x(float designX) {
        return sf(designX);
    }

    public float y(float designY) {
        return sf(designY);
    }
}