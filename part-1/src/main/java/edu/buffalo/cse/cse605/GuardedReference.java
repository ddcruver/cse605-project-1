package edu.buffalo.cse.cse605;

public class GuardedReference<T> {
	private T value;

	public GuardedReference(T value) {
		this.value = value;
	}

	public synchronized T get() {
		return value;
	}

	public synchronized void set(T value) {
		this.value = value;
	}
}
