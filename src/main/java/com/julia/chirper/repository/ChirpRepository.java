package com.julia.chirper.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.julia.chirper.model.Chirp;
import com.julia.chirper.model.User;

@Repository
public interface ChirpRepository extends CrudRepository<Chirp, Long> {
	List<Chirp> findAllByOrderByCreatedAtDesc();

	List<Chirp> findAllByUserOrderByCreatedAtDesc(User user);

	List<Chirp> findAllByUserInOrderByCreatedAtDesc(List<User> users);

	List<Chirp> findByTags_PhraseOrderByCreatedAtDesc(String phrase);
}