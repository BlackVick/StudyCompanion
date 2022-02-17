package esw.peeplo.studentstudycom.helpers;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StudyHelper implements Serializable {

    private String owner;
    private String course;
    private String schedule;
    private boolean studying;
    private long start;
    private long stop;

    public StudyHelper(String owner, String course, String schedule, boolean studying, long start, long stop) {
        this.owner = owner;
        this.course = course;
        this.schedule = schedule;
        this.studying = studying;
        this.start = start;
        this.stop = stop;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public boolean isStudying() {
        return studying;
    }

    public void setStudying(boolean studying) {
        this.studying = studying;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getStop() {
        return stop;
    }

    public void setStop(long stop) {
        this.stop = stop;
    }
}
