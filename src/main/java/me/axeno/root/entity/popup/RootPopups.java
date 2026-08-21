package me.axeno.root.entity.popup;

import me.axeno.root.entity.popup.impl.BonjourPopup;
import me.axeno.root.entity.popup.impl.CadeauPopup;
import net.minecraft.util.RandomSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RootPopups {

    private static final Map<String, RootPopup> POPUPS = new LinkedHashMap<>();

    public static final RootPopup BONJOUR = register(new BonjourPopup());
    public static final RootPopup CADEAU = register(new CadeauPopup());

    private static RootPopup register(RootPopup popup) {
        POPUPS.put(popup.getId(), popup);
        return popup;
    }

    public static RootPopup get(String id) {
        return POPUPS.get(id);
    }

    public static List<RootPopup> all() {
        return List.copyOf(POPUPS.values());
    }

    public static RootPopup random(RandomSource random) {
        List<RootPopup> values = all();

        double totalWeight = values.stream()
                .mapToDouble(RootPopup::getWeight)
                .sum();

        double roll = random.nextDouble() * totalWeight;
        double cumulative = 0.0D;

        for (RootPopup popup : values) {
            cumulative += popup.getWeight();
            if (roll < cumulative)
                return popup;
        }

        return values.get(values.size() - 1);
    }

}
