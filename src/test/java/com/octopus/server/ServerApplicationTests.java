package com.octopus.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        properties = {
            "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration," +
                "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration"
        }
)
public class ServerApplicationTests {

    @Autowired
    private MockMvc mock;

    @Autowired
    private ObjectMapper mapper;

    @InjectMocks
	private JobsController controller;

    @MockBean
    private JobsRepository repository;

	@Test
	public void contextLoads() {
		assertThat(controller).isNotNull();
	}

    @Test
    public void testNoJobs() throws Exception {

        when(repository.findAll()).thenReturn(new ArrayList<>());

	    this.mock.perform(get("/jobs")).andExpect(status().isOk())
                 .andExpect(content().string(containsString("[]")));
    }

    @Test
    public void createJobSameDstNotQueued() throws Exception {

	    CreateJobRequest request = new CreateJobRequest();
        request.srcUrl = "http://test.com/file.txt";
        request.dstUrl = "http://test-dst.com/file1.txt";
        request.description = "Test job";

        when(repository.findByDstUrl(request.dstUrl)).thenReturn(new ArrayList<>());

        String json = mapper.writeValueAsString(request);
        this.mock.perform(
                post("/jobs")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createJobSameDstQueued() throws Exception {

        CreateJobRequest request = new CreateJobRequest();
        request.srcUrl = "http://test.com/some_file.txt";
        request.dstUrl = "http://test-dst.com/some_file.mov";
        request.description = "Test job 2";

        Job job = new Job();
        job.srcUrl = "http://my.com/video.mov";
        job.dstUrl = request.dstUrl;
        job.description = "Test job 3";
        job.status = "Running";

        List<Job> jobs = new ArrayList<>();
        jobs.add(job);

        when(repository.findByDstUrl(request.dstUrl)).thenReturn(jobs);

        String json = mapper.writeValueAsString(request);
        this.mock.perform(
                post("/jobs")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }
}
