package org.example.resources;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.commons.io.FileUtils;
import org.example.api.MockScenarioList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MockScenarioApiTest {

    private File[] files;
    private MockScenarioResource resource;
    private String filePath;

    @Before
    public void setup(){
        resource=new MockScenarioResource(new MetricRegistry());
        filePath = "./src/main/resources/MockScenarios";
        File folder=new File(filePath);
        files=folder.listFiles();
    }

    @Rule
    public final ResourceTestRule resources= ResourceTestRule.builder()
            .addResource(new MockScenarioResource(new MetricRegistry()))
            .build();

    @Test
    public void ApiHasNoId() throws IOException{
        String apiPath="/MockScenario";
        String response=resources.client().target(apiPath)
                    .request().get(String.class);
        File file=new File("./src/main/resources/MockScenarioList");
        String data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        ObjectMapper mapper=new ObjectMapper();
        JsonNode tree1 = mapper.readTree(data);
        JsonNode tree2 = mapper.readTree(response);
        boolean fine=tree1.equals(tree2);
        assertThat(fine).isEqualTo(true);
    }


    @Test
    public void JsonDataFromIdApiCheck() throws IOException {
        boolean check=true;
        for(File file:files){
            if (file.isFile() && file.getName().contains("MockScenario")){
                String fileName=file.getName();
                String id=fileName.replace("MockScenario","");
                String apiPath="/MockScenario/"+id;
                String response=resources.client().target(apiPath)
                        .request().get(String.class);
                String data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                ObjectMapper mapper=new ObjectMapper();
                JsonNode tree1 = mapper.readTree(data);
                JsonNode tree2 = mapper.readTree(response);
                boolean fine=tree1.equals(tree2);
                if(!fine){
                    if(check)
                        System.out.println("Following files have data that doesn't match with the REST API:");
                    System.out.println(fileName);
                    System.out.println("data\n"+data);
                    System.out.println("response\n"+response);
                    check=false;
                }
            }
        }
        assertThat(check).isEqualTo(true);
    }

    @Test
    public void DataFromCountFilesApiCheck(){
        String apiPath="/MockScenario/countFiles";
        String response=resources.client().target(apiPath)
                .request().get(String.class);
        long countActual=Long.parseLong(response);
        long countExpected=0;
        for(File file:files){
            if (file.isFile() && file.getName().contains("MockScenario"))
                countExpected++;
        }
        assertThat(countExpected).isEqualTo(countActual);
    }

    @Test
    public void SaveEditDelteTest() throws IOException {
        String apiPath="/MockScenario/saveData";
        String data="{\"name\":\"\",\"nodes\":[],\"edges\":[],\"description\":\"\"}";
        Response res=resources.client().target(apiPath).request().post(Entity.entity(data, MediaType.TEXT_PLAIN));
        MockScenarioList mockScenarioList=resource.fetchDescription();
        File file=new File(filePath+"/MockScenario"+mockScenarioList.count);
        String fileData = FileUtils.readFileToString(file,StandardCharsets.UTF_8);
        assertThat(data).isEqualTo(fileData);
        apiPath="/MockScenario/editData/"+mockScenarioList.count;
        data="{\"name\":\"Hello World\",\"nodes\":[],\"edges\":[],\"description\":\"\"}";
        res=resources.client().target(apiPath).request().put(Entity.entity(data, MediaType.TEXT_PLAIN));
        fileData = FileUtils.readFileToString(file,StandardCharsets.UTF_8);
        assertThat(data).isEqualTo(fileData);
        apiPath="/MockScenario/delete/"+mockScenarioList.count;
        res=resources.client().target(apiPath).request().delete();
        File newFile=new File(filePath+"/MockScenario"+mockScenarioList.count);
        boolean g=true;
        if(newFile.exists())
            g=false;
        else
            mockScenarioList.count=mockScenarioList.count-1;
        assertThat(g).isEqualTo(true);
    }

}
