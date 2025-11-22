package survivalblock.dual_wielding.mixin.client.entityrenderer;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Definition(id = "dispatcher", field = "Lnet/minecraft/client/render/entity/EntityRenderer;dispatcher:Lnet/minecraft/client/render/entity/EntityRenderDispatcher;")
    @Definition(id = "targetedEntity", field = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;targetedEntity:Lnet/minecraft/entity/Entity;")
    @Expression("? == this.dispatcher.targetedEntity")
    @WrapOperation(method = "hasLabel", at = @At("MIXINEXTRAS:EXPRESSION"), allow = 1)
    private boolean canAlsoBeOffhandTargetedEntity(Object left, Object right, Operation<Boolean> original) {
        return original.call(left, right) || original.call(left, MinecraftClient.getInstance().dual_wielding$getOffhandTargetedEntity());
    }
}
