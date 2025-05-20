package movie.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/auth/login", "/auth/register", "/home/**", 
                             "/movie-detail/**", "/oauth/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/auth/login")
                .defaultSuccessUrl("/home/", true)
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/home/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and()
            .csrf().disable(); // Tạm thời vô hiệu hóa CSRF, nên bật lại trong production
    }
}