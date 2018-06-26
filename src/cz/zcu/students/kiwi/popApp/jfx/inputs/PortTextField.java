package cz.zcu.students.kiwi.popApp.jfx.inputs;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextField;

public class PortTextField extends TextField {

	private static final int MAX_PORT = 65535, MIN_PORT = 0;

	private final SimpleIntegerProperty Port;

	public void setPort(int port) {
		this.Port.set(port);
	}

	public int getPort() {
		return this.getPort(false);
	}

    public int getPort(boolean tryPrompt) {
        int i = this.Port.get();
        if (i == -1 && tryPrompt) {
            i = Integer.parseInt(this.getPromptText());
            System.out.println(i);
        }
        return i;
	}

	public PortTextField() {
		this.Port = createPortProperty();
	}

	public PortTextField(int serverPort) {
		this();
		this.Port.set(serverPort);

		this.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				setText(newValue.replaceAll("[^\\d]", ""));
				return;
			}
			if (newValue.length() < 1) {
				Port.set(-1);
				return;
			}

			try {
				int n = Integer.parseInt(newValue);
				Port.set(n);
			} catch (NumberFormatException ex) {
				System.err.println("Format exception: " + ex);
				setText(Port.get() + "");
			}
		});
	}

	private SimpleIntegerProperty createPortProperty() {
		SimpleIntegerProperty port = new SimpleIntegerProperty(-1);

		port.addListener((observable, oldValue, newValue) -> {
            int newInt = newValue.intValue();
            if(newInt == -1) {
                return;
            }

		    if (newInt > MAX_PORT) {
				port.set(MAX_PORT);
				return;
			}
			if (newInt < MIN_PORT) {
				port.set((oldValue.intValue() < MIN_PORT) ? MIN_PORT : oldValue.intValue());
				return;
			}
			setText(newInt + "");
		});

		return port;
	}
}
