package com.example.recipes.graphql.dataFetcher;
import java.util.ArrayList;
import java.util.List;

import com.example.recipes.data.entity.Recipe;
import com.example.recipes.data.entity.Tag;
import com.example.recipes.data.entity.User;
import com.example.recipes.data.repos.RecipeRepo;
import com.example.recipes.data.repos.RolesRepo;
import com.example.recipes.data.repos.TagRepo;
import com.example.recipes.data.repos.UserRepo;
import com.example.recipes.security.SecurityDataFetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;

@Component
public class RecipeDF {
    
    @Autowired
    UserRepo userR;

    @Autowired
    RolesRepo rolesR;

    @Autowired
    RecipeRepo recipeR;

    @Autowired
    TagRepo tagR;

    @Autowired
    SecurityDataFetcher auxSecurityDF; //my security nologic

    // all recipes
    public DataFetcher<List<Recipe>> getRecipeDF() {
        return dataFetchingEnvironment -> {

            //security: NO ANONYMOUS
            auxSecurityDF.noAnomimous();

            //query
            String id = dataFetchingEnvironment.getArgument("id");
            String name = dataFetchingEnvironment.getArgument("name");

            List<Recipe>recipes = new ArrayList<>();

            //if ID only one on list
            if (id!=null){
                Long rid;
                try{
                    rid = Long.parseLong(id);
                }catch(NumberFormatException e){
                    return null;
                }
                
                Recipe recipe = recipeR.findById(rid).orElse(null);
                if (recipe != null) recipes.add(recipe);
                return recipes;
            }

            //sin nada devolvemos todo
            if (id==null && name==null){
                for(Recipe recipe : recipeR.findAll()){
                    recipes.add(recipe);
                }
                return recipes;
            }

            //findByName
            for(Recipe r :recipeR.findByNameContaining(name)){
                recipes.add(r);
            }

            return recipes;
        };
    }

    //mutations: SUPER or Owner
    public DataFetcher <Recipe> createRecipeDF(){
        return dataFetchingEnvironment -> {

            //get author
            String authorID = dataFetchingEnvironment.getArgument("autorID");
            User author =  UserDF.getUserById(authorID,userR);
            if (author == null) throw new Exception("User null with id: "+authorID);

            //author only User rol
            if (!author.getRoles().contains(rolesR.findFirstByName("USER"))) throw new Exception("Recipe author must have a user role");

            //security: SuperUser or Me
            auxSecurityDF.onlySUPERorOWNER(author);

            //recipe name unique?
            String name = dataFetchingEnvironment.getArgument("name");
            int size = recipeR.findByName(name).size();
            if (size>0) throw new Exception("Recipe name in use.");

            //valide tags
            ArrayList<String> tagnames = dataFetchingEnvironment.getArgument("tags");
            ArrayList<Tag> tags = TagDF.valideAndGetTags(tagnames, tagR);
            
            //save recipe
            Recipe recipe = new Recipe(name,author);
            recipe.setIngredients(dataFetchingEnvironment.getArgument("ingredients"));
            recipe.setInstruccions(dataFetchingEnvironment.getArgument("instruccions"));
            recipe = recipeR.save(recipe);

            //asing tags
            if (tags!=null && tags.size()>0){
                recipe.setTags(tags);
            }
            return recipeR.save(recipe);
        };
    }

    public DataFetcher <Recipe> updateRecipeDF(){
        return dataFetchingEnvironment -> {
            //get recipe
            String strID = dataFetchingEnvironment.getArgument("id");
            Recipe recipe = RecipeDF.getRecipeById(strID, recipeR);
            if (recipe==null)return null;

            //get author
            String authorID = dataFetchingEnvironment.getArgument("autorID");
            User author =  UserDF.getUserById(authorID,userR);

            if (author != null){
                 //author only User rol
                if (!author.getRoles().contains(rolesR.findFirstByName("USER"))) throw new Exception("Recipe author must have a user role");
                //security: Only super can change author
                auxSecurityDF.onlySUPER();
            }else{
                //security: SuperUser or Me
                auxSecurityDF.onlySUPERorOWNER(author);
            }

            //valide tags first
            ArrayList<String> tagnames = dataFetchingEnvironment.getArgument("tags");
            ArrayList<Tag> tags = TagDF.valideAndGetTags(tagnames, tagR);

            //recipe new name free?
            String name = dataFetchingEnvironment.getArgument("name");
            if (name!=null){
                if (!name.equals(recipe.getName())){
                    if (recipeR.existsByName(name)) throw new Exception("Recipe name in use.");
                    recipe.setName(name);
                }
            }
            
            //set data
            String ingredientes = dataFetchingEnvironment.getArgument("ingredients");
            if (ingredientes!=null) recipe.setIngredients(ingredientes);

            String instruccions = dataFetchingEnvironment.getArgument("instruccions");
            if (ingredientes!=null) recipe.setInstruccions(instruccions);

            // asing tags
            if (tags!=null && tags.size()>0){
                recipe.setTags(tags);
            }

            // save
            return  recipeR.save(recipe);
        };
    }

    public DataFetcher <Recipe> deleteRecipeDF(){ 
        return dataFetchingEnvironment -> {
            //Get recipe
            String strID = dataFetchingEnvironment.getArgument("id");
            Recipe recipe = RecipeDF.getRecipeById(strID, recipeR);

            if (recipe!=null) {
                //security: SuperUser or Me
                auxSecurityDF.onlySUPERorOWNER(recipe.getAutor());
                 // Finally delete
                recipeR.delete(recipe);
            }
            return recipe;
        };
    }
    
    //AUX
    public static Recipe getRecipeById(String strId, RecipeRepo recipeR){
        if (strId == null)return null;
        long id;

        try{
            id = Long.parseLong(strId);
        }catch(NumberFormatException e){
            return null;
        }

        Recipe recipe = recipeR.findById(id).orElse(null);
        return recipe;
    }
}
