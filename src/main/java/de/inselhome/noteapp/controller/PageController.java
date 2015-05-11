package de.inselhome.noteapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.annotation.Secured;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author  iweinzierl
 */
@Controller
@RequestMapping(value = "/")
public class PageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageController.class);

    @Secured("hasRole(USER)")
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/html")
    public String index() {
        LOGGER.info("Inbound Http request: GET - /");
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "text/html")
    public String login() {
        LOGGER.info("Inbound Http request: GET - /login");
        return "login";
    }
}
