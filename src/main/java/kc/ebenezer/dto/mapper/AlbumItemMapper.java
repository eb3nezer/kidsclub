package kc.ebenezer.dto.mapper;

import kc.ebenezer.dto.AlbumDto;
import kc.ebenezer.dto.AlbumItemDto;
import kc.ebenezer.dto.StudentDto;
import kc.ebenezer.model.Album;
import kc.ebenezer.model.AlbumItem;
import kc.ebenezer.model.Student;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;

@Component
public class AlbumItemMapper extends BaseMapper<AlbumItem, AlbumItemDto> implements Mapper<AlbumItem, AlbumItemDto> {
    @Inject
    private ImageCollectionMapper imageCollectionMapper;

    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    public AlbumItemDto toDto(AlbumItem model) {
        if (model == null) {
            return null;
        }
        AlbumItemDto dto = super.toDto(model);
        dto.setUpdated(dateFormat.format(model.getUpdated()));
        dto.setCreated(dateFormat.format(model.getCreated()));
        dto.setImageCollection(imageCollectionMapper.toDto(model.getImageCollection()));

        return dto;
    }

    @Override
    protected AlbumItem constructModel() {
        return new AlbumItem();
    }

    @Override
    protected AlbumItemDto constructDto() {
        return new AlbumItemDto();
    }

    @Override
    public String[] getIgnoreProperties() {
        return new String[]{"created", "updated", "imageCollection"};
    }
}
