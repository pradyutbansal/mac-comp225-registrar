package registar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import registrar.Course;
import registrar.Student;

import static org.junit.Assert.*;

public class RegistrarTest {

    // ------ Setup ------

    private TestObjectFactory factory = new TestObjectFactory();
    private Course comp225, math6, basketWeaving101;
    private Student sally, fred, zongo;

    @Before
    public void createStudents() {
        sally = factory.makeStudent("Sally");
        fred  = factory.makeStudent("Fred");
        zongo = factory.makeStudent("Zongo Jr.");
    }

    @Before
    public void createCourses() {
        comp225 = factory.makeCourse("COMP 225", "Software Fun Fun");
        comp225.setEnrollmentLimit(16);

        math6 = factory.makeCourse("Math 6", "All About the Number Six");

        basketWeaving101 = factory.makeCourse("Underwater Basket Weaving 101", "Senior spring semester!");
    }

    // ------ Enrolling ------

    @Test
    public void studentStartsInNoCourses() {
        assertEquals(Collections.emptySet(), sally.getCourses());
    }

    @Test
    public void studentCanEnroll() {
        sally.enrollIn(comp225);
        assertEquals(set(comp225), sally.getCourses());
        assertEquals(set(sally), comp225.getStudents());
    }

    @Test
    public void doubleEnrollingHasNoEffect() {
        sally.enrollIn(comp225);
        sally.enrollIn(comp225);
        assertEquals(set(comp225), sally.getCourses());
        assertEquals(set(sally), comp225.getStudents());
    }


    // ------ Enrollment limits ------

    @Test
    public void coursesHaveEnrollmentLimits() {
        comp225.setEnrollmentLimit(16);
        assertEquals(16, comp225.getEnrollmentLimit());
    }

    @Test
    public void enrollingUpToLimitAllowed() {
        factory.enrollMultipleStudents(comp225, 15);
        assertTrue(sally.enrollIn(comp225));
        assertEquals(list(), comp225.getWaitList());
        assertTrue(comp225.getStudents().contains(sally));
    }

    @Test
    public void enrollingPastLimitPushesToWaitList() {
        factory.enrollMultipleStudents(comp225, 16);
        assertFalse(sally.enrollIn(comp225));
        assertEquals(list(sally), comp225.getWaitList());
        assertFalse(comp225.getStudents().contains(sally));
    }

    @Test
    public void waitListPreservesEnrollmentOrder() {
        factory.enrollMultipleStudents(comp225, 16);
        sally.enrollIn(comp225);
        fred.enrollIn(comp225);
        zongo.enrollIn(comp225);
        assertEquals(list(sally, fred, zongo), comp225.getWaitList());
    }

    @Test
    public void doubleEnrollingInFullCourseHasNoEffect() {
        sally.enrollIn(comp225);
        factory.enrollMultipleStudents(comp225, 20);
        assertTrue(sally.enrollIn(comp225)); // full now, but Sally was already enrolled
        assertTrue(comp225.getStudents().contains(sally));
        assertFalse(comp225.getWaitList().contains(sally));
    }

    @Test
    public void doubleEnrollingAfterWaitListedHasNoEffect() {
        factory.enrollMultipleStudents(comp225, 16);
        sally.enrollIn(comp225);
        fred.enrollIn(comp225);
        zongo.enrollIn(comp225);
        fred.enrollIn(comp225);
        assertFalse(sally.enrollIn(comp225));

        assertEquals(list(sally, fred, zongo), comp225.getWaitList());
    }

    @Test
    public void cannotChangeEnrollmentLimitOnceStudentsRegister(){
        assertTrue(basketWeaving101.setEnrollmentLimit(10));
        fred.enrollIn(basketWeaving101);
        assertFalse(basketWeaving101.setEnrollmentLimit(8));
    }

    // ------ Drop courses ------

    @Test
    public void studentCanDrop() {
        sally.enrollIn(comp225);
        sally.drop(comp225);
        assertEquals(set(), sally.getCourses());
        assertEquals(set(), comp225.getStudents());
    }

    @Test
    public void dropHasNoEffectOnOtherCoursesOrStudents() {
        sally.enrollIn(comp225);
        fred.enrollIn(comp225);
        sally.enrollIn(math6);
        sally.drop(comp225);
        assertEquals(set(math6), sally.getCourses());
        assertEquals(set(fred), comp225.getStudents());
    }

    @Test
    public void dropRemovesFromWaitList() {
        factory.enrollMultipleStudents(comp225, 16);
        sally.enrollIn(comp225);
        fred.enrollIn(comp225);
        zongo.enrollIn(comp225);
        fred.drop(comp225);
        assertEquals(list(sally, zongo), comp225.getWaitList());
    }

    @Test
    public void dropEnrollsWaitListedStudents() {
        sally.enrollIn(comp225);
        factory.enrollMultipleStudents(comp225, 15);
        zongo.enrollIn(comp225);
        fred.enrollIn(comp225);
        sally.drop(comp225);
        assertTrue(comp225.getStudents().contains(zongo));
        assertEquals(list(fred), comp225.getWaitList());
    }

    // ------ Post-test invariant check ------
    //
    // This is a bit persnickety for day-to-day testing, but these kinds of checks are appropriate
    // for security sensitive or otherwise mission critical code. Some people even add them as
    // runtime checks in the code, instead of writing them as tests.

    @After
    public void checkInvariants() {
        for(Student s : factory.allStudents())
            checkStudentInvariants(s);
        for(Course c : factory.allCourses())
            checkCourseInvariants(c);
    }

    private void checkStudentInvariants(Student s) {
        for(Course c : s.getCourses())
            assertTrue(
                    s + " thinks they are enrolled in " + c + ", but " + c + " does not have them in the list of students",
                    c.getStudents().contains(s));
    }

    private void checkCourseInvariants(Course c) {
        Set<Student> waitListUnique = new HashSet<Student>(c.getWaitList());
        assertEquals(
                c + " wait list contains duplicates: " + c.getWaitList(),
                waitListUnique.size(),
                c.getWaitList().size());

        waitListUnique.retainAll(c.getStudents());
        assertEquals(
                c + " contains students who are both registered and waitlisted",
                Collections.emptySet(),
                waitListUnique);

        for(Student s : c.getStudents())
            assertTrue(
                    c + " thinks " + s + " is enrolled, but " + s + " doesn't think they're in the class",
                    s.getCourses().contains(c));

        for(Student s : c.getWaitList())
            assertFalse(
                    c + " lists " + s + " as waitlisted, but " + s + " thinks they are enrolled",
                    s.getCourses().contains(c));

        assertTrue(
                c + " has an enrollment limit of " + c.getEnrollmentLimit()
                        + ", but has " + c.getStudents().size() + " students",
                c.getStudents().size() <= c.getEnrollmentLimit());

        if(c.getStudents().size() < c.getEnrollmentLimit())
            assertEquals(
                    c + " is not full, but has students waitlisted",
                    Collections.emptyList(),
                    c.getWaitList());
    }

    // ------ Helpers ------

    private static <T> Set<T> set(T... args) {
        return new HashSet<T>(Arrays.asList(args));
    }

    private static <T> List<T> list(T... args) {
        return Arrays.asList(args);
    }
}