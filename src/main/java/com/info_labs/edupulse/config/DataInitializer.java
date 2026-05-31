//package com.info_labs.edupulse.config;
//
//import com.info_labs.edupulse.entity.*;
//import com.info_labs.edupulse.repository.*;
//import com.info_labs.edupulse.utils.ProfileType;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//
//@Component
//@RequiredArgsConstructor
//public class DataInitializer implements CommandLineRunner {
//
//    private final ClassRepository       classRepository;
//    private final UserRepository        userRepository;
//    private final ExamRepository        examRepository;
//    private final QuestionRepository    questionRepository;
//    private final StudentExamRepository studentExamRepository;
//    private final NotificationRepository notificationRepository;
//    private final PasswordEncoder       passwordEncoder;
//
//    @Override
//    public void run(String... args) {
//        String pw = passwordEncoder.encode("password123");
//
//        // ── Always ensure admin + teacher exist (idempotent) ─────────────────
//        if (!userRepository.existsByMobile("0700000000")) {
//            User admin = new User();
//            admin.setName("Admin");
//            admin.setMobile("0700000000");
//            admin.setPassword(pw);
//            admin.setProfileType(ProfileType.ADMIN);
//            admin.setClasses(new HashSet<>());
//            userRepository.save(admin);
//            System.out.println("  ✔ Admin created: 0700000000 / password123");
//        }
//
//        if (!userRepository.existsByMobile("0711111111")) {
//            User teacher = new User();
//            teacher.setName("Teacher");
//            teacher.setMobile("0711111111");
//            teacher.setPassword(pw);
//            teacher.setProfileType(ProfileType.TEACHER);
//            List<ClassEntity> all = classRepository.findAll();
//            teacher.setClasses(new HashSet<>(all.subList(0, Math.min(3, all.size()))));
//            userRepository.save(teacher);
//            System.out.println("  ✔ Teacher created: 0711111111 / password123");
//        }
//
//        if (classRepository.count() > 0) return; // students/exams already seeded
//
//        // ── Classes ──────────────────────────────────────────────────────────
//        ClassEntity c1 = save(cls("Grade 10 - A"));
//        ClassEntity c2 = save(cls("Grade 10 - B"));
//        ClassEntity c3 = save(cls("Grade 11 - A"));
//        ClassEntity c4 = save(cls("Grade 11 - B"));
//
//        // ── Users (students can be enrolled in multiple classes) ─────────────
//        User alice   = save(user("Alice Johnson",  "0771234567", pw, c1));
//        User bob     = save(user("Bob Smith",      "0772345678", pw, c1, c3)); // enrolled in 10-A and 11-A
//        User charlie = save(user("Charlie Brown",  "0773456789", pw, c1));
//        User diana   = save(user("Diana Prince",   "0774567890", pw, c1));
//        User eve     = save(user("Eve Wilson",     "0775678901", pw, c1));
//        User frank   = save(user("Frank Miller",   "0776789012", pw, c1, c2)); // enrolled in 10-A and 10-B
//        User grace   = save(user("Grace Lee",      "0777890123", pw, c1));
//        User henry   = save(user("Henry Adams",    "0778901234", pw, c2));
//        User isla    = save(user("Isla Martin",    "0779012345", pw, c2));
//        User jake    = save(user("Jake Turner",    "0770123456", pw, c2));
//        User karen   = save(user("Karen White",    "0761234567", pw, c3));
//        User leo     = save(user("Leo Clark",      "0762345678", pw, c3));
//
//        // ── Exams ─────────────────────────────────────────────────────────────
//        Exam math    = save(exam("Mathematics Mid-Term", 100, "2026-04-15", c1));
//        Exam science = save(exam("Science Quiz 1",        50, "2026-04-20", c1));
//        Exam english = save(exam("English Essay",         80, "2026-05-01", c1));
//        Exam history = save(exam("History Test",          60, "2026-06-10", c1));
//        Exam physics = save(exam("Physics Final",        100, "2026-07-01", c1));
//        Exam mathB   = save(exam("Mathematics Basics",   80, "2026-04-18", c2));
//        Exam sciB    = save(exam("Science Foundations",  60, "2026-04-25", c2));
//        Exam advMath = save(exam("Advanced Mathematics",100, "2026-04-22", c3));
//        Exam advPhys = save(exam("Physics Advanced",    100, "2026-05-05", c3));
//
//        // ── Questions ────────────────────────────────────────────────────────
//        seedMathQuestions(math);
//        seedScienceQuestions(science);
//        seedEnglishQuestions(english);
//        seedHistoryQuestions(history);
//        seedPhysicsQuestions(physics);
//        seedMathQuestions(mathB);
//        seedScienceQuestions(sciB);
//        seedMathQuestions(advMath);
//        seedPhysicsQuestions(advPhys);
//
//        // ── Student Exam Records ──────────────────────────────────────────────
//        saveAttempt(alice,   math,    88, "2026-04-15");
//        saveAttempt(alice,   science, 45, "2026-04-20");
//        saveAttempt(alice,   english, 72, "2026-05-01");
//        saveAttempt(bob,     math,    92, "2026-04-15");
//        saveAttempt(bob,     science, 48, "2026-04-20");
//        saveAttempt(bob,     english, 68, "2026-05-01");
//        saveAttempt(charlie, math,    75, "2026-04-15");
//        saveAttempt(charlie, science, 40, "2026-04-20");
//        saveAttempt(charlie, english, 65, "2026-05-01");
//        saveAttempt(diana,   math,    95, "2026-04-15");
//        saveAttempt(diana,   science, 46, "2026-04-20");
//        saveAttempt(eve,     math,    60, "2026-04-15");
//        saveAttempt(eve,     science, 35, "2026-04-20");
//        saveAttempt(frank,   math,    82, "2026-04-15");
//        saveAttempt(frank,   science, 42, "2026-04-20");
//        saveAttempt(grace,   math,    78, "2026-04-15");
//        saveAttempt(grace,   science, 38, "2026-04-20");
//        saveAttempt(henry,   mathB,   70, "2026-04-18");
//        saveAttempt(isla,    mathB,   65, "2026-04-18");
//        saveAttempt(jake,    mathB,   75, "2026-04-18");
//        saveAttempt(karen,   advMath, 88, "2026-04-22");
//        saveAttempt(leo,     advMath, 92, "2026-04-22");
//
//        // ── Notifications ─────────────────────────────────────────────────────
//        saveNotif(alice,   "Welcome to EduPulse, Alice! Start exploring your exams.",           true,  "2026-04-01");
//        saveNotif(alice,   "Your score for Mathematics Mid-Term: 88/100.",                      true,  "2026-04-16");
//        saveNotif(alice,   "Your score for Science Quiz 1: 45/50.",                             false, "2026-04-21");
//        saveNotif(alice,   "New exam available: English Essay — due May 1st.",                  false, "2026-04-22");
//        saveNotif(bob,     "Welcome to EduPulse, Bob! Start exploring your exams.",             true,  "2026-04-01");
//        saveNotif(bob,     "Congratulations! You ranked #1 in class this week!",                false, "2026-04-28");
//        saveNotif(charlie, "Welcome to EduPulse, Charlie! Start exploring your exams.",         true,  "2026-04-01");
//        saveNotif(charlie, "Your score for Mathematics Mid-Term: 75/100.",                      false, "2026-04-16");
//        saveNotif(charlie, "New exam available: English Essay — due May 1st.",                  false, "2026-04-22");
//        saveNotif(charlie, "You ranked #3 in class this week!",                                 true,  "2026-04-28");
//        saveNotif(charlie, "Reminder: History Test on June 10th.",                              false, "2026-05-01");
//        saveNotif(diana,   "Welcome to EduPulse, Diana! Start exploring your exams.",           true,  "2026-04-01");
//        saveNotif(diana,   "Excellent! You scored 95/100 on Mathematics Mid-Term.",             false, "2026-04-16");
//        saveNotif(eve,     "Welcome to EduPulse, Eve! Start exploring your exams.",             true,  "2026-04-01");
//
//        System.out.println("\n✔ EduPulse seed data loaded successfully.\n" +
//            "  Test accounts (password: password123):\n" +
//            "  ADMIN:      0700000000 (Admin)\n" +
//            "  TEACHER:    0711111111 (Teacher — teaches Grade 10-A, 10-B, 11-A)\n" +
//            "  Grade 10-A: 0771234567 (Alice) | 0772345678 (Bob) | 0773456789 (Charlie)\n" +
//            "              0774567890 (Diana)  | 0775678901 (Eve)  | 0776789012 (Frank)\n" +
//            "  Grade 10-B: 0778901234 (Henry)  | 0779012345 (Isla) | 0770123456 (Jake)\n" +
//            "  Grade 11-A: 0761234567 (Karen)  | 0762345678 (Leo)\n");
//    }
//
//    // ── Helpers ──────────────────────────────────────────────────────────────
//
//    private ClassEntity save(ClassEntity c) { return classRepository.save(c); }
//    private User        save(User u)        { return userRepository.save(u); }
//    private Exam        save(Exam e)        { return examRepository.save(e); }
//
//    private ClassEntity cls(String name) {
//        ClassEntity c = new ClassEntity(); c.setName(name); return c;
//    }
//
//    private User user(String name, String mobile, String pw, ClassEntity... classes) {
//        User u = new User();
//        u.setName(name);
//        u.setMobile(mobile);
//        u.setPassword(pw);
//        u.setProfileType(ProfileType.STUDENT);
//        u.setClasses(new HashSet<>(Arrays.asList(classes)));
//        return u;
//    }
//
//    private Exam exam(String title, int total, String date, ClassEntity cls) {
//        Exam e = new Exam(); e.setTitle(title); e.setTotal(total);
//        e.setDate(date); e.setClassEntity(cls); return e;
//    }
//
//    private void saveAttempt(User student, Exam exam, int score, String date) {
//        StudentExam se = new StudentExam();
//        se.setStudent(student); se.setExam(exam); se.setScore(score); se.setSubmittedAt(date);
//        studentExamRepository.save(se);
//    }
//
//    private void saveNotif(User user, String msg, boolean read, String date) {
//        Notification n = new Notification();
//        n.setUser(user); n.setMessage(msg); n.setIsRead(read); n.setDate(date);
//        notificationRepository.save(n);
//    }
//
//    private void saveQ(Exam exam, int num, String text, List<String> opts, int correct) {
//        Question q = new Question();
//        q.setExam(exam); q.setNumber(num); q.setText(text);
//        q.setOptions(opts); q.setCorrectAnswer(correct);
//        questionRepository.save(q);
//    }
//
//    private List<String> opts(String... o) { return Arrays.asList(o); }
//
//    // ── Question banks ────────────────────────────────────────────────────────
//
//    private void seedMathQuestions(Exam e) {
//        saveQ(e,  1, "What is 12 × 13?",                                           opts("144","156","148","150","169"), 2);
//        saveQ(e,  2, "Solve: 2x + 6 = 14. Find x.",                               opts("8","4","6","3","7"),           2);
//        saveQ(e,  3, "Area of a rectangle: length 8, width 5?",                   opts("13","30","35","40","45"),       4);
//        saveQ(e,  4, "What is 15% of 200?",                                        opts("20","25","30","35","40"),       3);
//        saveQ(e,  5, "What is √144?",                                              opts("11","12","13","14","15"),       2);
//        saveQ(e,  6, "Find the LCM of 4 and 6.",                                   opts("8","10","12","16","24"),        3);
//        saveQ(e,  7, "Evaluate 2³.",                                               opts("6","4","8","16","12"),          3);
//        saveQ(e,  8, "Perimeter of a square with side 7?",                        opts("21","28","14","35","42"),        2);
//        saveQ(e,  9, "If y = 2x + 3, what is y when x = 4?",                     opts("9","10","14","11","8"),          4);
//        saveQ(e, 10, "What is 48 ÷ 6?",                                           opts("9","8","7","6","10"),            2);
//        saveQ(e, 11, "What is the HCF of 12 and 18?",                             opts("3","4","6","9","12"),            3);
//        saveQ(e, 12, "Convert 0.75 to a fraction.",                               opts("3/4","1/2","2/3","4/5","5/7"),  1);
//        saveQ(e, 13, "Sum of angles in a triangle?",                              opts("90°","120°","180°","270°","360°"), 3);
//        saveQ(e, 14, "Simplify: 4x + 2x − 3x.",                                  opts("x","3x","6x","2x","5x"),         2);
//        saveQ(e, 15, "What is 7² − 4²?",                                          opts("25","30","33","35","45"),         3);
//        saveQ(e, 16, "Factorise: x² − 9.",                                        opts("(x−3)(x+3)","(x−9)(x+1)","(x−3)²","(x+3)²","(x−1)(x+9)"), 1);
//        saveQ(e, 17, "Median of: 3, 7, 9, 1, 5?",                                opts("7","3","9","5","1"),              4);
//        saveQ(e, 18, "Triangle with sides 3, 4, 5 is?",                           opts("equilateral","isosceles","scalene","right-angled","obtuse"), 4);
//        saveQ(e, 19, "Value of π to 2 decimal places?",                           opts("3.12","3.14","3.16","3.18","3.41"), 2);
//        saveQ(e, 20, "Solve: 5x − 10 = 0.",                                       opts("0","1","2","5","10"),             3);
//        saveQ(e, 21, "Volume of a cube with side 4?",                             opts("16","32","48","64","80"),          4);
//        saveQ(e, 22, "What is 20% of 150?",                                       opts("25","30","35","40","50"),          2);
//        saveQ(e, 23, "Circumference of circle r=7 (π=22/7)?",                    opts("22","33","44","55","66"),           3);
//        saveQ(e, 24, "Slope of y = 3x − 5?",                                     opts("−5","5","3","−3","0"),             3);
//        saveQ(e, 25, "How many sides does a hexagon have?",                       opts("5","6","7","8","9"),               2);
//        saveQ(e, 26, "What is 3/4 + 1/4?",                                        opts("1/2","3/8","1","4/4","2"),         3);
//        saveQ(e, 27, "Express 0.3 as a percentage.",                              opts("3%","0.3%","30%","33%","0.03%"),   3);
//        saveQ(e, 28, "Next prime after 7?",                                        opts("8","9","10","11","12"),            4);
//        saveQ(e, 29, "Evaluate: (3 + 2) × 4.",                                    opts("14","16","20","24","18"),          3);
//        saveQ(e, 30, "Square of 13?",                                              opts("130","156","169","196","144"),     3);
//        saveQ(e, 31, "Solve: 4(x − 2) = 12. Find x.",                            opts("2","3","4","5","6"),               4);
//        saveQ(e, 32, "2/5 of 100?",                                                opts("20","25","30","40","50"),          4);
//        saveQ(e, 33, "Which is a prime number?",                                   opts("9","15","21","23","27"),           4);
//        saveQ(e, 34, "Mode of: 4,4,5,6,6,6,7?",                                  opts("4","5","6","7","none"),             3);
//        saveQ(e, 35, "Simplify: (2x²)(3x³).",                                     opts("5x⁵","6x⁵","6x⁶","5x⁶","6x⁵"),   2);
//        saveQ(e, 36, "5! (5 factorial)?",                                          opts("25","60","120","24","720"),        3);
//        saveQ(e, 37, "Distance = speed 60 × time 3?",                             opts("63","120","160","180","200"),       4);
//        saveQ(e, 38, "Complementary angle of 35°?",                               opts("55°","65°","145°","45°","75°"),    1);
//        saveQ(e, 39, "x/5 = 4, find x.",                                          opts("9","1","20","4","16"),              3);
//        saveQ(e, 40, "Degrees in a full rotation?",                                opts("90","180","270","360","720"),       4);
//        saveQ(e, 41, "0.5 × 0.5?",                                                opts("0.1","0.5","0.25","0.025","1"),     3);
//        saveQ(e, 42, "Interior angles of a quadrilateral?",                       opts("180°","270°","360°","90°","540°"),  3);
//        saveQ(e, 43, "Expand (x + 3)².",                                          opts("x²+6x+9","x²+3x+9","x²+9","x²+6x+3","x²+9x+6"), 1);
//        saveQ(e, 44, "Reciprocal of 4?",                                           opts("4","2","0.5","0.25","8"),          4);
//        saveQ(e, 45, "18 is what % of 72?",                                        opts("18%","20%","25%","30%","15%"),     3);
//        saveQ(e, 46, "Average of 10, 20, 30?",                                     opts("10","15","20","25","30"),          3);
//        saveQ(e, 47, "√x = 9, find x.",                                            opts("3","18","27","81","9"),            4);
//        saveQ(e, 48, "Gradient of a horizontal line?",                             opts("1","−1","0","undefined","∞"),      3);
//        saveQ(e, 49, "1000 in Roman numerals?",                                    opts("C","D","M","L","X"),               3);
//        saveQ(e, 50, "The ratio 3:5 expressed as % of total is 3 out of?",        opts("37.5%","60%","40%","30%","50%"),    2);
//    }
//
//    private void seedScienceQuestions(Exam e) {
//        saveQ(e,  1, "Water is composed of which elements?",                        opts("H & C","H & O","O & N","N & H","C & O"),       2);
//        saveQ(e,  2, "Chemical symbol for Gold?",                                   opts("Gd","Go","Au","Ag","Gl"),                       3);
//        saveQ(e,  3, "Planet closest to the Sun?",                                  opts("Mercury","Venus","Earth","Mars","Saturn"),       1);
//        saveQ(e,  4, "Atomic number of Carbon?",                                    opts("4","6","8","12","14"),                           2);
//        saveQ(e,  5, "Photosynthesis produces which gas?",                          opts("CO₂","N₂","O₂","H₂","Ar"),                      3);
//        saveQ(e,  6, "Chambers in the human heart?",                                opts("2","3","4","5","6"),                             3);
//        saveQ(e,  7, "Approximate speed of light in km/s?",                        opts("300,000","30,000","3,000,000","3,000","300"),     1);
//        saveQ(e,  8, "Main gas in Earth's atmosphere?",                             opts("Oxygen","Nitrogen","CO₂","Argon","Hydrogen"),    2);
//        saveQ(e,  9, "Newton's first law relates to?",                             opts("gravity","inertia","friction","momentum","velocity"), 2);
//        saveQ(e, 10, "Powerhouse of the cell?",                                     opts("mitochondria","nucleus","ribosome","vacuole","lysosome"), 1);
//        saveQ(e, 11, "Formula for table salt?",                                     opts("KCl","NaOH","NaCl","HCl","CaCl₂"),              3);
//        saveQ(e, 12, "Organ that produces insulin?",                                opts("liver","kidney","pancreas","stomach","spleen"),  3);
//        saveQ(e, 13, "Type of bond that shares electrons?",                        opts("ionic","metallic","covalent","hydrogen","van der Waals"), 3);
//        saveQ(e, 14, "Layer containing ozone?",                                     opts("troposphere","mesosphere","stratosphere","thermosphere","exosphere"), 3);
//        saveQ(e, 15, "SI unit of temperature?",                                     opts("Celsius","Fahrenheit","Rankine","Kelvin","Joule"), 4);
//        saveQ(e, 16, "Bones in adult human body?",                                  opts("186","206","216","226","246"),                   2);
//        saveQ(e, 17, "Process plants lose water vapour?",                           opts("respiration","transpiration","photosynthesis","diffusion","osmosis"), 2);
//        saveQ(e, 18, "Planet with most moons?",                                     opts("Jupiter","Saturn","Uranus","Neptune","Mars"),    2);
//        saveQ(e, 19, "pH of pure water?",                                           opts("5","6","7","8","9"),                             3);
//        saveQ(e, 20, "DNA stands for?",                                             opts("Deoxyribose Nucleic Acid","Deoxyribonucleic Acid","Dinucleic Acid","Double Nucleic Acid","Dextroribonucleic Acid"), 2);
//        saveQ(e, 21, "Universal blood donor type?",                                 opts("A","B","AB","O+","O−"),                          5);
//        saveQ(e, 22, "Acceleration due to gravity on Earth?",                      opts("8.8 m/s²","9.8 m/s²","10.8 m/s²","11.8 m/s²","7.8 m/s²"), 2);
//        saveQ(e, 23, "Solid turning directly to gas?",                              opts("evaporation","condensation","sublimation","deposition","melting"), 3);
//        saveQ(e, 24, "Symbol for Iron?",                                            opts("Io","In","Fe","Ir","Is"),                        3);
//        saveQ(e, 25, "Osmosis is the movement of?",                                 opts("solute","water across semi-permeable membrane","gas","all molecules","proteins"), 2);
//        saveQ(e, 26, "Normal chromosomes in human body cell?",                     opts("23","44","46","48","22"),                         3);
//        saveQ(e, 27, "Gas used in photosynthesis?",                                 opts("O₂","N₂","CO₂","H₂","Cl₂"),                    3);
//        saveQ(e, 28, "SI unit of electric current?",                                opts("Volt","Watt","Ohm","Ampere","Joule"),            4);
//        saveQ(e, 29, "Rock formed from cooled magma?",                              opts("sedimentary","metamorphic","igneous","limestone","marble"), 3);
//        saveQ(e, 30, "Organelle containing chlorophyll?",                          opts("mitochondria","nucleus","chloroplast","ribosome","vacuole"), 3);
//        saveQ(e, 31, "Boiling point of water at sea level?",                       opts("90°C","95°C","100°C","105°C","110°C"),           3);
//        saveQ(e, 32, "Sound travels fastest through?",                              opts("air","water","vacuum","steel","wood"),           4);
//        saveQ(e, 33, "Vitamin from sunlight?",                                      opts("A","B12","C","D","E"),                           4);
//        saveQ(e, 34, "Function of red blood cells?",                                opts("fight infection","transport oxygen","hormones","digest food","filter blood"), 2);
//        saveQ(e, 35, "Energy stored in food?",                                      opts("kinetic","nuclear","electrical","chemical","thermal"), 4);
//        saveQ(e, 36, "Friction acts in which direction?",                           opts("same as motion","perpendicular","opposite to motion","upward","downward"), 3);
//        saveQ(e, 37, "What does a catalyst do?",                                    opts("increases energy","slows reaction","speeds up without being consumed","produces heat","adds mass"), 3);
//        saveQ(e, 38, "Brain part controlling balance?",                             opts("cerebrum","cerebellum","medulla","thalamus","hypothalamus"), 2);
//        saveQ(e, 39, "Symbol for Silver?",                                          opts("Si","Sv","Sg","Ag","Sr"),                        4);
//        saveQ(e, 40, "Neutrons in Carbon-14?",                                      opts("6","7","8","12","14"),                           3);
//        saveQ(e, 41, "Kidneys are part of which system?",                           opts("digestive","nervous","excretory","endocrine","respiratory"), 3);
//        saveQ(e, 42, "Convex lenses cause light to?",                               opts("diverge","reflect","absorb","converge","refract equally"), 4);
//        saveQ(e, 43, "Gas produced during fermentation?",                           opts("O₂","N₂","CO₂","H₂","CH₄"),                    3);
//        saveQ(e, 44, "Most abundant element in universe?",                          opts("Oxygen","Carbon","Helium","Hydrogen","Nitrogen"), 4);
//        saveQ(e, 45, "Newton's third law: every action has?",                      opts("a greater reaction","a lesser reaction","equal and opposite reaction","no reaction","proportional reaction"), 3);
//        saveQ(e, 46, "Substance that donates protons?",                             opts("base","salt","acid","catalyst","indicator"),     3);
//        saveQ(e, 47, "Red Planet?",                                                  opts("Jupiter","Venus","Mars","Mercury","Saturn"),     3);
//        saveQ(e, 48, "Organisms that make their own food?",                         opts("heterotrophs","consumers","autotrophs","decomposers","parasites"), 3);
//        saveQ(e, 49, "Unit of electrical potential difference?",                   opts("Ampere","Watt","Coulomb","Volt","Ohm"),           4);
//        saveQ(e, 50, "Nuclear force holds the nucleus of an atom?",               opts("electrical","gravitational","nuclear","magnetic","chemical bond"), 3);
//    }
//
//    private void seedEnglishQuestions(Exam e) {
//        saveQ(e,  1, "Which is a noun?",                                            opts("run","beautiful","happiness","quickly","above"),  3);
//        saveQ(e,  2, "Past tense of 'write'?",                                     opts("wrote","written","writing","writed","writ"),       1);
//        saveQ(e,  3, "Grammatically correct sentence?",                            opts("She don't like apples.","She doesn't like apples.","She doesn't likes apples.","Her don't like apples.","She not like apples."), 2);
//        saveQ(e,  4, "Synonym for 'happy'?",                                        opts("sad","angry","joyful","tired","cold"),            3);
//        saveQ(e,  5, "Literary device repeating initial consonant?",               opts("simile","alliteration","metaphor","personification","hyperbole"), 2);
//        saveQ(e,  6, "Plural of 'ox'?",                                             opts("oxen","oxes","ox's","oxs","oxens"),               1);
//        saveQ(e,  7, "Antonym of 'ancient'?",                                       opts("old","past","modern","historic","aged"),          3);
//        saveQ(e,  8, "'I run fast.' Type of verb?",                                 opts("transitive","intransitive","linking","auxiliary","modal"), 2);
//        saveQ(e,  9, "Identify the simile.",                                        opts("The stars are diamonds.","He was a lion.","She sang like a nightingale.","The wind howled.","He ran."), 3);
//        saveQ(e, 10, "Correct word: '___ going home.'",                            opts("There","Their","They're","Thier","Ther"),          3);
//        saveQ(e, 11, "Word describing a verb?",                                     opts("adjective","adverb","noun","conjunction","preposition"), 2);
//        saveQ(e, 12, "'The sky is a blue canvas.' This is?",                       opts("simile","alliteration","metaphor","irony","onomatopoeia"), 3);
//        saveQ(e, 13, "Passive voice sentence?",                                     opts("The dog bit the man.","The man bought the car.","The letter was written by Tom.","She sings.","He runs."), 3);
//        saveQ(e, 14, "Prefix 'un-' means?",                                         opts("again","before","not","after","under"),           3);
//        saveQ(e, 15, "Which is a conjunction?",                                     opts("quickly","beautiful","because","above","run"),    3);
//        saveQ(e, 16, "'Buzz' and 'hiss' are examples of?",                         opts("simile","alliteration","metaphor","onomatopoeia","personification"), 4);
//        saveQ(e, 17, "Correct apostrophe use?",                                     opts("its raining","the dogs tail","the girl's book","the boys shoes","its mine"), 3);
//        saveQ(e, 18, "Autobiography is written by?",                                opts("another person","the subject themselves","a biographer","a journalist","a historian"), 2);
//        saveQ(e, 19, "Comparative of 'good'?",                                      opts("gooder","more good","best","better","goodest"),   4);
//        saveQ(e, 20, "Example of personification?",                                 opts("The sun smiled down.","He runs fast.","The car is red.","She cried.","It rained."), 1);
//        saveQ(e, 21, "Subject of 'The dog chased the cat.'?",                     opts("chased","the cat","dog","The dog","cat"),           4);
//        saveQ(e, 22, "Genre with dragons and magic?",                               opts("mystery","romance","fantasy","thriller","biography"), 3);
//        saveQ(e, 23, "Word opposite in meaning?",                                   opts("synonym","homophone","antonym","homonym","prefix"), 3);
//        saveQ(e, 24, "Main argument of an essay?",                                  opts("introduction","conclusion","thesis statement","body paragraph","hook"), 3);
//        saveQ(e, 25, "'She has been studying all day.' Tense?",                    opts("simple past","present simple","present perfect continuous","past continuous","future perfect"), 3);
//        saveQ(e, 26, "Punctuation ending a question?",                              opts(".","!","?",",",";"),                              3);
//        saveQ(e, 27, "First-person pronoun?",                                       opts("he","she","they","I","it"),                       4);
//        saveQ(e, 28, "Haiku is?",                                                   opts("14-line poem","poem with rhyme","3-line 5-7-5 syllable poem","a limerick","a sonnet"), 3);
//        saveQ(e, 29, "Sentence with an error?",                                     opts("I went to the shop.","She runs every day.","He have a new car.","They arrived late.","We are leaving now."), 3);
//        saveQ(e, 30, "Climax of a story?",                                          opts("beginning","rising action","most exciting point","resolution","falling action"), 3);
//        saveQ(e, 31, "Synonym of 'enormous'?",                                      opts("tiny","average","huge","mild","distant"),         3);
//        saveQ(e, 32, "Example of irony?",                                           opts("A sunny day.","A fireman's house burns down.","A dog barks.","She smiled.","He ran fast."), 2);
//        saveQ(e, 33, "Object in 'John kicked the ball.'?",                         opts("John","kicked","the","ball","the ball"),           5);
//        saveQ(e, 34, "Suffix making 'care' a noun?",                               opts("-ful","-less","-ness","-tion","-ly"),              3);
//        saveQ(e, 35, "Lines in a limerick?",                                        opts("3","4","5","6","7"),                              3);
//        saveQ(e, 36, "'Let the cat out of the bag' is?",                           opts("simile","alliteration","idiom","metaphor","personification"), 3);
//        saveQ(e, 37, "Correctly spelled word?",                                     opts("recieve","beleive","achieve","freind","theif"),   3);
//        saveQ(e, 38, "Thesis statement appears in?",                                opts("body","conclusion","introduction","appendix","abstract"), 3);
//        saveQ(e, 39, "Compound sentence?",                                          opts("She ran.","I like cats.","He sings and she dances.","Running is fun.","Although tired, she continued."), 3);
//        saveQ(e, 40, "Tone of writing is?",                                         opts("font size","author's attitude","the plot","the setting","the characters"), 2);
//        saveQ(e, 41, "Verb form after 'to' in infinitive?",                        opts("past","present participle","base form","past participle","future"), 3);
//        saveQ(e, 42, "Allegory uses characters to represent?",                     opts("real people","abstract ideas","historical events","scientific concepts","geographical places"), 2);
//        saveQ(e, 43, "Which is a command?",                                         opts("Is it raining?","It is raining.","Please close the door.","I love rain.","Rain falls."), 3);
//        saveQ(e, 44, "Foreshadowing in a story?",                                   opts("summarises plot","hints at future events","describes setting","introduces characters","resolves conflict"), 2);
//        saveQ(e, 45, "Punctuation introducing a list?",                            opts("comma","full stop","colon","semicolon","apostrophe"), 3);
//        saveQ(e, 46, "Narrative technique showing thoughts?",                      opts("dialogue","soliloquy","stream of consciousness","flashback","imagery"), 3);
//        saveQ(e, 47, "'Beautiful' is what part of speech?",                        opts("noun","verb","adverb","adjective","preposition"),  4);
//        saveQ(e, 48, "NOT a type of poem?",                                         opts("sonnet","haiku","limerick","novella","ballad"),    4);
//        saveQ(e, 49, "Suffix '-ology' means?",                                      opts("fear of","love of","study of","hatred of","ability to"), 3);
//        saveQ(e, 50, "Correct sentence?",                                           opts("Neither of them are ready.","Neither of them is ready.","Neither were ready.","Neither them is ready.","Neither them are ready."), 2);
//    }
//
//    private void seedHistoryQuestions(Exam e) {
//        saveQ(e,  1, "When did World War II end?",                                  opts("1940","1945","1943","1948","1950"),               2);
//        saveQ(e,  2, "First U.S. President?",                                       opts("George Washington","Thomas Jefferson","Abraham Lincoln","John Adams","Benjamin Franklin"), 1);
//        saveQ(e,  3, "India gained independence in?",                               opts("1945","1946","1947","1948","1949"),               3);
//        saveQ(e,  4, "Magna Carta signed in?",                                      opts("1066","1215","1337","1415","1492"),               2);
//        saveQ(e,  5, "Renaissance was primarily a period of?",                     opts("warfare","cultural and intellectual rebirth","religious reformation","industrialisation","democratic revolution"), 2);
//        saveQ(e,  6, "Who invented the printing press?",                            opts("Johannes Gutenberg","Isaac Newton","Leonardo da Vinci","Galileo","Thomas Edison"), 1);
//        saveQ(e,  7, "Berlin Wall fell in?",                                        opts("1985","1987","1989","1991","1993"),               3);
//        saveQ(e,  8, "Julius Caesar ruled which empire?",                           opts("Greek","Ottoman","British","Roman","Byzantine"),  4);
//        saveQ(e,  9, "French Revolution began in?",                                 opts("1765","1775","1789","1799","1815"),               3);
//        saveQ(e, 10, "Last Pharaoh of ancient Egypt?",                              opts("Nefertiti","Ramesses II","Tutankhamun","Cleopatra VII","Hatshepsut"), 4);
//        saveQ(e, 11, "First atomic bomb dropped on?",                               opts("Tokyo","Osaka","Nagasaki","Hiroshima","Kyoto"),   4);
//        saveQ(e, 12, "Who wrote the Communist Manifesto?",                          opts("Lenin","Stalin","Marx and Engels","Mao Zedong","Trotsky"), 3);
//        saveQ(e, 13, "Silk Road connected?",                                        opts("Europe & Africa","Americas & Asia","China & Mediterranean","India & Japan","Arabia & Americas"), 3);
//        saveQ(e, 14, "Battle of Waterloo fought in?",                               opts("1805","1810","1812","1815","1820"),               4);
//        saveQ(e, 15, "Led the Cuban Revolution?",                                   opts("Che Guevara","Batista","Fidel Castro","Carlos Prío","Raúl Castro"), 3);
//        saveQ(e, 16, "Western Roman Empire fell in?",                               opts("410","455","476","500","527"),                    3);
//        saveQ(e, 17, "First person to walk on the Moon?",                          opts("Buzz Aldrin","Yuri Gagarin","Neil Armstrong","Alan Shepard","John Glenn"), 3);
//        saveQ(e, 18, "Great Wall of China built against?",                         opts("floods","sea invaders","nomadic invasions from north","trade rivals","eruptions"), 3);
//        saveQ(e, 19, "Nelson Mandela became president in?",                        opts("1990","1992","1994","1996","1998"),                3);
//        saveQ(e, 20, "War fought 1950–1953 in Asia?",                              opts("Vietnam War","Korean War","Gulf War","Sino-Japanese War","Cold War"), 2);
//        saveQ(e, 21, "Titanic sank in?",                                            opts("1908","1910","1912","1914","1916"),               3);
//        saveQ(e, 22, "Leader of Nazi Germany?",                                     opts("Mussolini","Franco","Stalin","Hitler","Hirohito"), 4);
//        saveQ(e, 23, "United Nations founded in?",                                  opts("1942","1944","1945","1947","1948"),               3);
//        saveQ(e, 24, "Industrial Revolution began in?",                             opts("France","Germany","USA","Britain","Italy"),       4);
//        saveQ(e, 25, "Who sailed to Americas in 1492?",                            opts("Vasco da Gama","Francis Drake","Christopher Columbus","Ferdinand Magellan","Amerigo Vespucci"), 3);
//        saveQ(e, 26, "Cold War mainly between?",                                    opts("USA & China","UK & USSR","USA & USSR","China & USSR","USA & Germany"), 3);
//        saveQ(e, 27, "Built the pyramids at Giza?",                                opts("Mesopotamian","Greek","Roman","Egyptian","Persian"), 4);
//        saveQ(e, 28, "Treaty of Versailles after which war?",                      opts("WWI","WWII","Crimean War","Franco-Prussian War","Korean War"), 1);
//        saveQ(e, 29, "Gandhi is associated with?",                                  opts("armed revolution","non-violent civil disobedience","parliamentary democracy","military coup","economic boycott only"), 2);
//        saveQ(e, 30, "Black Death reached Europe in?",                             opts("1200s","1300s","1400s","1500s","1100s"),           2);
//        saveQ(e, 31, "Country that launched Sputnik?",                              opts("USA","China","Germany","USSR","France"),          4);
//        saveQ(e, 32, "Hundred Years' War fought between?",                        opts("England & Spain","France & Germany","England & France","Spain & Portugal","Italy & Austria"), 3);
//        saveQ(e, 33, "First female PM of the UK?",                                  opts("Queen Victoria","Theresa May","Margaret Thatcher","Angela Merkel","Jacinda Ardern"), 3);
//        saveQ(e, 34, "1989 pro-democracy protests in?",                            opts("Shanghai","Hong Kong","Beijing","Tokyo","Seoul"),  3);
//        saveQ(e, 35, "Ottoman Empire centred in?",                                  opts("Iran","Egypt","Turkey","Saudi Arabia","Iraq"),    3);
//        saveQ(e, 36, "Assassination sparking WWI?",                                 opts("Kaiser Wilhelm II","Archduke Franz Ferdinand","Tsar Nicholas II","President Poincaré","PM Asquith"), 2);
//        saveQ(e, 37, "Apartheid was in?",                                           opts("Kenya","Zimbabwe","Nigeria","South Africa","Mozambique"), 4);
//        saveQ(e, 38, "Boston Tea Party in?",                                        opts("1765","1770","1773","1776","1781"),               3);
//        saveQ(e, 39, "Who wrote 'The Art of War'?",                                opts("Confucius","Lao Tzu","Sun Tzu","Genghis Khan","Kublai Khan"), 3);
//        saveQ(e, 40, "Russian revolution of 1917?",                                opts("French Revolution","American Revolution","Bolshevik Revolution","Cultural Revolution","Industrial Revolution"), 3);
//        saveQ(e, 41, "League of Nations preceded?",                                opts("NATO","European Union","United Nations","WHO","WTO"), 3);
//        saveQ(e, 42, "MLK 'I Have a Dream' in?",                                   opts("1960","1962","1963","1965","1968"),               3);
//        saveQ(e, 43, "Colosseum located in?",                                       opts("Athens","Cairo","London","Rome","Paris"),         4);
//        saveQ(e, 44, "World War I ended in?",                                       opts("1916","1917","1918","1919","1920"),               3);
//        saveQ(e, 45, "Renaissance began in?",                                       opts("France","Germany","England","Italy","Spain"),     4);
//        saveQ(e, 46, "Emperor of France early 1800s?",                              opts("Louis XVI","Charles X","Napoleon Bonaparte","Louis XVIII","Philippe I"), 3);
//        saveQ(e, 47, "Suez Canal connects?",                                        opts("Atlantic & Pacific","Red Sea & Mediterranean","Black Sea & Caspian","Persian Gulf & Arabian Sea","Indian & Pacific"), 2);
//        saveQ(e, 48, "USA declared independence in?",                               opts("1773","1775","1776","1781","1783"),               3);
//        saveQ(e, 49, "Commanded Allied forces D-Day 1944?",                        opts("MacArthur","Churchill","Eisenhower","Montgomery","Patton"), 3);
//        saveQ(e, 50, "Aztec Empire conquered by?",                                  opts("Francis Drake","Hernán Cortés","Francisco Pizarro","Vasco da Gama","Columbus"), 2);
//    }
//
//    private void seedPhysicsQuestions(Exam e) {
//        saveQ(e,  1, "SI unit of force?",                                           opts("Pascal","Newton","Joule","Watt","Ampere"),        2);
//        saveQ(e,  2, "F = ma is Newton's which law?",                              opts("First","Second","Third","Gravitation","Conservation"), 2);
//        saveQ(e,  3, "Speed of sound in air at room temp?",                        opts("343 m/s","233 m/s","443 m/s","143 m/s","543 m/s"), 1);
//        saveQ(e,  4, "Moving object has which energy?",                            opts("potential","thermal","nuclear","kinetic","chemical"), 4);
//        saveQ(e,  5, "Ohm's Law: V = ?",                                            opts("I/R","IR","I+R","R/I","I²R"),                     2);
//        saveQ(e,  6, "SI unit of electrical resistance?",                          opts("Volt","Ampere","Watt","Ohm","Siemens"),            4);
//        saveQ(e,  7, "Rainbows formed due to?",                                     opts("reflection","absorption","dispersion/refraction","diffraction","polarisation"), 3);
//        saveQ(e,  8, "E = mc² proposed by?",                                        opts("Newton","Bohr","Einstein","Faraday","Maxwell"),   3);
//        saveQ(e,  9, "Acceleration due to gravity on Earth?",                      opts("8.8 m/s²","9.8 m/s²","10.8 m/s²","11.8 m/s²","7.8 m/s²"), 2);
//        saveQ(e, 10, "Sound is which type of wave?",                               opts("transverse","electromagnetic","longitudinal","light","radio"), 3);
//        saveQ(e, 11, "Energy cannot be created or destroyed: which law?",          opts("Hooke's","Ohm's","1st Law Thermodynamics","2nd Law Thermodynamics","Newton's 3rd"), 3);
//        saveQ(e, 12, "Transformer changes?",                                        opts("frequency","voltage levels","resistance","temperature","capacitance"), 2);
//        saveQ(e, 13, "Mirror producing virtual upright magnified image?",          opts("plane","convex","concave","parabolic","spherical"), 3);
//        saveQ(e, 14, "Unit of power?",                                              opts("Joule","Newton","Watt","Pascal","Volt"),           3);
//        saveQ(e, 15, "Gravitational potential energy formula?",                    opts("mv²","½mv²","mgh","Fd","½kx²"),                   3);
//        saveQ(e, 16, "Electromagnetic waves speed in vacuum?",                     opts("3×10⁶","3×10⁷","3×10⁸","3×10⁹","3×10⁵"),        3);
//        saveQ(e, 17, "Particle with positive charge?",                              opts("electron","neutron","proton","photon","neutrino"), 3);
//        saveQ(e, 18, "Bending of light passing through medium?",                  opts("reflection","diffraction","refraction","dispersion","absorption"), 3);
//        saveQ(e, 19, "Work = Force × ?",                                            opts("time","mass","velocity","displacement","acceleration"), 4);
//        saveQ(e, 20, "Resistance as temperature increases in conductors?",         opts("decreases","stays constant","increases","zero","infinite"), 3);
//        saveQ(e, 21, "Buoyancy discovered by?",                                     opts("Newton","Archimedes","Pascal","Bernoulli","Galileo"), 2);
//        saveQ(e, 22, "Most penetrating radiation?",                                 opts("alpha","beta","gamma","X-ray","UV"),              3);
//        saveQ(e, 23, "Unit of frequency?",                                          opts("Hertz","Watt","Joule","Newton","Pascal"),         1);
//        saveQ(e, 24, "Momentum = mass × ?",                                         opts("force","acceleration","displacement","velocity","time"), 4);
//        saveQ(e, 25, "Longest wavelength in visible light?",                       opts("violet","blue","green","yellow","red"),            5);
//        saveQ(e, 26, "Kinetic energy formula?",                                     opts("mgh","Fd","½mv²","mv","½kx²"),                   3);
//        saveQ(e, 27, "Projectile follows which path?",                              opts("circular","linear","parabolic","elliptical","hyperbolic"), 3);
//        saveQ(e, 28, "Pressure = Force ÷ ?",                                        opts("mass","volume","area","length","density"),       3);
//        saveQ(e, 29, "Lens equation 1/f = 1/v + ?",                               opts("1/m","1/u","1/r","1/n","1/d"),                     2);
//        saveQ(e, 30, "Gas volume proportional to temperature: which law?",         opts("Boyle's","Charles'","Gay-Lussac's","Dalton's","Avogadro's"), 2);
//        saveQ(e, 31, "Half-life of radioactive substance?",                        opts("time to fully decay","time for half to decay","rate of decay","amount of radiation","energy released"), 2);
//        saveQ(e, 32, "Critical angle used for?",                                    opts("diffraction","total internal reflection","refraction","polarisation","dispersion"), 2);
//        saveQ(e, 33, "Current = Charge ÷ ?",                                        opts("Voltage","Resistance","Power","Time","Distance"), 4);
//        saveQ(e, 34, "Light travels fastest through?",                              opts("water","glass","diamond","vacuum","oil"),         4);
//        saveQ(e, 35, "Wavelength and frequency are?",                               opts("directly proportional","no relationship","inversely proportional","exponential","logarithmic"), 3);
//        saveQ(e, 36, "Circuit element storing energy in magnetic field?",          opts("resistor","capacitor","inductor","diode","transistor"), 3);
//        saveQ(e, 37, "Gravitational force depends on?",                             opts("charge","temperature","mass and distance","color","volume"), 3);
//        saveQ(e, 38, "Superposition principle: waves?",                            opts("cancel only","add amplitudes when overlapping","reflect","diffract","refract"), 2);
//        saveQ(e, 39, "Efficiency = (useful output ÷ input) × ?",                  opts("10","50","100","1000","π"),                       3);
//        saveQ(e, 40, "Force keeping planets in orbit?",                            opts("magnetic","nuclear","electromagnetic","gravitational","centrifugal"), 4);
//        saveQ(e, 41, "Terminal velocity reached when?",                            opts("weight equals drag","acceleration is maximum","velocity is zero","force is maximum","mass increases"), 1);
//        saveQ(e, 42, "SI unit of electric charge?",                                opts("Ampere","Volt","Coulomb","Farad","Tesla"),         3);
//        saveQ(e, 43, "Series circuit: same?",                                       opts("voltage","power","current","resistance","impedance"), 3);
//        saveQ(e, 44, "Doppler effect relates to?",                                  opts("amplitude","wavelength/frequency due to motion","speed of light","intensity","phase"), 2);
//        saveQ(e, 45, "Absolute zero in Celsius?",                                   opts("0°C","-100°C","-273°C","-373°C","-173°C"),      3);
//        saveQ(e, 46, "Convex lens is thicker at?",                                  opts("edges","concave side","centre","one side","bottom"), 3);
//        saveQ(e, 47, "Hooke's Law: force proportional to?",                        opts("velocity","mass","extension","time","temperature"), 3);
//        saveQ(e, 48, "Unit of magnetic flux density?",                              opts("Ampere","Volt","Tesla","Weber","Henry"),          3);
//        saveQ(e, 49, "Object in free fall has?",                                    opts("zero net force","weight minus drag","its weight","weight plus drag","upward buoyancy"), 3);
//        saveQ(e, 50, "Planes can fly due to?",                                      opts("Newton's 1st","Archimedes","Bernoulli's Principle","Pascal's Law","Boyle's Law"), 3);
//    }
//}
