package com.example.recipes.data.repos;

import java.util.List;

import com.example.recipes.data.entity.User;

import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User,Long> {
    //exact
    Boolean existsByName(String username);
	Boolean existsByEmail(String email);
    User findFirstById(Long id);
    User findFirstByName(String name);
    List<User> findByName(String name);
    //like
    User findFirstByNameContaining(String name);
    List<User> findByNameContaining(String name);
}
