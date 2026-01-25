package survivalblock.dual_wielding.mixin.client.particle;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.dual_wielding.client.resload.OffhandSweepParticleTextureCreator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
@Mixin(ResourceManagerHelperImpl.class)
public class ResourceManagerHelperImplMixin {

    @WrapOperation(method = "sort(Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Ljava/util/Set;containsAll(Ljava/util/Collection;)Z"), remap = false)
    private boolean addParticleTextureCreatorBeforeManager(Set<ResourceLocation> resolvedIds, Collection<ResourceLocation> dependencies, Operation<Boolean> original, List<PreparableReloadListener> listeners, @Local IdentifiableResourceReloadListener listener, @Local Iterator<IdentifiableResourceReloadListener> it) {
        if (!OffhandSweepParticleTextureCreator.ID.equals(listener.getFabricId())) {
            return original.call(resolvedIds, dependencies);
        }

        resolvedIds.add(OffhandSweepParticleTextureCreator.ID);
        listeners.addFirst(listener);
        it.remove();
        return false;
    }
}
