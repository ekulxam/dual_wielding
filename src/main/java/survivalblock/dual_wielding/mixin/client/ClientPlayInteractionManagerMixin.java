package survivalblock.dual_wielding.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.dual_wielding.DualWieldingUnbound;

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

    @WrapOperation(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;resetLastAttackedTicks()V"))
    private void resetForOffhand(PlayerEntity instance, Operation<Void> original) {
        DualWieldingUnbound.resetLastAttackedTicks(instance, original);
    }
}
