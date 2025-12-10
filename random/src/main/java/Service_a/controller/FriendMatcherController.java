package Service_a.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FriendMatcherController {

    private final List<String> people = new ArrayList<>(List.of(
        "Alice","Bob","Charlie"
        // extend list as needed
    ));

    // Precomputed mapping: person -> friend
    private final Map<String, String> friendMap = new HashMap<>();

    // Track who has already drawn
    private final Set<String> drawnPeople = new HashSet<>();

    public FriendMatcherController() {
        generateDerangement();
    }

    private void generateDerangement() {
        List<String> shuffled = new ArrayList<>(people);
        Random random = new Random();

        boolean valid = false;
        while (!valid) {
            Collections.shuffle(shuffled, random);
            valid = true;
            for (int i = 0; i < people.size(); i++) {
                if (people.get(i).equalsIgnoreCase(shuffled.get(i))) {
                    valid = false; // someone matched with themselves
                    break;
                }
            }
        }

        // Build mapping
        friendMap.clear();
        for (int i = 0; i < people.size(); i++) {
            friendMap.put(people.get(i), shuffled.get(i));
        }
    }

    @PostMapping("/match")
    public String matchFriend(@RequestParam("name") String name, Model model) {
        // Normalize input (case-insensitive)
        String normalizedName = people.stream()
                .filter(p -> p.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);

        if (normalizedName == null) {
            model.addAttribute("message", "Name not found in the group!");
            return "index";
        }

        // ✅ New feature: check if already drawn
        if (drawnPeople.contains(normalizedName)) {
            model.addAttribute("message", "Already Drawn");
            return "index";
        }

        // Get assigned friend from precomputed mapping
        String friend = friendMap.get(normalizedName);

        // Mark this person as having drawn
        drawnPeople.add(normalizedName);

        model.addAttribute("message", normalizedName + " is matched with " + friend + "!");
        return "index";
    }
}
