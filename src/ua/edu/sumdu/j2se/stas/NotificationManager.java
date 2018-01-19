package ua.edu.sumdu.j2se.stas;

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
        } catch (NoSuchElementException e) {
            return;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(interval);
        } catch (InterruptedException e) {
            System.out.println("interrupted " + Thread.currentThread().getName());
        }
        StringBuilder notification = new StringBuilder("Notification: ");
        for (TaskModel task : taskSet) {
            notification.append(task.getTitle() + ", ");
        }
        System.out.println(notification.substring(0, notification.length() - 2));
        taskList.remove(taskList.firstKey());
        Thread newThread = new Thread(this);
        newThread.start();
    }

}
