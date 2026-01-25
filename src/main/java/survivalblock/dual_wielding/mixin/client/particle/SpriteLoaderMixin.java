package survivalblock.dual_wielding.mixin.client.particle;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceList;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.dual_wielding.client.resload.ArtificialDirectorySpriteSource;

import static survivalblock.dual_wielding.client.DualWieldingUnboundClient.particleTexturesWithOrdinal;
import static survivalblock.dual_wielding.common.init.DualWieldingUnboundParticleTypes.OFFHAND_SWEEP_ID;

@Mixin(SpriteLoader.class)
public class SpriteLoaderMixin {

    @ModifyExpressionValue(method = "method_47660", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/atlas/SpriteSourceList;load(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/texture/atlas/SpriteSourceList;"))
    private static SpriteSourceList addArtificialDirectory(SpriteSourceList original, @Local(argsOnly = true)ResourceLocation atlas) {
        if (!atlas.equals(ParticleManagerAccessor.dual_wielding$getParticleAtlasPath())) {
            return original;
        }

        ((AtlasLoaderAccessor) original).dual_wielding$getSources().add(new ArtificialDirectorySpriteSource(particleTexturesWithOrdinal(OFFHAND_SWEEP_ID.withPath(path -> "textures/particle/" + path), ".png", 7)));
        return original;
    }
}
