package survivalblock.dual_wielding.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import survivalblock.dual_wielding.common.injected_interface.IHaveAnotherTarget;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements IHaveAnotherTarget {

    @Nullable
    @Unique
    private Entity dual_wielding$offhandTargetedEntity;

    @Override
    public void dual_wielding$setOffhandTargetedEntity(@Nullable Entity targetedEntity) {
        this.dual_wielding$offhandTargetedEntity = targetedEntity;
    }

    @Override
    public @Nullable Entity dual_wielding$getOffhandTargetedEntity() {
        return this.dual_wielding$offhandTargetedEntity;
    }
}
