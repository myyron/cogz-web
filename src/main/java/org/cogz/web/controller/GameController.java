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
@RequestMapping("/game")
public class GameController {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String showGameDetail(@PathVariable("id") long id) {
        return "game";
    }
}
