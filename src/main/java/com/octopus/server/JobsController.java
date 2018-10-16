package com.octopus.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JobsController {

    @Autowired
    private JobsRepository repository;

    @GetMapping("/jobs")
    List<Job> getJobs() {
        return repository.findAll();
    }
}
