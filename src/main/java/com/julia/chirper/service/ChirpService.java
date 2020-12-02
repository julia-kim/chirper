package com.julia.chirper.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.julia.chirper.model.Chirp;
import com.julia.chirper.model.ChirpDisplay;
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

	public List<ChirpDisplay> findAll() {
		List<Chirp> chirps = chirpRepository.findAllByOrderByCreatedAtDesc();
		return formatChirps(chirps);
	}

	public List<ChirpDisplay> findAllByUser(User user) {
		List<Chirp> chirps = chirpRepository.findAllByUserOrderByCreatedAtDesc(user);
		return formatChirps(chirps);
	}

	public List<ChirpDisplay> findAllByUsers(List<User> users) {
		List<Chirp> chirps = chirpRepository.findAllByUserInOrderByCreatedAtDesc(users);
		return formatChirps(chirps);
	}

	public List<ChirpDisplay> findAllWithTag(String tag) {
		List<Chirp> chirps = chirpRepository.findByTags_PhraseOrderByCreatedAtDesc(tag);
		return formatChirps(chirps);
	}

	public void save(Chirp chirp) {
		handleTags(chirp);
		chirpRepository.save(chirp);
	}

	private List<ChirpDisplay> formatChirps(List<Chirp> chirps) {
		addTagLinks(chirps);
		shortenLinks(chirps);
		List<ChirpDisplay> displayChirps = formatTimestamps(chirps);
		return displayChirps;
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

	private List<ChirpDisplay> formatTimestamps(List<Chirp> chirps) {
		List<ChirpDisplay> response = new ArrayList<>();
		PrettyTime prettyTime = new PrettyTime();
		SimpleDateFormat simpleDate = new SimpleDateFormat("M/d/yy");
		Date now = new Date();
		for (Chirp chirp : chirps) {
			ChirpDisplay chirpDisplay = new ChirpDisplay();
			chirpDisplay.setUser(chirp.getUser());
			chirpDisplay.setMessage(chirp.getMessage());
			chirpDisplay.setTags(chirp.getTags());
			long diffInMillies = Math.abs(now.getTime() - chirp.getCreatedAt().getTime());
			long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
			if (diff > 3) {
				chirpDisplay.setDate(simpleDate.format(chirp.getCreatedAt()));
			} else {
				chirpDisplay.setDate(prettyTime.format(chirp.getCreatedAt()));
			}
			response.add(chirpDisplay);
		}
		return response;
	}

}
