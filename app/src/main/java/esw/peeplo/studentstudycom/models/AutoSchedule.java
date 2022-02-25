package esw.peeplo.studentstudycom.models;

import java.io.Serializable;

public class AutoSchedule implements Serializable {

    private String day;
    private String start;
    private String stop;

    public AutoSchedule(String day, String start, String stop) {
        this.day = day;
        this.start = start;
        this.stop = stop;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }
}
