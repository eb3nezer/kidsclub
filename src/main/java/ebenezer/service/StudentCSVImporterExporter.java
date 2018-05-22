package ebenezer.service;

import ebenezer.dao.StudentDao;
import ebenezer.model.Gender;
import ebenezer.model.Project;
import ebenezer.model.Student;
import ebenezer.model.User;
import ebenezer.rest.ValidationException;
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

    @Inject
    private StudentDao studentDao;

    @Inject
    private AuditService auditService;

    private void findColumn(CSVRecord record, Map<String, Integer> headerMapping, Set<Integer> unusedColumns, String columnName) {
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
        findColumn(record, result, unusedColumns, GENDER_COLUMN);

        return result;
    }

    public List<Student> bulkCreateStudents(Project project, InputStream inputStream) throws IOException {
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
            String name = null;
            String givenName = null;
            String familyName = null;
            String phone = null;
            String email = null;
            String school = null;
            Integer age = null;
            String schoolYear = null;
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
            if (name == null) {
                if (givenName != null && familyName != null) {
                    name = givenName + " " + familyName;
                } else {
                    throw new ValidationException("Unable to determine the name. The file must include either a \"" +
                            NAME_COLUMN + "\" column or else both a \"" + GIVEN_NAME_COLUMN + "\" column and a \"" + FAMILY_NAME_COLUMN +
                            "\" column");
                }
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

            Student student = new Student(
                    null,
                    name,
                    givenName,
                    familyName,
                    null,
                    null,
                    email,
                    phone,
                    school,
                    age,
                    schoolYear,
                    gender,
                    project,
                    null
            );
            Optional<Student> existing = studentDao.findForProjectAndExactName(project.getId(), name);
            if (existing.isPresent()) {
                existing.get().setName(name);
                existing.get().setGivenName(givenName);
                existing.get().setFamilyName(familyName);
                existing.get().setEmail(email);
                existing.get().setPhone(phone);
                existing.get().setSchool(school);
                existing.get().setAge(age);
                existing.get().setSchoolYear(schoolYear);
                existing.get().setGender(gender);
                auditService.audit("Updating existing student " + existing.get() + " from CSV", new Date());
                newStudents.add(existing.get());
            } else {
                student = studentDao.create(student);
                auditService.audit("Bulk added new student " + student + " from CSV", new Date());
                newStudents.add(student);
            }
        });

        return newStudents;
    }

    public String bulkExportStudents(List<Student> students) throws IOException {
        StringWriter stringWriter = new StringWriter();
        CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT);
        String[] header = {
                GIVEN_NAME_COLUMN,
                FAMILY_NAME_COLUMN,
                NAME_COLUMN,
                GENDER_COLUMN,
                AGE_COLUMN,
                PHONE_NUMBER_COLUMN,
                EMAIL_COLUMN,
                SCHOOL_COLUMN,
                SCHOOL_YEAR_COLUMN
        };

        csvPrinter.printRecord(header);
        for (Student student : students) {
            String age = null;
            if (student.getAge() != null) {
                age = student.getAge().toString();
            }
            String gender = null;
            if (student.getGender() != null) {
                gender = student.getGender().getDescription();
            }
            String[] record = {
                    student.getGivenName(),
                    student.getFamilyName(),
                    student.getName(),
                    gender,
                    age,
                    student.getPhone(),
                    student.getEmail(),
                    student.getSchool(),
                    student.getSchoolYear()
            };
            csvPrinter.printRecord(record);
        }

        csvPrinter.close();
        return stringWriter.toString();
    }
}
