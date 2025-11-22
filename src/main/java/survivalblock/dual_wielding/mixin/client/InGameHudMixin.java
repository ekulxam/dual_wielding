package survivalblock.dual_wielding.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    @Final
    private static Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE;

    @Shadow
    @Final
    private static Identifier CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE;

    @Shadow
    @Final
    private static Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE;

    @Shadow
    @Final
    private static Identifier HOTBAR_ATTACK_INDICATOR_BACKGROUND_TEXTURE;

    @Shadow
    @Final
    private static Identifier HOTBAR_ATTACK_INDICATOR_PROGRESS_TEXTURE;

    @Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"))
    private void crosshairOffhandAttackProgress(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        ClientPlayerEntity player = this.client.player;

        if (player == null || player.getOffHandStack().isEmpty()) {
            return;
        }

        Entity targetedEntity = this.client.dual_wielding$getOffhandTargetedEntity();
        float attackProgress = this.client.player.dual_wielding$getOffhandAttackCooldownProgress(0.0F);

        boolean drawFull = false;
        if (targetedEntity instanceof LivingEntity && attackProgress >= 1.0F) {
            drawFull = this.client.player.dual_wielding$getOffhandAttackCooldownProgressPerTick() > 5.0F;
            drawFull &= targetedEntity.isAlive();
        }

        int j = context.getScaledWindowHeight() / 2 - 7 + 16 + 6;
        int k = context.getScaledWindowWidth() / 2 - 8;
        if (drawFull) {
            context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE, k, j, 16, 16);
        } else if (attackProgress < 1.0F) {
            int l = (int) (attackProgress * 17.0F);
            context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, k, j, 16, 4);
            context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16, 4, 0, 0, k, j, l, 4);
        }
    }

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"))
    private void hotbarOffhandAttackProgress(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci, @Local Arm arm, @Local(ordinal = 0) int halfWidth) {
        ClientPlayerEntity player = this.client.player;

        if (player == null || player.getOffHandStack().isEmpty()) {
            return;
        }

        float attackProgress = this.client.player.dual_wielding$getOffhandAttackCooldownProgress(0.0F);

        if (attackProgress < 1.0F) {
            int n = context.getScaledWindowHeight() - 24 - 18;
            int o;
            if (arm.getOpposite() == Arm.RIGHT) {
                o = halfWidth - 91 - 26;
            } else {
                o = halfWidth + 91 + 10;
            }

            int p = (int) (attackProgress * 19.0F);
            context.drawGuiTexture(HOTBAR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, o, n, 18, 18);
            context.drawGuiTexture(HOTBAR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 18, 18, 0, 18 - p, o, n + 18 - p, 18, p);
        }
    }
}
