package org.cogz.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author mlatorilla
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/dashboard").setViewName("dashboard");
//        registry.addViewController("/registration").setViewName("registration");
//        registry.addViewController("/settings/users").setViewName("settings/users");
        registry.addViewController("/login").setViewName("login");
    }
}
