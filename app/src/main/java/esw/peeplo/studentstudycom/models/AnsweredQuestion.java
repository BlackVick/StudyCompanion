package esw.peeplo.studentstudycom.models;

import java.io.Serializable;
import java.util.List;

public class AnsweredQuestion implements Serializable {

    private String question_id;
    private String answer_id;
    private String next_question;
    private int score;

    public AnsweredQuestion() {
    }

    public AnsweredQuestion(String question_id, String answer_id, String next_question, int score) {
        this.question_id = question_id;
        this.answer_id = answer_id;
        this.next_question = next_question;
        this.score = score;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(String answer_id) {
        this.answer_id = answer_id;
    }

    public String getNext_question() {
        return next_question;
    }

    public void setNext_question(String next_question) {
        this.next_question = next_question;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
