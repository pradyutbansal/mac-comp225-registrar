package registrar;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bjackson on 2/21/2016.
 */
public class Student {

    private String name;
    private Set<Course> courses;

    public Student(){
        courses = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Set<Course> getCourses(){
        return Collections.unmodifiableSet(courses);
    }

    public boolean enrollIn(Course course) {
        if (course.enroll(this)) {
            courses.add(course);
            return true;
        }
        else {
            return false;
        }
    }

    public void drop(Course course) {
        courses.remove(course);
        course.dropStudent(this);
    }

    @Override
    public String toString() {
        return getName();
    }
}