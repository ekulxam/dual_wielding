package survivalblock.dual_wielding.common.injected_interface;

import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;

public interface OffhandAttackingPlayer {

    default int dual_wielding$getOffhandLastAttackedTicks() {
        throw new UnsupportedOperationException();
    }

    default void dual_wielding$setOffhandLastAttackedTicks(int lastAttackedTicks) {
        throw new UnsupportedOperationException();
    }

    default void dual_wielding$resetOffhandLastAttackedTicks() {
        this.dual_wielding$setOffhandLastAttackedTicks(0);
    }

    default float dual_wielding$getOffhandAttackCooldownProgress(float baseTime) {
        return MathHelper.clamp((this.dual_wielding$getOffhandLastAttackedTicks() + baseTime) / this.dual_wielding$getOffhandAttackCooldownProgressPerTick(), 0.0F, 1.0F);
    }

    default float dual_wielding$getOffhandAttackCooldownProgressPerTick() {
        return (float)(1.0 / this.dual_wielding$getOffhandAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED) * 20.0);
    }

    default AttributeContainer dual_wielding$getOffhandAttributes(AttributeContainer original) {
        throw new UnsupportedOperationException();
    }

    default AttributeContainer dual_wielding$getOffhandAttributes() {
        throw new UnsupportedOperationException();
    }

    default double dual_wielding$getOffhandAttributeValue(RegistryEntry<EntityAttribute> attribute) {
        return this.dual_wielding$getOffhandAttributes().getValue(attribute);
    }

    default boolean dual_wielding$shouldAttackWithOffhand() {
        throw new UnsupportedOperationException();
    }
}
