package kc.ebenezer.service;

import kc.ebenezer.dao.StudentDao;
import kc.ebenezer.dao.StudentTeamDao;
import kc.ebenezer.dto.StudentTeamDto;
import kc.ebenezer.model.*;
import kc.ebenezer.rest.ValidationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.*;
import java.util.*;

@Component
public class StudentCSVImporterExporter {
    private static final String NAME_COLUMN = "name";
    private static final String GIVEN_NAME_COLUMN = "given name";
    private static final String FAMILY_NAME_COLUMN = "family name";
    private static final String PHONE_NUMBER_COLUMN = "phone";
    private static final String EMAIL_COLUMN = "email";
    private static final String SCHOOL_COLUMN = "school";
    private static final String AGE_COLUMN = "age";
    private static final String SCHOOL_YEAR_COLUMN = "school year";
    private static final String GENDER_COLUMN = "gender";
    private static final String GENDER_COLUMN_ALT = "sex";
    private static final String EMERGENCY_CONTACT = "emergency contact";
    private static final String EMERGENCY_CONTACT_ALT = "contact name";
    private static final String EMERGENCY_CONTACT_ALT2 = "parent";
    private static final String SPECIAL_INSTRUCTIONS = "special instructions";
    private static final String CONTACT_RELATIONSHIP = "contact relationship";
    private static final String CONTACT_RELATIONSHIP_ALT = "relationship";
    private static final String TEAM = "team";

    @Inject
    private StudentDao studentDao;

    @Inject
    private StudentTeamDao studentTeamDao;

    @Inject
    private AuditService auditService;

    @Inject
    private ProjectService projectService;

    private boolean findColumn(CSVRecord record, Map<String, Integer> headerMapping, Set<Integer> unusedColumns, String columnName) {
        int index = 0;
        boolean found = false;
        while(index < record.size() && !found) {
            if (record.get(index).toLowerCase().equals(columnName) && unusedColumns.contains(index)) {
                headerMapping.put(columnName, index);
                unusedColumns.remove(index);
                found = true;
            }
            index++;
        }
        return found;
    }

    private Map<String, Integer> getHeaderMapping(CSVRecord record) {
        Map<String, Integer> result = new HashMap<>();

        Set<Integer> unusedColumns = new HashSet<>();
        for (int i = 0; i < record.size(); i++) {
            unusedColumns.add(i);
        }

        findColumn(record, result, unusedColumns, NAME_COLUMN);
        findColumn(record, result, unusedColumns, GIVEN_NAME_COLUMN);
        findColumn(record, result, unusedColumns, FAMILY_NAME_COLUMN);
        findColumn(record, result, unusedColumns, PHONE_NUMBER_COLUMN);
        findColumn(record, result, unusedColumns, EMAIL_COLUMN);
        findColumn(record, result, unusedColumns, SCHOOL_COLUMN);
        findColumn(record, result, unusedColumns, AGE_COLUMN);
        findColumn(record, result, unusedColumns, SCHOOL_YEAR_COLUMN);
        findColumn(record, result, unusedColumns, SPECIAL_INSTRUCTIONS);
        if (!findColumn(record, result, unusedColumns, GENDER_COLUMN)) {
            if (findColumn(record, result, unusedColumns, GENDER_COLUMN_ALT)) {
                result.put(GENDER_COLUMN, result.get(GENDER_COLUMN_ALT));
            }
        }
        if (!findColumn(record, result, unusedColumns, EMERGENCY_CONTACT)) {
            if (findColumn(record, result, unusedColumns, EMERGENCY_CONTACT_ALT)) {
                result.put(EMERGENCY_CONTACT, result.get(EMERGENCY_CONTACT_ALT));
            } else {
                if (findColumn(record, result, unusedColumns, EMERGENCY_CONTACT_ALT2)) {
                    result.put(EMERGENCY_CONTACT, result.get(EMERGENCY_CONTACT_ALT2));
                }
            }
        }
        if (!findColumn(record, result, unusedColumns, CONTACT_RELATIONSHIP)) {
            if (findColumn(record, result, unusedColumns, CONTACT_RELATIONSHIP_ALT)) {
                result.put(CONTACT_RELATIONSHIP, result.get(CONTACT_RELATIONSHIP_ALT));
            }
        }
        findColumn(record, result, unusedColumns, TEAM);

        return result;
    }

