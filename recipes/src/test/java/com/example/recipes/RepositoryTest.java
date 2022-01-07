package com.example.recipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import com.example.recipes.data.entity.Role;
import com.example.recipes.data.entity.User;
import com.example.recipes.data.repos.RecipeRepo;
import com.example.recipes.data.repos.RolesRepo;
import com.example.recipes.data.repos.TagRepo;
import com.example.recipes.data.repos.UserRepo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RepositoryTest {
    
    @Autowired
    UserRepo userR;
    
    @Autowired
    RolesRepo rolesR;
    
    @Autowired
    TagRepo tagsR;
    
    @Autowired
    RecipeRepo recipesR;

    @Test
    public void userRepoTest(){
        User user = new User("Pepa","mail@mail.com","pass");
        userR.save(user);
        
        List<User> findByName = userR.findByName("Pepa");
        assertNotNull(findByName.get(0));

        userR.delete(findByName.get(0));
        findByName = userR.findByName("Pepa");
        assertEquals(0,findByName.size());
    }

    @Test
    public void rolesRepoTest(){
        rolesR.save(new Role("TEST"));
        Role findByName = rolesR.findFirstByName("TEST");
        assertNotNull(findByName);

        rolesR.delete(findByName);
        findByName = rolesR.findFirstByName("TEST");
        assertNull(findByName);
    }

}
