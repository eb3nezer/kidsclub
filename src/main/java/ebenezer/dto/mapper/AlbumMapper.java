package ebenezer.dto.mapper;

import ebenezer.dto.AlbumDto;
import ebenezer.dto.AlbumItemDto;
import ebenezer.model.Album;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component
public class AlbumMapper extends BaseMapper<Album, AlbumDto> implements Mapper<Album, AlbumDto> {
    @Inject
    private ProjectMapper projectMapper;

    @Inject
    private AlbumItemMapper albumItemMapper;

    @Override
    public Album toModel(AlbumDto dto) {
        if (dto == null) {
            return null;
        }

        Album album = new Album(
                dto.getName(),
                dto.getDescription(),
                projectMapper.toModel(dto.getProject()),
                dto.getShared());
        album.getItems().addAll(albumItemMapper.toModel(new ArrayList<>(dto.getItems())));
        return album;
    }

    @Override
    public AlbumDto toDto(Album model) {
        if (model == null) {
            return null;
        }

        List<AlbumItemDto> items = albumItemMapper.toDto(new ArrayList<>(model.getItems()));
        return new AlbumDto(
                model.getId(),
                model.getName(),
                model.getDescription(),
                projectMapper.toDto(model.getProject()),
                items,
                model.isShared(),
                model.getCreated().getTime(),
                model.getUpdated().getTime()
        );
    }

    @Override
    protected Album constructModel() {
        return new Album();
    }

    @Override
    protected AlbumDto constructDto() {
        return new AlbumDto();
    }
}
