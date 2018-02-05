package ua.edu.sumdu.j2se.stas.tasks;

import java.util.Iterator;
import java.io.Serializable;


public abstract class TaskListModel implements Iterable<TaskModel>, Serializable {

    protected int countTask = 0;

    @Override
    public int hashCode() {
        int result = 1;
        for (TaskModel task : this)
            result = 31 * result + task.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof TaskListModel)) return false;
        TaskListModel list;
        if (o instanceof ArrayTaskList) {
            list = (ArrayTaskList) o;
        } else {
            list = (LinkedTaskList) o;
        }
        Iterator thisCollection = list.iterator();
        Iterator compareCollection = this.iterator();
        while (thisCollection.hasNext() && compareCollection.hasNext()) {
            if (!thisCollection.next().equals(compareCollection.next()))
                return false;
        }

        return true;
    }
    
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder("TaskListModel");
        for (TaskModel task : this)
            result.append(task.toString()+" ");
        return result.toString();
    }

    public abstract Iterator<TaskModel> iterator();

    public abstract void add(TaskModel task);

    public abstract boolean remove(TaskModel task);

    public abstract TaskModel getTask(int index);

    /**
     * return size list
     * @return
     */
    public int size() {
        return countTask;
    }
    
} 