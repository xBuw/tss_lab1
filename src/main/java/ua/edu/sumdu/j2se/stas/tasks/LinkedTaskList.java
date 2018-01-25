package ua.edu.sumdu.j2se.stas.tasks;

import java.util.Iterator;
import java.io.Serializable;
import java.util.NoSuchElementException;

public class LinkedTaskList extends TaskListModel implements Cloneable, Serializable {

    private Node first = null;

    private class Node implements Cloneable {
        private TaskModel current = null;
        private Node next = null;

        @Override
        public Node clone() {
            Node clone;
            try{
                clone = (Node) super.clone();
            }catch(CloneNotSupportedException e){
                throw new RuntimeException("super class messed up");
            }
            clone.current = this.current.clone();
            clone.next = null;
            return clone;
        }
        
        private Node(TaskModel task) {
            current = task;
        }
    }
    
    @Override
    public LinkedTaskList clone() {
        LinkedTaskList clone;
        try{
            clone = (LinkedTaskList) super.clone();
        }catch(CloneNotSupportedException e){
            throw new RuntimeException("super class messed up");
        }
        if(this.first!=null){
            clone.first = this.first.clone();
            Node temp = this.first;
            Node tempClone = clone.first;
            while(temp.next!=null){
                tempClone.next = temp.next.clone();
                temp = temp.next;
                tempClone = tempClone.next;
            }
        }
        return clone;
    }
    
    public Iterator<TaskModel> iterator() {
        return new TaskIterator();
    }

    private class TaskIterator implements Iterator<TaskModel> {

        private Node currentNode;
        private Node previousNode;
        
        public TaskIterator(){
            currentNode = new Node(null);
            currentNode.next = LinkedTaskList.this.first;
        }

        public boolean hasNext() {
            if (currentNode.next == null)
               return false;
            return true;       
        }

        public TaskModel next() {
            previousNode = currentNode;
            currentNode = currentNode.next;
            return currentNode.current;
        }

        public void remove() {
            if (currentNode == null || currentNode.current == null)
                throw new IllegalStateException("try to check collection");
            LinkedTaskList.this.remove(currentNode.current);
            if (previousNode != null && previousNode.current != null)
                currentNode = previousNode;
        }
    }

    public void add(TaskModel task) {
        if (first == null)
            first = new Node(task);
        else {
            Node temp = first;
            while (temp.next != null)
                temp = temp.next;
            temp.next = new Node(task);
        }
        countTask++;
    }

    public boolean remove(TaskModel task) {
        if (first != null) {
            if (task.equals(first.current)) {
                first = first.next;
                countTask--;
                return true;
            }
            Node temp = first.next, old = first;
            do {
                if (task.equals(temp.current)) {
                    if (temp.next == null) {
                        old.next = null;
                    } else {
                        old.next = temp.next;
                    }
                    countTask--;
                    return true;
                }
                temp = temp.next;
                old = old.next;
            } while (temp != null);
        }
        return false;
    }

    public TaskModel getTask(int index) {
        Node temp;
        if(index<0)
            throw new IndexOutOfBoundsException(index+" index is less then zero");
        if (first != null) {
            temp = first;
            while (temp.next != null && index > 0) {
                temp = temp.next;
                index--;
            }
        }else{
            throw new NoSuchElementException("list is empty");
        }
        if (index > 0)
            throw new IndexOutOfBoundsException("this index out of range");
        return temp.current;
    }

}