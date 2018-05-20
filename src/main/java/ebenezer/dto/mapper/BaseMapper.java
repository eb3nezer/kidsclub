package ebenezer.dto.mapper;

import ebenezer.dto.DtoObject;
import ebenezer.model.ModelObject;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMapper<J extends ModelObject, K extends DtoObject> implements Mapper<J, K> {
    public List<J> toModel(List<K> dtoList) {
        if (dtoList == null) {
            return new ArrayList<>();
        }

        List<J> result = new ArrayList<>(dtoList.size());
        for (K dto : dtoList) {
            result.add(toModel(dto));
        }
        return result;
    }

    public List<K> toDto(List<J> modelList) {
        if (modelList == null) {
            return new ArrayList<>();
        }

        List<K> result = new ArrayList<>(modelList.size());
        for (J model : modelList) {
            result.add(toDto(model));
        }
        return result;
    }
}
