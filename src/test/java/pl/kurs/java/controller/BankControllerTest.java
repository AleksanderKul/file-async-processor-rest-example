package pl.kurs.java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.kurs.java.Main;
import pl.kurs.java.model.response.JobIdResponse;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class BankControllerTest {

    @Autowired
    private MockMvc postman;

    @Test
    public void shouldReturnUuid() throws Exception {
        postman.perform(MockMvcRequestBuilders.multipart("/bank/import")
                .file(createFileForJob())//
                .contentType(MediaType.APPLICATION_JSON)//
                .accept(MediaType.APPLICATION_JSON))//
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())//
                .andExpect(jsonPath("$.uuid", Matchers.notNullValue()));//
    }

    @Test
    public void shouldGetOkReturn() throws Exception {
        JobIdResponse createTaskResponse = createTaskResponse();
        postman.perform(MockMvcRequestBuilders.get("/bank/job/running/{id}", createTaskResponse.getUuid())
                .contentType(MediaType.APPLICATION_JSON)//
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())//
                .andReturn();//
    }

    @Test
    public void shouldNotFoundIdReturn() throws Exception {
        postman.perform(MockMvcRequestBuilders.get("/bank/job/running/23432423")
                .contentType(MediaType.APPLICATION_JSON)//
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())//
                .andReturn();//
    }

    @Test
    public void shouldGetGoodReturn() throws Exception {
        JobIdResponse createTaskResponse = createTaskResponse();
        //wait until job is finished
        Thread.sleep(12000);
        postman.perform(MockMvcRequestBuilders.get("/bank/job/status/" + createTaskResponse.getUuid())
                .contentType(MediaType.APPLICATION_JSON)//
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());//
    }

    @Test
    public void shouldGetBadReturn() throws Exception {
        JobIdResponse createTaskResponse = createTaskResponse();
        postman.perform(MockMvcRequestBuilders.get("/bank/job/status/" + createTaskResponse.getUuid())
                .contentType(MediaType.APPLICATION_JSON)//
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());//
    }

    @Test
    public void shouldGetNotFoundReturn() throws Exception {
        createTaskResponse();
        postman.perform(MockMvcRequestBuilders.get("/bank/job/status/{id}", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)//
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());//
    }

    private MockMultipartFile createFileForJob() throws IOException {
        Resource fileResource = new ClassPathResource("banks.txt");
        return new MockMultipartFile(
                "file", fileResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                fileResource.getInputStream());
    }

    private JobIdResponse createTaskResponse() throws Exception {
        MvcResult result = postman.perform(MockMvcRequestBuilders.multipart("/bank/import")
                .file(createFileForJob())//
                .contentType(MediaType.APPLICATION_JSON)//
                .accept(MediaType.APPLICATION_JSON))//
                .andReturn();//
        String content = result.getResponse().getContentAsString();
        ObjectMapper om = new ObjectMapper();
        return om.readValue(content, JobIdResponse.class);
    }
}