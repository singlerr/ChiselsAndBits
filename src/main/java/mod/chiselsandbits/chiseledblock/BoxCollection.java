package mod.chiselsandbits.chiseledblock;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.world.phys.AABB;

public class BoxCollection implements Collection<AABB> {

  private final AABB[][] arrays;

  public BoxCollection(final AABB[] a) {
    arrays = new AABB[1][];
    arrays[0] = a;
  }

  public BoxCollection(final AABB[] a, final AABB[] b) {
    arrays = new AABB[2][];
    arrays[0] = a;
    arrays[1] = b;
  }

  public BoxCollection(final AABB[] a, final AABB[] b, final AABB[] c) {
    arrays = new AABB[3][];
    arrays[0] = a;
    arrays[1] = b;
    arrays[2] = c;
  }

  @Override
  public boolean add(final AABB e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(final Collection<? extends AABB> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean contains(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEmpty() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<AABB> iterator() {
    return new BoxIterator(arrays);
  }

  @Override
  public boolean remove(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int size() {
    int size = 0;

    for (final AABB[] bb : arrays) {
      if (bb != null) {
        size += bb.length;
      }
    }

    return size;
  }

  @Override
  public Object[] toArray() {
    int s = size();
    final AABB[] storage = new AABB[s];

    s = 0;
    for (final AABB bb : this) {
      storage[s++] = bb;
    }

    return storage;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T[] toArray(T[] a) {
    int s = 0;
    for (final AABB bb : this) {
      a[s++] = (T) bb;
    }

    return a;
  }

  static class BoxIterator implements Iterator<AABB> {

    private final AABB[][] arrays;
    private int arrayOffset = 0, idx = -1;

    public BoxIterator(final AABB[][] a) {
      arrays = a;
      findNextItem();
    }

    private void findNextItem() {
      ++idx;

      if (arrays[arrayOffset] == null || idx >= arrays[arrayOffset].length) {
        idx = -1;
        ++arrayOffset;

        if (hasNext()) {
          findNextItem();
        }
      }
    }

    @Override
    public boolean hasNext() {
      return arrays.length > arrayOffset;
    }

    @Override
    public AABB next() {
      final AABB box = arrays[arrayOffset][idx];

      findNextItem();

      return box;
    }

    @Override
    public void remove() {
      throw new RuntimeException("Not Implemented.");
    }
  }
}
