package org.example.graphics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.example.algorithm.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;

public class SchedulerGUI extends JFrame {
    private JSpinner numJobsSpinner;
    private List<JobPanel> jobPanels;
    private JPanel jobsContainerPanel;
    private JButton scheduleButton;
    private JPanel chartPanel;
    private DefaultTableModel tableModel;

    public SchedulerGUI() {
        setTitle("Job Shop Scheduler");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel numJobsLabel = new JLabel("Number of Jobs:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(numJobsLabel, gbc);

        numJobsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JTextField spinnerTextField = ((JSpinner.DefaultEditor) numJobsSpinner.getEditor()).getTextField();
        spinnerTextField.setHorizontalAlignment(JTextField.CENTER);
        gbc.gridx = 1;
        gbc.gridy = 0;
        topPanel.add(numJobsSpinner, gbc);

        JButton addJobsButton = new JButton("Add Jobs");
        gbc.gridx = 2;
        gbc.gridy = 0;
        topPanel.add(addJobsButton, gbc);

        addJobsButton.addActionListener(e -> {
            if (validateNumJobs()) {
                addJobPanels();
            }
        });

        scheduleButton = new JButton("Schedule");
        scheduleButton.setEnabled(false);
        gbc.gridx = 3;
        gbc.gridy = 0;
        topPanel.add(scheduleButton, gbc);

        scheduleButton.addActionListener(e -> {
            if (validateTasks()) {
                scheduleJobs();
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);

        jobsContainerPanel = new JPanel();
        jobsContainerPanel.setLayout(new BoxLayout(jobsContainerPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(jobsContainerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);

        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setPreferredSize(new Dimension(400, 300));
        panel.add(chartPanel, BorderLayout.SOUTH);

        JScrollPane tableScrollPane = createTableScrollPane();
        panel.add(tableScrollPane, BorderLayout.EAST);

        add(panel);
    }

    private JScrollPane createTableScrollPane() {
        String[] columnNames = {"Job ID", "Task ID", "Machine", "Start Time", "End Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable scheduleTable = new JTable(tableModel);

        TableCellRenderer rendererFromHeader = scheduleTable.getTableHeader().getDefaultRenderer();
        var headerRenderer = new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = rendererFromHeader.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                ((JLabel) component).setHorizontalAlignment(SwingConstants.CENTER);
                component.setFont(component.getFont().deriveFont(Font.BOLD));
                return component;
            }
        };

        scheduleTable.getTableHeader().setDefaultRenderer(headerRenderer);

        return new JScrollPane(scheduleTable);
    }

    private boolean validateNumJobs() {
        try {
            int numJobs = (int) numJobsSpinner.getValue();
            if (numJobs <= 0) {
                throw new NumberFormatException();
            }
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number of jobs.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void addJobPanels() {
        int numJobs = (int) numJobsSpinner.getValue();
        jobPanels = new ArrayList<>();

        jobsContainerPanel.removeAll();

        for (int i = 0; i < numJobs; i++) {
            JobPanel jobPanel = new JobPanel(i);
            jobPanels.add(jobPanel);
            jobsContainerPanel.add(jobPanel);
        }

        revalidate();
        repaint();
        scheduleButton.setEnabled(true);
    }

    private boolean validateTasks() {
        for (JobPanel jobPanel : jobPanels) {
            if (!jobPanel.validateTasks()) {
                return false;
            }
        }
        return true;
    }

    private void scheduleJobs() {
        List<List<Task>> jobs = new ArrayList<>();
        for (JobPanel jobPanel : jobPanels) {
            jobs.add(jobPanel.getTasks());
        }

        JobShopScheduler scheduler = new JobShopScheduler();
        List<ScheduledTask> scheduledTasks = scheduler.schedule(jobs);
        tableModel.setRowCount(0);
        for (ScheduledTask task : scheduledTasks) {
            Object[] rowData = {
                    task.getTask().getJobId(),
                    task.getTask().getTaskId(),
                    task.getTask().getMachine(),
                    task.getStart(),
                    task.getEnd()
            };
            tableModel.addRow(rowData);
        }
        displayGanttChart(scheduledTasks);
    }

    private void displayGanttChart(List<ScheduledTask> scheduledTasks) {
        TaskSeriesCollection dataset = new TaskSeriesCollection();
        List<TaskSeries> jobSeriesList = new ArrayList<>();

        int maxJobId = scheduledTasks.stream().map(ScheduledTask::getTask).mapToInt(Task::getJobId).max().orElse(0);

        for (int i = 0; i <= maxJobId; i++) {
            jobSeriesList.add(new TaskSeries("Job " + i));
        }

        for (ScheduledTask st : scheduledTasks) {
            int jobId = st.getTask().getJobId();
            TaskSeries jobSeries = jobSeriesList.get(jobId);

            org.jfree.data.gantt.Task jfreeTask = new org.jfree.data.gantt.Task(
                    "Machine " + st.getTask().getMachine(),
                    new SimpleTimePeriod(st.getStart(), st.getEnd())
            );

            jobSeries.add(jfreeTask);
        }

        for (TaskSeries jobSeries : jobSeriesList) {
            dataset.add(jobSeries);
        }

        JFreeChart chart = ChartFactory.createGanttChart(
                "Job Shop Schedule",
                "Machines",
                "Time",
                dataset,
                true,
                true,
                false
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        DateAxis axis = (DateAxis) plot.getRangeAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("SS"));
        axis.setMaximumDate(new Date(scheduledTasks.stream().map(ScheduledTask::getEnd).max(Integer::compare).orElse(0) + 1));


        ChartPanel chartPanelComponent = new ChartPanel(chart);
        chartPanel.removeAll();
        chartPanel.add(chartPanelComponent, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}
