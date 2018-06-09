package kc.ebenezer.dto.mapper;

import kc.ebenezer.dto.AuditRecordDto;
import kc.ebenezer.model.AuditLevel;
import kc.ebenezer.model.AuditRecord;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AuditRecordMapper extends BaseMapper<AuditRecord, AuditRecordDto> implements Mapper<AuditRecord, AuditRecordDto> {
    @Inject
    private UserMapper userMapper;
    @Inject
    private ProjectMapper projectMapper;

    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");

    @Override
    protected String[] getIgnoreProperties() {
        return new String[]{"changeTime", "user", "project", "created", "updated"};
    }

    @Override
    public AuditRecord toModel(AuditRecordDto dto) {
        if (dto == null) {
            return null;
        }

        AuditRecord model = super.toModel(dto);
        try {
            model.setChangeTime(dateFormat.parse(dto.getChangeTime()));
        } catch (ParseException e) {
            model.setChangeTime(null);
        }
        model.setUser(userMapper.toModel(dto.getUser()));
        model.setProject(projectMapper.toModel(dto.getProject()));
        model.setCreated(new Date(dto.getCreated()));
        model.setUpdated(new Date(dto.getUpdated()));

        return model;
    }

    @Override
    public AuditRecordDto toDto(AuditRecord model) {
        AuditRecordDto dto = super.toDto(model);
        dto.setChangeTime(dateFormat.format(model.getChangeTime()));
        dto.setUser(userMapper.toDto(model.getUser()));
        dto.setProject(projectMapper.toDtoNoUsers(model.getProject()));
        dto.setCreated(model.getCreated().getTime());
        dto.setUpdated(model.getUpdated().getTime());
        return dto;
    }

    @Override
    protected AuditRecord constructModel() {
        return new AuditRecord();
    }

    @Override
    protected AuditRecordDto constructDto() {
        return new AuditRecordDto();
    }
}
