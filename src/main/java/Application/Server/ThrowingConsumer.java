package Application.Server;

@FunctionalInterface
interface ThrowingConsumer<T, E extends Exception> {
    void accept(T t) throws E;
}