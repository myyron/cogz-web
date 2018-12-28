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
@RequestMapping("/registration")
public class GameController {

    @RequestMapping(value = "/games/", method = RequestMethod.GET)
    public String showGameList() {
        return "registration/games";
    }

    @RequestMapping(value = "/game/{id}", method = RequestMethod.GET)
    public String showGameDetail(@PathVariable("id") long id) {
        return "registration/game";
    }

    @RequestMapping(value = "/report/{id}", method = RequestMethod.GET)
    public String showReport(@PathVariable("id") long id) {
        return "registration/report";
    }
}
