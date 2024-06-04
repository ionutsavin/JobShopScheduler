package org.example.algorithm;

public class Task {
    private final int jobId;
    private final int taskId;
    private final int machine;
    private final int duration;

    public Task(int jobId, int taskId, int machine, int duration) {
        this.jobId = jobId;
        this.taskId = taskId;
        this.machine = machine;
        this.duration = duration;
    }

    public int getJobId() {
        return jobId;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getMachine() {
        return machine;
    }

    public int getDuration() {
        return duration;
    }
}

