package cz.ondrejmarz.taborakserver.service;

import cz.ondrejmarz.taborakserver.model.Group;
import cz.ondrejmarz.taborakserver.model.Participant;
import cz.ondrejmarz.taborakserver.repository.GroupRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupService groupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllGroups() {
        when(groupRepository.findAll()).thenReturn(Flux.just(new Group("ID1", "1", null), new Group("ID2", "2", null)));

        List<Group> result = groupService.getAllGroups();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(groupRepository, times(1)).findAll();
    }

    @Test
    void getGroupById() {
        String groupId = "ID1";
        Group group = new Group(groupId, "Group1", null);

        when(groupRepository.findById(groupId)).thenReturn(Mono.just(group));

        Group result = groupService.getGroupById(groupId);

        assertNotNull(result);
        assertEquals(groupId, result.getGroupId());
        assertEquals("Group1", result.getNumber());
        verify(groupRepository, times(1)).findById(anyString());
    }

    @Test
    void getAllByIds() {
        List<String> groupIds = Arrays.asList("ID1", "ID2");
        Group group1 = new Group("ID1", "Group1", null);
        Group group2 = new Group("ID2", "Group2", null);

        when(groupRepository.findAllById(groupIds)).thenReturn(Flux.just(group1, group2));

        List<Group> result = groupService.getAllByIds(groupIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(groupRepository, times(1)).findAllById(anyList());
    }

    @Test
    void saveGroup() {
        Group group = new Group("ID1", "Group1", null);

        when(groupRepository.save(group)).thenReturn(Mono.just(group));

        Group result = groupService.saveGroup(group);

        assertNotNull(result);
        assertEquals(group, result);
        verify(groupRepository, times(1)).save(any());
    }

    @Test
    void deleteGroup() {
        Group group = new Group("ID1", "Group1", null);

        groupService.deleteGroup(group);

        verify(groupRepository, times(1)).delete(group);
    }

    @Test
    void existsGroupById() {
        String groupId = "ID1";

        when(groupRepository.existsById(groupId)).thenReturn(Mono.just(true));

        boolean result = groupService.existsGroupById(groupId);

        assertTrue(result);
        verify(groupRepository, times(1)).existsById(groupId);
    }

    @Test
    void createGroupWithXlsx() {
        byte[] validXlsxContent = prepareValidXlsxContent();

        List<Participant> expectedParticipants = prepareExpectedParticipants();

        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> {
            Group savedGroup = invocation.getArgument(0);
            return Mono.just(savedGroup);
        });

        Group createdGroup = groupService.createGroupWithXlsx(validXlsxContent);

        verify(groupRepository, times(1)).save(
                argThat(savedGroup -> Objects.equals(savedGroup.getNumber(), "0"))
        );

        assertEquals(expectedParticipants, createdGroup.getParticipants());
    }

    private byte[] prepareValidXlsxContent() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Participants");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Surname");
            headerRow.createCell(2).setCellValue("Age");
            headerRow.createCell(3).setCellValue("Email");
            headerRow.createCell(4).setCellValue("Phone");

            Row dataRow1 = sheet.createRow(1);
            dataRow1.createCell(0).setCellValue("Name1");
            dataRow1.createCell(1).setCellValue("Surname1");
            dataRow1.createCell(2).setCellValue("1");
            dataRow1.createCell(3).setCellValue("Phone1");
            dataRow1.createCell(4).setCellValue("Email1");

            Row dataRow2 = sheet.createRow(2);
            dataRow2.createCell(0).setCellValue("Name2");
            dataRow2.createCell(1).setCellValue("Surname2");
            dataRow2.createCell(2).setCellValue("2");
            dataRow2.createCell(3).setCellValue("Phone2");
            dataRow2.createCell(4).setCellValue("Email2");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Participant> prepareExpectedParticipants() {
        List<Participant> participants = new ArrayList<>();

        participants.add(new Participant("Surname1 Name1", "1", "Phone1", "Email1"));
        participants.add(new Participant("Surname2 Name2", "2", "Phone2", "Email2"));

        return participants;
    }
}