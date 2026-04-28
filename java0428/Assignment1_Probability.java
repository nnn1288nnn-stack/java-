public class Assignment1_Probability {

    public static void main(String[] args) {
        System.out.println("=== Assignment 1: Probability Lecture Notes ===\n");
        
        Page1_Title page1 = new Page1_Title();
        page1.display();
        
        Page2_Definition page2 = new Page2_Definition();
        page2.display();
        
        Page3_SampleSpace page3 = new Page3_SampleSpace();
        page3.display();
        
        Page4_Event page4 = new Page4_Event();
        page4.display();
        
        Page5_BasicFormula page5 = new Page5_BasicFormula();
        page5.display();
        
        Page6_ComplementaryEvent page6 = new Page6_ComplementaryEvent();
        page6.display();
        
        Page7_Union page7 = new Page7_Union();
        page7.display();
        
        Page8_Intersection page8 = new Page8_Intersection();
        page8.display();
        
        Page9_ConditionalProbability page9 = new Page9_ConditionalProbability();
        page9.display();
        
        Page10_IndependentEvents page10 = new Page10_IndependentEvents();
        page10.display();
        
        Page11_BayesTheorem page11 = new Page11_BayesTheorem();
        page11.display();
        
        Page12_LawOfTotalProbability page12 = new Page12_LawOfTotalProbability();
        page12.display();
        
        Page13_SchoolExample page13 = new Page13_SchoolExample();
        page13.display();
        
        Page14_UnionExample page14 = new Page14_UnionExample();
        page14.display();
    }

    // --- Individual Class Objects for Each Page ---

    static class Page1_Title {
        public void display() {
            System.out.println("[Page 1] Title Page");
            System.out.println("Topic: Probability");
            System.out.println("Instructor: Xiang-Rui Huang");
            System.out.println("Course: Java Programming (I)");
            System.out.println("--------------------------------------------------");
        }
    }

    static class Page2_Definition {
        public void display() {
            System.out.println("[Page 2] 1. Definition of Probability");
            System.out.println("- Probability indicates the likelihood of an event occurring.");
            System.out.println("- Formula: P(A) = Number of occurrences of event A / Total possible outcomes");
            System.out.println("--------------------------------------------------");
        }
    }

    static class Page3_SampleSpace {
        public void display() {
            System.out.println("[Page 3] 2. Sample Space");
            System.out.println("- S: The set of all possible outcomes");
            System.out.println("- Example: Drawing a student, all students represent the sample space");
            System.out.println("--------------------------------------------------");
        }
    }

    static class Page4_Event {
        public void display() {
            System.out.println("[Page 4] 3. Event");
            System.out.println("- A: The event we care about");
            System.out.println("- Example: Drawing a student from Jian Guo High School");
            System.out.println("--------------------------------------------------");
        }
    }

    static class Page5_BasicFormula {
        public void display() {
            System.out.println("[Page 5] 4. Basic Formula");
            System.out.println("- Formula: P(A) = n(A) / n(S)");
            System.out.println("- Tip: What I want / Total");
            System.out.println("--------------------------------------------------");
        }
    }

    static class Page6_ComplementaryEvent {
        public void display() {
            System.out.println("[Page 6] 5. Complementary Event");
            System.out.println("- Formula: P(A^C) = 1 - P(A)");
            System.out.println("- Example: The probability of NOT being a Jian Guo High School student");
            System.out.println("--------------------------------------------------");
        }
    }

    static class Page7_Union {
        public void display() {
            System.out.println("[Page 7] 6. Union (OR)");
            System.out.println("- Formula: P(A U B) = P(A) + P(B) - P(A n B)");
            System.out.println("- If mutually exclusive: P(A U B) = P(A) + P(B)");
            System.out.println("--------------------------------------------------");
        }
    }

    static class Page8_Intersection {
        public void display() {
            System.out.println("[Page 8] 7. Intersection (AND)");
            System.out.println("- Formula: P(A n B) = P(A) * P(B|A)");
            System.out.println("- Can also be written as: P(B) * P(A|B)");
            System.out.println("--------------------------------------------------");
        }
    }

    static class Page9_ConditionalProbability {
        public void display() {
            System.out.println("[Page 9] 8. Conditional Probability");
            System.out.println("- Formula: P(A|B) = P(A n B) / P(B)");
            System.out.println("- Meaning: The probability of A given that B has occurred");
            System.out.println("--------------------------------------------------");
        }
    }

    static class Page10_IndependentEvents {
        public void display() {
            System.out.println("[Page 10] 9. Independent Events");
            System.out.println("- If A and B are independent:");
            System.out.println("  - P(A n B) = P(A) * P(B)");
            System.out.println("  - And P(A|B) = P(A)");
            System.out.println("--------------------------------------------------");
        }
    }

    static class Page11_BayesTheorem {
        public void display() {
            System.out.println("[Page 11] 10. Bayes' Theorem");
            System.out.println("- Formula: P(A|B) = P(B|A) * P(A) / P(B)");
            System.out.println("- Use case: Inferring the cause given a known outcome");
            System.out.println("--------------------------------------------------");
        }
    }

    static class Page12_LawOfTotalProbability {
        public void display() {
            System.out.println("[Page 12] 11. Law of Total Probability");
            System.out.println("- Formula: P(A) = Summation of P(A|Bi) * P(Bi)");
            System.out.println("--------------------------------------------------");
        }
    }

    static class Page13_SchoolExample {
        public void display() {
            System.out.println("[Page 13] 12. School Example");
            System.out.println("- NCTU students: J from Jian Guo High, B from Taipei First Girls High, Total = N");
            System.out.println("- P(Jian Guo) = J / N");
            System.out.println("- P(Taipei First Girls) = B / N");
            System.out.println("--------------------------------------------------");
        }
    }

    static class Page14_UnionExample {
        public void display() {
            System.out.println("[Page 14] 13. Jian Guo OR Taipei First Girls");
            System.out.println("- Formula: P(Jian Guo U Taipei First Girls) = (J + B) / N");
            System.out.println("- Reason: The two events are mutually exclusive.");
            System.out.println("--------------------------------------------------");
        }
    }
}