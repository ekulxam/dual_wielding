package survivalblock.dual_wielding.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
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
    MinecraftClient client;

    @Shadow
    protected abstract HitResult findCrosshairTarget(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta);

    @Inject(method = "updateCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getBlockInteractionRange()D"))
    private void updateOffhandCrosshairTarget(float tickDelta, CallbackInfo ci, @Local Entity entity) {
        AttributeContainer offhandAttributes = this.client.player.dual_wielding$getOffhandAttributes();
        double blockReach = offhandAttributes.getValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
        double entityReach = offhandAttributes.getValue(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);
        HitResult hitResult = this.findCrosshairTarget(entity, blockReach, entityReach, tickDelta);
        if ((this.client.crosshairTarget == null || this.client.crosshairTarget.getType() == HitResult.Type.MISS)
                && hitResult instanceof EntityHitResult entityHitResult) {
            this.client.crosshairTarget = hitResult;
            this.client.dual_wielding$setOffhandTargetedEntity(entityHitResult.getEntity());
            return;
        }
        this.client.dual_wielding$setOffhandTargetedEntity(null);
    }
}
