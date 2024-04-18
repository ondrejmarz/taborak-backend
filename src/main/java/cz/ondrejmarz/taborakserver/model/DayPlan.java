package cz.ondrejmarz.taborakserver.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

import java.util.Calendar;
import java.util.Date;

@Document(collectionName = "calendar")
public class DayPlan {

    @DocumentId
    String dayId;

    String day;

    Activity dishBreakfast;
    Activity dishMorningSnack;
    Activity dishLunch;
    Activity dishAfternoonSnack;
    Activity dishDinner;
    Activity dishEveningSnack;

    Activity wakeUp;
    Activity warmUp;
    Activity summon;
    Activity prepForNight;
    Activity lightsOut;

    Activity programMorning;
    Activity programAfternoon;
    Activity programEvening;
    Activity programNight;

    public DayPlan() {
        dishBreakfast = new Activity();
        dishMorningSnack = new Activity();
        dishLunch = new Activity();
        dishAfternoonSnack = new Activity();
        dishDinner = new Activity();
        dishEveningSnack = new Activity();

        wakeUp = new Activity();
        warmUp = new Activity();
        summon = new Activity();
        prepForNight = new Activity();
        lightsOut = new Activity();

        programMorning = new Activity();
        programAfternoon = new Activity();
        programEvening = new Activity();
        programNight = new Activity();
    }


    public DayPlan(String day, Activity dishBreakfast, Activity morningSnack, Activity dishLunch, Activity afternoonSnack, Activity dishDinner, Activity eveningSnack, Activity wakeUp, Activity warmUp, Activity summon, Activity programMorning, Activity programAfternoon, Activity programEvening, Activity programNight, Activity prepForNight, Activity lightsOut) {
        this.day = day;
        this.dishBreakfast = dishBreakfast;
        this.dishMorningSnack = morningSnack;
        this.dishLunch = dishLunch;
        this.dishAfternoonSnack = afternoonSnack;
        this.dishDinner = dishDinner;
        this.dishEveningSnack = eveningSnack;
        this.wakeUp = wakeUp;
        this.warmUp = warmUp;
        this.summon = summon;
        this.programMorning = programMorning;
        this.programAfternoon = programAfternoon;
        this.programEvening = programEvening;
        this.programNight = programNight;
        this.prepForNight = prepForNight;
        this.lightsOut = lightsOut;
    }

    public String getDayId() {
        return dayId;
    }

