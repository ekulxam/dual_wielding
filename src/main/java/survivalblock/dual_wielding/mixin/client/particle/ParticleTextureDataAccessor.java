package survivalblock.dual_wielding.mixin.client.particle;

import net.minecraft.client.particle.ParticleDescription;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ParticleDescription.class)
public interface ParticleTextureDataAccessor {
    @Invoker("<init>")
    static ParticleDescription dual_wielding$invokeInit(List<ResourceLocation> textures) {
        throw new UnsupportedOperationException();
    }
}
