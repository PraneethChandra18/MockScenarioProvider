package org.example.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.commons.io.FileUtils;
import org.example.MockScenerioSupplierApplication;
import org.example.MockScenerioSupplierConfiguration;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class MockScenarioServerTest {

    private File[] files;
    private Client client;
    @Before
    public void setup(){
        client=new JerseyClientBuilder().build();
        String filePath = "./src/main/resources/MockScenarios";
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
}
