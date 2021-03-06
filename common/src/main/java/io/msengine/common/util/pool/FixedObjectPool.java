package io.msengine.common.util.pool;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Supplier;

public class FixedObjectPool<T> extends ObjectPool<T> {
	
	private final Queue<Holder> pool = new ArrayDeque<>();
	
	public FixedObjectPool(Supplier<T> provider, int size) {
		try {
			for (int i = 0; i < size; ++i) {
				this.pool.add(new Holder(provider.get()));
			}
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("The provider caused an exception.", e);
		}
	}
	
	@Override
	protected Holder innerAcquire() throws NoSuchElementException {
		if (this.pool.isEmpty()) {
			throw new NoSuchElementException("No more object in this pool, be careful of objects leaks or increase the max count !");
		} else {
			return this.pool.poll();
		}
	}
	
	@Override
	protected void innerRelease(Holder obj) {
		this.pool.add(obj);
	}
	
	@Override
	public boolean hasMore() {
		return !this.pool.isEmpty();
	}
	
	public static <U> ObjectPool<U> newFixed(Supplier<U> provider, int size) {
		return new FixedObjectPool<>(provider, size);
	}
	
	public static <U> ObjectPool<U> newSyncFixed(Supplier<U> provider, int size) {
		return new FixedObjectPool<>(provider, size).toSynchronized();
	}
	
}
