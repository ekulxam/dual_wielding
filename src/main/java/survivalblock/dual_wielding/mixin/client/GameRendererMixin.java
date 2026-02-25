package survivalblock.dual_wielding.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    @Final
    Minecraft minecraft;

    @Shadow
    protected abstract HitResult pick(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta);

    @WrapOperation(method = "pick(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;"))
    private HitResult captureCameraEntity(GameRenderer instance, Entity entity, double blockInteractionRange, double entityInteractionRange, float partialTick, Operation<HitResult> original, @Share("cameraEntity")LocalRef<Entity> localRef) {
        localRef.set(entity);
        return original.call(instance, entity, blockInteractionRange, entityInteractionRange, partialTick);
    }

    @Inject(method = "pick(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
    private void updateOffhandCrosshairTarget(float tickDelta, CallbackInfo ci, @Share("cameraEntity")LocalRef<Entity> localRef) {
        AttributeMap offhandAttributes = this.minecraft.player.dual_wielding$getOffhandAttributes();
        double blockReach = offhandAttributes.getValue(Attributes.BLOCK_INTERACTION_RANGE);
        double entityReach = offhandAttributes.getValue(Attributes.ENTITY_INTERACTION_RANGE);
        HitResult hitResult = this.pick(localRef.get(), blockReach, entityReach, tickDelta);
        if ((this.minecraft.hitResult == null || this.minecraft.hitResult.getType() == HitResult.Type.MISS)
                && hitResult instanceof EntityHitResult entityHitResult) {
            this.minecraft.hitResult = hitResult;
            this.minecraft.dual_wielding$setOffhandTargetedEntity(entityHitResult.getEntity());
            return;
        }
        this.minecraft.dual_wielding$setOffhandTargetedEntity(this.minecraft.crosshairPickEntity);
    }
}
