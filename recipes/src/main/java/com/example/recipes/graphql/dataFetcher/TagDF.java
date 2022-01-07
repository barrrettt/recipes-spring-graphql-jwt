package com.example.recipes.graphql.dataFetcher;

import java.util.ArrayList;
import java.util.List;

import com.example.recipes.data.entity.Recipe;
import com.example.recipes.data.entity.Tag;
import com.example.recipes.data.repos.RecipeRepo;
import com.example.recipes.data.repos.TagRepo;
import com.example.recipes.security.SecurityDataFetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;

@Component
public class TagDF {

    @Autowired
    TagRepo tagR;

    @Autowired
    RecipeRepo recipeR;

    @Autowired 
    SecurityDataFetcher auxSecurityDF; //my security unlogic
    
    // gets 
    public DataFetcher<List<Tag>> getTagDF() {
        return dataFetchingEnvironment -> {

            auxSecurityDF.noAnomimous();//security
            
            String id = dataFetchingEnvironment.getArgument("id");
            String name = dataFetchingEnvironment.getArgument("name");
            List<Tag> tags = new ArrayList<>();
            //byID (only 1)
            if (id!= null){
                Tag tag = TagDF.getTagByID(id, tagR);
                if (tag!=null){
                    tags.add(tag);
                }
                return tags;
            }
            //all
            if (id==null && name==null){
                for(Tag t:  tagR.findAll()){
                    tags.add(t);
                }
                return tags;
            }
            //find 
            for(Tag t : tagR.findByNameContaining(name)){
                tags.add(t);
            }
            return tags;
        };
    }

    public DataFetcher<Integer> getTagCountDF() {
        return dataFetchingEnvironment -> {
            Tag tag = dataFetchingEnvironment.getSource();
            int size = tag.getRecipes().size();
            return size;
        };
    }
    
    // mutations 
    public DataFetcher<Tag> createTagDF(){
        return dataFetchingEnvironment->{
            auxSecurityDF.onlySUPERorUSER();//security

            String name = dataFetchingEnvironment.getArgument("name");
            //unique?
            Tag tag = tagR.findFirstByName(name);
            if (tag!=null) throw new Exception("Tag name must be unique: " + name);
            
            //new
            tag = new Tag(name);
            tag = tagR.save(tag);
            return tag;
        };
    }

    public DataFetcher<Tag> updateTagDF(){
        return dataFetchingEnvironment->{
            
            auxSecurityDF.onlySUPER();//security

            //check if exist and get tag
            String strID = dataFetchingEnvironment.getArgument("id");
            Tag tag = TagDF.getTagByID(strID,tagR);
            if (tag==null)return null;

            //params
            String name = dataFetchingEnvironment.getArgument("name");
            if (name!=null){
                if (!name.equalsIgnoreCase(tag.getName())){
                    //unique?
                    Tag otherTag = tagR.findFirstByName(name);
                    if (otherTag!=null) throw new Exception("Tag name must be unique: " + name);

                    //change name
                    tag.setName(name);
                }
            }

            tagR.save(tag);
            return tag;
        };
    }

    public DataFetcher<Tag> deleteTagDF(){
        return dataFetchingEnvironment->{

            auxSecurityDF.onlySUPER();//security
            
            //check if exist and get tag 
            String strID = dataFetchingEnvironment.getArgument("id"); 
            Tag tag = TagDF.getTagByID(strID,tagR); 
            if (tag==null)return null; 
            
            //remove tag from recipes before delete tag
            List<Recipe> recipes = tag.getRecipes();
            for (Recipe recipe: recipes){
                recipe.getTags().remove(tag);
            }
            recipeR.saveAll(recipes);

            //delete 
            tagR.delete(tag); 
            return tag; 
        };
    }
 
    //AUX 
	public static Tag getTagByID(String strId, TagRepo tagR){
        if (strId == null) return null;
        long id;

        try{
            id = Long.parseLong(strId);
        }catch(NumberFormatException e){
            return null;
        }

        Tag tag = tagR.findById(id).orElse(null);
        return tag;
    }

	public static ArrayList<Tag> valideAndGetTags(ArrayList<String> names, TagRepo tagR) throws Exception{
		ArrayList<Tag> tags = null;
		if (names != null){
			tags = new ArrayList<Tag>();
			for (String tagname : names){
				Tag tag = tagR.findFirstByName(tagname);
				if (tag==null){
					throw new Exception("Tag no exist: "+ tagname);
				}else{
					tags.add(tag);
				}
			}
		}
		return tags;
	}

}
