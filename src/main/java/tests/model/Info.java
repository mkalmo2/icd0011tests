package tests.model;

public class Info {
    private String firstName;
    private String lastName;
    private String passwordHash;
    private String formOfStudy;
    private boolean iHaveReadTheRulesOfTheCourse;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFormOfStudy() {
        return formOfStudy;
    }

    public void setFormOfStudy(String formOfStudy) {
        this.formOfStudy = formOfStudy;
    }

    public boolean isiHaveReadTheRulesOfTheCourse() {
        return iHaveReadTheRulesOfTheCourse;
    }

    public void setiHaveReadTheRulesOfTheCourse(boolean iHaveReadTheRulesOfTheCourse) {
        this.iHaveReadTheRulesOfTheCourse = iHaveReadTheRulesOfTheCourse;
    }
}
