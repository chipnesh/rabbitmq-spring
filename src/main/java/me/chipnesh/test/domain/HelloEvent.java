package me.chipnesh.test.domain;

public class HelloEvent implements Event {
	private String greeting;

	public HelloEvent() {
	}

	public HelloEvent(String greeting) {
		this.greeting = greeting;
	}

	public String getGreeting() {
		return greeting;
	}

	public void setGreeting(String greeting) {
		this.greeting = greeting;
	}

	@Override
	public String toString() {
		return greeting;
	}
}
