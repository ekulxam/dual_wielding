package survivalblock.dual_wielding.client.resload;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import survivalblock.dual_wielding.common.DualWieldingUnbound;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static survivalblock.dual_wielding.client.DualWieldingUnboundClient.particleTexturesWithOrdinal;
import static survivalblock.dual_wielding.common.init.DualWieldingUnboundParticleTypes.OFFHAND_SWEEP_ID;

public final class OffhandSweepParticleTextureCreator implements SimpleSynchronousResourceReloadListener {
    public static final OffhandSweepParticleTextureCreator INSTANCE = new OffhandSweepParticleTextureCreator();
    public static final ResourceLocation ID = DualWieldingUnbound.id("offhand_sweep_particle_texture_creator");

    public final Map<ResourceLocation, NativeImage> images = new HashMap<>();

    private OffhandSweepParticleTextureCreator() {
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.images.clear();

        TextureManager textureManager = Minecraft.getInstance().getTextureManager();

        List<ResourceLocation> originalSweepTexturePaths = particleTexturesWithOrdinal(ResourceLocation.withDefaultNamespace("textures/particle/sweep"), ".png", 7);
        List<ResourceLocation> offhandSweepTexturePaths = particleTexturesWithOrdinal(OFFHAND_SWEEP_ID.withPath(path -> "textures/particle/" + path), ".png", 7);
        
        for (int i = 0; i <= 7; i++) {
            ResourceLocation offhandSweepTexturePath = offhandSweepTexturePaths.get(i);
            AbstractTexture maybeExisting = textureManager.getTexture(offhandSweepTexturePath, null);
            //noinspection ConstantValue
            if (maybeExisting != null && !(maybeExisting instanceof DynamicTexture)) {
                continue; // do not generate if the texture exists already (but allow regen if it was previously generated)
            }

            ResourceLocation originalSweepTexturePath = originalSweepTexturePaths.get(i);
            NativeImage original;
            try {
                original = NativeImage.read(resourceManager.open(originalSweepTexturePath));
            } catch (IOException e) {
                DualWieldingUnbound.LOGGER.error("An error occurred when loading the vanilla sweep particle texture \"{}\"!", originalSweepTexturePath, e);
                continue;
            }

            if (original == null) {
                continue;
            }

            int width = original.getWidth();
            int height = original.getHeight();
            try {
                NativeImage generated = new NativeImage(width, height, false);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        generated.setPixelRGBA(width - 1 - x, y, original.getPixelRGBA(x, y));
                    }
                }
                images.put(offhandSweepTexturePath, generated);
            } catch (Throwable throwable) {
                DualWieldingUnbound.LOGGER.error("An error occurred when dynamically generating the offhand sweep particle texture \"{}\"!", offhandSweepTexturePath, throwable);
            }
        }
    }
}
