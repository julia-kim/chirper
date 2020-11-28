package com.julia.chirper.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.julia.chirper.model.Chirp;
import com.julia.chirper.model.User;
import com.julia.chirper.repository.ChirpRepository;

@Service
public class ChirpService {

    @Autowired
    private ChirpRepository chirpRepository;

    public List<Chirp> findAll() {
        List<Chirp> chirps = chirpRepository.findAllByOrderByCreatedAtDesc();
        return chirps;
    }
	
    public List<Chirp> findAllByUser(User user) {
        List<Chirp> chirps = chirpRepository.findAllByUserOrderByCreatedAtDesc(user);
        return chirps;
    }
	
    public List<Chirp> findAllByUsers(List<User> users){
        List<Chirp> chirps = chirpRepository.findAllByUserInOrderByCreatedAtDesc(users);
        return chirps;
    }
	
    public void save(Chirp chirp) {
        chirpRepository.save(chirp);
    }
}
