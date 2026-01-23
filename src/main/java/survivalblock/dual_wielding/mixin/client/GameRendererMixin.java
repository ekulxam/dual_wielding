package survivalblock.dual_wielding.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
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

    @Inject(method = "pick(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;blockInteractionRange()D"))
    private void updateOffhandCrosshairTarget(float tickDelta, CallbackInfo ci, @Local Entity entity) {
        AttributeMap offhandAttributes = this.minecraft.player.dual_wielding$getOffhandAttributes();
        double blockReach = offhandAttributes.getValue(Attributes.BLOCK_INTERACTION_RANGE);
        double entityReach = offhandAttributes.getValue(Attributes.ENTITY_INTERACTION_RANGE);
        HitResult hitResult = this.pick(entity, blockReach, entityReach, tickDelta);
        if ((this.minecraft.hitResult == null || this.minecraft.hitResult.getType() == HitResult.Type.MISS)
                && hitResult instanceof EntityHitResult entityHitResult) {
            this.minecraft.hitResult = hitResult;
            this.minecraft.dual_wielding$setOffhandTargetedEntity(entityHitResult.getEntity());
            return;
        }
        this.minecraft.dual_wielding$setOffhandTargetedEntity(null);
    }
}
