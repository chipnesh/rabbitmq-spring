package me.chipnesh.test.domain;

public class ByeEvent implements Event {
	private String bye;

	public ByeEvent() {
	}

	public ByeEvent(String bye) {
		this.bye = bye;
	}

	public String getBye() {
		return bye;
	}

	public void setBye(String bye) {
		this.bye = bye;
	}

	@Override
	public String toString() {
		return bye;
	}
}
