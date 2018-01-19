package ua.edu.sumdu.j2se.stas.tasks;

import java.util.Date;
import java.util.*;

public class TasksModel {
    public static Iterable<TaskModel> incoming(Iterable<TaskModel> tasks, Date start, Date end) {
        ArrayTaskList arr = new ArrayTaskList();
        if(start == null || end == null){
            throw new IllegalArgumentException("start date is null or end time");
        }
        for (TaskModel task : tasks){
            if (task.nextTimeAfter(start) != null && task.nextTimeAfter(start).compareTo(end) <= 0) {
                arr.add(task);
            }
        }
        return arr;
    }
    
    public static SortedMap<Date, Set<TaskModel>> calendar(Iterable<TaskModel> tasks, Date start, Date end) {
        ArrayTaskList incoming = (ArrayTaskList)incoming(tasks, start,end);
        SortedMap<Date, Set<TaskModel>> map = new TreeMap<Date, Set<TaskModel>>();
        Set<TaskModel> tempTaskSet;
        for (TaskModel task : incoming) {
            Date tempDate = new Date(start.getTime());
            while(task.nextTimeAfter(tempDate)!=null && task.nextTimeAfter(tempDate).compareTo(end) <= 0){
                tempDate = new Date(task.nextTimeAfter(tempDate).getTime());
                tempTaskSet = map.get(tempDate);
                if(tempTaskSet!=null){
                    tempTaskSet.add(task);
                }else{
                    tempTaskSet = new HashSet<>();
                    tempTaskSet.add(task);
                    map.put(tempDate, tempTaskSet);
                }
            }
        }
        return map;
    }
}