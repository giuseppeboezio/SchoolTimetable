package mas.project.boezio.ay2122.ontology;


import jade.content.Concept;

import java.util.Objects;

public class Lesson implements Concept {

    private int hour;
    private int day;

    public Lesson(){
        this.hour = 0;
        this.day = 0;
    }

    public Lesson(int hour, int day) {
        this.hour = hour;
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "hour=" + hour +
                ", day=" + day +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return hour == lesson.hour && day == lesson.day;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, day);
    }
}
