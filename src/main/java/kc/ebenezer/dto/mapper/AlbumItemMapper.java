package kc.ebenezer.dto.mapper;

import kc.ebenezer.dto.AlbumDto;
import kc.ebenezer.dto.AlbumItemDto;
import kc.ebenezer.dto.StudentDto;
import kc.ebenezer.model.Album;
import kc.ebenezer.model.AlbumItem;
import kc.ebenezer.model.Student;
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

    @Override
    protected AlbumItem constructModel() {
        return new AlbumItem();
    }

    @Override
    protected AlbumItemDto constructDto() {
        return new AlbumItemDto();
    }
}
