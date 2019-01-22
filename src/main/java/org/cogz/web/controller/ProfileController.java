package org.cogz.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author mlatorilla
 */
@Controller
@RequestMapping("/profiles")
public class ProfileController {

    @RequestMapping(value = "/players/", method = RequestMethod.GET)
    public String showPlayerList() {
        return "profiles/players";
    }
    
    @RequestMapping(value = "/player/{id}", method = RequestMethod.GET)
    public String showPlayerDetail(@PathVariable("id") long id) {
        return "profiles/player";
    }
}
