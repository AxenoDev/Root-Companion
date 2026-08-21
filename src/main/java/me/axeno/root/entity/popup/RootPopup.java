package me.axeno.root.entity.popup;

import lombok.Getter;
import me.axeno.root.entity.RootEntity;
import net.minecraft.server.level.ServerPlayer;

public abstract class RootPopup {

    private static final double DEFAULT_WEIGHT = 1.0D;

    @Getter
    private final String id;
    @Getter
    private final String text;
    @Getter
    private final double weight;

    public RootPopup(String id, String text) {
        this(id, text, DEFAULT_WEIGHT);
    }

    public RootPopup(String id, String text, double weight) {
        this.id = id;
        this.text = text;
        this.weight = weight;
    }

    public void onServerTrigger(RootEntity entity, ServerPlayer player) {
    }

    public void onClientTrigger(RootEntity entity) {
    }
}