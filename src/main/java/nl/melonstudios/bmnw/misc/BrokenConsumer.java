package nl.melonstudios.bmnw.misc;

@FunctionalInterface
public interface BrokenConsumer<T> extends ReturningConsumer<T, Boolean> {
    Boolean accept(T obj);
}
