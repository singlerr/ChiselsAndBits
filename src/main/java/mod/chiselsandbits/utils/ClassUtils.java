package mod.chiselsandbits.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClassUtils {
    private ClassUtils() {
        throw new IllegalStateException("Can not instantiate an instance of: ClassUtils. This is a utility class");
    }

    @Nullable
    public static Class<?> getDeclaringClass(final Class<?> blkClass, final String methodName, final Class<?>... args) {
        final ClassLookupResult result = lookupResult(blkClass, methodName, args);
        if (result.isPresent()) {
            return result.clazz();
        }

        return null;
    }

    @NotNull
    private static ClassLookupResult lookupResult(
            final Class<?> blkClass, final String methodName, final Class<?>... args) {

        try {
            return new ClassLookupResult(blkClass.getMethod(methodName, args).getDeclaringClass(), true);
        } catch (final NoSuchMethodException e) {
            // nothing here...
        } catch (final SecurityException e) {
            // nothing here..
        } catch (final Throwable e) {
            e.printStackTrace();
            return new ClassLookupResult(null, false);
        }

        if (blkClass.getSuperclass() == null) {
            return new ClassLookupResult(null, false);
        }

        return lookupResult(blkClass.getSuperclass(), methodName, args);
    }

    private record Key(Class<?> sourceClass, String methodName, Class<?>... args) {}

    private record ClassLookupResult(@Nullable Class<?> clazz, boolean isPresent) {}
}
