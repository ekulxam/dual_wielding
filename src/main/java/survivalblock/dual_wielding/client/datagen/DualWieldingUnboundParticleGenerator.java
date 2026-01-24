package survivalblock.dual_wielding.client.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.particle.ParticleDescription;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import survivalblock.dual_wielding.common.DualWieldingUnbound;
import survivalblock.dual_wielding.common.init.DualWieldingUnboundParticleTypes;
import survivalblock.dual_wielding.mixin.client.ParticleDescriptionAccessor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static survivalblock.dual_wielding.client.DualWieldingUnboundClient.particleTexturesWithOrdinal;
import static survivalblock.dual_wielding.common.init.DualWieldingUnboundParticleTypes.OFFHAND_SWEEP_ID;

public class DualWieldingUnboundParticleGenerator extends FabricCodecDataProvider<ParticleDescription> {

    public static final Codec<ParticleDescription> CODEC = RecordCodecBuilder.<ParticleDescription>create(
                    instance -> instance.group(
                                    ResourceLocation.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("textures").forGetter(ParticleDescription::getTextures)
                            )
                            .apply(instance, DualWieldingUnboundParticleGenerator::create)
            );

    protected DualWieldingUnboundParticleGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture, PackOutput.Target outputType, String directoryName, Codec<ParticleDescription> codec) {
        super(dataOutput, registriesFuture, outputType, directoryName, codec);
    }

    public DualWieldingUnboundParticleGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        this(dataOutput, registriesFuture, PackOutput.Target.RESOURCE_PACK, "particles", CODEC);
    }

    @Override
    protected void configure(BiConsumer<ResourceLocation, ParticleDescription> biConsumer, HolderLookup.Provider provider) {
        biConsumer.accept(
                OFFHAND_SWEEP_ID,
                create(particleTexturesWithOrdinal(OFFHAND_SWEEP_ID, 7))
        );
    }

    public static ParticleDescription create(List<ResourceLocation> textures) {
        return ParticleDescriptionAccessor.dual_wielding$invokeInit(textures);
    }

    @Override
    public String getName() {
        return "Particles";
    }
}
