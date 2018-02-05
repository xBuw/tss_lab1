package ua.edu.sumdu.j2se.stas.tasks;

import java.util.Iterator;
import java.io.Serializable;

public class ArrayTaskList extends TaskListModel implements Cloneable, Serializable {

    private TaskModel arrTask[] = new TaskModel[10];
    private int maxCountTask = 10;
    
    public Iterator<TaskModel> iterator() {
        return new TaskIterator();
    }

    @Override
    public ArrayTaskList clone() {
        ArrayTaskList newCollection;
        try{
            newCollection = (ArrayTaskList) super.clone();
        }catch(CloneNotSupportedException e){
            throw new RuntimeException("super class messed up");
        }
        newCollection.arrTask = this.arrTask.clone();
        for (TaskModel task : this){
            newCollection.add(task.clone());
        }
        return newCollection;
    }
    
    private class TaskIterator implements Iterator<TaskModel> {

        private int index = 0;
        private boolean hasOneNext = false;

        public boolean hasNext() {
            try {
                ArrayTaskList.this.getTask(index);
            } catch (IndexOutOfBoundsException e) {
                return false;
            }
            return true;
        }

        public TaskModel next() {
            hasOneNext = true;
            return ArrayTaskList.this.getTask(index++);
        }

        public void remove() {
            if (!hasOneNext)
                throw new IllegalStateException("try to check collection");
            ArrayTaskList.this.remove(ArrayTaskList.this.getTask(--index));
        }
    }

    /**
     * add new task in list
     * @param task
     */
    public void add(TaskModel task) {
        if (countTask == maxCountTask) {
            maxCountTask *= 10;
            TaskModel newArrTask[] = new TaskModel[maxCountTask];
            for (int i = 0; i < maxCountTask / 10; i++)
                newArrTask[i] = arrTask[i];
            arrTask = newArrTask;
        }
        arrTask[countTask] = task;
        countTask++;
    }

    /**
     * remove current task
     * @param task
     * @return
     */
    public boolean remove(TaskModel task) {
        for (int i = 0; i < countTask; i++)
            if (task.equals(arrTask[i])) {
                while (i + 1 < countTask) {
                    arrTask[i] = arrTask[i + 1];
                    i++;
                }
                countTask--;
                return true;
            }
        return false;
    }

    /**
     * get task from index
     * @param index
     * @return
     */
    public TaskModel getTask(int index) {
        if (index >= 0 && index < this.size()) {
            return (arrTask[index]);
        }
        throw new IndexOutOfBoundsException("Incorrect index");
    }
}