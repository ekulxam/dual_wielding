package survivalblock.dual_wielding.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.dual_wielding.common.injected_interface.PreviouslyOffhandedSource;

@Mixin(DamageSource.class)
public class DamageSourceMixin implements PreviouslyOffhandedSource {
    @Shadow
    @Final
    private @Nullable Entity directEntity;
    @Unique
    private boolean dual_wielding$wasPreviouslyOffhanding = false;

    @Override
    public void dual_wielding$setWasPreviouslyOffhanding(boolean previouslyOffhanding) {
        this.dual_wielding$wasPreviouslyOffhanding = previouslyOffhanding;
    }

    @ModifyExpressionValue(method = "getWeaponItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getWeaponItem()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack maybeGetOffhandWeaponStack(ItemStack original) {
        return this.dual_wielding$wasPreviouslyOffhanding && this.directEntity instanceof Player player ? player.getOffhandItem() : original;
    }
}
