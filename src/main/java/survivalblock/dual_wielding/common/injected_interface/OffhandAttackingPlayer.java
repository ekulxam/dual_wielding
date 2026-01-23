package survivalblock.dual_wielding.common.injected_interface;

import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;

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
        return Mth.clamp((this.dual_wielding$getOffhandLastAttackedTicks() + baseTime) / this.dual_wielding$getOffhandAttackCooldownProgressPerTick(), 0.0F, 1.0F);
    }

    default float dual_wielding$getOffhandAttackCooldownProgressPerTick() {
        return (float)(1.0 / this.dual_wielding$getOffhandAttributeValue(Attributes.ATTACK_SPEED) * 20.0);
    }

    default AttributeMap dual_wielding$getOffhandAttributes() {
        throw new UnsupportedOperationException();
    }

    default double dual_wielding$getOffhandAttributeValue(Holder<Attribute> attribute) {
        return this.dual_wielding$getOffhandAttributes().getValue(attribute);
    }

    default boolean dual_wielding$shouldAttackWithOffhand() {
        throw new UnsupportedOperationException();
    }
}
