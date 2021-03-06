package ua.edu.sumdu.j2se.stas.tasks;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.*;
import java.time.LocalTime;

public class TaskModel implements Cloneable, Serializable {

    private String title;
    private Date start;
    private Date end;
    private int interval;
    private boolean active;

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof TaskModel)) return false;
        TaskModel t = (TaskModel) o;
        if (end == null && t.end == null)
            return title.equals(t.title) && start.equals(t.start) && active == t.active;
        return title.equals(t.title) && start.equals(t.start) && end.equals(t.end) && interval == t.interval && active == t.active;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (int) start.getTime();
        result = 31 * result + (int) start.getTime();
        if (end != null) {
            result = 31 * result + (int) end.getTime();
            result = 31 * result + interval;
        }
        result = 31 * result + (active ? 1 : 0);
        return result;
    }

    @Override
    public TaskModel clone() {
        try {
            return (TaskModel) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public TaskModel() {
    }

    /**
     * @param title - task title
     * @param time  - task time
     */
    public TaskModel(String title, long time) {
        if (!(title != null && time >= 0)) {
            throw new IllegalArgumentException("title is null or time is negative");
        }
        this.title = title;
        this.start = new Date(time * 1000);
    }

    public TaskModel(String title, Date time) {
        if (title == null || time == null) {
            throw new IllegalArgumentException("title is null or time is negative");
        }
        this.title = title;
        this.start = new Date(time.getTime());
    }

    public TaskModel(String title, Date start, Date end, int interval) {
        if (title == null || start == null || end == null || interval <= 0) {
            throw new IllegalArgumentException("title is null or time is negative");
        }
        this.title = title;
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        this.interval = interval;
    }

    /**
     * Set title task
     */
    public void setTitle(String title) {
        if (title == null) {
            throw new IllegalArgumentException("title is null");
        }
        this.title = title;
    }

    /**
     * @return - task title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @return - activity task
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Set activity task
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public void setTime(Date time) {
        if (time == null) {
            throw new IllegalArgumentException("time is negative");
        }
        this.start = new Date(time.getTime());
        this.end = null;
    }

    /**
     * Set repeatable task time
     *
     * @param start    - first call task
     * @param end      - last call task
     * @param interval - interval with tasks
     */
    public void setTime(Date start, Date end, int interval) {
        if (!(title != null && start != null && end != null && interval >= 0)) {
            throw new IllegalArgumentException("title is null or time is negative");
        }
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        this.interval = interval;
    }


    /**
     * @return - {@code time} if unrepeatable.
     * {@code start} if repeatable.
     */
    public Date getTime() {
        return start;
    }

    /**
     * @return - {@code time} if unrepeatable.
     * {@code start} if repeatable.
     */
    public Date getStartTime() {
        return start;
    }

    /**
     * @return - {@code time} if unrepeatable.
     * {@code end} if repeatable.
     */
    public Date getEndTime() {
        if (end != null)
            return end;
        else
            return start;
    }

    /**
     * @return {@code interval} - if repeatable.
     * {@code '0'} - if unrepeatable.
     */
    public int getRepeatInterval() {
        return end != null ? interval : 0;
    }

    /**
     * @return {@code true} - if repeatable.
     * {@code false} - if unrepeatable.
     */
    public boolean isRepeated() {
        return (end != null);
    }

    /**
     * String format
     */
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String inactive = new String(" inactive");
        if (active)
            inactive = new String("");
        if (end == null)
            return "\"" + title + "\" at [" + dateFormat.format(start) + "]" + inactive;
        else {
            StringBuilder interval = new StringBuilder();
            LocalTime timeInterval = LocalTime.ofSecondOfDay(this.interval % 86400);
            if (this.interval / 86400 > 1)
                interval.append(this.interval / 86400 + " days, ");
            if (this.interval / 86400 == 1)
                interval.append(this.interval / 86400 + " day, ");
            if (timeInterval.getHour() > 1)
                interval.append(timeInterval.getHour() + " hours, ");
            if (timeInterval.getHour() == 1)
                interval.append(timeInterval.getHour() + " hour, ");
            if (timeInterval.getMinute() > 1)
                interval.append(timeInterval.getMinute() + " minutes, ");
            if (timeInterval.getMinute() == 1)
                interval.append(timeInterval.getMinute() + " minute, ");
            if (timeInterval.getSecond() > 1)
                interval.append(timeInterval.getSecond() + " seconds, ");
            if (timeInterval.getSecond() == 1)
                interval.append(timeInterval.getSecond() + " second, ");
            interval.setLength(interval.length() - 2);
            return "\"" + title + "\" from [" + dateFormat.format(start) + "] to [" + dateFormat.format(end) + "] every [" + interval + "]" + inactive;
        }
    }

    public Date nextTimeAfter(Date time) {
        if (!isActive()) {
            return null;
        } else {
            if (this.end == null) {
                if (time.before(this.start))
                    return this.start;
            } else {
                if (this.start.after(time)) {
                    return start;
                }
                Date tempTime = new Date(start.getTime());
                while (tempTime.compareTo(time) <= 0) {
                    tempTime = new Date(interval * 1000 + tempTime.getTime());
                }
                if (tempTime.compareTo(end) <= 0) {
                    return tempTime;
                }
            }
            return null;
        }
    }
}