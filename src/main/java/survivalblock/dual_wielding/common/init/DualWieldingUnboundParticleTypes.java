package survivalblock.dual_wielding.common.init;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import survivalblock.dual_wielding.common.DualWieldingUnbound;

public final class DualWieldingUnboundParticleTypes {
    private DualWieldingUnboundParticleTypes() {
    }

    public static final ResourceLocation OFFHAND_SWEEP_ID = DualWieldingUnbound.id("offhand_sweep");
    public static final SimpleParticleType OFFHAND_SWEEP = FabricParticleTypes.simple(true);

    public static void init() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, OFFHAND_SWEEP_ID, OFFHAND_SWEEP);
    }
}
