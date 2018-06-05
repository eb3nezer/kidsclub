package kc.ebenezer.dto.mapper;

import kc.ebenezer.dto.DtoObject;
import kc.ebenezer.model.ModelObject;

public interface Mapper<J extends ModelObject, K extends DtoObject> {
    J toModel(K dto);

    K toDto(J model);
}
