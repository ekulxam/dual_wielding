package survivalblock.dual_wielding.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapMethod(method = "renderLevel")
    private void maybeUseOffhandResult(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, Operation<Void> original) {
        Entity targetedEntity = null;
        try {
            targetedEntity = this.minecraft.crosshairPickEntity;
            if (targetedEntity == null && this.minecraft.dual_wielding$getOffhandTargetedEntity() != null) {
                this.minecraft.crosshairPickEntity = this.minecraft.dual_wielding$getOffhandTargetedEntity();
            }
            original.call(deltaTracker, renderBlockOutline, camera, gameRenderer, lightTexture, frustumMatrix, projectionMatrix);
        } finally {
            this.minecraft.crosshairPickEntity = targetedEntity;;
        }
    }
}
