package survivalblock.dual_wielding.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getOffhandItem();

    @Shadow
    public abstract AttributeMap getAttributes();

    @Shadow
    public abstract ItemStack getMainHandItem();

    @ModifyReturnValue(method = "getWeaponItem", at = @At("RETURN"))
    protected ItemStack maybeGetOffhandWeaponStack(ItemStack original) {
        return original;
    }
}
