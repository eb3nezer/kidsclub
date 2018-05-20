package ebenezer.dto.mapper;

import ebenezer.dto.UserDto;
import ebenezer.dto.mapper.Mapper;
import ebenezer.model.User;
import ebenezer.model.UserSitePermission;
import ebenezer.permissions.SitePermission;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper extends BaseMapper<User, UserDto> implements Mapper<User, UserDto> {
    @Override
    public User toModel(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User result = new User(
                dto.getId(),
                dto.getName(),
                dto.getGivenName(),
                dto.getFamilyName(),
                dto.getEmail(),
                dto.getHomePhone(),
                dto.getMobilePhone(),
                dto.getLoggedIn(),
                dto.getActive(),
                dto.getAvatarUrl(),
                dto.getMediaDescriptor(),
                dto.getRemoteCredential(),
                dto.getRemoteCredentialSource()
        );
        if (dto.getUserSitePermissions() != null) {
            for (String key : dto.getUserSitePermissions()) {
                UserSitePermission userSitePermission = new UserSitePermission(result, SitePermission.valueOf(key));
                result.getUserSitePermissions().add(userSitePermission);
            }
        }
        return result;
    }

    @Override
    public UserDto toDto(User model) {
        if (model == null) {
            return null;
        }
        return new UserDto(
                model.getId(),
                model.getName(),
                model.getGivenName(),
                model.getFamilyName(),
                model.getEmail(),
                model.getHomePhone(),
                model.getMobilePhone(),
                model.getLoggedIn(),
                model.getActive(),
                model.getAvatarUrl(),
                model.getMediaDescriptor(),
                model.getRemoteCredential(),
                model.getRemoteCredentialSource(),
                model.getUserSitePermissions().stream().map(s -> s.getPermissionKey().toString()).collect(Collectors.toList()));
    }
}
