package org.example.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.UnitOfWork;

import io.swagger.annotations.*;
import org.apache.commons.io.FileUtils;

import org.example.api.Data;
import org.example.api.Detail;
import org.example.api.MockScenarioList;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/*
This class is for making the apis for fetching the data from the mock scenarios
 */

@Api("/MockScenario")
@Path("/MockScenario")
public class MockScenarioResource {

    private MetricRegistry service;

    public MockScenarioResource(MetricRegistry service){
        this.service=service;
    }

    @GET
    @ApiOperation(value = "Fetch Description of Each Mock Scenario",
            notes = "Returns JSON object having description of each Mock Scenario",
            response = MockScenarioList.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "description of all mock data has been successfully fetched"),
            @ApiResponse(code = 404, message = "Enter valid url")})
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)   //returns json object
    @UnitOfWork

    //api for fetching the list and descriptions of each of the mock scenarios
    public MockScenarioList fetchDescription() throws IOException{
        //timer starts
        Timer timer=service.timer("MockScenarioList");
        Timer.Context time=timer.time();
        String fileName = "./src/main/resources/MockScenarioList";
        MockScenarioList mockScenarioList=null;
        File file=new File(fileName);
        String data= FileUtils.readFileToString(file,StandardCharsets.UTF_8);
        mockScenarioList = new ObjectMapper().readValue(data, MockScenarioList.class);
        //timer ends
        time.stop();
        return mockScenarioList;
    }



    @GET
    @ApiOperation(value = "Fetch Mock Scenario Data",
            notes = "Returns Mock Scenario Data",
            response = Data.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "mock data has been successfully fetched"),
                           @ApiResponse(code = 404, message = "Enter valid id")})
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON) //returns json object
    @UnitOfWork
    //api for fetching the data within each of the mock scenario files in the backend
    public Data fetchData( @ApiParam(value = "ID of required mock scenario", allowableValues = "range[0,1000000000]", required = true) @PathParam("id") String id) throws NotFoundException, IOException
    {
        Timer timer=service.timer("Mock Scenario "+id+" API");
        Timer.Context time=timer.time();
        String filename = "./src/main/resources/MockScenarios/MockScenario" + id;
        String data;
        Data node = null;
        try {
            File file=new File(filename);
            data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            node = new ObjectMapper().readValue(data, Data.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
      if(node!=null){
          time.stop();
          return node;
      }
      else {
          throw new NotFoundException();
      }
    }

    @GET
    @ApiOperation(value = "Fetch number of scenarios", notes = "Returns number of scenarios")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Count for available mocked scenarios successfully fetched")})
    @Path("/countFiles")
    @UnitOfWork
    @Produces(MediaType.TEXT_PLAIN)  //return string (text)

    //api for returning the count of the files present in the backend
    public long countFiles() {
        Timer timer=service.timer("Count Files API");
        Timer.Context time=timer.time();
        long count = 0;

        File folder = new File("./src/main/resources/MockScenarios");
        File[] files = folder.listFiles();

        if(files!=null)
        {
            for (File file: files) {
                if (file.isFile() && file.getName().contains("MockScenario")) {
                    count++;
                }
            }
        }
        time.stop();
        return count;
    }

    @POST
    @ApiOperation(value = "Add a new Mock Scenario",
            notes = "Adds a new Mock Scenario File to the end of the current list of Mock Scenarios")
    @ApiResponses(value = {@ApiResponse(code = 204, message = "Mock Data has been Added Successfully | No Content"),
                           @ApiResponse(code = 415, message = "Unsupported Media Type")})	
    @Path("/saveData")
    @UnitOfWork
    @Consumes(MediaType.TEXT_PLAIN)
    public void saveData(String data) {
        Timer timer=service.timer("saveData");
        Timer.Context time=timer.time();

        try {
            Data node = new ObjectMapper().readValue(data, Data.class);

            if(node != null) {
                MockScenarioList mockScenarioList = fetchDescription();
                if(mockScenarioList != null) {
                    mockScenarioList.count = mockScenarioList.count + 1;

                    Detail detail = new Detail();
                    detail.name = node.name;
                    detail.description = node.description;
                    detail.id = String.valueOf(mockScenarioList.count);

                    mockScenarioList.mockScenarioList.add(detail);

                    String fileName = "./src/main/resources/MockScenarios/MockScenario" + mockScenarioList.count;

                    File file = new File(fileName);
                    FileUtils.touch(file);
                    FileUtils.writeStringToFile(file, data, StandardCharsets.UTF_8);

                    File file2 = new File("./src/main/resources/MockScenarioList");
                    String text = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(mockScenarioList);
                    FileUtils.writeStringToFile(file2, text, StandardCharsets.UTF_8);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        time.stop();
    }

    @DELETE
    @ApiOperation(value = "Removes a Mock Scenario from the backend",
            notes = "Removes a particular Mock Scenario File which has the given id parameter")
    @ApiResponses(value = {@ApiResponse(code = 204, message = "Mock Data has been removed successfully | No Content")})
    @Path("/delete/{id}")
    @UnitOfWork
    public void deleteData(@ApiParam(value = "ID of the mock scenario which you want to delete", allowableValues = "range[0,1000000000]", required = true) @PathParam("id") String id) {
        Timer timer=service.timer("Delete Mock Scenario "+id);
        Timer.Context time=timer.time();
        String fileName = "./src/main/resources/MockScenarios/MockScenario" + id;
        File file = new File(fileName);

        try {
            MockScenarioList mockScenarioList = fetchDescription();
            if(mockScenarioList != null) {
                mockScenarioList.mockScenarioList.removeIf(x -> Objects.equals(x.id, id));

                File file2 = new File("./src/main/resources/MockScenarioList");
                String text = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(mockScenarioList);
                FileUtils.writeStringToFile(file2, text, StandardCharsets.UTF_8);
            }

            if(file.exists()) {
                FileUtils.deleteQuietly(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        time.stop();
    }

    @PUT
    @ApiOperation(value = "Edits a Mock Scenario present in the backend",
            notes = "Edits a particular Mock Scenario File which has the given id parameter")
    @ApiResponses(value = {@ApiResponse(code = 204, message = "Mock Data has been edited successfully | No Content"),
			   @ApiResponse(code = 415, message = "Unsupported Media Type")})
    @Path("/editData/{id}")
    @UnitOfWork
    @Consumes(MediaType.TEXT_PLAIN)
    public void editData(@ApiParam(value = "ID of the mock scenario which you want to edit", allowableValues = "range[0,1000000000]", required = true) @PathParam("id") String id, String data){
        Timer timer=service.timer("Edit Mock Scenario "+id);
        Timer.Context time=timer.time();
        File folder = new File("./src/main/resources/MockScenarios");
        File[] files = folder.listFiles();
	    boolean g=true;
        if(files!=null)
        {
            for (File file: files) {
		        String fileName=file.getName();
		        fileName=fileName.replace("MockScenario","");
                if(fileName.equals(id)){
		            g=true;
		            break;
		        }
		        else
		        g=false;
            }
        }
	    if(g==false)
	    return;
        try{
            Data newData = new ObjectMapper().readValue(data, Data.class);
            MockScenarioList mockScenarioList= fetchDescription();
            if(mockScenarioList != null) {

                Detail detail = new Detail();
                detail.name = newData.name;
                detail.description = newData.description;
                detail.id=id;
                for (int i = 0; i < mockScenarioList.mockScenarioList.size(); i++) {
                    if(mockScenarioList.mockScenarioList.get(i).id.equals(id)){
                        mockScenarioList.mockScenarioList.get(i).name=newData.name;
                        mockScenarioList.mockScenarioList.get(i).name = newData.name;
                        mockScenarioList.mockScenarioList.get(i).description = newData.description;
                    }
                }
                String fileName = "./src/main/resources/MockScenarios/MockScenario" + id;
                File file = new File(fileName);
                FileUtils.touch(file);
                FileUtils.writeStringToFile(file, data, StandardCharsets.UTF_8);

                File file2 = new File("./src/main/resources/MockScenarioList");
                String text = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(mockScenarioList);
                FileUtils.writeStringToFile(file2, text, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        time.stop();
    }

}
