package registrar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bjackson on 2/21/2016.
 */
public class Student {

    public String name;
    public Set<Course> enrolledIn;

    public Student(){
        enrolledIn = new HashSet<>();
    }

    public void setName(String name){
        this.name = name;
    }

    public Set<Course> getCourses(){
        return enrolledIn;
    }

    public boolean enrollIn(Course c){
        if(c.enrollIn(this)) {
            enrolledIn.add(c);
            return true;
        }
        else {
            return false;
        }
    }

    public void drop(Course c){
        if (enrolledIn.contains(c)) {
            enrolledIn.remove(c);
        }
        c.dropStudent(this);
    }
}
