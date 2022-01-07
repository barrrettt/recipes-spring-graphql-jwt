package com.example.recipes.data.repos;

import com.example.recipes.data.entity.Role;

import org.springframework.data.repository.CrudRepository;

public interface RolesRepo  extends CrudRepository<Role,Long> {
    Role findFirstByName(String name);
    Boolean existsByName(String name);
}
