package ua.edu.sumdu.j2se.stas;

import ua.edu.sumdu.j2se.stas.controller.TaskListController;
import ua.edu.sumdu.j2se.stas.tasks.TaskModel;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class NotificationManager implements Runnable {

    private TreeMap<Date, Set<TaskModel>> taskList;

    public NotificationManager(SortedMap<Date, Set<TaskModel>> list) {
        this.taskList = (TreeMap<Date, Set<TaskModel>>) list;
    }

    public void add(TaskModel task) {
        Set<TaskModel> tempTaskSet;
        Date tempDate = new Date();
        TaskListController.logger.info("Add task in notification manager");
        while (task.nextTimeAfter(tempDate) != null) {
            tempDate = new Date(task.nextTimeAfter(tempDate).getTime());
            tempTaskSet = taskList.get(tempDate);
            if (tempTaskSet == null) {
                tempTaskSet = new HashSet<>();
                tempTaskSet.add(task);
                taskList.put(tempDate, tempTaskSet);
            } else {
                tempTaskSet.add(task);
            }
            if (!task.isRepeated())
                break;
        }
    }

    public void remove(TaskModel task) {
        TaskListController.logger.info("Remove task in notification manager");
        for (Map.Entry<Date, Set<TaskModel>> entry : taskList.entrySet()) {
            for (TaskModel loopTask : entry.getValue()) {
                if (loopTask.equals(task)) {
                    entry.getValue().remove(task);
                }
            }
        }
    }

    @Override
    public void run() {
        long interval;
        Set<TaskModel> taskSet;
        try {
            while ((interval = taskList.firstKey().getTime() - new Date().getTime()) < 0) {
                taskList.remove(taskList.firstKey());
            }
            taskSet = taskList.get(taskList.firstKey());
            TaskListController.logger.info("Start timer for earliest tasks:");
        } catch (NoSuchElementException e) {
            TaskListController.logger.info("Not found tasks for notification");
            return;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(interval);
        } catch (InterruptedException e) {
            TaskListController.logger.info("Interrupted " + Thread.currentThread().getName());
        }
        StringBuilder notification = new StringBuilder("Notification: ");
        for (TaskModel task : taskSet) {
            notification.append(task.getTitle() + ", ");
        }
        TaskListController.logger.info("\nNotified: "+notification+"\n");
        System.out.println(notification.substring(0, notification.length() - 2));
        taskList.remove(taskList.firstKey());
        Thread newThread = new Thread(this);
        newThread.start();
    }

}
