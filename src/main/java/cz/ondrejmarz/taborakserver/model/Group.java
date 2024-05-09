package cz.ondrejmarz.taborakserver.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;

import java.util.List;
import java.util.Objects;

@Document(collectionName = "group")
public class Group {

    @DocumentId
    private String groupId;

    private String number;
    private List<Participant> participants;

    public Group() {

    }

    public Group(String groupId, String number, List<Participant> participants) {
        this.groupId = groupId;
        this.number = number;
        this.participants = participants;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupIp(String groupId) {
        this.groupId = groupId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(groupId, group.groupId) && Objects.equals(number, group.number) && Objects.equals(participants, group.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, number, participants);
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupIp='" + groupId + '\'' +
                ", number=" + number +
                ", participants=" + participants +
                '}';
    }
}
