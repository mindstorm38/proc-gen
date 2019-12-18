package fr.theorozier.procgen.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 *
 * Immutable set that contains the two boolean TRUE and FALSE.
 *
 * @author Theo Rozier
 *
 */
public class BooleanSet implements Set<Boolean> {
	
	public static final BooleanSet IMMUTABLE = new BooleanSet();
	
	private BooleanSet() {}
	
	private static class BooleansIterator implements Iterator<Boolean> {
		
		private byte next = 0;
		
		@Override
		public boolean hasNext() {
			return next < 2;
		}
		
		@Override
		public Boolean next() {
			if (next > 1) throw new NoSuchElementException();
			return (next++) == 1;
		}
		
	}
	
	@Override
	public int size() {
		return 2;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public boolean contains(Object o) {
		return o == Boolean.TRUE || o == Boolean.FALSE;
	}
	
	@Override
	public Iterator<Boolean> iterator() {
		return new BooleansIterator();
	}
	
	@Override
	public Object[] toArray() {
		return new Boolean[] {false, true};
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		
		if (a.getClass().getComponentType() != boolean.class && a.getClass().getComponentType() != Boolean.class)
			throw new IllegalArgumentException("Invalid array type !");
		
		if (a.length != 2)
			throw new IllegalArgumentException("Invalid array length (must have length of 2).");
		
		Boolean[] ba = (Boolean[]) a;
		
		ba[0] = false;
		ba[1] = true;
		
		return a;
		
	}
	
	@Override
	public boolean add(Boolean aBoolean) {
		throw new UnsupportedOperationException("Can't change boolean set structure.");
	}
	
	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("Can't change boolean set structure.");
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return c.contains(false) && c.contains(true);
	}
	
	@Override
	public boolean addAll(Collection<? extends Boolean> c) {
		throw new UnsupportedOperationException("Can't change boolean set structure.");
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Can't change boolean set structure.");
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Can't change boolean set structure.");
	}
	
	@Override
	public void clear() {
		throw new UnsupportedOperationException("Can't change boolean set structure.");
	}
	
}
