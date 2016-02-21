package registar;

import registrar.Course;
import registrar.Student;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by paul on 2015/4/17.
 */
public class TestObjectFactory {
    private List<Student> students = new LinkedList<Student>();
    private List<Course> courses = new LinkedList<Course>();

    public Student makeStudent(String name) {
        Student s = new Student();
        s.setName(name);
        students.add(s);
        return s;
    }

    public Course makeCourse(String catalogNumber, String title) {
        Course c = new Course();
        c.setCatalogNumber(catalogNumber);
        c.setTitle(title);
        courses.add(c);
        return c;
    }

    public void enrollMultipleStudents(Course c, int count) {
        for(; count > 0; count--)
            makeStudent("Anonymous student " + count).enrollIn(c);
    }

    public List<Course> allCourses() {
        return courses;
    }

    public List<Student> allStudents() {
        return students;
    }
}