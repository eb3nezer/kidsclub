package ebenezer.dto.mapper;

import ebenezer.dto.AuditRecordDto;
import ebenezer.model.AuditLevel;
import ebenezer.model.AuditRecord;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AuditRecordMapper extends BaseMapper<AuditRecord, AuditRecordDto> implements Mapper<AuditRecord, AuditRecordDto> {
    @Inject
    private UserMapper userMapper;

    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");

    @Override
    public AuditRecord toModel(AuditRecordDto dto) {
        AuditRecord model = new AuditRecord(
                dto.getChange(),
                new Date(dto.getChangeTime()),
                userMapper.toModel(dto.getUser()),
                AuditLevel.forCode(dto.getAuditLevel()));
        model.setCreated(new Date(dto.getCreated()));
        model.setUpdated(new Date(dto.getUpdated()));

        return model;
    }

    @Override
    public AuditRecordDto toDto(AuditRecord model) {
        AuditRecordDto dto = new AuditRecordDto(model.getId(),
                model.getChange(),
                dateFormat.format(model.getChangeTime()),
                userMapper.toDto(model.getUser()),
                model.getAuditLevel().getCode(),
                model.getCreated().getTime(),
                model.getUpdated().getTime());
        return dto;
    }
}
