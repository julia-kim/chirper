package com.julia.chirper.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.julia.chirper.model.Chirp;
import com.julia.chirper.model.User;
import com.julia.chirper.service.ChirpService;
import com.julia.chirper.service.UserService;

@Controller
public class ChirpController {
	@Autowired
	private UserService userService;

	@Autowired
	private ChirpService chirpService;

	@GetMapping(value = {"/chirps", "/"})
	public String getFeed(Model model) {
		List<Chirp> chirps = chirpService.findAll();
		model.addAttribute("chirpList", chirps);
		return "feed";
	}

	@GetMapping(value = "/chirps/new")
	public String getChirpForm(Model model) {
		model.addAttribute("chirp", new Chirp());
		return "newChirp";
	}

	@PostMapping(value = "/chirps")
	public String submitChirpForm(@Valid Chirp chirp, BindingResult bindingResult, Model model) {
		User user = userService.getLoggedInUser();
		if (!bindingResult.hasErrors()) {
			chirp.setUser(user);
			chirpService.save(chirp);
			model.addAttribute("successMessage", "Chirp successfully created!");
			model.addAttribute("chirp", new Chirp());
		}
		return "newChirp";
	}
}