package org.example.resources;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.commons.io.FileUtils;
import org.example.MockScenerioSupplierApplication;
import org.example.MockScenerioSupplierConfiguration;
import org.example.api.MockScenarioList;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class MockScenarioServerTest {

    private File[] files;
    private Client client;
    private MockScenarioResource resource;
    private String filePath;
    @Before
    public void setup(){
        resource=new MockScenarioResource(new MetricRegistry());
        client=new JerseyClientBuilder().build();
        filePath = "./src/main/resources/MockScenarios";
        File folder=new File(filePath);
        files=folder.listFiles();
    }

    @Rule
    public final DropwizardAppRule<MockScenerioSupplierConfiguration> RULE=
            new DropwizardAppRule<MockScenerioSupplierConfiguration>(MockScenerioSupplierApplication.class,
                    ResourceHelpers.resourceFilePath("config.yml"));

    @Test
    public void runListServerTest() throws IOException {
        File file=new File("./src/main/resources/MockScenarioList");
        String response=client.target(String.format("http://localhost:%d/MockScenario",
                RULE.getLocalPort())).request().get(String.class);
        String data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        ObjectMapper mapper=new ObjectMapper();
        JsonNode tree1 = mapper.readTree(data);
        JsonNode tree2 = mapper.readTree(response);
        boolean fine=tree1.equals(tree2);
        assertThat(fine).isEqualTo(true);
    }

    @Test
    public void runIdServerTest() throws IOException {
        boolean allFine=true;
        for(File file:files) {
            if (file.isFile() && file.getName().contains("MockScenario")) {
                String fileName = file.getName();
                String id = fileName.replace("MockScenario", "");
                String response=client.target(String.format("http://localhost:%d/MockScenario/%s",
                        RULE.getLocalPort(),id)).request().get(String.class);
                String data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                ObjectMapper mapper=new ObjectMapper();
                JsonNode tree1 = mapper.readTree(data);
                JsonNode tree2 = mapper.readTree(response);
                boolean fine=tree1.equals(tree2);
                if(!fine){
                    if(allFine)
                        System.out.println("Following Servers are not working fine:");
                    System.out.println(fileName);
                    allFine=false;
                }
            }
        }
        assertThat(allFine).isEqualTo(true);
    }

    @Test
    public void runCountFilesServerTest(){
        String response=client.target(String.format("http://localhost:%d/MockScenario/countFiles",
                RULE.getLocalPort())).request().get(String.class);
        long countActual=Long.parseLong(response);
        long countExpected=0;
        for(File file:files){
            if (file.isFile() && file.getName().contains("MockScenario"))
                countExpected++;
        }
        assertThat(countExpected).isEqualTo(countActual);
    }

    @Test
    public void runCreateEditDeleteServerTest() throws IOException {
        String data="{\"name\":\"\",\"nodes\":[],\"edges\":[],\"description\":\"\"}";
        Response response=client.target(String.format("http://localhost:%d/MockScenario/saveData",
                RULE.getLocalPort())).request().post(Entity.entity(data, MediaType.TEXT_PLAIN));
        MockScenarioList mockScenarioList=resource.fetchDescription();
        File file=new File(filePath+"/MockScenario"+mockScenarioList.count);
        String fileData = FileUtils.readFileToString(file,StandardCharsets.UTF_8);
        assertThat(data).isEqualTo(fileData);
        data="{\"name\":\"Hello World\",\"nodes\":[],\"edges\":[],\"description\":\"\"}";
        response=client.target(String.format("http://localhost:%d/MockScenario/editData/%d",
                RULE.getLocalPort(),mockScenarioList.count)).request().put(Entity.entity(data, MediaType.TEXT_PLAIN));
        fileData = FileUtils.readFileToString(file,StandardCharsets.UTF_8);
        assertThat(data).isEqualTo(fileData);
        response=client.target(String.format("http://localhost:%d/MockScenario/delete/%d",
                RULE.getLocalPort(),mockScenarioList.count)).request().delete();
        File newFile=new File(filePath+"/MockScenario"+mockScenarioList.count);
        boolean g=true;
        if(newFile.exists())
            g=false;
        else
            mockScenarioList.count=mockScenarioList.count-1;
        assertThat(g).isEqualTo(true);
    }

}
