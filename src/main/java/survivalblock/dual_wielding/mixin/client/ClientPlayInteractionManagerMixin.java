package survivalblock.dual_wielding.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.dual_wielding.common.DualWieldingUnbound;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayInteractionManagerMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyReturnValue(method = "hasLimitedAttackSpeed", at = @At("RETURN"))
    private boolean offhandSpam(boolean original) {
        if (this.client.player == null) {
            return original;
        }
        return original && !this.client.player.dual_wielding$shouldAttackWithOffhand();
    }

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void wasSupposedToAttackWithOffhand(PlayerEntity player, Entity target, CallbackInfo ci, @Share("wasSupposedToAttackWithOffhand") LocalBooleanRef localBooleanRef) {
        localBooleanRef.set(this.client.player.dual_wielding$shouldAttackWithOffhand());
    }

    @WrapOperation(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;resetLastAttackedTicks()V"))
    private void resetForOffhand(PlayerEntity instance, Operation<Void> original, @Share("wasSupposedToAttackWithOffhand") LocalBooleanRef localBooleanRef) {
        DualWieldingUnbound.resetLastAttackedTicks(instance, original, localBooleanRef.get());
    }
}
