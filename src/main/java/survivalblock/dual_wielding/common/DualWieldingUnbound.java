package survivalblock.dual_wielding.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.api.ModInitializer;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DualWieldingUnbound implements ModInitializer {
	public static final String MOD_ID = "dual_wielding";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
	}

    public static void resetLastAttackedTicks(PlayerEntity instance, Operation<Void> original) {
        resetLastAttackedTicks(instance, original, instance.dual_wielding$shouldAttackWithOffhand());
    }

    public static void resetLastAttackedTicks(PlayerEntity instance, Operation<Void> original, boolean offhandAttack) {
        if (offhandAttack) {
            instance.dual_wielding$resetOffhandLastAttackedTicks();
            return;
        }
        original.call(instance);
    }

    public record HandStackPair(ItemStack stack, Hand hand) {

        @SuppressWarnings("unused")
        public EquipmentSlot handSlot() {
            if (this.hand == Hand.MAIN_HAND) {
                return EquipmentSlot.MAINHAND;
            }
            if (this.hand == Hand.OFF_HAND) {
                return EquipmentSlot.OFFHAND;
            }
            throw new IllegalStateException("Expected hand to be MAIN_HAND or OFF_HAND but actually was " + hand.name());
        }
    }
}