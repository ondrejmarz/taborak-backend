package cz.ondrejmarz.taborakserver.model;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Activity {

    private String name;
    private String type;
    private String desc;

    private Boolean visible;

    private Date startTime;
    private Date endTime;

    public Activity() {
        visible = true;
    }

    public Activity(String name, String type, Date startTime) {
        this.name = name;
        this.type = type;
        this.visible = true;
        this.startTime = startTime;
    }

    public Activity(String name, String type, String desc, Boolean visible, Date startTime, Date endTime) {
        this.name = name;
        this.type = type;
        this.desc = desc;
        this.visible = visible;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Activity initializeIfNeeded(String name, String type, Date startTime) {
        if (this.name == null) {
            this.name = name;
        }
        if (this.type == null) {
            this.type = type;
        }
        if (this.startTime == null) {
            this.startTime = startTime;
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setStartTimeDay(Date startTimeDay) {

        Calendar calendarThis = Calendar.getInstance();
        Calendar calendarCopy = Calendar.getInstance();

        calendarCopy.setTime(startTimeDay);

        calendarThis.setTime(startTime);
        calendarThis.set(Calendar.YEAR, calendarCopy.get(Calendar.YEAR));
        calendarThis.set(Calendar.MONTH, calendarCopy.get(Calendar.MONTH));
        calendarThis.set(Calendar.DAY_OF_MONTH, calendarCopy.get(Calendar.DAY_OF_MONTH));
        setStartTime( calendarThis.getTime() );
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setEndTimeDay(Date endTimeDay) {

        Calendar calendarThis = Calendar.getInstance();
        Calendar calendarCopy = Calendar.getInstance();

        calendarCopy.setTime(endTimeDay);

        calendarThis.setTime(endTime);
        calendarThis.set(Calendar.YEAR, calendarCopy.get(Calendar.YEAR));
        calendarThis.set(Calendar.MONTH, calendarCopy.get(Calendar.MONTH));
        calendarThis.set(Calendar.DAY_OF_MONTH, calendarCopy.get(Calendar.DAY_OF_MONTH));
        setEndTime( calendarThis.getTime() );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return Objects.equals(name, activity.name) && Objects.equals(type, activity.type) && Objects.equals(desc, activity.desc) && Objects.equals(visible, activity.visible) && Objects.equals(startTime, activity.startTime) && Objects.equals(endTime, activity.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, desc, visible, startTime, endTime);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", desc='" + desc + '\'' +
                ", visible=" + visible +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
