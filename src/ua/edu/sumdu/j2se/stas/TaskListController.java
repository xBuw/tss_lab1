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
        File file = new File("TaskListModel");
        String line;
        line = question("You want load old task list, otherwise it will be deleted?[Y,n]:");
        if (line.equals("Y") || line.equals("y") || line.equals("")) {
            if (file.exists())
                TaskIOModel.readText(list, file);
            else
                System.out.println("Sorry...your task list is lost...");
        }
        notificationManager = new NotificationManager(TasksModel.calendar(list, new Date(), TasksModel.getLaterDate(list)));
        thread = new Thread(notificationManager);
        thread.start();
        line = question("Menu task manager: edit, add, remove, show, calendar, quit : ");
        while (!line.equals("quit")) {
            try {
                switch (line) {
                    case "edit":
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
                                start = timeForm.parse(question("Input new task start time[yyyy-mm-dd hh-mm]:"));
                                String endString = question("Input new task end time, empty for single task:");
                                if (endString.equals(""))
                                    editTask(editTask, editTask.clone().setTime(start));
                                else {
                                    end = timeForm.parse(endString);
                                    interval = interForm.parse(question("Input new interval [dd-hh-mm-ss]:"));
                                    editTask(editTask, editTask.clone().setTime(start, end, (int) interval.getTime() / 1000 + 60 * 60 * 26));
                                }
                                break;
                            case "activity":
                                editTask(editTask, editTask.clone().setActive(!editTask.isActive()));
                                break;
                            default:
                                System.out.println("Wrong argument");
                        }
                        break;
                    case "add":
                        ArrayTaskList single = new ArrayTaskList();
                        TaskIOModel.read(single, new StringReader(question("Input new task. Format: (\"task name\" at [year-mm-dd hh:mm:ss.000];)" +
                                " or " + System.lineSeparator() + ("\"task name\" from [year-mm-dd hh:mm:ss.000] to [year-mm-dd hh:mm:ss.000] every [2 day(s), 60 second(s)];)"))));
                        editTask(null, single.getTask(0));
                        break;
                    case "remove":
                        editTask(list.getTask(Integer.valueOf(question("Remove task.input index task:"))), null);
                        break;
                    case "show":
                        for (int i = 0; i < list.size(); i++) {
                            System.out.println(i + ": " + list.getTask(i));
                        }
                        break;
                    case "calendar":
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date start = formatter.parse(question("input start date year-mm-dd: "));
                        Date end = formatter.parse(question("input end date year-mm-dd: "));
                        SortedMap<Date, Set<TaskModel>> calendar = TasksModel.calendar(list, start, end);
                        printCalendar(calendar);
                        break;
                    case "quit":
                        TaskIOModel.writeText(list, file);
                        break;
                }
            } catch (RuntimeException e) {
                System.out.println("Exception: " + e.getMessage());
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            } finally {
                line = question("Menu task manager: edit, add, remove, show, calendar, quit : ");
            }
        }
    }
}
