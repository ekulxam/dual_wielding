package survivalblock.dual_wielding.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Shadow
    private ItemStack offHand;

    @Shadow
    private float equipProgressOffHand;

    @WrapOperation(method = "updateHeldItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F", ordinal = 1), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F")))
    private float offhandRaiseAnimation(float value, float min, float max, Operation<Float> original, @Local ClientPlayerEntity player, @Local(ordinal = 1) ItemStack offhandStack) {
        float progress = player.dual_wielding$getOffhandAttackCooldownProgress(1.0F);
        value = this.offHand == offhandStack ? progress * progress * progress : 0;
        return original.call(value - this.equipProgressOffHand, min, max);
    }
}
