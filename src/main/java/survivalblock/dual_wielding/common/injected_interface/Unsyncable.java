package survivalblock.dual_wielding.common.injected_interface;

public interface Unsyncable {

    default void dual_wielding$setUnsyncable(boolean unsyncable) {
        throw new UnsupportedOperationException();
    }
}
