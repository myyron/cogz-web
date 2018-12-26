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
@RequestMapping("/settings/users")
public class UsersController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showUserList(HttpSession session) {
        return "settings/users";
    }
}
