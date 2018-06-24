package cz.zcu.students.kiwi.popApp.pop3.adapter;

public interface ISignalHandler {
	/**
	 * Used to signalizes processor of TcpConnection status changes.
	 *
	 * @param signal signal to be processed
	 */
	void signal(Signal signal);
}
