package survivalblock.dual_wielding;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.api.ModInitializer;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
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
        if (instance.dual_wielding$shouldAttackWithOffhand()) {
            instance.dual_wielding$resetOffhandLastAttackedTicks();
            return;
        }
        original.call(instance);
    }

    public record HandStackPair(ItemStack stack, EquipmentSlot handSlot) {

        public HandStackPair(ItemStack stack, Hand hand) {
            this(stack, LivingEntity.getSlotForHand(hand));
        }

        public Hand getHand() {
            if (this.handSlot == EquipmentSlot.MAINHAND) {
                return Hand.MAIN_HAND;
            }
            if (this.handSlot == EquipmentSlot.OFFHAND) {
                return Hand.OFF_HAND;
            }
            throw new IllegalStateException("Expected handSlot to be MAINHAND or OFFHAND but actually was " + handSlot.name());
        }
    }
}