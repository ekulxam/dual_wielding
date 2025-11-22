package survivalblock.dual_wielding.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.dual_wielding.DualWieldingUnbound;
import survivalblock.dual_wielding.DualWieldingUnbound.HandStackPair;
import survivalblock.dual_wielding.common.injected_interface.OffhandAttackingPlayer;

import java.util.List;

@Debug(export = true)
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin implements OffhandAttackingPlayer {

    @Shadow
    public abstract float getAttackCooldownProgress(float baseTime);

    @Unique
    private int dual_wielding$offhandLastAttackedTicks = 0;

    @Unique
    private ItemStack dual_wielding$prevOffhandWeaponStack = ItemStack.EMPTY;

    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void dual_wielding$setOffhandLastAttackedTicks(int lastAttackedTicks) {
        this.dual_wielding$offhandLastAttackedTicks = lastAttackedTicks;
    }

    @Override
    public int dual_wielding$getOffhandLastAttackedTicks() {
        return this.dual_wielding$offhandLastAttackedTicks;
    }

    @Override
    protected ItemStack useOffhandSometimes(ItemStack original) {
        return this.dual_wielding$shouldAttackWithOffhand() ? this.getOffHandStack() : original;
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;lastAttackedTicks:I", opcode = Opcodes.PUTFIELD))
    private void tickOffhandWeapon(CallbackInfo ci) {
        this.dual_wielding$offhandLastAttackedTicks++;
        ItemStack stack = this.getOffHandStack();
        if (!ItemStack.areEqual(this.dual_wielding$prevOffhandWeaponStack, stack)) {
            if (!ItemStack.areItemsEqual(this.dual_wielding$prevOffhandWeaponStack, stack)) {
                this.dual_wielding$resetOffhandLastAttackedTicks();
            }

            this.dual_wielding$prevOffhandWeaponStack = stack.copy();
        }
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/registry/entry/RegistryEntry;)D"))
    private double getOffhandAttributeValue(PlayerEntity instance, RegistryEntry<EntityAttribute> registryEntry, Operation<Double> original) {
        if (instance.dual_wielding$shouldAttackWithOffhand()) {
            return instance.dual_wielding$getOffhandAttributeValue(registryEntry);
        }
        return original.call(instance, registryEntry);
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;resetLastAttackedTicks()V"))
    private void resetForOffhand(PlayerEntity instance, Operation<Void> original) {
        DualWieldingUnbound.resetLastAttackedTicks(instance, original);
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"))
    private float getOffhandAttackProgress(PlayerEntity instance, float baseTime, Operation<Float> original) {
        if (instance.dual_wielding$shouldAttackWithOffhand()) {
            return instance.dual_wielding$getOffhandAttackCooldownProgress(baseTime);
        }
        return original.call(instance, baseTime);
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack getOffhandAttackProgress(PlayerEntity instance, Hand hand, Operation<ItemStack> original) {
        if (instance.dual_wielding$shouldAttackWithOffhand()) {
            return original.call(instance, Hand.OFF_HAND);
        }
        return original.call(instance, hand);
    }

    @Override
    public AttributeContainer dual_wielding$getOffhandAttributes() {
        return this.dual_wielding$getOffhandAttributes(this.getAttributes());
    }

    @Override
    public AttributeContainer dual_wielding$getOffhandAttributes(AttributeContainer original) {
        List<HandStackPair> handStacks = List.of(
                new HandStackPair(this.getMainHandStack(), Hand.MAIN_HAND),
                new HandStackPair(this.getOffHandStack(), Hand.OFF_HAND)
        );

        // offhand
        dual_wielding$switchHandAttributes(original, handStacks, true);

        //noinspection unchecked
        AttributeContainer attributes = new AttributeContainer(DefaultAttributeRegistry.get((EntityType<? extends LivingEntity>) this.getType()));

        // go back to mainhand
        dual_wielding$switchHandAttributes(original, handStacks, false);

        return attributes;
    }

    @Override
    public boolean dual_wielding$shouldAttackWithOffhand() {
        if (this.getOffHandStack().isEmpty()) {
            return false;
        }
        if (this.getMainHandStack().isEmpty()) {
            return true;
        }
        return this.getAttackCooldownProgress(0.0F) < this.dual_wielding$getOffhandAttackCooldownProgress(0.0F);
    }

    @Unique
    private void dual_wielding$switchHandAttributes(AttributeContainer original, List<HandStackPair> handStacks, boolean toOffhand) {
        for (HandStackPair pair : handStacks) {
            ItemStack stack = pair.stack();

            if (stack.isEmpty()) {
                continue;
            }

            EquipmentSlot originalSlot;
            EquipmentSlot swapSlot;

            if (toOffhand) {
                originalSlot = pair.handSlot();
                swapSlot = originalSlot == EquipmentSlot.MAINHAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
            } else {
                swapSlot = pair.handSlot();
                originalSlot = swapSlot == EquipmentSlot.MAINHAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
            }

            stack.applyAttributeModifiers(originalSlot, (attribute, modifier) -> {
                EntityAttributeInstance entityAttributeInstance = original.getCustomInstance(attribute);
                if (entityAttributeInstance != null) {
                    entityAttributeInstance.removeModifier(modifier);
                }

                EnchantmentHelper.removeLocationBasedEffects(stack, (PlayerEntity) (Object) this, originalSlot);
            });

            stack.applyAttributeModifiers(swapSlot, (registryEntry, entityAttributeModifier) -> {
                EntityAttributeInstance entityAttributeInstance = original.getCustomInstance(registryEntry);
                if (entityAttributeInstance != null) {
                    entityAttributeInstance.removeModifier(entityAttributeModifier.id());
                    entityAttributeInstance.addTemporaryModifier(entityAttributeModifier);
                }

                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    EnchantmentHelper.applyLocationBasedEffects(serverWorld, stack, (PlayerEntity) (Object) this, swapSlot);
                }
            });
        }
    }
}