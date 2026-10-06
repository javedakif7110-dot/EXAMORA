package com.examora.data;

import com.examora.model.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Data Loader for synthetic institutional datasets and reference benchmark datasets.
 * Includes Chennai Institute of Technology dataset structures.
 */
public class ExamoraDataLoader {

    public record DatasetBundle(
            List<Student> students,
            List<Exam> exams,
            List<ExamSlot> slots,
            List<Hall> halls,
            List<Seat> seats,
            List<Invigilator> invigilators
    ) {}

    /**
     * Benchmark Reference Prototype Dataset (180 Students, 4 Halls x 60 Seats, 20 Accessibility).
     * Used for genuine prototype demonstration.
     */
    public static DatasetBundle loadBenchmarkDataset() {
        List<Hall> halls = List.of(
                new Hall("H01", "Hall H01 (Main Block)", "Main", 60, true),
                new Hall("H02", "Hall H02 (Main Block)", "Main", 60, true),
                new Hall("H03", "Hall H03 (CS Block)", "CS", 60, false),
                new Hall("H04", "Hall H04 (CS Block)", "CS", 60, false)
        );

        List<Seat> seats = new ArrayList<>();
        for (Hall h : halls) {
            char rowChar = 'A';
            for (int r = 1; r <= 6; r++) {
                for (int c = 1; c <= 10; c++) {
                    String seatCode = String.format("%c%02d", (char)(rowChar + r - 1), c);
                    boolean isAcc = (r == 1 && c <= 5 && h.isAccessible()); // First 5 seats accessible in accessible halls
                    seats.add(new Seat(Seat.generateSeatId(h.getId(), seatCode), h.getId(), seatCode, r, c, isAcc));
                }
            }
        }

        List<Exam> exams = List.of(
                new Exam("CS201", "Java Programming", "CSE", 180),
                new Exam("CS202", "Database Systems", "CSE", 180),
                new Exam("EC201", "Digital Electronics", "ECE", 180),
                new Exam("ME201", "Thermodynamics", "MECH", 180)
        );

        List<ExamSlot> slots = List.of(
                new ExamSlot("SLOT-1", LocalDate.now().plusDays(1), ExamSlot.SessionType.MORNING),
                new ExamSlot("SLOT-2", LocalDate.now().plusDays(1), ExamSlot.SessionType.AFTERNOON)
        );

        List<Student> students = new ArrayList<>();
        String[] depts = {"CSE", "ECE", "MECH", "IT"};

        for (int i = 1; i <= 180; i++) {
            String id = String.format("CIT-%04d", i);
            String name = "Student " + i;
            String dept = depts[(i - 1) % depts.length];
            boolean acc = (i <= 20); // First 20 students require accessibility seating

            List<String> registeredExams = (i % 2 == 0) ? List.of("CS201") : List.of("CS202");
            students.add(new Student(id, name, dept, (i % 4) + 1, registeredExams, acc));
        }

        List<Invigilator> invigilators = List.of(
                new Invigilator("INV-01", "Dr. A. Sharma", "CSE"),
                new Invigilator("INV-02", "Prof. R. Kumar", "ECE"),
                new Invigilator("INV-03", "Dr. M. Venkatesh", "MECH"),
                new Invigilator("INV-04", "Prof. K. Lakshmi", "IT")
        );

        return new DatasetBundle(students, exams, slots, halls, seats, invigilators);
    }

    /**
     * Large Synthetic Institutional Dataset (1,000 Students, 20 Exams, 10 Halls, 1,200 Seats, 30 Invigilators).
     */
    public static DatasetBundle loadLargeSyntheticDataset() {
        List<Hall> halls = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String hId = String.format("H%02d", i);
            String name = "Examination Hall " + hId;
            String block = (i <= 5) ? "Main Academic Block" : "Science & Tech Block";
            boolean acc = (i <= 4); // First 4 halls fully accessible
            halls.add(new Hall(hId, name, block, 120, acc));
        }

        List<Seat> seats = new ArrayList<>();
        for (Hall h : halls) {
            char rowChar = 'A';
            for (int r = 1; r <= 12; r++) {
                for (int c = 1; c <= 10; c++) {
                    String seatCode = String.format("%c%02d", (char)(rowChar + r - 1), c);
                    boolean isAcc = (r <= 2 && h.isAccessible());
                    seats.add(new Seat(Seat.generateSeatId(h.getId(), seatCode), h.getId(), seatCode, r, c, isAcc));
                }
            }
        }

        List<Exam> exams = new ArrayList<>();
        String[] subjects = {
                "Java Programming", "Database Systems", "Operating Systems", "Data Structures",
                "Computer Networks", "Web Technologies", "Software Engineering", "Artificial Intelligence",
                "Cloud Computing", "Cyber Security", "Machine Learning", "Mobile App Development",
                "Discrete Mathematics", "Theory of Computation", "Compiler Design", "Embedded Systems",
                "Signals & Systems", "VLSI Design", "Control Systems", "Engineering Graphics"
        };
        String[] depts = {"CSE", "ECE", "MECH", "IT", "EEE", "CIVIL", "AIDS", "AIML"};

        for (int i = 0; i < 20; i++) {
            String exId = String.format("EXAM-%02d", i + 1);
            exams.add(new Exam(exId, subjects[i], depts[i % depts.length], 180));
        }

        List<ExamSlot> slots = new ArrayList<>();
        for (int d = 1; d <= 5; d++) {
            LocalDate date = LocalDate.now().plusDays(d);
            slots.add(new ExamSlot("SLOT-D" + d + "-AM", date, ExamSlot.SessionType.MORNING));
            slots.add(new ExamSlot("SLOT-D" + d + "-PM", date, ExamSlot.SessionType.AFTERNOON));
        }

        List<Student> students = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            String id = String.format("CIT-2026-%04d", i);
            String name = "CIT Student " + i;
            String dept = depts[(i - 1) % depts.length];
            boolean acc = (i % 25 == 0); // 40 accessible students

            String ex1 = exams.get((i - 1) % exams.size()).id();
            String ex2 = exams.get((i + 3) % exams.size()).id();
            students.add(new Student(id, name, dept, ((i - 1) % 4) + 1, List.of(ex1, ex2), acc));
        }

        List<Invigilator> invigilators = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            String id = String.format("INV-%02d", i);
            invigilators.add(new Invigilator(id, "Faculty " + i, depts[i % depts.length]));
        }

        return new DatasetBundle(students, exams, slots, halls, seats, invigilators);
    }
}
