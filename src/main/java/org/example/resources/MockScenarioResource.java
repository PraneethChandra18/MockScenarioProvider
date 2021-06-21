package org.example.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.hibernate.UnitOfWork;

import io.swagger.annotations.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.example.api.MockScenarioList;
import org.example.api.Node;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;


@Api("/MockScenario")
@Path("/MockScenario")
public class MockScenarioResource {

    @GET
    @ApiOperation(value = "Fetch Description of Each Mock Scenario",
            notes = "Returns JSON object having description of each Mock Scenario",
            response = MockScenarioList.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "description of all mock data has been successfully fetched"),
            @ApiResponse(code = 404, message = "Enter valid url")})
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public MockScenarioList fetchDescription() throws IOException{
        String fileName = "./src/main/resources/MockScenarioList";
        MockScenarioList mockScenarioList=null;

        File file=new File(fileName);
        String data= FileUtils.readFileToString(file,StandardCharsets.UTF_8);
        mockScenarioList = new ObjectMapper().readValue(data, MockScenarioList.class);
        return mockScenarioList;
    }



    @GET
    @ApiOperation(value = "Fetch Mock Scenario Data",
            notes = "Returns Mock Scenario Data",
            response = Node.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "mock data has been successfully fetched"),
                           @ApiResponse(code = 404, message = "Enter valid id")})
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Node fetchData( @ApiParam(value = "ID of required mock scenario", allowableValues = "range[1,6]", required = true) @PathParam("id") String id) throws NotFoundException
    {

        String filename = "MockScenarios/MockScenario" + id;
        String data;
        Node node = null;

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(filename);

            if(inputStream!=null)
            {
                data = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                node = new ObjectMapper().readValue(data, Node.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
      if(node!=null)
          return node;
      else {
          throw new NotFoundException();
      }
    }

    @GET
    @ApiOperation(value = "Fetch number of scenarios", notes = "Returns number of scenarios")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Count for available mocked scenarios successfully fetched")})
    @Path("/countFiles")
    @UnitOfWork
    @Produces(MediaType.TEXT_PLAIN)
    public long countFiles() {
        long count = 0;

        File folder = new File("./src/main/resources/MockScenarios");
        File[] files = folder.listFiles();

        if(files!=null)
        {
            for (File file: files) {
                if (file.isFile() && file.getName().contains("MockScenario")) {
//                    System.out.println(file.getAbsolutePath());
                    count++;
                }
            }
        }

        return count;
    }
}
