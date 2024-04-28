package cz.ondrejmarz.taborakserver.model;

import java.util.Objects;

public class Participants {

    private String name;
    private Number age;

    private String parentPhone;
    private String parentEmail;

    Participants() {

    }

    public Participants(String name, Number age, String parentPhone, String parentEmail) {
        this.name = name;
        this.age = age;
        this.parentPhone = parentPhone;
        this.parentEmail = parentEmail;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Number getAge() {
        return age;
    }

    public void setAge(Number age) {
        this.age = age;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participants that = (Participants) o;
        return Objects.equals(name, that.name) && Objects.equals(age, that.age) && Objects.equals(parentPhone, that.parentPhone) && Objects.equals(parentEmail, that.parentEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, parentPhone, parentEmail);
    }

    @Override
    public String toString() {
        return "Participants{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", parentPhone='" + parentPhone + '\'' +
                ", parentEmail='" + parentEmail + '\'' +
                '}';
    }
}
