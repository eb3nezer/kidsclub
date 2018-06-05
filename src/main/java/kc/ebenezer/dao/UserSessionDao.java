package kc.ebenezer.dao;

import kc.ebenezer.model.UserSession;
import kc.ebenezer.service.UserService;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class UserSessionDao extends BaseDaoImpl<UserSession> implements PersistentTokenRepository {
    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        UserSession userSession = new UserSession(
                token.getUsername(),
                token.getSeries(),
                token.getTokenValue(),
                token.getDate());
        getEntityManager().persist(userSession);
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        TypedQuery<UserSession> query = getEntityManager().createQuery("select session from UserSession session where session.key.series = :series", UserSession.class);
        query.setParameter("series", series);
        List<UserSession> sessions = query.getResultList();
        for (UserSession session : sessions) {
            session.setTokenValue(tokenValue);
            session.setDate(lastUsed);
        }
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        TypedQuery<UserSession> query = getEntityManager().createQuery("select session from UserSession session where session.key.series = :series", UserSession.class);
        query.setParameter("series", seriesId);
        try {
            UserSession session = query.getSingleResult();
            return new PersistentRememberMeToken(session.getUserId(), session.getSeries(), session.getTokenValue(), session.getDate());
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void removeUserTokens(String username) {
        TypedQuery<UserSession> query = getEntityManager().createQuery("select session from UserSession session where session.key.userId = :userId", UserSession.class);
        query.setParameter("userId", username);
        List<UserSession> sessions = query.getResultList();
        for (UserSession session : sessions) {
            getEntityManager().remove(session);
        }
    }

    @Override
    public Optional findById(Long id) {
        return Optional.empty();
    }
}
