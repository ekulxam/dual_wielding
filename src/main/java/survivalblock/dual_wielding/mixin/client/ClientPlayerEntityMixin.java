package survivalblock.dual_wielding.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import survivalblock.dual_wielding.mixin.PlayerEntityMixin;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntityMixin {
    @Shadow
    @Final
    protected Minecraft minecraft;

    public ClientPlayerEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    public boolean dual_wielding$shouldAttackWithOffhand() {
        return super.dual_wielding$shouldAttackWithOffhand() && this.minecraft.dual_wielding$getOffhandTargetedEntity() != null;
    }
}
