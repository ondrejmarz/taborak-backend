package cz.ondrejmarz.taborakserver.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Document(collectionName = "tours")
public class Tour {

    @DocumentId
    private String tourId;
    private String title;
    private String topic;
    private String description;
    private Date startDate;
    private Date endDate;

    public Tour() {
    }

    public Tour(String tourId, String title, String topic, String description) {
        this.tourId = tourId;
        this.title = title;
        this.topic = topic;
        this.description = description;
    }

    public Tour(String tourId, String title, String topic, String description, Date startDate, Date endDate) {
        this.tourId = tourId;
        this.title = title;
        this.topic = topic;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndTime(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Tour{" +
                "tourId=" + tourId +
                ", title='" + title + '\'' +
                ", topic='" + topic + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endTime=" + endDate +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(tourId, title, topic, description, startDate, endDate);
    }
}
