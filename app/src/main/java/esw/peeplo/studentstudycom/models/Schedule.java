package esw.peeplo.studentstudycom.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "schedules")
public class Schedule implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private int id;

    @SerializedName("owner")
    private String owner;

    @SerializedName("course")
    private String course;

    @SerializedName("day")
    private String day;

    @SerializedName("start")
    private String start;

    @SerializedName("end")
    private String end;

    public Schedule(String owner, String course, String day, String start, String end) {
        this.owner = owner;
        this.course = course;
        this.day = day;
        this.start = start;
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Schedule))  {
            return false;
        }
        Schedule other = (Schedule) obj;
        return id == (other.id);
    }
    @Override
    public int hashCode() {
        return 31 * String.valueOf(id).hashCode();
    }
}
