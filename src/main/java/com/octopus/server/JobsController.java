package com.octopus.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JobsController {

    private JobsRepository repository;

    @Autowired
    JobsController(JobsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/jobs")
    public List<Job> getJobs() {
        return repository.findAll();
    }

    @PostMapping("/jobs")
    public ResponseEntity<?> createJob(@RequestBody CreateJobRequest request) {

        List<Job> jobs = repository.findByDstUrl(request.dstUrl);

        // if at least one job with the same dst url already exists and
        // its not in Error state (otherwise it will interfere with other jobs)
        boolean atLeastOne = jobs.stream().anyMatch(t -> !t.status.equals("Error"));
        if (atLeastOne) {
            RequestError error = new RequestError("A job with same destination is already queued or in process");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Job job = new Job();
        job.status = "Created";
        job.dstUrl = request.dstUrl;
        job.srcUrl = request.srcUrl;
        job.description = request.description;

        job = repository.save(job);

        return new ResponseEntity<>(job, HttpStatus.OK);
    }
}
