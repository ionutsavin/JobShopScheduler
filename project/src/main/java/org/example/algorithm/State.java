package org.example.algorithm;

import java.util.ArrayList;
import java.util.List;

public class State {
    private final int[] machineAvailable;
    private final int[] jobNextTask;
    private final List<ScheduledTask> scheduledTasks;
    private int currentTime;

    public State(int numMachines, int numJobs) {
        this.machineAvailable = new int[numMachines];
        this.jobNextTask = new int[numJobs];
        this.scheduledTasks = new ArrayList<>();
        this.currentTime = 0;
    }

    public State(State other) {
        this.machineAvailable = other.machineAvailable.clone();
        this.jobNextTask = other.jobNextTask.clone();
        this.scheduledTasks = new ArrayList<>(other.scheduledTasks);
        this.currentTime = other.currentTime;
    }

    public boolean isComplete(List<List<Task>> jobs) {
        for (int i = 0; i < jobNextTask.length; i++) {
            if (jobNextTask[i] < jobs.get(i).size()) {
                return false;
            }
        }
        return true;
    }

    public void scheduleTask(Task task, int start, int end) {
        machineAvailable[task.getMachine()] = end;
        jobNextTask[task.getJobId()]++;
        scheduledTasks.add(new ScheduledTask(start, end, task));
        currentTime = Math.max(currentTime, end);
    }

    public int getEarliestStartTime(Task task) {
        int jobId = task.getJobId();
        int taskId = task.getTaskId();

        int earliestStart = machineAvailable[task.getMachine()];
        if (taskId > 0) {
            for (ScheduledTask scheduledTask : scheduledTasks) {
                if (scheduledTask.getTask().getJobId() == jobId && scheduledTask.getTask().getTaskId() == taskId - 1) {
                    earliestStart = Math.max(earliestStart, scheduledTask.getEnd());
                    break;
                }
            }
        }
        return earliestStart;
    }

    public List<ScheduledTask> getScheduledTasks() {
        return scheduledTasks;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public int[] getJobNextTask() {
        return jobNextTask;
    }
}