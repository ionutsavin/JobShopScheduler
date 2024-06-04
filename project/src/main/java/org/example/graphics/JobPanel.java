package org.example.graphics;

import org.example.algorithm.Task;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JobPanel extends JPanel {
    private final int jobId;
    private final List<TaskPanel> taskPanels;
    private final JSpinner numTasksField;

    public JobPanel(int jobId) {
        this.jobId = jobId;
        this.taskPanels = new ArrayList<>();
        setLayout(new BorderLayout());
        add(new JLabel("Job " + jobId), BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new FlowLayout());
        numTasksField = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        inputPanel.add(new JLabel("Number of Tasks:"));
        inputPanel.add(numTasksField);
        JButton addTasksButton = new JButton("Add Tasks");
        inputPanel.add(addTasksButton);

        addTasksButton.addActionListener(e -> {
            if (validateNumTasks()) {
                addTaskPanels();
            }
        });

        add(inputPanel, BorderLayout.CENTER);

        JPanel tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        add(tasksPanel, BorderLayout.SOUTH);
    }

    private boolean validateNumTasks() {
        try {
            int numTasks = (int) numTasksField.getValue();
            if (numTasks <= 0) {
                throw new NumberFormatException();
            }
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(getTopLevelAncestor(), "Please enter a valid number of tasks.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void addTaskPanels() {
        int numTasks = (int) numTasksField.getValue();
        JPanel tasksPanel = (JPanel) getComponent(2);
        tasksPanel.removeAll();
        taskPanels.clear();

        for (int j = 0; j < numTasks; j++) {
            TaskPanel taskPanel = new TaskPanel();
            taskPanels.add(taskPanel);
            JPanel taskLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            taskLabelPanel.add(new JLabel("Task " + j + ":"));
            tasksPanel.add(taskLabelPanel);
            tasksPanel.add(taskPanel);
        }

        revalidate();
        repaint();
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        int taskId = 0;
        for (TaskPanel taskPanel : taskPanels) {
            int machine = (int) taskPanel.getMachineField().getValue();
            int duration = (int) taskPanel.getDurationField().getValue();
            tasks.add(new Task(jobId, taskId++, machine, duration));
        }
        return tasks;
    }

    public boolean validateTasks() {
        for (TaskPanel taskPanel : taskPanels) {
            if (!taskPanel.validateTask()) {
                JOptionPane.showMessageDialog(getTopLevelAncestor(), "Please enter valid task data for Job " + jobId + ".", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        if (taskPanels.isEmpty()) {
            JOptionPane.showMessageDialog(getTopLevelAncestor(), "Please add at least one task for Job " + jobId + ".", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
