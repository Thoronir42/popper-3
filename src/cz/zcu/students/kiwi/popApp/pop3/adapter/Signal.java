package cz.zcu.students.kiwi.popApp.pop3.adapter;

public class Signal {

	private final Type type;
	private final String message;

	public Signal(Type type) {
		this(type, "");
	}

	public Signal(Type type, String message) {
		this.type = type;
		this.message = message;
	}

	public Type getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public enum Type {
		ConnectingTimedOut,
		ConnectionNoRouteToHost,
		ConnectingFailedUnexpectedError,

		ConnectionEstablished,
		ConnectionReset,
		UnknownHost,
	}
}
