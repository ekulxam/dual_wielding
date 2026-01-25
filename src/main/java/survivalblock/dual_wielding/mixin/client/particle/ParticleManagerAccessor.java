package survivalblock.dual_wielding.mixin.client.particle;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ParticleEngine.class)
public interface ParticleManagerAccessor {
    @Accessor("PARTICLES_ATLAS_INFO")
    static ResourceLocation dual_wielding$getParticleAtlasPath() {
        throw new UnsupportedOperationException();
    }
}
