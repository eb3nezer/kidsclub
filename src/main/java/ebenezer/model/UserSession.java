package ebenezer.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user_sessions")
public class UserSession extends ModelObject {
    @EmbeddedId
    private UserSessionPK key;

    @NotNull
    @Column(name = "token")
    private String tokenValue;

    @NotNull
    @Column(name = "date")
    private Long date;

    public UserSession() {
        super();
    }

    public UserSession(@NotNull String userId, @NotNull String series, @NotNull String tokenValue, @NotNull Date date) {
        super();
        this.key = new UserSessionPK(userId, series);
        this.tokenValue = tokenValue;
        this.date = date.getTime();
    }

    public String getUserId() {
        return key.getUserId();
    }

    public String getSeries() {
        return key.getSeries();
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public Date getDate() {
        return new Date(date);
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public void setDate(Date date) {
        this.date = date.getTime();
    }

    @Embeddable
    public static class UserSessionPK implements Serializable {
        @NotNull
        @Column(name = "userid")
        private String userId;

        @NotNull
        @Column(name = "series")
        private String series;

        public UserSessionPK() {
        }

        public UserSessionPK(@NotNull String userId, @NotNull String series) {
            this.userId = userId;
            this.series = series;
        }

        public String getUserId() {
            return userId;
        }

        public String getSeries() {
            return series;
        }
    }
}
