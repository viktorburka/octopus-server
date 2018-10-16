package com.octopus.server;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "jobs")
public class Job {

    @Id
    public String id;

    public String description;
    public String status;
    public String src_url;
    public String dst_url;

    public Job(String id) {
        this.id = id;
    }
}
