package ebenezer.dto.mapper;

import ebenezer.dto.AlbumDto;
import ebenezer.dto.AlbumItemDto;
import ebenezer.dto.StudentDto;
import ebenezer.model.Album;
import ebenezer.model.AlbumItem;
import ebenezer.model.Student;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;

@Component
public class AlbumItemMapper extends BaseMapper<AlbumItem, AlbumItemDto> implements Mapper<AlbumItem, AlbumItemDto> {
    @Override
    public AlbumItem toModel(AlbumItemDto dto) {
        if (dto == null) {
            return null;
        }

        return new AlbumItem(dto.getOrder(), dto.getMediaDescriptor(), dto.getDescription());
    }

    @Override
    public AlbumItemDto toDto(AlbumItem model) {
        if (model == null) {
            return null;
        }

        return new AlbumItemDto(model.getId(), model.getOrder(), model.getDescription(), model.getMediaDescriptor(),
                model.getCreated().getTime(), model.getUpdated().getTime());
    }
}
