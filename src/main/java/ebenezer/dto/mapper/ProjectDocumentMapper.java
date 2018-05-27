package ebenezer.dto.mapper;

import ebenezer.dto.ProjectDocumentDto;
import ebenezer.model.ProjectDocument;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Component
public class ProjectDocumentMapper extends BaseMapper<ProjectDocument, ProjectDocumentDto> implements Mapper<ProjectDocument, ProjectDocumentDto> {
    @Inject
    private ProjectMapper projectMapper;

    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private String contentTypeToIcon(String contentType) {
        if (contentType.equals("application/pdf")) {
            return "pdf.png";
        } else if (contentType.startsWith("image/")) {
            return "image.png";
        } else if (contentType.contains("msword") || contentType.contains("wordprocessingml")) {
            return "word.png";
        } else if (contentType.contains("excel") || contentType.contains("spreadsheetml")) {
            return "excel.png";
        } else if (contentType.contains("powerpoint") || contentType.contains("presentationml")) {
            return "powerpoint.png";
        } else if (contentType.contains("keynote")) {
            return "keynote.png";
        } else {
            return "unknown.png";
        }
    }

    @Override
    public ProjectDocument toModel(ProjectDocumentDto dto) {
        if (dto == null) {
            return null;
        }

        return new ProjectDocument(
                projectMapper.toModel(dto.getProject()),
                dto.getName(),
                dto.getDescription(),
                dto.getContentType(),
                dto.getMediaDescriptor()
        );
    }

    @Override
    public ProjectDocumentDto toDto(ProjectDocument model) {
        return new ProjectDocumentDto(
                model.getId(),
                model.getName(),
                model.getDescription(),
                model.getMediaDescriptor(),
                model.getContentType(),
                projectMapper.toDto(model.getProject()),
                contentTypeToIcon(model.getContentType()),
                dateFormat.format(model.getCreated()),
                dateFormat.format(model.getUpdated())
        );
    }

    @Override
    protected ProjectDocument constructModel() {
        return new ProjectDocument();
    }

    @Override
    protected ProjectDocumentDto constructDto() {
        return new ProjectDocumentDto();
    }
}
