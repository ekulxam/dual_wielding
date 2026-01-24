package survivalblock.dual_wielding.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DualWieldingUnbound implements ModInitializer {
	public static final String MOD_ID = "dual_wielding";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final float DOUBLE_ATTACK_CHARGE_THRESHOLD = 0.99F;

	@Override
	public void onInitialize() {
        // TODO: double smash attack?
        // TODO: fix offhand sweep particle
        // TODO: port
	}

    public static void resetLastAttackedTicks(Player instance, Operation<Void> original) {
        resetLastAttackedTicks(instance, original, instance.dual_wielding$shouldAttackWithOffhand());
    }

    public static void resetLastAttackedTicks(Player instance, Operation<Void> original, boolean offhandAttack) {
        if (offhandAttack) {
            instance.dual_wielding$resetOffhandLastAttackedTicks();
            return;
        }
        original.call(instance);
    }

    public record HandStackPair(ItemStack stack, InteractionHand hand) {

        @SuppressWarnings("unused")
        public EquipmentSlot handSlot() {
            if (this.hand == InteractionHand.MAIN_HAND) {
                return EquipmentSlot.MAINHAND;
            }
            if (this.hand == InteractionHand.OFF_HAND) {
                return EquipmentSlot.OFFHAND;
            }
            throw new IllegalStateException("Expected hand to be MAIN_HAND or OFF_HAND but actually was " + hand.name());
        }
    }
}