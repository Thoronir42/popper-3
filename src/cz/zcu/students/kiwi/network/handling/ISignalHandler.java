package cz.zcu.students.kiwi.network.handling;

public interface ISignalHandler {
	/**
	 * Used to signalizes processor of TcpConnection status changes.
	 *
	 * @param signal signal to be processed
	 */
	void signal(Signal signal);
}
