package kc.ebenezer.dto.mapper;

import kc.ebenezer.dto.UserDto;
import kc.ebenezer.dto.mapper.Mapper;
import kc.ebenezer.model.User;
import kc.ebenezer.model.UserSitePermission;
import kc.ebenezer.permissions.SitePermission;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper extends BaseMapper<User, UserDto> implements Mapper<User, UserDto> {
    @Inject
    private ImageCollectionMapper imageCollectionMapper;

    @Override
    public User toModel(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User result = super.toModel(dto);
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
        result.getUserSitePermissions().addAll(
                model.getUserSitePermissions()
                        .stream()
                        .map(s -> s.getPermissionKey().toString())
                        .collect(Collectors.toList())
        );
        result.setImageCollection(imageCollectionMapper.toDto(model.getImageCollection()));
        return result;
    }

    @Override
    protected String[] getIgnoreProperties() {
        return new String[]{"userSitePermissions", "imageCollection"};
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
