package survivalblock.dual_wielding.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.damagesource.DamageSource;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.dual_wielding.common.DualWieldingUnbound;
import survivalblock.dual_wielding.common.DualWieldingUnbound.HandStackPair;
import survivalblock.dual_wielding.common.init.DualWieldingUnboundParticleTypes;
import survivalblock.dual_wielding.common.injected_interface.OffhandAttackingPlayer;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import static survivalblock.dual_wielding.common.DualWieldingUnbound.ATTACK_COOLDOWN_PROGRESS_ADJUSTMENT;
import static survivalblock.dual_wielding.common.DualWieldingUnbound.DOUBLE_ATTACK_CHARGE_THRESHOLD;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin implements OffhandAttackingPlayer {

    @Shadow
    public abstract float getAttackStrengthScale(float baseTime);

    @Unique
    private int dual_wielding$offhandLastAttackedTicks = 0;

    @Unique
    private ItemStack dual_wielding$prevOffhandWeaponStack = ItemStack.EMPTY;

    @Unique
    private boolean dual_wielding$attackingWithOffhand = false;

    public PlayerEntityMixin(EntityType<?> type, Level world) {
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
    protected ItemStack maybeGetOffhandWeaponStack(ItemStack original) {
        if (this.dual_wielding$attackingWithOffhand) {
            return this.getOffhandItem();
        }
        return original;
    }

    @WrapMethod(method = "attack")
    private void wrapOffhandAttack(Entity target, Operation<Void> original) {
        this.dual_wielding$attackingWithOffhand = this.dual_wielding$shouldAttackWithOffhand();
        try {
            original.call(target);
        } catch (Throwable throwable) {
            this.dual_wielding$attackingWithOffhand = false;
            throw throwable;
        }
        this.dual_wielding$attackingWithOffhand = false;
    }
    
    @Inject(method = "attack", at = @At("HEAD"))
    private void checkDoubleAttack(CallbackInfo ci, @Share("doubleAttacking") LocalBooleanRef localBooleanRef) {
        localBooleanRef.set(
                this.getAttackStrengthScale(ATTACK_COOLDOWN_PROGRESS_ADJUSTMENT) >= DOUBLE_ATTACK_CHARGE_THRESHOLD
                        && this.dual_wielding$getOffhandAttackCooldownProgress(ATTACK_COOLDOWN_PROGRESS_ADJUSTMENT) >= DOUBLE_ATTACK_CHARGE_THRESHOLD
        );
    }

    @WrapOperation(
            method = "attack",
            at = {
                    @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
                    @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z")
            }
    )
    private boolean allowNextAttackIfDouble(@Coerce Entity instance, DamageSource damageSource, float amount, Operation<Boolean> original, @Share("doubleAttacking") LocalBooleanRef localBooleanRef) {
        if (!localBooleanRef.get()) {
            return original.call(instance, damageSource, amount);
        }

        int originalInvulTime = instance.invulnerableTime;
        boolean succeeded = original.call(instance, damageSource, amount);
        if (succeeded || originalInvulTime != instance.invulnerableTime) {
            instance.invulnerableTime = 0;
        }
        return succeeded;
    }

    @ModifyExpressionValue(method = "sweepAttack", at = @At(value = "FIELD", target = "Lnet/minecraft/core/particles/ParticleTypes;SWEEP_ATTACK:Lnet/minecraft/core/particles/SimpleParticleType;", opcode = Opcodes.GETSTATIC))
    private SimpleParticleType useOffhandSweepParticleWhenApplicable(SimpleParticleType original) {
        return this.dual_wielding$attackingWithOffhand ? DualWieldingUnboundParticleTypes.OFFHAND_SWEEP : original;
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;attackStrengthTicker:I", opcode = Opcodes.PUTFIELD))
    private void tickOffhandWeapon(CallbackInfo ci) {
        this.dual_wielding$offhandLastAttackedTicks++;
        ItemStack stack = this.getOffhandItem();
        if (!ItemStack.matches(this.dual_wielding$prevOffhandWeaponStack, stack)) {
            if (!ItemStack.isSameItem(this.dual_wielding$prevOffhandWeaponStack, stack)) {
                this.dual_wielding$resetOffhandLastAttackedTicks();
            }

            this.dual_wielding$prevOffhandWeaponStack = stack.copy();
        }
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    private double getOffhandAttributeValue(Player instance, Holder<Attribute> registryEntry, Operation<Double> original) {
        if (instance.dual_wielding$shouldAttackWithOffhand()) {
            return instance.dual_wielding$getOffhandAttributeValue(registryEntry);
        }
        return original.call(instance, registryEntry);
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;resetAttackStrengthTicker()V"))
    private void resetForOffhand(Player instance, Operation<Void> original) {
        DualWieldingUnbound.resetLastAttackedTicks(instance, original);
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttackStrengthScale(F)F"))
    private float getOffhandAttackProgress(Player instance, float baseTime, Operation<Float> original) {
        if (instance.dual_wielding$shouldAttackWithOffhand()) {
            return instance.dual_wielding$getOffhandAttackCooldownProgress(baseTime);
        }
        return original.call(instance, baseTime);
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack getOffhandAttackProgress(Player instance, InteractionHand hand, Operation<ItemStack> original) {
        if (instance.dual_wielding$shouldAttackWithOffhand()) {
            return original.call(instance, InteractionHand.OFF_HAND);
        }
        return original.call(instance, hand);
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;playerAttack(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource accountForOffhandSource(DamageSource original) {
        if (this.dual_wielding$attackingWithOffhand) {
            original.dual_wielding$setWasPreviouslyOffhanding(true);
        }
        return original;
    }

    @Override
    public AttributeMap dual_wielding$getOffhandAttributes() {
        AttributeMap original = this.getAttributes();

        List<HandStackPair> handStacks = List.of(
                new HandStackPair(this.getMainHandItem(), InteractionHand.MAIN_HAND),
                new HandStackPair(this.getOffhandItem(), InteractionHand.OFF_HAND)
        );

        //noinspection unchecked
        AttributeMap attributes = new AttributeMap(DefaultAttributes.getSupplier((EntityType<? extends LivingEntity>) this.getType()));
        attributes.dual_wielding$setUnsyncable(true);
        attributes.assignAllValues(original);

        dual_wielding$switchHandAttributes(attributes, handStacks, true);
        dual_wielding$switchHandAttributes(original, handStacks, false);
        return attributes;
    }

    @Override
    public boolean dual_wielding$shouldAttackWithOffhand() {
        if (this.getOffhandItem().isEmpty()) {
            return false;
        }
        if (this.getMainHandItem().isEmpty()) {
            return true;
        }
        return this.getAttackStrengthScale(0.0F) < this.dual_wielding$getOffhandAttackCooldownProgress(0.0F);
    }

    @SuppressWarnings("SameParameterValue")
    @Unique
    private void dual_wielding$switchHandAttributes(AttributeMap original, List<HandStackPair> handStacks, boolean toOffhand) {
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

            stack.forEachModifier(originalSlot, (attribute, modifier) -> {
                AttributeInstance entityAttributeInstance = original.getInstance(attribute);
                if (entityAttributeInstance != null) {
                    entityAttributeInstance.removeModifier(modifier);
                }

                EnchantmentHelper.stopLocationBasedEffects(stack, (Player) (Object) this, originalSlot);
            });

            stack.forEachModifier(swapSlot, (registryEntry, entityAttributeModifier) -> {
                AttributeInstance entityAttributeInstance = original.getInstance(registryEntry);
                if (entityAttributeInstance != null) {
                    entityAttributeInstance.removeModifier(entityAttributeModifier.id());
                    entityAttributeInstance.addTransientModifier(entityAttributeModifier);
                }

                if (this.level() instanceof ServerLevel serverWorld) {
                    EnchantmentHelper.runLocationChangedEffects(serverWorld, stack, (Player) (Object) this, swapSlot);
                }
            });
        }
    }
}