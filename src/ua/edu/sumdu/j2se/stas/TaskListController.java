package ua.edu.sumdu.j2se.stas;

import ua.edu.sumdu.j2se.stas.tasks.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TaskListController {

    private static LinkedTaskList list = new LinkedTaskList();
    private static Thread thread;
    private static NotificationManager notificationManager;
    private static Scanner sc = new Scanner(System.in);


    public static boolean isEmpty() {
        return list.size() <= 0;
    }

    public static void editTask(TaskModel oldTask, TaskModel newTask) {
        if (newTask != null) {
            notificationManager.add(newTask);
            list.add(newTask);
        }
        if (oldTask != null) {
            notificationManager.remove(oldTask);
            list.remove(oldTask);
        }
        thread.interrupt();
        thread = new Thread(notificationManager);
        thread.start();
    }

    public static void printCalendar(SortedMap<Date, Set<TaskModel>> calendar) {
        for (SortedMap.Entry<Date, Set<TaskModel>> entry : calendar.entrySet()) {
            System.out.print(entry.getKey() + ":");
            boolean flag = false;
            for (TaskModel task : entry.getValue()) {
                if (flag)
                    System.out.print("                            :");
                System.out.println(task);
                flag = true;
            }
        }
    }

    public static String question(String string) {
        System.out.print(string);
        return sc.nextLine();
    }

    public static void main(String[] args) {
        String line;

        System.out.print("You want load old task list, otherwise it will be deleted?[Y,n]:");
        line = sc.nextLine();
        File file = new File("TaskListModel");
        if (line.equals("Y") || line.equals("y") || line.equals("")) {
            if (file.exists())
                TaskIOModel.readText(list, file);
            else
                System.out.println("Your old task list is lost.");
        }

        notificationManager = new NotificationManager(TasksModel.calendar(list, new Date(), new Date(new Date().getTime() + 1000 * 60 * 60 * 24)));
        thread = new Thread(notificationManager);
        thread.start();

        line = question("Menu task manager: edit, add, remove, show, calendar, quit : ");
        while (!line.equals("quit")) {
            switch (line) {
                case "edit":
                    if (isEmpty()) {
                        System.out.println("empty list!");
                        break;
                    }
                    TaskModel editTask = list.getTask(Integer.valueOf(question("Menu edit task. input index task:")));
                    String subMenu = question(editTask + ". What you want change(title,time or activity)?");
                    switch (subMenu) {
                        case "title":
                            editTask(editTask, editTask.clone().setTitle(question("Input new task title:")));
                            break;
                        case "time":
                            Date start = null, end = null, interval;
                            SimpleDateFormat timeForm = new SimpleDateFormat("yyyy-MM-dd HH-mm");
                            SimpleDateFormat interForm = new SimpleDateFormat("dd-HH-mm-ss");
                            try {
                                start = timeForm.parse(question("Input new task start time[yyyy-mm-dd hh-mm]:"));
                                end = timeForm.parse(question("Input new task end time, empty for single task:"));
                                interval = interForm.parse(question("Input new interval [dd-hh-mm-ss]:"));
                                editTask(editTask, editTask.clone().setTime(start, end, (int) interval.getTime() / 1000 + 60 * 60 * 26));
                            } catch (ParseException e) {
                                if (start != null && end == null)
                                    editTask(editTask, editTask.clone().setTime(start));
                                else
                                    System.out.println("incorrect format date");
                            }
                            break;
                        case "activity":
                            editTask(editTask, editTask.clone().setActive(!editTask.isActive()));
                            break;
                    }
                    break;
                case "add":
                    ArrayTaskList single = new ArrayTaskList();
                    TaskIOModel.read(single, new StringReader(question("input new task (format: \"task name\" at [year-mm-dd hh:mm:ss.000])" +
                            " or " + System.lineSeparator() + "\"task name\" from [year-mm-dd hh:mm:ss.000] to [year-mm-dd hh:mm:ss.000] every [2 day(s), 60 second(s)];")));
                    editTask(null, single.getTask(0));
                    break;
                case "remove":
                    try {
                        editTask(list.getTask(Integer.valueOf(question("Remove task.input index task:"))), null);
                    } catch (RuntimeException e) {
                        System.out.println("Exception: "+ e.getMessage());
                    }
                    break;
                case "show":
                    for (int i = 0; i < list.size(); i++) {
                        System.out.println(i + ": " + list.getTask(i));
                    }
                    break;
                case "calendar":
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date start = formatter.parse(question("input start date year-mm-dd"));
                        Date end = formatter.parse(question("input end date year-mm-dd"));
                        SortedMap<Date, Set<TaskModel>> calendar = TasksModel.calendar(list, start, end);
                        printCalendar(calendar);
                    } catch (ParseException e) {
                        System.out.println("incorrect format date");
                    }
                    break;
                case "quit":
                    TaskIOModel.writeText(list, file);
                    break;
                default:
                    System.out.println("unknown operation");
                    break;
            }
            line = sc.nextLine();
        }
    }
}