    public List<Student> bulkCreateStudents(Project project, InputStream inputStream) throws IOException {
        final Boolean defaultMediaPermitted =
            projectService.hasPropertyValue(project, ProjectProperty.STUDENT_MEDIA_PERMITTED_DEFAULT)?
                projectService.getPropertyValueAsBoolean(project, ProjectProperty.STUDENT_MEDIA_PERMITTED_DEFAULT):
                false;
        InputStreamReader isr = new InputStreamReader(inputStream);
        CSVParser parser = new CSVParser(isr, CSVFormat.DEFAULT);
        Iterator<CSVRecord> recordIterator = parser.iterator();
        Map<String, Integer> headerMapping;
        if (recordIterator.hasNext()) {
            headerMapping = getHeaderMapping(recordIterator.next());
        } else {
            throw new ValidationException("Unable to parse the header row");
        }

        List<Student> newStudents = new ArrayList<>();
        recordIterator.forEachRemaining(record -> {
            String name = "";
            String givenName = "";
            String familyName = "";
            String contactName = "";
            String contactRelationship = "";
            String phone = "";
            String email = "";
            String school = "";
            String specialInstructions = "";
            Integer age = null;
            String schoolYear = "";
            String teamName = "";
            Optional<StudentTeam> team = Optional.empty();
            Gender gender = null;

            if (headerMapping.containsKey(NAME_COLUMN)) {
                name = record.get(headerMapping.get(NAME_COLUMN));
            }
            if (headerMapping.containsKey(GIVEN_NAME_COLUMN)) {
                givenName = record.get(headerMapping.get(GIVEN_NAME_COLUMN));
            }
            if (headerMapping.containsKey(FAMILY_NAME_COLUMN)) {
                familyName = record.get(headerMapping.get(FAMILY_NAME_COLUMN));
            }
            if (name == null || name.isEmpty()) {
                if ((givenName != null && !givenName.isEmpty()) && (familyName != null && !familyName.isEmpty())) {
                    name = givenName + " " + familyName;
                } else {
                    throw new ValidationException("Unable to determine the name. The file must include either a \"" +
                            NAME_COLUMN + "\" column or else both a \"" + GIVEN_NAME_COLUMN + "\" column and a \"" + FAMILY_NAME_COLUMN +
                            "\" column");
                }
            }

            if (headerMapping.containsKey(EMERGENCY_CONTACT)) {
                contactName = record.get(headerMapping.get(EMERGENCY_CONTACT));
            }
            if (headerMapping.containsKey(CONTACT_RELATIONSHIP)) {
                contactRelationship = record.get(headerMapping.get(CONTACT_RELATIONSHIP));
            }
            if (headerMapping.containsKey(PHONE_NUMBER_COLUMN)) {
                phone = record.get(headerMapping.get(PHONE_NUMBER_COLUMN));
            }
            if (headerMapping.containsKey(EMAIL_COLUMN)) {
                email = record.get(headerMapping.get(EMAIL_COLUMN));
            }
            if (headerMapping.containsKey(SCHOOL_COLUMN)) {
                school = record.get(headerMapping.get(SCHOOL_COLUMN));
            }
            if (headerMapping.containsKey(AGE_COLUMN)) {
                try {
                    age = Integer.parseInt(record.get(headerMapping.get(AGE_COLUMN)));
                } catch (NumberFormatException e) {
                }
            }
            if (headerMapping.containsKey(SCHOOL_YEAR_COLUMN)) {
                schoolYear = record.get(headerMapping.get(SCHOOL_YEAR_COLUMN));
            }
            if (headerMapping.containsKey(SPECIAL_INSTRUCTIONS)) {
                specialInstructions = record.get(headerMapping.get(SPECIAL_INSTRUCTIONS));
            }
            if (headerMapping.containsKey(GENDER_COLUMN)) {
                String genderText = record.get(headerMapping.get(GENDER_COLUMN));
                if (Gender.fromCode(genderText) != null) {
                    gender = Gender.fromCode(genderText);
                } else {
                    try {
                        if (Gender.valueOf(genderText.toUpperCase()) != null) {
                            gender = Gender.valueOf(genderText.toUpperCase());
                        }
                    } catch (Exception e) {
                    }
                }
            }
            if (headerMapping.containsKey(TEAM)) {
                teamName = record.get(headerMapping.get(TEAM));
                if (teamName != null && !teamName.isEmpty()) {
                    team = studentTeamDao.findByNameForProject(project.getId(), teamName);
                }
            }

            Optional<Student> existing = studentDao.findForProjectAndExactName(project.getId(), name);
            if (existing.isPresent()) {
                existing.get().setName(name);
                existing.get().setGivenName(givenName);
                existing.get().setFamilyName(familyName);
                existing.get().setContactName(contactName);
                existing.get().setContactRelationship(contactRelationship);
                existing.get().setEmail(email);
                existing.get().setPhone(phone);
                existing.get().setSchool(school);
                existing.get().setAge(age);
                existing.get().setSchoolYear(schoolYear);
                existing.get().setGender(gender);
                existing.get().setSpecialInstructions(specialInstructions);
                if (team.isPresent()) {
                    existing.get().setStudentTeam(team.orElseGet(null));
                }
                auditService.audit(project, "Updating existing student " + existing.get() + " from CSV", new Date());
                newStudents.add(existing.get());
            } else {
                Student student = new Student(
                        null,
                        name,
                        givenName,
                        familyName,
                        null,
                        contactName,
                        contactRelationship,
                        email,
                        phone,
                        school,
                        age,
                        schoolYear,
                        gender,
                        specialInstructions,
                        defaultMediaPermitted,
                        project,
                        team.orElse(null),
                    null
                );
                student = studentDao.create(student);
                auditService.audit(project, "Bulk added new student " + student + " from CSV", new Date());
                newStudents.add(student);
            }
        });

        return newStudents;
    }

