package cz.ondrejmarz.taborakserver.service;

import cz.ondrejmarz.taborakserver.model.DayPlan;
import cz.ondrejmarz.taborakserver.repository.DayPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class DayPlanService {

    private final DayPlanRepository dayPlanRepository;

    @Autowired
    public DayPlanService(DayPlanRepository dayPlanRepository) {
        this.dayPlanRepository = dayPlanRepository;
    }

    public List<DayPlan> getAllDayPlans() {
        return dayPlanRepository.findAll().collectList().block();
    }

    public DayPlan getDayPlanById(String id) {
        return dayPlanRepository.findById(id).block();
    }

    public DayPlan saveDayPlan(DayPlan dayPlan) {

        return dayPlanRepository.save(dayPlan).block();
    }

    public void deleteDayPlan(DayPlan tour) {
        dayPlanRepository.delete(tour).block();
    }
    public Boolean existsDayPlanById(String id) {
        return dayPlanRepository.existsById(id).block();
    }

    public DayPlan fillActivities(DayPlan dayPlan, DayPlan exemplary) {

        exemplary.setDatesToDay(dayPlan.getWakeUp().getStartTime());

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(dayPlan.getWakeUp().getStartTime());
        dayPlan.getWakeUp().setStartTime( calendar.getTime() );
        calendar.add(Calendar.MINUTE, 15);
        dayPlan.getWarmUp().setStartTime( calendar.getTime() );

        dayPlan.setWakeUp(    dayPlan.getWakeUp().initializeIfNeeded("Budíček", "Milník", exemplary.getWakeUp().getStartTime()) );
        dayPlan.setWarmUp(    dayPlan.getWarmUp().initializeIfNeeded("Rozcvička", "Milník", dayPlan.getWarmUp().getStartTime()) );

        dayPlan.setDishBreakfast(       dayPlan.getDishBreakfast().initializeIfNeeded("Snídaně", "Jídlo", exemplary.getDishBreakfast().getStartTime()) );
        dayPlan.setDishMorningSnack(    dayPlan.getDishMorningSnack().initializeIfNeeded("Svačina", "Jídlo", exemplary.getDishMorningSnack().getStartTime()) );
        dayPlan.setDishLunch(           dayPlan.getDishLunch().initializeIfNeeded("Oběd", "Jídlo", exemplary.getDishLunch().getStartTime()) );
        dayPlan.setDishAfternoonSnack(  dayPlan.getDishAfternoonSnack().initializeIfNeeded("Svačina", "Jídlo", exemplary.getDishAfternoonSnack().getStartTime()) );
        dayPlan.setDishDinner(          dayPlan.getDishDinner().initializeIfNeeded("Večeře", "Jídlo", exemplary.getDishDinner().getStartTime()) );
        dayPlan.setDishEveningSnack(    dayPlan.getDishEveningSnack().initializeIfNeeded("Svačina", "Jídlo", exemplary.getDishEveningSnack().getStartTime()) );

        dayPlan.setProgramMorning(      dayPlan.getProgramMorning().initializeIfNeeded("", "Dopolední činnost", exemplary.getProgramMorning().getStartTime()) );
        dayPlan.setProgramAfternoon(    dayPlan.getProgramAfternoon().initializeIfNeeded("", "Odpolední činnost", exemplary.getProgramAfternoon().getStartTime()) );
        dayPlan.setProgramEvening(      dayPlan.getProgramEvening().initializeIfNeeded("", "Podvečení činnost", exemplary.getProgramEvening().getStartTime()) );
        dayPlan.setProgramNight(        dayPlan.getProgramNight().initializeIfNeeded("", "Večerní činnost", exemplary.getProgramNight().getStartTime()) );

        dayPlan.setSummon(              dayPlan.getSummon().initializeIfNeeded("Nástup", "Milník", exemplary.getSummon().getStartTime()) );
        dayPlan.setPrepForNight(        dayPlan.getPrepForNight().initializeIfNeeded("Příprava na večerku", "Milník", exemplary.getPrepForNight().getStartTime()) );
        dayPlan.setLightsOut(           dayPlan.getLightsOut().initializeIfNeeded("Večerka", "Milník", exemplary.getLightsOut().getStartTime()) );

        calendar.setTime( dayPlan.getLightsOut().getStartTime() );
        dayPlan.getLightsOut().setStartTime( calendar.getTime() );
        calendar.add(Calendar.MINUTE, -15);
        dayPlan.getPrepForNight().setStartTimeDay( calendar.getTime() );

        return dayPlan;
    }

    public DayPlan findExemplaryDayPlan(List<String> exemplaryDayPlansIds) {
        List<DayPlan> dayPlans = dayPlanRepository.findAllById(exemplaryDayPlansIds).collectList().block();

        DayPlan exemplaryDayPlan = null;
        if (dayPlans == null || dayPlans.isEmpty()) return DayPlan.getDefaultDayPlan();

        for (DayPlan plan: dayPlans) {
            if (exemplaryDayPlan == null || exemplaryDayPlan.getDay().compareTo(plan.getDay()) < 0)
                exemplaryDayPlan = plan;
        }
        return exemplaryDayPlan;
    }
}
