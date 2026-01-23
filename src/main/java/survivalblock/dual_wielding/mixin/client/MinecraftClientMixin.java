package survivalblock.dual_wielding.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.dual_wielding.common.DualWieldingUnbound;
import survivalblock.dual_wielding.common.injected_interface.IHaveAnotherTarget;

@Mixin(Minecraft.class)
public class MinecraftClientMixin implements IHaveAnotherTarget {

    @Shadow
    @Nullable
    public LocalPlayer player;
    @Nullable
    @Unique
    private Entity dual_wielding$offhandTargetedEntity;

    @Override
    public void dual_wielding$setOffhandTargetedEntity(@Nullable Entity targetedEntity) {
        this.dual_wielding$offhandTargetedEntity = targetedEntity;
    }

    @Override
    public @Nullable Entity dual_wielding$getOffhandTargetedEntity() {
        return this.dual_wielding$offhandTargetedEntity;
    }

    @WrapOperation(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"))
    private void resetForOffhand(LocalPlayer instance, Operation<Void> original, @Share("wasSupposedToAttackWithOffhand")LocalBooleanRef localBooleanRef) {
        DualWieldingUnbound.resetLastAttackedTicks(instance, original, localBooleanRef.get());
    }

    @Inject(method = "startAttack", at = @At("HEAD"))
    private void wasSupposedToAttackWithOffhand(CallbackInfoReturnable<Boolean> cir, @Share("wasSupposedToAttackWithOffhand")LocalBooleanRef localBooleanRef) {
        localBooleanRef.set(this.player.dual_wielding$shouldAttackWithOffhand());
    }

    @WrapOperation(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"))
    private void swingOffhandSometimes(LocalPlayer instance, InteractionHand hand, Operation<Void> original, @Share("wasSupposedToAttackWithOffhand")LocalBooleanRef localBooleanRef) {
        if (localBooleanRef.get()) {
            hand = InteractionHand.OFF_HAND;
        }
        original.call(instance, hand);
    }
}
