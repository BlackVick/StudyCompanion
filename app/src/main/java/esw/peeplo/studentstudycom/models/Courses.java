package esw.peeplo.studentstudycom.models;

import java.util.List;

public class Courses {

    private List<String> courses;

    public Courses(List<String> courses) {
        this.courses = courses;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }
}
