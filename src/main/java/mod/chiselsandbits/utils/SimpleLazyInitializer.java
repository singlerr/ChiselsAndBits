package mod.chiselsandbits.utils;

import java.util.function.Supplier;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;

public final class SimpleLazyInitializer<T> extends LazyInitializer<T> {

  private final Supplier<T> factory;

  public SimpleLazyInitializer(Supplier<T> factory) {
    this.factory = factory;
  }

  public static <T> SimpleLazyInitializer<T> of(Supplier<T> factory) {
    return new SimpleLazyInitializer<>(factory);
  }

  @Override
  protected T initialize() throws ConcurrentException {
    return factory.get();
  }
}
