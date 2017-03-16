package org.hspconsortium.platform.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(path = "/test/errortest")
public class ErrorTestController {

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String hello() {
        return "hello world";
    }

    @RequestMapping(path = "/runtime", method = RequestMethod.GET)
    public String doTestRuntime() {
        throw new RuntimeException("Error Test");
    }

}
