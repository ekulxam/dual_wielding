package survivalblock.dual_wielding.common.injected_interface;

public interface PreviouslyOffhandedSource {
    default void dual_wielding$setWasPreviouslyOffhanding(boolean previouslyOffhanding) {
        throw new UnsupportedOperationException();
    }
}
