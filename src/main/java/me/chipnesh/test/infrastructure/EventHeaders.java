package me.chipnesh.test.infrastructure;

public final class EventHeaders {
	public static final String EVENT_TYPE = "x-event-type";
	public static final String CURRENT_RETRY = "x-current-retry";
	public static final String RETRY_COUNT = "x-retry-count";
	public static final String TIMEOUT = "x-message-ttl";
	public static final String STACK_TRACE = "x-exception-stacktrace";
	public static final String ERROR_MESSAGE = "x-exception-message";
}
