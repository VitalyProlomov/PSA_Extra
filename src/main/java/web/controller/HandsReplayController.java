package web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pokerlibrary.models.Game;
import pokerlibrary.parsers.gg.GGPokerokHoldem9MaxParser;
import web.model.dto.GameDTO;

import java.nio.charset.StandardCharsets;

@Controller
public class HandsReplayController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/hands")
    public String hands() {
        return "hands";
    }

    @PostMapping("/upload")
    public String viewHand(@RequestParam("fileToUpload") MultipartFile file,
                           Model model) {
        if (file.isEmpty()) {
            model.addAttribute("errorMessage", "Please select a file to upload.");
            return "hands";
        }

        try {
            byte[] bytes = file.getBytes();
            String fileContent = new String(bytes, StandardCharsets.UTF_8);

            GGPokerokHoldem9MaxParser parser = new GGPokerokHoldem9MaxParser();
            Game game = parser.parseGame(fileContent);

            // Convert to DTO
            GameDTO gameDTO = new GameDTO(game);
            model.addAttribute("game", gameDTO);

            // Serialize DTO to JSON for JavaScript
            String gameJson = objectMapper.writeValueAsString(gameDTO);
            System.out.println("=== GENERATED JSON ===");
            System.out.println(gameJson);
            System.out.println("=== END JSON ===");
            model.addAttribute("gameJson", gameJson);

        } catch (Exception ex) {
            ex.printStackTrace();
            model.addAttribute("errorMessage", "File was incorrect: " + ex.getMessage());
            return "hands";
        }

        return "view-hand";
    }
}