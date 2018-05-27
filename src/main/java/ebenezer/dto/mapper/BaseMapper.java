package ebenezer.dto.mapper;

import ebenezer.dto.DtoObject;
import ebenezer.model.ModelObject;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseMapper<J extends ModelObject, K extends DtoObject> implements Mapper<J, K> {
    public J toModel(K dto) {
        J model = constructModel();
        BeanUtils.copyProperties(dto, model, getIgnoreProperties());
        return model;
    }

    public K toDto(J model) {
        K dto = constructDto();
        BeanUtils.copyProperties(model, dto, getIgnoreProperties());
        return dto;
    }

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

    protected String[] getIgnoreProperties() {
        return new String[0];
    }

    protected abstract J constructModel();
    protected abstract K constructDto();
}
