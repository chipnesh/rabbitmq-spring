package me.chipnesh.test.domain;

public interface Event {
	default String getEventType() {
		return this.getClass().getSimpleName();
	}
}
