package com.julia.chirper.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.julia.chirper.model.User;
import com.julia.chirper.service.UserService;

@Controller
public class AuthorizationController {
	@Autowired
	private UserService userService;

	@GetMapping(value = "/")
	public String landing(Model model) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			return "redirect:/home";
		}
		model.addAttribute("title", "Chirper");
		return "index";
	}

	@GetMapping(value = "/login")
	public String login(Model model) {
		model.addAttribute("title", "Login | Chirper");
		return "login";
	}

	@GetMapping(value = "/signup")
	public String registration(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("title", "Signup | Chirper");
		return "registration";
	}

	@PostMapping(value = "/signup")
	public String createNewUser(@Valid User user, BindingResult bindingResult, Model model) {
		User userExists = userService.findByUsername(user.getUsername());
		if (userExists != null) {
			bindingResult.rejectValue("username", "error.user", "Username is already taken");
		}
		if (!bindingResult.hasErrors()) {
			userService.saveNewUser(user);
			model.addAttribute("success", "Sign up successful!");
			model.addAttribute("user", new User());
			model.addAttribute("title", "Signup Success | Chirper");
		}
		return "registration";
	}
}