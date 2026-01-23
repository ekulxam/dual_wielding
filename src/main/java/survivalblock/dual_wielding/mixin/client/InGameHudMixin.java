package survivalblock.dual_wielding.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private static ResourceLocation CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE;

    @Shadow
    @Final
    private static ResourceLocation CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE;

    @Shadow
    @Final
    private static ResourceLocation CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE;

    @Shadow
    @Final
    private static ResourceLocation HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE;

    @Shadow
    @Final
    private static ResourceLocation HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE;

    @Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private void crosshairOffhandAttackProgress(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        LocalPlayer player = this.minecraft.player;

        if (player == null || player.getOffhandItem().isEmpty()) {
            return;
        }

        Entity targetedEntity = this.minecraft.dual_wielding$getOffhandTargetedEntity();
        float attackProgress = this.minecraft.player.dual_wielding$getOffhandAttackCooldownProgress(0.0F);

        boolean drawFull = false;
        if (targetedEntity instanceof LivingEntity && attackProgress >= 1.0F) {
            drawFull = this.minecraft.player.dual_wielding$getOffhandAttackCooldownProgressPerTick() > 5.0F;
            drawFull &= targetedEntity.isAlive();
        }

        int j = context.guiHeight() / 2 - 7 + 16 + 6;
        int k = context.guiWidth() / 2 - 8;
        if (drawFull) {
            context.blitSprite(CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, k, j, 16, 16);
        } else if (attackProgress < 1.0F) {
            int l = (int) (attackProgress * 17.0F);
            context.blitSprite(CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k, j, 16, 4);
            context.blitSprite(CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, k, j, l, 4);
        }
    }

    @Inject(method = "renderItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private void hotbarOffhandAttackProgress(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci, @Local HumanoidArm arm, @Local(ordinal = 0) int halfWidth) {
        LocalPlayer player = this.minecraft.player;

        if (player == null || player.getOffhandItem().isEmpty()) {
            return;
        }

        float attackProgress = this.minecraft.player.dual_wielding$getOffhandAttackCooldownProgress(0.0F);

        if (attackProgress < 1.0F) {
            int n = context.guiHeight() - 24 - 18;
            int o;
            if (arm.getOpposite() == HumanoidArm.RIGHT) {
                o = halfWidth - 91 - 26;
            } else {
                o = halfWidth + 91 + 10;
            }

            int p = (int) (attackProgress * 19.0F);
            context.blitSprite(HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE, o, n, 18, 18);
            context.blitSprite(HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE, 18, 18, 0, 18 - p, o, n + 18 - p, 18, p);
        }
    }
}
