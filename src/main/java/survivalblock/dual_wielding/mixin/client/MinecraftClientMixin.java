package survivalblock.dual_wielding.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.dual_wielding.DualWieldingUnbound;
import survivalblock.dual_wielding.common.injected_interface.IHaveAnotherTarget;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements IHaveAnotherTarget {

    @Shadow
    @Nullable
    public ClientPlayerEntity player;
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

    @WrapOperation(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;resetLastAttackedTicks()V"))
    private void resetForOffhand(ClientPlayerEntity instance, Operation<Void> original, @Share("wasSupposedToAttackWithOffhand")LocalBooleanRef localBooleanRef) {
        DualWieldingUnbound.resetLastAttackedTicks(instance, original, localBooleanRef.get());
    }

    @Inject(method = "doAttack", at = @At("HEAD"))
    private void wasSupposedToAttackWithOffhand(CallbackInfoReturnable<Boolean> cir, @Share("wasSupposedToAttackWithOffhand")LocalBooleanRef localBooleanRef) {
        localBooleanRef.set(this.player.dual_wielding$shouldAttackWithOffhand());
    }

    @WrapOperation(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"))
    private void swingOffhandSometimes(ClientPlayerEntity instance, Hand hand, Operation<Void> original, @Share("wasSupposedToAttackWithOffhand")LocalBooleanRef localBooleanRef) {
        if (localBooleanRef.get()) {
            hand = Hand.OFF_HAND;
        }
        original.call(instance, hand);
    }
}
