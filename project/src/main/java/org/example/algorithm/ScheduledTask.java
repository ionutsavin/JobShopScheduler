package org.example.algorithm;

public class ScheduledTask {
    private final int start;
    private final int end;
    private final Task task;

    public ScheduledTask(int start, int end, Task task) {
        this.start = start;
        this.end = end;
        this.task = task;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Task getTask() {
        return task;
    }
}