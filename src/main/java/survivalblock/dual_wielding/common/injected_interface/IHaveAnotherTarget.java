package survivalblock.dual_wielding.common.injected_interface;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface IHaveAnotherTarget {

    default @Nullable Entity dual_wielding$getOffhandTargetedEntity() {
        throw new UnsupportedOperationException();
    }

    default void dual_wielding$setOffhandTargetedEntity(@Nullable Entity targetedEntity) {
        throw new UnsupportedOperationException();
    }
}
