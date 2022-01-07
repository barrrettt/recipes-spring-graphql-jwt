package com.example.recipes.graphql;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.annotation.PostConstruct;

import com.example.recipes.graphql.dataFetcher.AuthorDF;
import com.example.recipes.graphql.dataFetcher.RecipeDF;
import com.example.recipes.graphql.dataFetcher.RoleDF;
import com.example.recipes.graphql.dataFetcher.TagDF;
import com.example.recipes.graphql.dataFetcher.UserDF;
import com.example.recipes.graphql.dataFetcher.UtilDF;
import com.google.common.io.Resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

//Esta clase crea el schema y registra los datafetchers
@Component
public class GrphQLProvider {

    // dataFetchers
    @Autowired
    UserDF userF; 
    @Autowired
    RoleDF roleF; 
    @Autowired
    UtilDF utilDF;
    @Autowired
    AuthorDF authorDF;
    @Autowired
    RecipeDF recipeF; 
    @Autowired
    TagDF tagF; 
    
    // service
    private GraphQL graphQL;

    //build scherma, strategic: first definition schema file
    @Bean
    public GraphQL graphQL() {
        return graphQL; 
    }

    @PostConstruct
    public void init() throws IOException {
        URL url = Resources.getResource("schemas/schema.graphqls"); 
        String sdl = Resources.toString(url, Charset.defaultCharset()); 
        GraphQLSchema graphQLSchema = buildSchema(sdl); 
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build(); 
    }

    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl); 
        RuntimeWiring runtimeWiring = buildWiring(); 
        SchemaGenerator schemaGenerator = new SchemaGenerator(); 
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring); 
    }

    // And finaly, link schema with datafetchers... 
    private RuntimeWiring buildWiring(){ 
        return RuntimeWiring.newRuntimeWiring() 
                // queries 
                .type(newTypeWiring("Query") 
                    .dataFetcher("users", userF.getUserDF()) 
                    .dataFetcher("roles", roleF.getRolDF()) 
                    
                    .dataFetcher("authors", authorDF.getAuthorDF()) 
                    .dataFetcher("recipes", recipeF.getRecipeDF()) 
                    .dataFetcher("tags", tagF.getTagDF()) 
                    ) 

                // mutations 
                .type(newTypeWiring("Mutation")
                    .dataFetcher("signIn", userF.signIn())
                    .dataFetcher("signUp", userF.singUp())

                    .dataFetcher("createUser", userF.createUserDF())
                    .dataFetcher("updateUser", userF.updateUserDF())
                    .dataFetcher("deleteUser", userF.deleteUserDF())

                    .dataFetcher("createRecipe", recipeF.createRecipeDF())
                    .dataFetcher("updateRecipe", recipeF.updateRecipeDF())
                    .dataFetcher("deleteRecipe", recipeF.deleteRecipeDF())

                    .dataFetcher("createTag", tagF.createTagDF())
                    .dataFetcher("updateTag", tagF.updateTagDF())
                    .dataFetcher("deleteTag", tagF.deleteTagDF())

                    .dataFetcher("clearData", utilDF.clearDataDF())
                    .dataFetcher("mockData", utilDF.mockDataDF())
                )

                // types 
                .type(newTypeWiring("User"))
                .type(newTypeWiring("Role"))

                .type(newTypeWiring("Author"))
                .type(newTypeWiring("Recipe")
                    .dataFetcher("author", authorDF.getAuthorByRecipeDF())
                    )
                .type(newTypeWiring("Tag")
                    .dataFetcher("count", tagF.getTagCountDF())
                    ) 
                .build();
    }
}
