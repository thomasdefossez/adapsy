package com.adapsy.webapp.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
@Order(-1)
public class LoginConfig extends WebSecurityConfigurerAdapter {
	
	static final String PROCESSING_URL = "/_app/sign_in";
	static final String USERNAME_PARAMETER = "adtruster_account[email]";
	static final String PASSWORD_PÄRAMETER = "adthruster_account[password]";
	
	@Autowired
	DataSource dataSource;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
		.usersByUsernameQuery("select email, mot_de_passe, actif from utilisateur where email=?")
		.authoritiesByUsernameQuery("select email, role from utilisateur_role where email=?");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requestMatchers().regexMatchers("/login", "/login.+", "/oauth/.+", "/_app/sign_in", "/logout"); 
		http.exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"));
		http.formLogin().loginProcessingUrl(PROCESSING_URL).usernameParameter(USERNAME_PARAMETER).passwordParameter(PASSWORD_PÄRAMETER);
		http.httpBasic();
		http.authorizeRequests().antMatchers("/login**").permitAll();
		http.authorizeRequests().antMatchers("/oauth/**").authenticated();
		http.authorizeRequests().antMatchers(PROCESSING_URL).anonymous();
		http.logout().logoutSuccessUrl("/login?logout");
		http.csrf().disable();
	}

}
