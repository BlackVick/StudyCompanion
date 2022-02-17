package esw.peeplo.studentstudycom.converters;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

import esw.peeplo.studentstudycom.models.Courses;

public class CourseConverter {

    @TypeConverter
    public Courses storedStringToCourses(String value) {
        List<String> courses = Arrays.asList(value.split("\\s*,\\s*"));
        return new Courses(courses);
    }

    @TypeConverter
    public String coursesToStoredString(Courses courses) {
        String value = "";

        for (String course : courses.getCourses())
            value += course + ",";

        return value;
    }

}