    public String bulkExportStudents(List<Student> students) throws IOException {
        StringWriter stringWriter = new StringWriter();
        CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT);
        csvPrinter.printRecord(GIVEN_NAME_COLUMN,
                FAMILY_NAME_COLUMN,
                NAME_COLUMN,
                GENDER_COLUMN,
                AGE_COLUMN,
                EMERGENCY_CONTACT,
                CONTACT_RELATIONSHIP,
                PHONE_NUMBER_COLUMN,
                EMAIL_COLUMN,
                SCHOOL_COLUMN,
                SCHOOL_YEAR_COLUMN,
                SPECIAL_INSTRUCTIONS,
                TEAM);
        for (Student student : students) {
            String age = null;
            if (student.getAge() != null) {
                age = student.getAge().toString();
            }
            String gender = null;
            if (student.getGender() != null) {
                gender = student.getGender().getDescription();
            }
            String teamName = null;
            if (student.getStudentTeam() != null) {
                teamName = student.getStudentTeam().getName();
            }
            csvPrinter.printRecord(student.getGivenName(),
                    student.getFamilyName(),
                    student.getName(),
                    gender,
                    age,
                    student.getContactName(),
                    student.getContactRelationship(),
                    student.getPhone(),
                    student.getEmail(),
                    student.getSchool(),
                    student.getSchoolYear(),
                    student.getSpecialInstructions(),
                    teamName);
        }

        csvPrinter.close();
        return stringWriter.toString();
    }
}
