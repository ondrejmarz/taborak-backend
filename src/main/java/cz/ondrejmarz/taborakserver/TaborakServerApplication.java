package cz.ondrejmarz.taborakserver;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.spring.data.firestore.repository.config.EnableReactiveFirestoreRepositories;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;


@SpringBootApplication
@EnableReactiveFirestoreRepositories(basePackages = "cz.ondrejmarz.taborakserver.repository")
public class TaborakServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaborakServerApplication.class, args);

        try {
            FileInputStream serviceAccount = new FileInputStream("ServiceAccountKey.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
