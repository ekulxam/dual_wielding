package survivalblock.dual_wielding.common.injected_interface;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface IHaveAnotherTarget {

    @Nullable
    default Entity dual_wielding$get
}
