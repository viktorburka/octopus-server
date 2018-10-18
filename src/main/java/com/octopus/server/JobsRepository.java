package com.octopus.server;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobsRepository extends MongoRepository<Job, String> {

    List<Job> findByDstUrl(String dst_url);
}
