package esw.peeplo.studentstudycom.models;

import java.io.Serializable;

public class Answer implements Serializable {

    private int id;
    private int question_id;
    private int score;
    private String answer;
    private int next;

    public Answer(int id, int question_id, int score, String answer, int next) {
        this.id = id;
        this.question_id = question_id;
        this.score = score;
        this.answer = answer;
        this.next = next;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

}
