package ebenezer.dto.mapper;

import ebenezer.dto.UserDetailsDto;
import ebenezer.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsMapper implements Mapper<User, UserDetailsDto> {
    @Override
    public User toModel(UserDetailsDto dto) {
        return null;
    }

    @Override
    public UserDetailsDto toDto(User model) {
        if (model == null) {
            return null;
        }
        return new UserDetailsDto(
                model.getId(),
                model.getName(),
                model.getEmail(),
                model.getRemoteCredential(),
                model.getRemoteCredentialSource(),
                model.getActive());
    }
}
