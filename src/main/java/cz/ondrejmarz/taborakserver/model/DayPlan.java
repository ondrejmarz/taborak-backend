package cz.ondrejmarz.taborakserver.model;

import com.google.cloud.spring.data.firestore.Document;

import java.util.Date;

public class DailyProgram {

    Date day;

    Activity dishBreakfast;
    Activity morningSnack;
    Activity dishLunch;
    Activity afternoonSnack;
    Activity dishDinner;
    Activity eveningSnack;

    Activity wakeUp;
    Activity warmUp;
    Activity summon;

    Activity programMorning;
    Activity programAfternoon;
    Activity programEvening;
    Activity programNight;

    public DailyProgram() {

    }

    public DailyProgram(Date day, Activity dishBreakfast, Activity morningSnack, Activity dishLunch, Activity afternoonSnack, Activity dishDinner, Activity eveningSnack, Activity wakeUp, Activity warmUp, Activity summon, Activity programMorning, Activity programAfternoon, Activity programEvening, Activity programNight) {
        this.day = day;
        this.dishBreakfast = dishBreakfast;
        this.morningSnack = morningSnack;
        this.dishLunch = dishLunch;
        this.afternoonSnack = afternoonSnack;
        this.dishDinner = dishDinner;
        this.eveningSnack = eveningSnack;
        this.wakeUp = wakeUp;
        this.warmUp = warmUp;
        this.summon = summon;
        this.programMorning = programMorning;
        this.programAfternoon = programAfternoon;
        this.programEvening = programEvening;
        this.programNight = programNight;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public Activity getDishBreakfast() {
        return dishBreakfast;
    }

    public void setDishBreakfast(Activity dishBreakfast) {
        this.dishBreakfast = dishBreakfast;
    }

    public Activity getMorningSnack() {
        return morningSnack;
    }

    public void setMorningSnack(Activity morningSnack) {
        this.morningSnack = morningSnack;
    }

    public Activity getDishLunch() {
        return dishLunch;
    }

    public void setDishLunch(Activity dishLunch) {
        this.dishLunch = dishLunch;
    }

    public Activity getAfternoonSnack() {
        return afternoonSnack;
    }

    public void setAfternoonSnack(Activity afternoonSnack) {
        this.afternoonSnack = afternoonSnack;
    }

    public Activity getDishDinner() {
        return dishDinner;
    }

    public void setDishDinner(Activity dishDinner) {
        this.dishDinner = dishDinner;
    }

    public Activity getEveningSnack() {
        return eveningSnack;
    }

    public void setEveningSnack(Activity eveningSnack) {
        this.eveningSnack = eveningSnack;
    }

    public Activity getWakeUp() {
        return wakeUp;
    }

    public void setWakeUp(Activity wakeUp) {
        this.wakeUp = wakeUp;
    }

    public Activity getWarmUp() {
        return warmUp;
    }

    public void setWarmUp(Activity warmUp) {
        this.warmUp = warmUp;
    }

    public Activity getSummon() {
        return summon;
    }

    public void setSummon(Activity summon) {
        this.summon = summon;
    }

    public Activity getProgramMorning() {
        return programMorning;
    }

    public void setProgramMorning(Activity programMorning) {
        this.programMorning = programMorning;
    }

    public Activity getProgramAfternoon() {
        return programAfternoon;
    }

    public void setProgramAfternoon(Activity programAfternoon) {
        this.programAfternoon = programAfternoon;
    }

    public Activity getProgramEvening() {
        return programEvening;
    }

    public void setProgramEvening(Activity programEvening) {
        this.programEvening = programEvening;
    }

    public Activity getProgramNight() {
        return programNight;
    }

    public void setProgramNight(Activity programNight) {
        this.programNight = programNight;
    }

    @Override
    public String toString() {
        return "DailyProgram{" +
                "day=" + day +
                ", dishBreakfast=" + dishBreakfast +
                ", morningSnack=" + morningSnack +
                ", dishLunch=" + dishLunch +
                ", afternoonSnack=" + afternoonSnack +
                ", dishDinner=" + dishDinner +
                ", eveningSnack=" + eveningSnack +
                ", wakeUp=" + wakeUp +
                ", warmUp=" + warmUp +
                ", summon=" + summon +
                ", programMorning=" + programMorning +
                ", programAfternoon=" + programAfternoon +
                ", programEvening=" + programEvening +
                ", programNight=" + programNight +
                '}';
    }
}
