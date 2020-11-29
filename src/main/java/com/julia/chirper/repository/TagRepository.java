package com.julia.chirper.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.julia.chirper.model.Tag;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long>  {
	Tag findByPhrase(String phrase);
}