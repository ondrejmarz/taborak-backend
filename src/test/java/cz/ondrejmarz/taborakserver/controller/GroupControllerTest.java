package cz.ondrejmarz.taborakserver.controller;

import cz.ondrejmarz.taborakserver.controller.token.AuthTokenFirebaseValidator;
import cz.ondrejmarz.taborakserver.model.Group;
import cz.ondrejmarz.taborakserver.model.Participant;
import cz.ondrejmarz.taborakserver.model.Tour;
import cz.ondrejmarz.taborakserver.repository.DayPlanRepository;
import cz.ondrejmarz.taborakserver.repository.GroupRepository;
import cz.ondrejmarz.taborakserver.repository.TourRepository;
import cz.ondrejmarz.taborakserver.repository.UserRepository;
import cz.ondrejmarz.taborakserver.service.GroupService;
import cz.ondrejmarz.taborakserver.service.TourService;
import cz.ondrejmarz.taborakserver.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(GroupController.class)
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthTokenFirebaseValidator authTokenValidator;

    @MockBean
    private DayPlanRepository dayPlanRepository;

    @MockBean
    private GroupRepository groupRepository;

    @MockBean
    private TourRepository tourRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TourService tourService;

    @MockBean
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    @Test
    void getAllTourGroups() throws Exception {
        Participant participant1 = new Participant("Name1", "1", "Phone1", "Email1");
        Participant participant2 = new Participant("Name2", "2", "Phone2", "Email2");
        Participant participant3 = new Participant("Name3", "3", "Phone3", "Email3");

        List<Participant> participants1 = new ArrayList<>();
        List<Participant> participants2 = new ArrayList<>();

        Tour tour = new Tour("ID1", "Title1", "Topic1", "Description1", new Date(), new Date(), null, null, List.of("groupID1", "groupID2"), null);

        participants1.add(participant1); participants1.add(participant2); participants1.add(participant3);
        Group group1 = new Group("groupID1", "Number1", participants1);
        participants2.add(participant1); participants2.add(participant2);
        Group group2 = new Group("groupID2", "Number2", participants2);

        when(authTokenValidator.validateToken(anyString(), eq(tour.getTourId()), anyList())).thenReturn(true);
        when(tourService.existsTourById(tour.getTourId())).thenReturn(true);
        when(tourService.getTourById(tour.getTourId())).thenReturn(tour);
        when(groupService.getAllByIds(tour.getGroups())).thenReturn(List.of(group1, group2));

        mockMvc.perform(get("/tours/{tourId}/groups", tour.getTourId())
                        .header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].groupId").value("groupID1"))
                .andExpect(jsonPath("$[0].number").value("Number1"))
                .andExpect(jsonPath("$[0].participants[0].name").value("Name1"))
                .andExpect(jsonPath("$[0].participants[0].age").value("1"))
                .andExpect(jsonPath("$[0].participants[0].parentEmail").value("Email1"))
                .andExpect(jsonPath("$[0].participants[0].parentPhone").value("Phone1"))
                .andExpect(jsonPath("$[0].participants[1].name").value("Name2"))
                .andExpect(jsonPath("$[0].participants[1].age").value("2"))
                .andExpect(jsonPath("$[0].participants[1].parentEmail").value("Email2"))
                .andExpect(jsonPath("$[0].participants[1].parentPhone").value("Phone2"))
                .andExpect(jsonPath("$[0].participants[2].name").value("Name3"))
                .andExpect(jsonPath("$[0].participants[2].age").value("3"))
                .andExpect(jsonPath("$[0].participants[2].parentEmail").value("Email3"))
                .andExpect(jsonPath("$[0].participants[2].parentPhone").value("Phone3"))
                .andExpect(jsonPath("$[1].groupId").value("groupID2"))
                .andExpect(jsonPath("$[1].number").value("Number2"))
                .andExpect(jsonPath("$[1].participants[0].name").value("Name1"))
                .andExpect(jsonPath("$[1].participants[0].age").value("1"))
                .andExpect(jsonPath("$[1].participants[0].parentEmail").value("Email1"))
                .andExpect(jsonPath("$[1].participants[0].parentPhone").value("Phone1"))
                .andExpect(jsonPath("$[1].participants[1].name").value("Name2"))
                .andExpect(jsonPath("$[1].participants[1].age").value("2"))
                .andExpect(jsonPath("$[1].participants[1].parentEmail").value("Email2"))
                .andExpect(jsonPath("$[1].participants[1].parentPhone").value("Phone2"));
    }

    @Test
    void getGroupById() throws Exception {
        String tourId = "ID1";
        String groupId = "groupID1";
        Participant participant1 = new Participant("Name1", "1", "Phone1", "Email1");
        Participant participant2 = new Participant("Name2", "2", "Phone2", "Email2");
        List<Participant> participants = new ArrayList<>();
        participants.add(participant1); participants.add(participant2);
        Group group = new Group(groupId, "Number1", participants);

        when(authTokenValidator.validateToken(anyString(), eq(tourId), anyList())).thenReturn(true);
        when(groupService.existsGroupById(groupId)).thenReturn(true);
        when(groupService.getGroupById(groupId)).thenReturn(group);

        mockMvc.perform(get("/tours/{tourId}/groups/{groupId}", tourId, groupId)
                        .header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.groupId").value(groupId))
                .andExpect(jsonPath("$.number").value("Number1"))
                .andExpect(jsonPath("$.participants[0].name").value("Name1"))
                .andExpect(jsonPath("$.participants[0].age").value("1"))
                .andExpect(jsonPath("$.participants[0].parentEmail").value("Email1"))
                .andExpect(jsonPath("$.participants[0].parentPhone").value("Phone1"))
                .andExpect(jsonPath("$.participants[1].name").value("Name2"))
                .andExpect(jsonPath("$.participants[1].age").value("2"))
                .andExpect(jsonPath("$.participants[1].parentEmail").value("Email2"))
                .andExpect(jsonPath("$.participants[1].parentPhone").value("Phone2"));
    }

    @Test
    void createGroupWithListXlsx() throws Exception {
        String tourId = "ID1";
        byte[] xlsxContent = "Simple test".getBytes();
        Group createdGroup = new Group("groupID1", "Number1", Collections.emptyList());

        when(authTokenValidator.validateToken(anyString(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.getTourById(tourId)).thenReturn(new Tour(tourId, "Title1", "Topic1", "Description1", new Date(), new Date(), null, null, null, null));
        when(groupService.createGroupWithXlsx(xlsxContent)).thenReturn(createdGroup);

        mockMvc.perform(post("/tours/{tourId}/groups/createAllInXlsx", tourId)
                        .header("Authorization", "token")
                        .content(xlsxContent)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.groupId").value("groupID1"))
                .andExpect(jsonPath("$.number").value("Number1"))
                .andExpect(jsonPath("$.participants").isArray())
                .andExpect(jsonPath("$.participants").isEmpty());

        verify(tourService, times(1)).saveTour(
            argThat( savedTour ->
                savedTour.getGroups().size() == 1 && savedTour.getGroups().contains(createdGroup.getGroupId()
                )
            )
        );
    }

    @Test
    void updateGroup() {
        // method not implemented
    }

    @Test
    void deleteGroup() throws Exception {
        String tourId = "ID1";
        String groupId = "groupID1";
        List<String> groups = new ArrayList<>();
        groups.add(groupId);

        Tour tour = new Tour(tourId, "Title1", "Topic1", "Description1", new Date(), new Date(), null, null, groups, null);

        when(authTokenValidator.validateToken(anyString(), eq(tourId), anyList())).thenReturn(true);
        when(tourService.existsTourById(tourId)).thenReturn(true);
        when(tourService.getTourById(tourId)).thenReturn(tour);

        mockMvc.perform(delete("/tours/{tourId}/groups/{groupId}", tourId, groupId)
                        .header("Authorization", "token"))
                .andExpect(status().isNoContent());

        verify(tourService, times(1)).saveTour(argThat(savedTour -> savedTour.getGroups().isEmpty()));
    }
}