package org.example.resources;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class MockScenarioResourceTest {
    private MockScenarioResource resource;
    private File[] files;
    private String filePath;
    @Before
    public void setup(){
        resource=new MockScenarioResource();
        filePath="./src/main/resources/MockScenarios";
        File folder=new File(filePath);
        files=folder.listFiles();
    }

    @Test
    public void pathContainsMockScenarioFiles(){
        assertThat(files).isNotNull();
    }

    @Test
    public void pathContainsMockScenarioListFile() throws IOException{
        File file=new File("./src/main/resources/MockScenarioList");
        String data = "";
        boolean fine=true;
        try{
            data=FileUtils.readFileToString(file,StandardCharsets.UTF_8);
        }catch(FileNotFoundException e){
            fine=false;
        }
        assertThat(fine).isEqualTo(true);
    }

    @Test
    public void countFilesWorksFine(){
        long expected=0;
        if(files!=null)
        {
            for (File file: files) {
                if (file.isFile() && file.getName().contains("MockScenario")) {
                    expected++;
                }
            }
        }
        long actual=resource.countFiles();
        assertThat(expected).isEqualTo(actual);
    }

    @Test
    public void mockScenarioFilesNotEmpty() throws IOException{
        boolean allFilesContainContent=true;
        for(File file:files){
            if (file.isFile() && file.getName().contains("MockScenario")){
                String fileName=file.getName();
                BufferedReader br = new BufferedReader(new FileReader(filePath+"/"+fileName));
                if (br.readLine() == null) {
                    if(allFilesContainContent)
                        System.out.println("Following Files are Empty:\n-------------------------------");
                    allFilesContainContent=false;
                    System.out.println(fileName);
                }
            }
        }
        if(allFilesContainContent)
            System.out.println("None of the files are empty..... Good to GO");
        assertThat(allFilesContainContent).isEqualTo(true);
    }


    @Test
    public void mockScenarioListFileNotEmpty() throws IOException{
        boolean mockScenarioListFileContainContent=true;
        BufferedReader br = new BufferedReader(new FileReader("./src/main/resources/MockScenarioList"));
        if (br.readLine() == null) {
            mockScenarioListFileContainContent=false;
        }
        assertThat(mockScenarioListFileContainContent).isEqualTo(true);
    }

    public static boolean isJSONValid(String test){
        try {
            new JSONObject(test);
        }catch(JSONException ex){
            try{
                new JSONArray(test);
            }catch(JSONException ex1){
                return false;
            }
        }
        return true;
    }

    @Test
    public void filesContainJsonData() throws IOException {
        boolean allFilesContainJSON=true;
        for(File file:files){
            if (file.isFile() && file.getName().contains("MockScenario")){
                String fileName=file.getName();
                String data = FileUtils.readFileToString(file,StandardCharsets.UTF_8);
                if(!isJSONValid(data)){
                    if(allFilesContainJSON)
                        System.out.println("Following Files contain invalid JSON:\n-------------------------------");
                    allFilesContainJSON=false;
                    System.out.println(fileName);
                }
            }
        }
        if(allFilesContainJSON)
            System.out.println("All Files are OK..... Good to GO");
        assertThat(allFilesContainJSON).isEqualTo(true);
    }


    @Test
    public void mockScenarioListFileContainJsonData() throws IOException {
        File file=new File("./src/main/resources/MockScenarioList");
        String data = FileUtils.readFileToString(file,StandardCharsets.UTF_8);
        assertThat(isJSONValid(data)).isEqualTo(true);
    }

}
