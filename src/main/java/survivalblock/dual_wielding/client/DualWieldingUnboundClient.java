package survivalblock.dual_wielding.client;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.AttackSweepParticle;
import net.minecraft.resources.ResourceLocation;
import survivalblock.dual_wielding.common.init.DualWieldingUnboundParticleTypes;

import java.util.List;

public class DualWieldingUnboundClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance()
                .register(DualWieldingUnboundParticleTypes.OFFHAND_SWEEP, AttackSweepParticle.Provider::new);
        //ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(OffhandSweepParticleTextureCreator.INSTANCE);
    }

    public static List<ResourceLocation> particleTexturesWithOrdinal(ResourceLocation base, int max) {
        return particleTexturesWithOrdinal(base, "", max);
    }

    public static List<ResourceLocation> particleTexturesWithOrdinal(ResourceLocation base, String end, int max) {
        ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builderWithExpectedSize(max + 1);
        for (int i = 0; i <= max; i++) {
            final int ordinal = i;
            builder.add(base.withPath(path -> path + "_" + ordinal + end));
        }
        return builder.build();
    }
}
