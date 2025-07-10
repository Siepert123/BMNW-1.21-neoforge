package nl.melonstudios.bmnw.interfaces;

@SuppressWarnings("unchecked")
public interface ICanBeCasted {
    default <T> T cast() {
        return (T) this;
    }

    default <T> T cast(Class<T> clazz) {
        assert clazz.isInstance(this);
        return (T) this;
    }

    static <T> T cast(Object obj, Class<T> clazz) {
        assert clazz.isInstance(obj);
        return (T) obj;
    }
}
