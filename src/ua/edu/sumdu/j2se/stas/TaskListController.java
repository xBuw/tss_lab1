package ua.edu.sumdu.j2se.stas;

import ua.edu.sumdu.j2se.stas.tasks.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TaskListController {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line;
        System.out.print("You want load old task list, otherwise it will be deleted?[Y,n]:");
        line = sc.nextLine();
        LinkedTaskList list = new LinkedTaskList();
        File file = new File("TaskListModel");
        if (line.equals("Y") || line.equals("y") || line.equals("")) {
            if (file.exists())
                TaskIOModel.readText(list, file);
            else
                System.out.println("Your old task list is lost.");
        }
        NotificationManager notificationManager = new NotificationManager(TasksModel.calendar(list, new Date(), new Date(new Date().getTime()+1000*60*60*24)));
        Thread thread = new Thread(notificationManager);
        thread.start();

        line = new String("menu");
        System.out.println("Menu task manager: menu, edit, add, remove, show, calendar, quit");
        while (!line.equals("quit")) {
            line = sc.nextLine();
            switch (line) {
                case "menu":
                    System.out.println("Menu task manager: menu, edit, add, remove, show, calendar, quit");
                    break;
                case "edit":
                    if (list.size() <= 0) {
                        System.out.println("empty list!");
                        break;
                    }
                    System.out.println("Menu edit task: ");
                    System.out.println("input index task:");
                    TaskModel editTask = list.getTask(sc.nextInt());
                    sc.nextLine();
                    System.out.println(editTask+". What you want change(title,time or activity)?");
                    switch (sc.nextLine()) {
                        case "title":
                            System.out.print("Input new task title:");
                            String newTitle = new String(sc.nextLine());
                            thread.interrupt();
                            notificationManager.remove(editTask);
                            editTask.setTitle(newTitle);
                            notificationManager.add(editTask);
                            thread = new Thread(notificationManager);
                            thread.start();
                            break;
                        case "time":
                            Date start, end, interval;
                            SimpleDateFormat timeForm = new SimpleDateFormat("yyyy-MM-dd HH-mm");
                            SimpleDateFormat interForm = new SimpleDateFormat("dd-HH-mm-ss");
                            try {
                                System.out.print("Input new task start time[yyyy-mm-dd hh-mm]:");
                                start = timeForm.parse(sc.nextLine());
                                System.out.print("Input new task end time, empty for single task:");
                                String endTime = sc.nextLine();
                                if (endTime.equals("")) {
                                    thread.interrupt();
                                    notificationManager.remove(editTask);
                                    editTask.setTime(start);
                                } else {
                                    end = timeForm.parse(endTime);
                                    System.out.print("Input new interval [dd-hh-mm-ss]:");
                                    interval = interForm.parse(sc.nextLine());
                                    thread.interrupt();
                                    notificationManager.remove(editTask);
                                    editTask.setTime(start, end, (int) interval.getTime()/1000+60*60*26);
                                }
                                notificationManager.add(editTask);
                                thread = new Thread(notificationManager);
                                thread.start();
                            } catch (ParseException e) {
                                System.out.println("incorrect format date");
                            }
                            break;
                        case "activity":
                            thread.interrupt();
                            notificationManager.remove(editTask);
                            editTask.setActive(!editTask.isActive());
                            notificationManager.add(editTask);
                            thread = new Thread(notificationManager);
                            thread.start();
                            break;
                    }
                    break;
                //"name" from [2018-01-18 01:30:30.000] to [2018-01-18 02:30:30.000] every [10 minutes] inactive;
                //"name" at [2018-01-18 01:30:30.000] inactive;
                //"first" at [2018-01-19 08:01:00.000];
                case "add":
                    System.out.println("Create task...");
                    System.out.println("input new task (format: \"task name\" at [year-mm-dd hh:mm:ss.000])");
                    System.out.println("or \"task name\" from [year-mm-dd hh:mm:ss.000] to [year-mm-dd hh:mm:ss.000] every [2 day(s), 24 hour(s), 60 minute(s), 60 second(s)];");
                    String newTask = new String(sc.nextLine());
                    TaskIOModel.read(list, new StringReader(newTask));
                    notificationManager.add(list.getTask(list.size()-1));
                    thread.interrupt();
                    thread = new Thread(notificationManager);
                    thread.start();
                    break;
                case "remove":
                    if (list.size() <= 0) {
                        System.out.println("empty list!");
                        break;
                    }
                    System.out.println("Delete task...");
                    System.out.println("input index task:");
                    try {
                        int removedTask = sc.nextInt();
                        thread.interrupt();
                        notificationManager.remove(list.getTask(removedTask));
                        list.remove(list.getTask(removedTask));
                        thread = new Thread(notificationManager);
                        thread.start();
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println(e.getMessage());
                    }
                    sc.nextLine();
                    break;
                case "show":
                    for (int i = 0; i < list.size(); i++) {
                        System.out.println(i + ": " + list.getTask(i));
                    }
                    break;
                case "calendar":
                    Date start, end;
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        System.out.println("input start date year-mm-dd");
                        start = formatter.parse(sc.nextLine());
                        System.out.println("input end date year-mm-dd");
                        end = formatter.parse(sc.nextLine());
                        SortedMap<Date, Set<TaskModel>> calendar = TasksModel.calendar(list, start, end);
                        for (SortedMap.Entry<Date, Set<TaskModel>> entry : calendar.entrySet()) {
                            System.out.print(entry.getKey()+":");
                            boolean flag = false;
                            for (TaskModel task : entry.getValue()){
                                if(flag)
                                    System.out.print("                            :");
                                System.out.println(task);
                                flag = true;
                            }
                        }
                    }catch (ParseException e){
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
        }
    }
}
