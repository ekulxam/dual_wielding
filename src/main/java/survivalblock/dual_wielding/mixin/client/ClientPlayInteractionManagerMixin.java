package survivalblock.dual_wielding.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.dual_wielding.common.DualWieldingUnbound;

@Mixin(MultiPlayerGameMode.class)
public class ClientPlayInteractionManagerMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @ModifyReturnValue(method = "hasMissTime", at = @At("RETURN"))
    private boolean offhandSpam(boolean original) {
        if (this.minecraft.player == null) {
            return original;
        }
        return original && !this.minecraft.player.dual_wielding$shouldAttackWithOffhand();
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void wasSupposedToAttackWithOffhand(Player player, Entity target, CallbackInfo ci, @Share("wasSupposedToAttackWithOffhand") LocalBooleanRef localBooleanRef) {
        localBooleanRef.set(this.minecraft.player.dual_wielding$shouldAttackWithOffhand());
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;resetAttackStrengthTicker()V"))
    private void resetForOffhand(Player instance, Operation<Void> original, @Share("wasSupposedToAttackWithOffhand") LocalBooleanRef localBooleanRef) {
        DualWieldingUnbound.resetLastAttackedTicks(instance, original, localBooleanRef.get());
    }
}
