/*
 * Copyright 2025 Contractors of Ground Zero (CoGZ)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cogz.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author altrax
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/game-list").setViewName("game-list");
        registry.addViewController("/user-list").setViewName("user-list");
        registry.addViewController("/team-list").setViewName("team-list");
        registry.addViewController("/tools").setViewName("tools");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploaded-images/**", "/pdf/**")
                .addResourceLocations("file:///c:/altrax/codes/cogz-web/data/images/", "file:///c:/altrax/codes/cogz-web/data/pdf/");
//        registry.addResourceHandler("/uploaded-images/**", "/pdf/**")
//                .addResourceLocations("file:/home/ec2-user/app/data/images/", "file:/home/ec2-user/app/data/pdf/");
    }
}
