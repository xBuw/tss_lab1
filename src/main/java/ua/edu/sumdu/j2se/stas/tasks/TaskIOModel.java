package ua.edu.sumdu.j2se.stas.tasks;

import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskIOModel {

    /**
     * write tasks list to output stream
     * @param tasks
     * @param out
     */
    public static void write(TaskListModel tasks, OutputStream out) {
        try {
            new ObjectOutputStream(out).writeObject(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * read tasks list from input stream
     * @param tasks
     * @param in
     */
    public static void read(TaskListModel tasks, InputStream in) {
        try {
            ObjectInputStream ois = new ObjectInputStream(in);
            TaskListModel arr = (TaskListModel) ois.readObject();
            for (TaskModel task : arr) {
                tasks.add(task);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * write task list in file in binary format
     * @param tasks
     * @param file
     */
    public static void writeBinary(TaskListModel tasks, File file) {
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            fout = new FileOutputStream(file);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(tasks);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * read task list from file in binary format
     * @param tasks
     * @param file
     */
    public static void readBinary(TaskListModel tasks, File file) {
        FileInputStream fin = null;
        ObjectInputStream ois = null;


        try {
            fin = new FileInputStream(file);
            ois = new ObjectInputStream(fin);
            TaskListModel arr = (TaskListModel) ois.readObject();
            for (TaskModel task : arr) {
                tasks.add(task);
            }
        } catch (Exception ex) {
            ex.getMessage();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * write task list in Writer in string format
     * @param tasks
     * @param out
     */
    public static void write(TaskListModel tasks, Writer out) {
        try {
            boolean flag = false;
            for (TaskModel task : tasks) {
                if (flag)
                    out.write(";" + System.lineSeparator());
                out.write(task.toString());
                flag = true;
            }
            out.write("." + System.lineSeparator());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * read task list from Reader in string format
     * @param tasks
     * @param in
     */
    public static void read(TaskListModel tasks, Reader in) {
        try {
            BufferedReader fr = new BufferedReader(in);
            String line;
            Pattern titleSingleTask = Pattern.compile("^\"([^-]*)\"\\sat\\s\\[(\\d{4})-(\\d{2})-(\\d{2})\\s(\\d{2}):(\\d{2}):(\\d{2}).(\\d{3})](\\sinactive)?(;|.)");
            Pattern titleMultiTask = Pattern.compile("^\"([^-]*)\"\\sfrom\\s\\[(\\d{4})-(\\d{2})-(\\d{2})\\s(\\d{2}):(\\d{2}):(\\d{2}).(\\d{3})]\\sto\\s\\[(\\d{4})-(\\d{2})-(\\d{2})\\s(\\d{2}):(\\d{2}):(\\d{2}).(\\d{3})]\\severy\\s\\[((\\d*)\\sdays?,?\\s?)?((\\d{1,2})\\shours?,?\\s?)?((\\d{1,2})\\sminutes?,?\\s?)?((\\d{1,2})\\sseconds?)?](\\sinactive)?(;|.)?");

            Matcher SingleTasks, MultiTasks;
            Calendar dateStart, dateEnd;
            int interval;
            TaskModel tempTask;
            while ((line = fr.readLine()) != null) {
                tempTask = new TaskModel();
                SingleTasks = titleSingleTask.matcher(line);
                MultiTasks = titleMultiTask.matcher(line);
                dateStart = new GregorianCalendar();
                dateEnd = new GregorianCalendar();
                if (SingleTasks.find()) {
                    dateStart.set(Integer.parseInt(SingleTasks.group(2)), Integer.parseInt(SingleTasks.group(3)) - 1, Integer.parseInt(SingleTasks.group(4)), Integer.parseInt(SingleTasks.group(5)), Integer.parseInt(SingleTasks.group(6)), Integer.parseInt(SingleTasks.group(7)));
                    tempTask.setTime(new Date(dateStart.getTime().getTime()/1000*1000+Integer.parseInt(SingleTasks.group(8))));
                    tempTask.setTitle(SingleTasks.group(1));
                    if (SingleTasks.group(9) == null)
                        tempTask.setActive(true);
                    tasks.add(tempTask);
                } else if (MultiTasks.find()) {
                    dateStart.set(Integer.parseInt(MultiTasks.group(2)), Integer.parseInt(MultiTasks.group(3)) - 1, Integer.parseInt(MultiTasks.group(4)), Integer.parseInt(MultiTasks.group(5)), Integer.parseInt(MultiTasks.group(6)), Integer.parseInt(MultiTasks.group(7)));
                    dateEnd.set(Integer.parseInt(MultiTasks.group(9)), Integer.parseInt(MultiTasks.group(10)) - 1 , Integer.parseInt(MultiTasks.group(11)), Integer.parseInt(MultiTasks.group(12)), Integer.parseInt(MultiTasks.group(13)), Integer.parseInt(MultiTasks.group(14)));
                    interval = MultiTasks.group(23) != null ? Integer.parseInt(MultiTasks.group(23)) : 0;
                    interval += MultiTasks.group(21) != null ? Integer.parseInt(MultiTasks.group(21)) * 60 : 0;
                    interval += MultiTasks.group(19) != null ? Integer.parseInt(MultiTasks.group(19)) * 60 * 60 : 0;
                    interval += MultiTasks.group(17) != null ? Integer.parseInt(MultiTasks.group(17)) * 60 * 60 * 24 : 0;
                    tempTask.setTitle(MultiTasks.group(1));
                    tempTask.setTime(new Date(dateStart.getTime().getTime()/1000*1000+Integer.parseInt(MultiTasks.group(8))), new Date(dateEnd.getTime().getTime()/1000*1000+Integer.parseInt(MultiTasks.group(15))), interval);
                    if (MultiTasks.group(24) == null)
                        tempTask.setActive(true);
                    tasks.add(tempTask);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * write task list in file in string format
     * @param tasks
     * @param file
     */
    public static void writeText(TaskListModel tasks, File file) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            TaskIOModel.write(tasks, fw);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * read task list from file in string format
     * @param tasks
     * @param file
     */
    public static void readText(TaskListModel tasks, File file) {
        try {
            BufferedReader fr = new BufferedReader(new FileReader(file));
            TaskIOModel.read(tasks, fr);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}