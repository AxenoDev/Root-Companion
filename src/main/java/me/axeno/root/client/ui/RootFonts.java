package me.axeno.root.client.ui;

import lombok.Getter;
import me.axeno.noctisui.client.api.system.render.font.FontAtlas;
import me.axeno.root.Root;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class RootFonts {
    private final List<FontAtlas> loadedAtlases = new ArrayList<>();
    private FontAtlas pixelNes;

    public void reload(ResourceManager manager) {
        release();

        try {
            pixelNes = load(manager, "pixelnes");
        } catch (final IOException e) {
            throw new RuntimeException("Couldn't load fonts", e);
        }
    }

    private FontAtlas load(ResourceManager manager, String name) throws IOException {
        FontAtlas atlas = new FontAtlas(manager, name, Root.MODID);
        loadedAtlases.add(atlas);
        return atlas;
    }

    private void release() {
        for (FontAtlas atlas : loadedAtlases) {
            atlas.release();
        }
        loadedAtlases.clear();
    }
}
