package esw.peeplo.studentstudycom.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import esw.peeplo.studentstudycom.converters.CourseConverter;

@Entity(tableName = "users")
public class User implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private int id;

    @SerializedName("first_name")
    private String first_name;

    @SerializedName("last_name")
    private String last_name;

    @SerializedName("matric_num")
    private String matric_num;

    @SerializedName("dept")
    private String dept;

    @SerializedName("study_style")
    private String study_style;

    @SerializedName("avatar")
    private String avatar;

    @TypeConverters(CourseConverter.class)
    @SerializedName("courses")
    private Courses courses;

    @SerializedName("evaluation")
    private String evaluation;

    @SerializedName("password")
    private String password;

    public User() {
    }

    public User(String first_name, String last_name, String matric_num, String dept, String study_style, String avatar, Courses courses, String evaluation, String password) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.matric_num = matric_num;
        this.dept = dept;
        this.study_style = study_style;
        this.avatar = avatar;
        this.courses = courses;
        this.evaluation = evaluation;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMatric_num() {
        return matric_num;
    }

    public void setMatric_num(String matric_num) {
        this.matric_num = matric_num;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getStudy_style() {
        return study_style;
    }

    public void setStudy_style(String study_style) {
        this.study_style = study_style;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Courses getCourses() {
        return courses;
    }

    public void setCourses(Courses courses) {
        this.courses = courses;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
