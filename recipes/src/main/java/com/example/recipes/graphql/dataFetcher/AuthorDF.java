package com.example.recipes.graphql.dataFetcher;

import java.util.ArrayList;
import java.util.List;

import com.example.recipes.data.entity.Recipe;
import com.example.recipes.data.entity.Role;
import com.example.recipes.data.entity.User;
import com.example.recipes.data.repos.RolesRepo;
import com.example.recipes.data.repos.UserRepo;
import com.example.recipes.security.SecurityDataFetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;

@Component
public class AuthorDF {
    //repos
    @Autowired 
    UserRepo userR; 

    @Autowired 
    RolesRepo rolesR; 

    @Autowired 
    SecurityDataFetcher auxSecurityDF; //my security unlogic

    //QUERIES
    public DataFetcher<List<User>> getAuthorDF() {
        return dataFetchingEnvironment -> {

            auxSecurityDF.noAnomimous(); //security

            String id = dataFetchingEnvironment.getArgument("id");
            String name = dataFetchingEnvironment.getArgument("name");
            
            Role roleAuthor = rolesR.findFirstByName("USER");
            List<User> authors = roleAuthor.getUsers();
            List<User> result = new ArrayList<>();

            // byID (one author)
            if (id!=null){
                Long uid;
                try{
                    uid = Long.parseLong(id);
                }catch(NumberFormatException e){
                    return result;
                }
                User user = userR.findById(uid).orElse(null);
                if (user!=null){
                    if(authors.contains(user)){
                        result.add(user);
                    }
                }
                return result;
            }

            // whith Name
            if(name!= null){
                List<User> users = userR.findByNameContaining(name);
                for (User user:users){
                    if (authors.contains(user)){
                        result.add(user);
                    }
                }
            }else{
                result = authors;
            }

            return result;
        };
    }

    public DataFetcher<User> getAuthorByRecipeDF(){
        return dataFetchingEnvironment -> {
            Recipe recipe = dataFetchingEnvironment.getSource();
            return recipe.getAutor();
        };
    }

}
