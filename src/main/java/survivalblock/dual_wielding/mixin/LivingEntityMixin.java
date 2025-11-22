package survivalblock.dual_wielding.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getOffHandStack();

    @Shadow
    public abstract AttributeContainer getAttributes();

    @Shadow
    public abstract ItemStack getMainHandStack();

    @ModifyReturnValue(method = "getWeaponStack", at = @At("RETURN"))
    protected ItemStack useOffhandSometimes(ItemStack original) {
        return original;
    }
}
