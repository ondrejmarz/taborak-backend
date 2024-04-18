package cz.ondrejmarz.taborakserver;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import cz.ondrejmarz.taborakserver.model.Activity;
import cz.ondrejmarz.taborakserver.model.DayPlan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;


@SpringBootApplication
public class TaborakServerApplication {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        SpringApplication.run(TaborakServerApplication.class, args);

        try {
            FileInputStream serviceAccount = new FileInputStream("ServiceAccountKey.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);

            //"Skritp" k vytvoření rozvrhu na den v databázi
            /*
            Firestore firestore = FirestoreOptions.getDefaultInstance().getService();
            // Vytvoření instance DayPlan
            DayPlan dayPlan = new DayPlan();
            dayPlan.setDayId("yourDayId");
            dayPlan.setDay(new Date());

            Date startTime = new Date(); // Aktuální datum a čas
            Date endTime = new Date(startTime.getTime() + 3600 * 1000); // Přidání jedné hodiny k aktuálnímu času

            // Vytvoření instance třídy Activity
            Activity activity = new Activity("Jméno aktivity", "Typ aktivity", "Popis aktivity", true, startTime, endTime);

            // Nastavení aktivit
            dayPlan.setDishBreakfast(activity);
            dayPlan.setDishMorningSnack(activity);
            dayPlan.setDishLunch(activity);
            dayPlan.setDishAfternoonSnack(activity);
            dayPlan.setDishDinner(activity);
            dayPlan.setDishEveningSnack(activity);
            dayPlan.setWakeUp(activity);
            dayPlan.setWarmUp(activity);
            dayPlan.setSummon(activity);
            dayPlan.setProgramMorning(activity);
            dayPlan.setProgramAfternoon(activity);
            dayPlan.setProgramEvening(activity);
            dayPlan.setProgramNight(activity);
            dayPlan.setPrepForNight(activity);
            dayPlan.setLightsOut(activity);

            // Reference na kolekci "calendar"
            CollectionReference calendarCollection = firestore.collection("calendar");

            // Reference na nový dokument
            DocumentReference newDocRef = calendarCollection.document();

            // Uložení instance DayPlan do Firestore
            newDocRef.set(dayPlan, SetOptions.merge());

            System.out.println("DayPlan written!");
             */

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
