package com.julia.chirper.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.julia.chirper.model.Chirp;
import com.julia.chirper.model.ChirpDisplay;
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
        List<ChirpDisplay> chirps = chirpService.findAllByUser(user);
        List<User> following = loggedInUser.getFollowing();
        boolean isFollowing = false;
        for (User followedUser : following) {
            if (followedUser.getUsername().equals(username)) {
                isFollowing = true;
            }
        }
        boolean isSelfPage = loggedInUser.getUsername().equals(username);
        model.addAttribute("isSelfPage", isSelfPage);
        model.addAttribute("following", isFollowing);
        model.addAttribute("chirpList", chirps);
        model.addAttribute("user", user);
        model.addAttribute("title", user.getFirstName() + " " + user.getLastName() + " (@" + user.getUsername() + ") | Chirper");
        return "user";
    }
	
	@GetMapping(value = "/users")
	public String getUsers(@RequestParam(value = "filter", required = false) String filter, Model model) {
		List<User> users = new ArrayList<User>();

		User loggedInUser = userService.getLoggedInUser();

		List<User> usersFollowing = loggedInUser.getFollowing();
		List<User> usersFollowers = loggedInUser.getFollowers();
		if (filter == null) {
			filter = "all";
		}
		if (filter.equalsIgnoreCase("followers")) {
			users = usersFollowers;
			model.addAttribute("filter", "followers");
		} else if (filter.equalsIgnoreCase("following")) {
			users = usersFollowing;
			model.addAttribute("filter", "following");
		} else {
			users = userService.findAll();
			model.addAttribute("filter", "all");
		}
		model.addAttribute("users", users);

		SetChirpCounts(users, model);
		SetFollowingStatus(users, usersFollowing, model);

		return "users";
	}
	
	private void SetChirpCounts(List<User> users, Model model) {
	    HashMap<String,Integer> chirpCounts = new HashMap<>();
	    for (User user : users) {
	        List<ChirpDisplay> chirps = chirpService.findAllByUser(user);
	        chirpCounts.put(user.getUsername(), chirps.size());
	    }
	    model.addAttribute("chirpCounts", chirpCounts);
	}
	
	private void SetFollowingStatus(List<User> users, List<User> usersFollowing, Model model) {
		HashMap<String, Boolean> followingStatus = new HashMap<>();
		String username = userService.getLoggedInUser().getUsername();
		for (User user : users) {
			if (usersFollowing.contains(user)) {
				followingStatus.put(user.getUsername(), true);
			} else if (!user.getUsername().equals(username)) {
				followingStatus.put(user.getUsername(), false);
			}
		}
		model.addAttribute("followingStatus", followingStatus);
	}
}