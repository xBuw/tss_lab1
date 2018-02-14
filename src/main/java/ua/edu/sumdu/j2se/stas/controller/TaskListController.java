package ua.edu.sumdu.j2se.stas.controller;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import ua.edu.sumdu.j2se.stas.notification.NotificationManager;
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
    private static final SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd HH-mm");
    private static final String dateFormat = "Format: yyyy-mm-dd hh-mm: ";
    private static final String interFormat = "Input new interval, amount of minutes: ";
    private static final String wrongArgument = "Wrong argument";
    private static String line;


    /**
     * Remove old task and add new task.
     * If one of them empty, only add task or only remove task.
     *
     * @param oldTask
     * @param newTask
     */
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

    /**
     * Print calendar on CLI
     *
     * @param calendar
     */
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


    /**
     * Print question in CLI, and return answer.
     *
     * @param string question string
     * @return answer string
     */
    public static String question(String string) throws IOException {
        System.out.print(string);
        return sc.nextLine();
    }


    /**
     * Print main menu
     */
    public static void printMenu() {
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


    /**
     * Print task edit menu
     */
    public static void printTaskEditMenu() {
        System.out.println("----------------------------------------------------------");
        System.out.println("----------------Edit Task Menu Manager--------------------");
        System.out.println("1 - title");
        System.out.println("2 - time");
        System.out.println("3 - activity");
        System.out.println("----------------------------------------------------------");

    }


    /**
     * Check if (a >= comparable >= b)
     *
     * @param a
     * @param b
     * @param comparable
     * @return True if comparable number is less then b and more  then a. Else False
     */
    public static boolean isValid(int a, int b, String comparable) {
        if (comparable == null)
            return false;
        try {
            for (int i = a; i <= b; i++)
                if (i == Integer.parseInt(comparable))
                    return true;
        } catch (NumberFormatException e) {
            logger.error(wrongArgument, e);
            return false;
        }
        return false;
    }


    /**
     * Print welcome messages in CLI
     */
    public static void printWelcomeMessage() {
        System.out.println("Welcome to TASK MANAGER");
        System.out.println("Print 'exit' for exit");
    }


    /**
     * Load tasksList from files, or create new empty list.
     */
    public static void getTaskListFromFile() throws IOException {
        File file = null;
        do {
            line = question("1 - load taskList from dir taskLists/... \n2 - continue work with old taskList\n3 - create new taskList\nInput number: ");
            if (line.equals("1")) {
                File dir = new File("taskLists");
                String arr[] = dir.list();
                for (int i = 0; i < arr.length; i++) {
                    System.out.println(i + " - " + arr[i]);
                }
                String index;
                do {
                    index = question("Print file index: ");
                } while (!isValid(0, arr.length, index));
                file = new File("taskLists" + File.separator + arr[Integer.parseInt(index)]);
            } else if (line.equals("2")) {
                file = new File("taskLists" + File.separator + "TaskListModel");
            } else if (line.equals("3")) {
                file = new File("");
            } else
                System.out.println(line + wrongArgument);
        } while (!isValid(1, 3, line));

        if (file.exists()) {
            TaskIOModel.readText(list, file);
            logger.info("Read tasks from file:" + file.getAbsolutePath());
        } else {
            System.out.println("Sorry...your task list is lost...");
            logger.error("File not found");
        }
    }

    /**
     * Start notificationManager and generate starting calendar for notify
     */
    public static void startNotificationManager() {
        notificationManager = new NotificationManager(TasksModel.calendar(list, new Date(), TasksModel.getLaterDate(list)));
        thread = new Thread(notificationManager);
        logger.info("Start thread notificationManager");
        thread.start();
    }

    /**
     * print all existing tasks
     */
    public static void printALlTasks() {
        for (int i = 0; i < list.size(); i++) {
            System.out.println(i + ": " + list.getTask(i));
        }
    }

    public static void editTaskMenu() throws ParseException, IOException {
        printALlTasks();
        TaskModel editTask = list.getTask(Integer.valueOf(question("Menu edit task. input index task: ")));
        do {
            System.out.println(editTask);
            printTaskEditMenu();
            line = question("What you want change? Print number: ");
            switch (line) {
                case "1":
                    editTask(editTask, editTask.clone().setTitle(question("Input new task title: ")));
                    break;
                case "2":
                    String repeatable = question("Print (1) if you want Repeatable task, or (2) for Single");
                    while (!isValid(1, 2, repeatable)) {
                        System.out.println("Error argument");
                        repeatable = question("Print (1) if you want Repeatable task, or (2) for Single");
                    }
                    Date start = getValidDate("Input new task start time. ");
                    if (repeatable.equals("1")) {
                        Date end = getValidDate("Input new task end time. ");
                        editTask(editTask, editTask.clone().setTime(start, end, Integer.parseInt(question(interFormat)) * 60));
                    } else {
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
        } while (!isValid(1, 3, line));
    }

    public static Date getValidDate(String inputMessage) throws IOException {
        Date date = null;
        while (date == null) {
            try {
                date = dateForm.parse(question(inputMessage + dateFormat));
            } catch (ParseException e) {
                System.out.println("Incorrect date");
                logger.error("Incorrect date", e);
                date = null;
            }
        }
        return date;
    }

    public static void addNewTask() throws ParseException, IOException {
        TaskModel tempTask;
        line = question("Print (1) if you want Repeatable task, or (2) for Single");
        while (!isValid(1, 2, line)) {
            System.out.println(line + " - WRONG type argument!");
            line = question("Print (1) if you want Repeatable task, or (2) for Single");
        }
        String title = question("Input task name: ");

        Date startDate = getValidDate("Input new task start time. ");
        if (line.equals("2")) {
            tempTask = new TaskModel(title, startDate);
        } else {
            Date endDate = getValidDate("Input new task end time. ");
            tempTask = new TaskModel(title, startDate, endDate, Integer.parseInt(question(interFormat)) * 60);
        }
        String active = question("Print (1) if you want active task, or (2) for inactive: ");
        if (active.equals("2")) {
            tempTask.setActive(false);
        } else {
            tempTask.setActive(true);
        }
        editTask(null, tempTask);
        logger.info("Add new task");
    }


    public static void main(String[] args) throws IOException {

        printWelcomeMessage();
        PropertyConfigurator.configure(nameFile);
        getTaskListFromFile();
        printMenu();
        startNotificationManager();
        line = question("\tInput number menu(or exit):");

        while (!line.equals("exit")) {
            try {
                logger.debug("Selection menu item...");
                switch (line) {
                    case "1":
                        editTaskMenu();
                        break;
                    case "2":
                        addNewTask();
                        break;
                    case "3":
                        editTask(list.getTask(Integer.valueOf(question("Remove task.input index task: "))), null);
                        logger.info("Remove task");
                        break;
                    case "4":
                        printALlTasks();
                        break;
                    case "5":
                        Date start = dateForm.parse(question(dateFormat));
                        Date end = dateForm.parse(question(dateFormat));
                        SortedMap<Date, Set<TaskModel>> calendar = TasksModel.calendar(list, start, end);
                        printCalendar(calendar);
                        break;
                    case "6":
                        TaskIOModel.writeText(list, new File("taskLists" + File.separator + question("Input file name: ")));
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
        TaskIOModel.writeText(list, new File("taskLists" + File.separator + "TaskListModel"));
        logger.info("Write tasks in file");
    }
}
