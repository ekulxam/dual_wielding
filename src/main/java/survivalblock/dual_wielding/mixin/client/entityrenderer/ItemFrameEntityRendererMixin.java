package survivalblock.dual_wielding.mixin.client.entityrenderer;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemFrameRenderer.class)
public class ItemFrameEntityRendererMixin {

    @Definition(id = "dispatcher", field = "Lnet/minecraft/client/renderer/entity/ItemFrameRenderer;entityRenderDispatcher:Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;")
    @Definition(id = "targetedEntity", field = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;crosshairPickEntity:Lnet/minecraft/world/entity/Entity;")
    @Expression("this.dispatcher.targetedEntity == ?")
    @WrapOperation(method = "shouldShowName(Lnet/minecraft/world/entity/decoration/ItemFrame;)Z", at = @At("MIXINEXTRAS:EXPRESSION"), allow = 1)
    private boolean canAlsoBeOffhandTargetedEntity(Object left, Object right, Operation<Boolean> original) {
        return original.call(left, right) || original.call(left, Minecraft.getInstance().dual_wielding$getOffhandTargetedEntity());
    }
}
