package ebenezer.dto.mapper;

import ebenezer.dto.AttendanceRecordDto;
import ebenezer.model.AttendanceRecord;
import ebenezer.model.AttendanceType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
public class AttendanceRecordMapper extends BaseMapper<AttendanceRecord, AttendanceRecordDto> implements Mapper<AttendanceRecord, AttendanceRecordDto> {
    @Inject
    private UserMapper userMapper;

    @Inject
    private StudentMapper studentMapper;

    private DateFormat dateFormat = new SimpleDateFormat("dd MMM HH:mm");

    @Override
    protected AttendanceRecord constructModel() {
        return new AttendanceRecord();
    }

    @Override
    protected AttendanceRecordDto constructDto() {
        return new AttendanceRecordDto();
    }

    @Override
    protected String[] getIgnoreProperties() {
        return new String[]{"student", "attendanceType", "attendanceCode", "verifier"};
    }

    @Override
    public AttendanceRecordDto toDto(AttendanceRecord model) {
        if (model == null) {
            return null;
        }

        AttendanceRecordDto dto = super.toDto(model);

        dto.setAttendanceType(model.getAttendanceType().getName());
        dto.setAttendanceCode(model.getAttendanceType().getCode());
        dto.setRecordTime(dateFormat.format(model.getRecordTime()));
        dto.setVerifier(userMapper.toDto(model.getVerifier()));
        dto.setStudent(studentMapper.toDtoShallow(model.getStudent()));

        return dto;
    }

    @Override
    public AttendanceRecord toModel(AttendanceRecordDto dto) {
        if (dto == null) {
            return null;
        }

        AttendanceRecord model = super.toModel(dto);

        AttendanceType attendanceType = null;
        if (dto.getAttendanceType() != null) {
            attendanceType = AttendanceType.getForCode(dto.getAttendanceType());
            if (attendanceType == null) {
                attendanceType = AttendanceType.getForName(dto.getAttendanceType());
            }
        }
        model.setAttendanceType(attendanceType);
        if (dto.getRecordTime() != null && !dto.getRecordTime().isEmpty()) {
            try {
                model.setRecordTime(dateFormat.parse(dto.getRecordTime()));
            } catch (ParseException e) {
            }
        }
        model.setStudent(studentMapper.toModel(dto.getStudent()));
        model.setVerifier(userMapper.toModel(dto.getVerifier()));

        return model;
    }
}
