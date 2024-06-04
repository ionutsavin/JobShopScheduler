package org.example.algorithm;

import java.util.ArrayList;
import java.util.List;

public class JobShopScheduler {
    private int bestScheduleTime = Integer.MAX_VALUE;
    private List<ScheduledTask> bestSchedule = null;

    public List<ScheduledTask> schedule(List<List<Task>> jobs) {
        int numMachines = getNumMachines(jobs);
        State initialState = new State(numMachines, jobs.size());
        backtracking(initialState, jobs);

        return bestSchedule;
    }

    private void backtracking(State state, List<List<Task>> jobs) {
        if (state.isComplete(jobs)) {
            if (state.getCurrentTime() < bestScheduleTime) {
                bestScheduleTime = state.getCurrentTime();
                bestSchedule = new ArrayList<>(state.getScheduledTasks());
            }
            return;
        }

        for (int jobId = 0; jobId < jobs.size(); jobId++) {
            int taskId = state.getJobNextTask()[jobId];
            if (taskId < jobs.get(jobId).size()) {
                Task task = jobs.get(jobId).get(taskId);
                int startTime = state.getEarliestStartTime(task);
                int endTime = startTime + task.getDuration();

                State newState = new State(state);
                newState.scheduleTask(task, startTime, endTime);

                if (endTime < bestScheduleTime) {
                    backtracking(newState, jobs);
                }
            }
        }
    }

    private int getNumMachines(List<List<Task>> jobs) {
        return jobs.stream()
                .flatMap(List::stream)
                .mapToInt(Task::getMachine)
                .max()
                .orElse(0) + 1;
    }
}
