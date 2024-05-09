package cz.ondrejmarz.taborakserver.service;

import cz.ondrejmarz.taborakserver.model.Participant;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import cz.ondrejmarz.taborakserver.model.Group;
import cz.ondrejmarz.taborakserver.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll().collectList().block();
    }

    public Group getGroupById(String id) {
        return groupRepository.findById(id).block();
    }

    public List<Group> getAllByIds(List<String> groupIds) { return groupRepository.findAllById(groupIds).collectList().block(); }

    public Group saveGroup(Group group) {
        return groupRepository.save(group).block();
    }

    public void deleteGroup(Group group) {
        groupRepository.delete(group);
    }

    public Boolean existsGroupById(String id) {
        return groupRepository.existsById(id).block();
    }

    public Group createGroupWithXlsx(byte[] xlsxContent) {

        List<Participant> participants = new ArrayList<>();

        try {
            InputStream inputStream = new ByteArrayInputStream(xlsxContent);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

            int rowNum = 1; // Skip header row
            while (true) {
                Row row = sheet.getRow(rowNum);
                if (row == null || row.getCell(0) == null || Objects.equals(row.getCell(0).getStringCellValue(), "")) {
                    break; // End of data
                }

                Cell firstCell = row.getCell(0);
                if (isNumeric(firstCell.getStringCellValue())) {
                    rowNum++; // Skip row with a number in the first cell
                    continue;
                }

                Participant participant = new Participant();

                Cell name = row.getCell(1);
                if (!name.getStringCellValue().equals(""))
                    participant.setName(name.getStringCellValue() + " " + firstCell.getStringCellValue());
                else {
                    rowNum++;
                    continue;
                }
                Cell ageCell = row.getCell(2);
                if (isNumeric(ageCell.getStringCellValue())) {
                    participant.setAge(ageCell.getStringCellValue());
                }
                Cell emailCell = row.getCell(3);
                if (!emailCell.getStringCellValue().equals("")) {
                    participant.setParentPhone(emailCell.getStringCellValue());
                }
                Cell phoneCell = row.getCell(4);
                if (!phoneCell.getStringCellValue().equals("")) {
                    participant.setParentEmail(phoneCell.getStringCellValue());
                }

                participants.add(participant);
                rowNum++;
            }

            workbook.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        Group newGroup = new Group();
        newGroup.setNumber("0");
        newGroup.setParticipants(participants);

        return groupRepository.save( newGroup ).block();
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
