package org.example.graphics;

import javax.swing.*;
import java.awt.*;

public class TaskPanel extends JPanel {
    private final JSpinner machineField;
    private final JSpinner durationField;

    public TaskPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        machineField = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        durationField = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        add(new JLabel("Machine:"));
        add(machineField);
        add(new JLabel("Duration:"));
        add(durationField);
    }

    public boolean validateTask() {
        try {
            int machine = (int) machineField.getValue();
            int duration = (int) durationField.getValue();
            if (machine < 0 || duration <= 0) {
                throw new NumberFormatException();
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public JSpinner getMachineField() {
        return machineField;
    }

    public JSpinner getDurationField() {
        return durationField;
    }
}
