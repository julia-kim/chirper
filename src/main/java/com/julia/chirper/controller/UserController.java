package com.julia.chirper.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.julia.chirper.model.Chirp;
import com.julia.chirper.model.User;
import com.julia.chirper.service.ChirpService;
import com.julia.chirper.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private ChirpService chirpService;
	
	@GetMapping(value = "/users/{username}")
    public String getUser(@PathVariable(value = "username") String username, Model model) {
        User loggedInUser = userService.getLoggedInUser();
        User user = userService.findByUsername(username);
        List<Chirp> chirps = chirpService.findAllByUser(user);
        List<User> following = loggedInUser.getFollowing();
        boolean isFollowing = false;
        for (User followedUser : following) {
            if (followedUser.getUsername().equals(username)) {
                isFollowing = true;
            }
        }
        model.addAttribute("following", isFollowing);
        model.addAttribute("chirpList", chirps);
        model.addAttribute("user", user);
        return "user";
    }
	
//	@GetMapping(value = "/users")
//	public String getUsers(Model model) {
//	    List<User> users = userService.findAll();
//	    model.addAttribute("users", users);
//	    SetChirpCounts(users, model);
//	    return "users";
//	}
//	
//	private void SetChirpCounts(List<User> users, Model model) {
//	    HashMap<String,Integer> chirpCounts = new HashMap<>();
//	    for (User user : users) {
//	        List<Chirp> chirps = chirpService.findAllByUser(user);
//	        chirpCounts.put(user.getUsername(), chirps.size());
//	    }
//	    model.addAttribute("chirpCounts", chirpCounts);
//	}
}