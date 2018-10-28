package kc.ebenezer.dto.mapper;

import kc.ebenezer.dto.UserDto;
import kc.ebenezer.model.User;
import kc.ebenezer.model.UserSitePermission;
import kc.ebenezer.permissions.SitePermission;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.stream.Collectors;

@Component
public class UserMapper extends BaseMapper<User, UserDto> implements Mapper<User, UserDto> {
    @Inject
    private ImageCollectionMapper imageCollectionMapper;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User toModel(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User result = super.toModel(dto);
        result.setActive(dto.getActive());
        if (dto.getUserSitePermissions() != null) {
            for (String key : dto.getUserSitePermissions()) {
                UserSitePermission userSitePermission = new UserSitePermission(result, SitePermission.valueOf(key));
                result.getUserSitePermissions().add(userSitePermission);
            }
        }
        result.setImageCollection(imageCollectionMapper.toModel(dto.getImageCollection()));
        return result;
    }

    @Override
    public UserDto toDto(User model) {
        if (model == null) {
            return null;
        }

        UserDto result = super.toDto(model);
        result.setActive(model.getActive());
        if (entityManager.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(model, "userSitePermissions")) {
            result.getUserSitePermissions().addAll(
                model.getUserSitePermissions()
                    .stream()
                    .map(s -> s.getPermissionKey().toString())
                    .collect(Collectors.toList())
            );
        }
        result.setImageCollection(imageCollectionMapper.toDto(model.getImageCollection()));
        return result;
    }

    @Override
    protected String[] getIgnoreProperties() {
        return new String[]{"userSitePermissions", "imageCollection", "active", "enabled"};
    }

    @Override
    protected User constructModel() {
        return new User();
    }

    @Override
    protected UserDto constructDto() {
        return new UserDto();
    }
}
