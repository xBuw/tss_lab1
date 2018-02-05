package ua.edu.sumdu.j2se.stas.controller;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import ua.edu.sumdu.j2se.stas.NotificationManager;
import ua.edu.sumdu.j2se.stas.tasks.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TaskListController {

    private static ArrayTaskList list = new ArrayTaskList();
    private static Thread thread;
    private static NotificationManager notificationManager;
    private static Scanner sc = new Scanner(System.in);
    private static String nameFile = "log4j.properties";
    public static Logger logger = Logger.getLogger("logfile");
    private static SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd HH-mm");
    private static SimpleDateFormat interForm = new SimpleDateFormat("dd-HH-mm-ss");
    private static String line;
    private static String dateFormat = "Input new task start time. Format: yyyy-mm-dd hh-mm: ";
    private static String interFormat = "Input new interval dd-hh-mm-ss: ";
    private static String wrongArgument = "Wrong argument";



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
        logger.info("Restart thread for next notification");
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

    public static void printMenu(){
        System.out.println("----------------------------------------------------------");
        System.out.println("-------------------Menu Task Manager----------------------");
        System.out.println("1 - edit task");
        System.out.println("2 - add task");
        System.out.println("3 - remove task");
        System.out.println("4 - show tasks");
        System.out.println("5 - make calendar");
        System.out.println("6 - save taskList in file");
        System.out.println("----------------------------------------------------------");
    }

    public static void printTaskEditMenu(){
        System.out.println("----------------------------------------------------------");
        System.out.println("----------------Edit Task Menu Manager--------------------");
        System.out.println("1 - title");
        System.out.println("2 - time");
        System.out.println("3 - activity");
        System.out.println("----------------------------------------------------------");


    }

    public static boolean isValid(int[] correctValues, String comparable){
        if(comparable==null)
            return false;
            try{
            for(int i : correctValues)
                if(i==Integer.parseInt(comparable))
                    return true;
            }catch (NumberFormatException e){
                logger.error(wrongArgument ,e);
                return false;
            }
        return false;
    }

    public static void printWelcomeMessage(){
        System.out.println("Welcome to TASK MANAGER");
        System.out.println("Print 'exit' for exit");
    }

    public static void main(String[] args) {

        printWelcomeMessage();
        PropertyConfigurator.configure(nameFile);
        File file = null;
        do{
            line = question("1 - load taskList from file1\n2 - continue work with old taskList\n3 - create new taskList\nInput number: ");
            if (line.equals("1")){
                File dir = new File("taskLists");
                for(String i : dir.list()){
                    System.out.println(i);
                }
                file = new File("taskLists"+File.separator+question("Print file name from list: "));
            }else if(line.equals("2")) {
                file = new File("TaskListModel");
            }else if(line.equals("3")) {
                file = new File("");
            }else
                System.out.println(line + wrongArgument);
        }while(!isValid(new int[]{1,2,3},line));

        if (file.exists()) {
            TaskIOModel.readText(list, file);
            logger.info("Read tasks from file:" + file.getAbsolutePath());
        } else {
            System.out.println("Sorry...your task list is lost...");
            logger.error("File not found");
        }

        notificationManager = new NotificationManager(TasksModel.calendar(list, new Date(), TasksModel.getLaterDate(list)));
        thread = new Thread(notificationManager);
        logger.info("Start thread notificationManager");
        thread.start();

        printMenu();
        line = question("\tInput number menu(or exit):");

        while (!line.equals("exit")) {
            try {
                logger.debug("Selection menu item...");
                switch (line) {
                    case "1":
                        TaskModel editTask = list.getTask(Integer.valueOf(question("Menu edit task. input index task: ")));
                        do{
                            System.out.println(editTask);
                            printTaskEditMenu();
                            line = question("What you want change? Print number: ");
                            switch (line) {
                                case "1":
                                    editTask(editTask, editTask.clone().setTitle(question("Input new task title: ")));
                                    break;
                                case "2":
                                    Date start, end, interval;
                                    String repeatable = question("Print (1) if you want Repeatable task, or (2) for Single");
                                    if(!(repeatable.equals("1")||repeatable.equals("2"))){
                                        System.out.println("Error argument");
                                        break;
                                    }
                                    start = dateForm.parse(question(dateFormat));
                                    if(repeatable.equals("1")){
                                        String endString = question(dateFormat);
                                        end = dateForm.parse(endString);
                                        interval = interForm.parse(question(interFormat));
                                        editTask(editTask, editTask.clone().setTime(start, end, (int) interval.getTime() / 1000 + 60 * 60 * 27));
                                    }else {
                                        editTask(editTask, editTask.clone().setTime(start));
                                    }
                                    break;
                                case "3":
                                    editTask(editTask, editTask.clone().setActive(!editTask.isActive()));
                                    break;
                                default:
                                    logger.error(line + wrongArgument);
                            }
                            logger.info("Edit task");
                        }while(!isValid(new int[]{1,2,3},line));
                        break;
                    case "2":
                        TaskModel tempTask;
                        String type = question("Print (1) if you want Repeatable task, or (2) for Single");
                        if(!(type.equals("1")||type.equals("2"))){
                            System.out.println(type + " - WRONG type argument!");
                            break;
                        }
                        String title = question("Input task name: ");

                        Date startDate = dateForm.parse(question(dateFormat));
                        if ( type.equals("2") ){
                            tempTask = new TaskModel(title, startDate);
                        }else{
                            Date endDate = dateForm.parse(question(dateFormat));
                            Date interval = interForm.parse(question(interFormat));
                            tempTask = new TaskModel(title, startDate, endDate, (int) interval.getTime() / 1000 + 60 * 60 * 27);
                        }
                        String active = question("Print (1) if you want active task, or (2) for inactive: ");
                        if ( active.equals("2") ) {
                            tempTask.setActive(false);
                        } else {
                            tempTask.setActive(true);
                        }
                            editTask(null, tempTask);
                        logger.info("Add new task");
                        break;
                    case "3":
                        editTask(list.getTask(Integer.valueOf(question("Remove task.input index task: "))), null);
                        logger.info("Remove task");
                        break;
                    case "4":
                        for (int i = 0; i < list.size(); i++) {
                            System.out.println(i + ": " + list.getTask(i));
                        }
                        break;
                    case "5":
                        Date start = dateForm.parse(question(dateFormat));
                        Date end = dateForm.parse(question(dateFormat));
                        SortedMap<Date, Set<TaskModel>> calendar = TasksModel.calendar(list, start, end);
                        printCalendar(calendar);
                        break;
                    case "6":
                        TaskIOModel.writeText(list, new File("taskLists"+File.separator+question("Input file name: ")));
                        logger.info("Write tasks in file");
                        break;
                    default:
                        System.out.println(line + wrongArgument);
                        break;
                }
            } catch (RuntimeException e) {
                System.out.println("Exception: " + e.getMessage());
                logger.error("Exception: " + e);
            } catch (ParseException e) {
                System.out.println(e.getMessage());
                logger.error("Exception: " + e);
            } finally {
                printMenu();
                line = question("\tInput number menu(or exit):");
            }
        }
        TaskIOModel.writeText(list, file);
        logger.info("Write tasks in file");
    }
}
