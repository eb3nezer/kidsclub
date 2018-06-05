package kc.ebenezer.auth;

import kc.ebenezer.dao.UserSessionDao;
import kc.ebenezer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.Arrays;

@EnableOAuth2Client
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Value(value = "${kidsclub.admin}")
    private String adminEmail;

    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    @Autowired
    private GoogleAuthenticationSuccessHandler googleAuthenticationSuccessHandler;

    @Autowired
    private FacebookAuthenticationSuccessHandler facebookAuthenticationSuccessHandler;

    @Autowired
    private TwitterAuthenticationSuccessHandler twitterAuthenticationSuccessHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSessionDao userSessionDao;

    @Bean
    @ConfigurationProperties("kidsclub.google.client")
    public AuthorizationCodeResourceDetails google() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("kidsclub.google.resource")
    public ResourceServerProperties googleResource() {
        return new ResourceServerProperties();
    }

    @Bean
    @ConfigurationProperties("kidsclub.facebook.client")
    public AuthorizationCodeResourceDetails facebook() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("kidsclub.facebook.resource")
    public ResourceServerProperties facebookResource() {
        return new ResourceServerProperties();
    }

    @Bean
    @ConfigurationProperties("kidsclub.twitter.client")
    public AuthorizationCodeResourceDetails twitter() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("kidsclub.twitter.resource")
    public ResourceServerProperties twitterResource() {
        return new ResourceServerProperties();
    }

    private Filter ssoGoogleFilter() {
        OAuth2ClientAuthenticationProcessingFilter googleFilter = new KidsClubOAuth2Filter("/login/google", userService, adminEmail);
        OAuth2RestTemplate googleTemplate = new OAuth2RestTemplate(google(), oauth2ClientContext);
        googleFilter.setRestTemplate(googleTemplate);
        googleFilter.setTokenServices(new UserInfoTokenServices(googleResource().getUserInfoUri(), google().getClientId()));
        googleFilter.setAuthenticationSuccessHandler(googleAuthenticationSuccessHandler);
        return googleFilter;
    }

    private Filter ssoFacebookFilter() {
        OAuth2ClientAuthenticationProcessingFilter facebookFilter = new KidsClubOAuth2Filter("/login/facebook", userService, adminEmail);
        OAuth2RestTemplate facebookTemplate = new OAuth2RestTemplate(facebook(), oauth2ClientContext);
        facebookFilter.setRestTemplate(facebookTemplate);
        facebookFilter.setTokenServices(new UserInfoTokenServices(facebookResource().getUserInfoUri(), facebook().getClientId()));
        facebookFilter.setAuthenticationSuccessHandler(facebookAuthenticationSuccessHandler);
        return facebookFilter;
    }

    private Filter ssoTwitterFilter() {
        OAuth2ClientAuthenticationProcessingFilter twitterFilter = new KidsClubOAuth2Filter("/login/twitter", userService, adminEmail);
        OAuth2RestTemplate twitterTemplate = new OAuth2RestTemplate(twitter(), oauth2ClientContext);
        twitterFilter.setRestTemplate(twitterTemplate);
        twitterFilter.setTokenServices(new UserInfoTokenServices(twitterResource().getUserInfoUri(), twitter().getClientId()));
        twitterFilter.setAuthenticationSuccessHandler(twitterAuthenticationSuccessHandler);
        return twitterFilter;
    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        filter.setFilters(Arrays.asList(ssoGoogleFilter(), ssoTwitterFilter(), ssoFacebookFilter()));
        return filter;
    }

    private KidsClubRememberMeServices rememberMeServices() {
        return new KidsClubRememberMeServices("abc", userService, userSessionDao);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        googleAuthenticationSuccessHandler.setKidsClubRememberMeServices(rememberMeServices());
        facebookAuthenticationSuccessHandler.setKidsClubRememberMeServices(rememberMeServices());

        http
                .csrf()
                .disable()
                .antMatcher("/**")
                    .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/", "/index.html", "/login/**", "/webjars/**", "/login.html", "/error", "/public/**")
                    .permitAll()
                .anyRequest()
                    .authenticated()
                .and()
                    .formLogin()
                        .loginPage("/login.html")
                .and()
                    .rememberMe().rememberMeServices(rememberMeServices()).key("abc")
                .and()
                    .logout().logoutSuccessUrl("/").permitAll();
    }
}