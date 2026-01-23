package survivalblock.dual_wielding.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ItemInHandRenderer.class)
public class HeldItemRendererMixin {

    @Shadow
    private ItemStack offHandItem;

    @Shadow
    private float offHandHeight;

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F", ordinal = 1), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F")))
    private float offhandRaiseAnimation(float value, float min, float max, Operation<Float> original, @Local LocalPlayer player, @Local(ordinal = 1) ItemStack offhandStack) {
        float progress = player.dual_wielding$getOffhandAttackCooldownProgress(1.0F);
        value = this.offHandItem == offhandStack ? progress * progress * progress : 0;
        return original.call(value - this.offHandHeight, min, max);
    }
}
