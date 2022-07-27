package com.ecobinary.rest;


import com.ecobinary.service.CommandService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @PostMapping("/execute")
    public CommandService execute(@RequestBody CommandService command) {
        System.out.println(command.toString());
        command.acquireOutput();

        return command;
    }


}