    public void setDayId(String dayId) {
        this.dayId = dayId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Activity getDishBreakfast() {
        return dishBreakfast;
    }

    public void setDishBreakfast(Activity dishBreakfast) {
        this.dishBreakfast = dishBreakfast;
    }

    public Activity getDishMorningSnack() {
        return dishMorningSnack;
    }

    public void setDishMorningSnack(Activity dishMorningSnack) {
        this.dishMorningSnack = dishMorningSnack;
    }

    public Activity getDishLunch() {
        return dishLunch;
    }

    public void setDishLunch(Activity dishLunch) {
        this.dishLunch = dishLunch;
    }

    public Activity getDishAfternoonSnack() {
        return dishAfternoonSnack;
    }

    public void setDishAfternoonSnack(Activity dishAfternoonSnack) {
        this.dishAfternoonSnack = dishAfternoonSnack;
    }

    public Activity getDishDinner() {
        return dishDinner;
    }

    public void setDishDinner(Activity dishDinner) {
        this.dishDinner = dishDinner;
    }

    public Activity getDishEveningSnack() {
        return dishEveningSnack;
    }

    public void setDishEveningSnack(Activity dishEveningSnack) {
        this.dishEveningSnack = dishEveningSnack;
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

    public Activity getPrepForNight() {
        return prepForNight;
    }

    public void setPrepForNight(Activity prepForNight) {
        this.prepForNight = prepForNight;
    }

    public Activity getLightsOut() {
        return lightsOut;
    }

    public void setLightsOut(Activity lightsOut) {
        this.lightsOut = lightsOut;
    }

    public boolean isSameDay(String date) {
        return day.equals(date);
    }

    public void setDatesToDay(Date date) {
        getProgramMorning().setStartTimeDay(date);
        getProgramAfternoon().setStartTimeDay(date);
        getProgramEvening().setStartTimeDay(date);
        getProgramNight().setStartTimeDay(date);

        getDishBreakfast().setStartTimeDay(date);
        getDishMorningSnack().setStartTimeDay(date);
        getDishLunch().setStartTimeDay(date);
        getDishAfternoonSnack().setStartTimeDay(date);
        getDishDinner().setStartTimeDay(date);
        getDishEveningSnack().setStartTimeDay(date);

        getWakeUp().setStartTimeDay(date);
        getWarmUp().setStartTimeDay(date);
        getSummon().setStartTimeDay(date);
        getPrepForNight().setStartTimeDay(date);
        getLightsOut().setStartTimeDay(date);
    }

    public static DayPlan getDefaultDayPlan() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(1970, Calendar.JANUARY, 1, 8, 0, 0);
        Activity wakeUp = new Activity("Budíček", "Milník", calendar.getTime() );
        calendar.set(1970, Calendar.JANUARY, 1, 8, 15, 0);
        Activity warmUp = new Activity("Rozcvička", "Milník", calendar.getTime() );

        calendar.set(1970, Calendar.JANUARY, 1, 8, 30, 0);
        Activity dish1 = new Activity("Snídaně", "Jídlo", calendar.getTime() );
        calendar.set(1970, Calendar.JANUARY, 1, 9, 30, 0);
        Activity dish2 = new Activity("Dopolední svačina", "Jídlo", calendar.getTime() );
        calendar.set(1970, Calendar.JANUARY, 1, 12, 30, 0);
        Activity dish3 = new Activity("Oběd",    "Jídlo", calendar.getTime() );
        calendar.set(1970, Calendar.JANUARY, 1, 16, 0, 0);
        Activity dish4 = new Activity("Odpolední svačina", "Jídlo", calendar.getTime() );
        calendar.set(1970, Calendar.JANUARY, 1, 19, 0, 0);
        Activity dish5 = new Activity("Večeře",  "Jídlo", calendar.getTime() );
        calendar.set(1970, Calendar.JANUARY, 1, 21, 0, 0);
        Activity dish6 = new Activity("Druhá večeře", "Jídlo", calendar.getTime() );

        calendar.set(1970, Calendar.JANUARY, 1, 10, 0, 0);
        Activity program1 = new Activity("", "Dopolední činnost", calendar.getTime() );
        calendar.set(1970, Calendar.JANUARY, 1, 14, 30, 0);
        Activity program2 = new Activity("", "Odpolední činnost", calendar.getTime() );
        calendar.set(1970, Calendar.JANUARY, 1, 16, 30, 0);
        Activity program3 = new Activity("","Podvečerní činnost", calendar.getTime() );
        calendar.set(1970, Calendar.JANUARY, 1, 20, 0, 0);
        Activity program4 = new Activity("'", "Večerní činnost", calendar.getTime() );

        calendar.set(1970, Calendar.JANUARY, 1, 18, 30, 0);
        Activity summon = new Activity("Nástup", "Milník", calendar.getTime() );
        calendar.set(1970, Calendar.JANUARY, 1, 22, 15, 0);
        Activity prep   = new Activity("Příprava na večerku", "Milník", calendar.getTime() );
        calendar.set(1970, Calendar.JANUARY, 1, 22, 30, 0);
        Activity lights = new Activity("Večerka", "Milník", calendar.getTime() );

        return new DayPlan(
                "1970-01-01",
                dish1,
                dish2,
                dish3,
                dish4,
                dish5,
                dish6,
                wakeUp,
                warmUp,
                summon,
                program1,
                program2,
                program3,
                program4,
                prep,
                lights
        );
    }

    @Override
    public String toString() {
        return "DailyProgram{" +
                "dayId=" + dayId +
                ", dishBreakfast=" + dishBreakfast +
                ", morningSnack=" + dishMorningSnack +
                ", dishLunch=" + dishLunch +
                ", afternoonSnack=" + dishAfternoonSnack +
                ", dishDinner=" + dishDinner +
                ", eveningSnack=" + dishEveningSnack +
                ", wakeUp=" + wakeUp +
                ", warmUp=" + warmUp +
                ", summon=" + summon +
                ", programMorning=" + programMorning +
                ", programAfternoon=" + programAfternoon +
                ", programEvening=" + programEvening +
                ", programNight=" + programNight +
                ", prepForNight=" + prepForNight +
                ", lightsOut=" + lightsOut +
                '}';
    }
}
