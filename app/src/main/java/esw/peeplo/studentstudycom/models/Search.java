package esw.peeplo.studentstudycom.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "searches")
public class Search implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private int id;

    @SerializedName("owner")
    private String owner;

    @SerializedName("course")
    private String course;

    @SerializedName("params")
    private String params;

    public Search(String owner, String course, String params) {
        this.owner = owner;
        this.course = course;
        this.params = params;
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

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Search))  {
            return false;
        }
        Search other = (Search) obj;
        return id == (other.id);
    }
    @Override
    public int hashCode() {
        return 31 * String.valueOf(id).hashCode();
    }
}
