package ebenezer.dto.mapper;

import ebenezer.dto.DtoObject;
import ebenezer.model.ModelObject;

public interface Mapper<J extends ModelObject, K extends DtoObject> {
    J toModel(K dto);

    K toDto(J model);
}
