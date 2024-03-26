package cz.ondrejmarz.taborakserver;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;


@SpringBootApplication
public class TaborakServerApplication {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        SpringApplication.run(TaborakServerApplication.class, args);


        /*
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = db.collection("tours").get();
        QuerySnapshot querySnapshot = query.get();
        for (QueryDocumentSnapshot document : querySnapshot) {
            // Získání dat z dokumentu
            String tourId = document.getId();
            String title = document.getString("title");
            String topic = document.getString("topic");
            String description = document.getString("description");
            // A tak dále pro další atributy dokumentu

            // Zde můžete provádět další operace s daty, například je ukládat do kolekce nebo zpracovávat
            System.out.println("Tour ID: " + tourId);
            System.out.println("Title: " + title);
            System.out.println("Topic: " + topic);
            System.out.println("Description: " + description);
            // A tak dále
        }*/
    }
}
