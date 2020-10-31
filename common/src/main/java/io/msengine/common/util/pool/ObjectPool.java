package io.msengine.common.util.pool;

import java.util.NoSuchElementException;

public abstract class ObjectPool<T> {
	
	/**
	 * <p>Acquire an object from this pool.</p>
	 * <p><i><b>You can call this in a try-for-resource block an the object will be release automatically when leave the block.</b></i></p>
	 * @return Acquired object.
	 * @throws NoSuchElementException If no more object can be acquired.
	 */
	public Holder acquire() throws NoSuchElementException {
		Holder obj = this.innerAcquire();
		obj.acq = true;
		return obj;
	}
	
	/**
	 * Release an previously acquired pool object.
	 * @param obj The pool object belong to this pool.
	 * @throws IllegalArgumentException If the given object does not belong to this pool.
	 */
	public void release(Holder obj) throws IllegalArgumentException {
		if (obj.getPool() != this) {
			throw new IllegalArgumentException("This object does not belong to this pool.");
		} else {
			if (obj.acq) {
				this.innerRelease(obj);
				obj.acq = false;
			}
		}
	}
	
	/**
	 * Internal method called to acquire next object available in the pool.
	 * <b>The returned object is set to acquired.</b>
	 * @return The object acquired.
	 * @throws NoSuchElementException If no more object can be acquired.
	 */
	protected abstract Holder innerAcquire() throws NoSuchElementException;
	
	/**
	 * Internal method that <i><b>must only be called</b></i> after the obj was checked to
	 * belong to this pool, and the object is not already acquired. <b>After this method is
	 * called, the given object is set to not acquired.</b>
	 * @param obj The object to release.
	 */
	protected abstract void innerRelease(Holder obj);
	
	/**
	 * @return True if an object can be acquired.
	 */
	public abstract boolean hasMore();
	
	/**
	 * @return A synchronized wrapper for this object pool.
	 */
	public ObjectPool<T> toSynchronized() {
		return this.new SynchronizedObjectPool();
	}
	
	public class Holder implements AutoCloseable {
		
		private final T obj;
		private boolean acq;
		
		protected Holder(T obj) {
			this.obj = obj;
			
		}
		
		public T get() {
			return this.obj;
		}
		
		public ObjectPool<T> getPool() {
			return ObjectPool.this;
		}
		
		@Override
		public void close() {
			ObjectPool.this.release(this);
		}
		
	}
	
	protected class SynchronizedObjectPool extends ObjectPool<T> {
		
		@Override
		public synchronized Holder acquire() throws NoSuchElementException {
			return ObjectPool.this.acquire();
		}
		
		@Override
		public synchronized void release(Holder obj) throws IllegalArgumentException {
			ObjectPool.this.release(obj);
		}
		
		@Override
		protected Holder innerAcquire() throws NoSuchElementException {
			throw new UnsupportedOperationException("This method must not be called in synchronized ObjectPool.");
		}
		
		@Override
		protected void innerRelease(Holder obj) {
			throw new UnsupportedOperationException("This method must not be called in synchronized ObjectPool.");
		}
		
		@Override
		public synchronized boolean hasMore() {
			return ObjectPool.this.hasMore();
		}
		
		@Override
		public ObjectPool<T> toSynchronized() {
			return this;
		}
		
	}

}
