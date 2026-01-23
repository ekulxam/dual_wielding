package survivalblock.dual_wielding.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.dual_wielding.common.injected_interface.Unsyncable;

import java.util.Collection;
import java.util.Set;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

@Mixin(AttributeMap.class)
public class AttributeContainerMixin implements Unsyncable {

    @Unique
    private boolean dual_wielding$unsyncable = false;

    @Override
    public void dual_wielding$setUnsyncable(boolean unsyncable) {
        this.dual_wielding$unsyncable = unsyncable;
    }

    @Inject(method = "onAttributeModified", at = @At("HEAD"), cancellable = true)
    private void noUpdateIfUnsyncable(AttributeInstance instance, CallbackInfo ci) {
        if (this.dual_wielding$unsyncable) {
            ci.cancel();
        }
    }

    @ModifyReturnValue(method = {"getAttributesToSync", "getAttributesToUpdate"}, at = @At("RETURN"))
    private Set<AttributeInstance> alwaysEmpty(Set<AttributeInstance> original) {
        if (this.dual_wielding$unsyncable) {
            original.clear();
        }
        return original;
    }

    @Inject(method = "getSyncableAttributes", at = @At("HEAD"), cancellable = true)
    private void unsyncable(CallbackInfoReturnable<Collection<AttributeInstance>> cir) {
        if (this.dual_wielding$unsyncable) {
            cir.setReturnValue(Set.of());
        }
    }
}
