package kc.ebenezer.dto.mapper;

import kc.ebenezer.dto.UserDto;
import kc.ebenezer.model.User;
import kc.ebenezer.model.UserSitePermission;
import kc.ebenezer.permissions.SitePermission;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class UserMapperTest {
    @Mock
    private ImageCollectionMapper imageCollectionMapper;

    @InjectMocks
    private UserMapper userMapper;

    @Test
    public void toModel() {
        String[] permissions = {"VIEW_AUDIT"};
        UserDto dto = new UserDto(
                1441L,
                "Ben Kelley",
                "B",
                "Kelley",
                "bkelley@email.com",
                "9876 5432",
                "0456 789012",
                true,
                true,
                "http://image.com/me.jpg",
                "abcde",
                "10010101",
                "FriendFace",
                Arrays.asList(permissions)
        );
        User model = userMapper.toModel(dto);
        assertEquals(1441L, model.getId().longValue());
        assertEquals("Ben Kelley", model.getName());
        assertEquals("B", model.getGivenName());
        assertEquals("Kelley", model.getFamilyName());
        assertEquals("bkelley@email.com", model.getEmail());
        assertEquals("9876 5432", model.getHomePhone());
        assertEquals("0456 789012", model.getMobilePhone());
        assertTrue(model.getLoggedIn());
        assertTrue(model.getActive());
        assertEquals("http://image.com/me.jpg", model.getAvatarUrl());
        assertEquals("abcde", model.getMediaDescriptor());
        assertEquals("10010101", model.getRemoteCredential());
        assertEquals("FriendFace", model.getRemoteCredentialSource());
        assertTrue(model.getUserSitePermissions().contains(new UserSitePermission(model, SitePermission.VIEW_AUDIT)));
    }

    @Test
    public void toDto() {
        User model = new User(
                1441L,
                "Ben Kelley",
                "B",
                "Kelley",
                "bkelley@email.com",
                "9876 5432",
                "0456 789012",
                true,
                true,
                "http://image.com/me.jpg",
                "abcde",
                "FriendFace",
                "10010101"
        );
        model.getUserSitePermissions().add(new UserSitePermission(model, SitePermission.INVITE_USERS));
        UserDto dto = userMapper.toDto(model);
        assertEquals(1441L, model.getId().longValue());
        assertEquals("Ben Kelley", dto.getName());
        assertEquals("B", dto.getGivenName());
        assertEquals("Kelley", dto.getFamilyName());
        assertEquals("bkelley@email.com", dto.getEmail());
        assertEquals("9876 5432", dto.getHomePhone());
        assertEquals("0456 789012", dto.getMobilePhone());
        assertTrue(dto.getLoggedIn());
        assertTrue(dto.getActive());
        assertEquals("http://image.com/me.jpg", dto.getAvatarUrl());
        assertEquals("abcde", dto.getMediaDescriptor());
        assertEquals("10010101", dto.getRemoteCredential());
        assertEquals("FriendFace", dto.getRemoteCredentialSource());
        assertTrue(dto.getUserSitePermissions().contains("INVITE_USERS"));

    }
}
