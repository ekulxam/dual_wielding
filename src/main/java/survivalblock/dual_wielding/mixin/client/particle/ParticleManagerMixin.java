package survivalblock.dual_wielding.mixin.client.particle;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.dual_wielding.client.resload.OffhandSweepParticleTextureCreator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ParticleEngine.class)
public class ParticleManagerMixin {

    @ModifyExpressionValue(method = "reload", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;allOf([Ljava/util/concurrent/CompletableFuture;)Ljava/util/concurrent/CompletableFuture;", remap = false))
    private CompletableFuture<Void> genTexturesFirst(
            CompletableFuture<Void> original,
            PreparableReloadListener.PreparationBarrier preparationBarrier,
            ResourceManager resourceManager,
            ProfilerFiller preparationsProfiler,
            ProfilerFiller reloadProfiler,
            Executor backgroundExecutor,
            Executor gameExecutor) {
        return original;
    }
}
