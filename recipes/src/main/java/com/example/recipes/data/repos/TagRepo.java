package com.example.recipes.data.repos;

import java.util.List;

import com.example.recipes.data.entity.Tag;

import org.springframework.data.repository.CrudRepository;

public interface TagRepo  extends CrudRepository<Tag,Long>{
    Boolean existsByName(String name);
    Tag findFirstByName(String name);
    List<Tag> findByNameContaining(String name);
}
