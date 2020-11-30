package com.julia.chirper.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.julia.chirper.model.Chirp;
import com.julia.chirper.model.Tag;
import com.julia.chirper.model.User;
import com.julia.chirper.repository.ChirpRepository;
import com.julia.chirper.repository.TagRepository;

@Service
public class ChirpService {

	@Autowired
	private ChirpRepository chirpRepository;

	@Autowired
	private TagRepository tagRepository;

	public List<Chirp> findAll() {
		List<Chirp> chirps = chirpRepository.findAllByOrderByCreatedAtDesc();
		return formatChirps(chirps);
	}

	public List<Chirp> findAllByUser(User user) {
		List<Chirp> chirps = chirpRepository.findAllByUserOrderByCreatedAtDesc(user);
		return chirps;
	}

	public List<Chirp> findAllByUsers(List<User> users) {
		List<Chirp> chirps = chirpRepository.findAllByUserInOrderByCreatedAtDesc(users);
		return chirps;
	}

	public void save(Chirp chirp) {
		handleTags(chirp);
		chirpRepository.save(chirp);
	}

	public List<Chirp> findAllWithTag(String tag) {
		List<Chirp> chirps = chirpRepository.findByTags_PhraseOrderByCreatedAtDesc(tag);
		return formatChirps(chirps);
	}

	private void handleTags(Chirp chirp) {
		List<Tag> tags = new ArrayList<Tag>();
		Pattern pattern = Pattern.compile("#\\w+");
		Matcher matcher = pattern.matcher(chirp.getMessage());
		while (matcher.find()) {
			String phrase = matcher.group().substring(1).toLowerCase();
			Tag tag = tagRepository.findByPhrase(phrase);
			if (tag == null) {
				tag = new Tag();
				tag.setPhrase(phrase);
				tagRepository.save(tag);
			}
			tags.add(tag);
		}
		chirp.setTags(tags);
	}

	private List<Chirp> formatChirps(List<Chirp> chirps) {
		addTagLinks(chirps);
		shortenLinks(chirps);
		return chirps;
	}

	private void addTagLinks(List<Chirp> chirps) {
		Pattern pattern = Pattern.compile("#\\w+");
		for (Chirp chirp : chirps) {
			String message = chirp.getMessage();
			Matcher matcher = pattern.matcher(message);
			Set<String> tags = new HashSet<String>();
			while (matcher.find()) {
				tags.add(matcher.group());
			}
			for (String tag : tags) {
				message = message.replaceAll(tag,
						"<a class=\"tag\" href=\"/chirps/" + tag.substring(1).toLowerCase() + "\">" + tag + "</a>");
			}
			chirp.setMessage(message);
		}
	}

	private void shortenLinks(List<Chirp> chirps) {
		Pattern pattern = Pattern.compile("https?[^ ]+");
		for (Chirp chirp : chirps) {
			String message = chirp.getMessage();
			Matcher matcher = pattern.matcher(message);
			while (matcher.find()) {
				String link = matcher.group();
				String shortenedLink = link;
				if (link.length() > 23) {
					shortenedLink = link.substring(0, 20) + "...";
					message = message.replace(link,
							"<a class=\"tag\" href=\"" + link + "\" target=\"_blank\">" + shortenedLink + "</a>");
				}
				chirp.setMessage(message);
			}

		}
	}
}
