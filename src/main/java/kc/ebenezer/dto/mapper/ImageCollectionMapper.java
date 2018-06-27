package kc.ebenezer.dto.mapper;

import kc.ebenezer.dto.ImageCollectionDto;
import kc.ebenezer.model.Image;
import kc.ebenezer.model.ImageCollection;
import kc.ebenezer.model.ImageSize;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class ImageCollectionMapper extends BaseMapper<ImageCollection, ImageCollectionDto> implements Mapper<ImageCollection, ImageCollectionDto> {
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");

    @Override
    public ImageCollectionDto toDto(ImageCollection model) {
        if (model == null) {
            return null;
        }
        ImageCollectionDto dto = super.toDto(model);
        Map<String, String> images = new HashMap<>();
        model.getImages().forEach(image -> images.put(image.getSize().getCode(), image.getMediaDescriptor()));
        String original = images.get(ImageSize.DEFAULT.getCode());
        if (original != null) {
            for (ImageSize imageSize : ImageSize.values()) {
                images.putIfAbsent(imageSize.getCode(), original);
            }
        }
        if (!images.isEmpty()) {
            dto.setImages(images);
        }
        dto.setCreated(dateFormat.format(model.getCreated()));
        dto.setUpdated(dateFormat.format(model.getUpdated()));
        return dto;
    }

    @Override
    public ImageCollection toModel(ImageCollectionDto dto) {
        if (dto == null) {
            return null;
        }
        ImageCollection model = super.toModel(dto);
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            Set<Image> images = new HashSet<>();
            dto.getImages().forEach((key, value) -> images.add(new Image(model, ImageSize.getForCode(key), value)));
            model.setImages(images);
        }
        return model;
    }

    @Override
    protected ImageCollection constructModel() {
        return new ImageCollection();
    }

    @Override
    protected ImageCollectionDto constructDto() {
        return new ImageCollectionDto();
    }

    @Override
    protected String[] getIgnoreProperties() {
        return new String[]{"images", "created", "updated"};
    }
}
