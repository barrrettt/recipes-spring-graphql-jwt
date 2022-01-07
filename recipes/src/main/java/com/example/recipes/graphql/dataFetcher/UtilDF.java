package com.example.recipes.graphql.dataFetcher;

import com.example.recipes.data.ToolsJPA;
import com.example.recipes.security.SecurityDataFetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;

@Component
public class UtilDF {
    //my security unlogic
    @Autowired 
    SecurityDataFetcher securityDF;

    @Autowired
    ToolsJPA toolsJpa;

    //reset
    public DataFetcher <String> clearDataDF(){
        return dataFetchingEnvironment -> {
            securityDF.onlyADMIN();
            toolsJpa.resetDataBase();
            return "Deleted data.";
        };
    }
    
    //reset and mocks
    public DataFetcher <String> mockDataDF(){
        return dataFetchingEnvironment -> {
            securityDF.onlyADMIN();
            toolsJpa.resetDataBase();
            toolsJpa.createMobs();
            return "Deleted data and mocks created.";
        };
    }
}
