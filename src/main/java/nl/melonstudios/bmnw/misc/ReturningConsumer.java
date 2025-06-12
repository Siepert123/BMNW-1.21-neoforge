package nl.melonstudios.bmnw.misc;

import java.util.function.Function;

@FunctionalInterface
public interface ReturningConsumer<T, R> extends Function<T, R> {
    R accept(T obj);

    @Override
    default R apply(T t) {
        return this.accept(t);
    }
}
