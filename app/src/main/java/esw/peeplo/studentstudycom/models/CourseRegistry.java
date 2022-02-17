package esw.peeplo.studentstudycom.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import esw.peeplo.studentstudycom.converters.CourseConverter;

@Entity(tableName = "registry")
public class CourseRegistry implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private int id;

    @SerializedName("owner")
    private String owner;

    @SerializedName("course")
    private String course;

    @SerializedName("schedule")
    private String schedule;

    @SerializedName("distracted")
    private long distracted;

    @SerializedName("focused")
    private long focused;

    public CourseRegistry(String owner, String course, String schedule, long distracted, long focused) {
        this.owner = owner;
        this.course = course;
        this.schedule = schedule;
        this.distracted = distracted;
        this.focused = focused;
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

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public long getDistracted() {
        return distracted;
    }

    public void setDistracted(long distracted) {
        this.distracted = distracted;
    }

    public long getFocused() {
        return focused;
    }

    public void setFocused(long focused) {
        this.focused = focused;
    }
}
