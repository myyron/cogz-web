package org.cogz.web.controller;

import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author mlatorilla
 */
@Controller
public class DashboardController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showDashboard(HttpSession session) {
        return "dashboard";
    }
}