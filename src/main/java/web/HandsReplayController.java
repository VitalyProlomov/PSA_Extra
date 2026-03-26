package web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pokerlibrary.models.Game;
import pokerlibrary.parsers.gg.GGPokerokHoldem9MaxParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Date;

@Controller
public class HandsReplayController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    @GetMapping("/hands")
    public String hands() {
        return "hands";
    }

    @PostMapping("/upload")
    public String viewHand(@RequestParam("fileToUpload") MultipartFile file,
                           Model model) throws IOException {
        if (file.isEmpty()) {
            model.addAttribute("errorMessage", "Please select a file to upload.");
            return "hands";
        }

        byte[] bytes = file.getBytes();
        String fileContent = new String(bytes, StandardCharsets.UTF_8);

        try {
            GGPokerokHoldem9MaxParser parser = new GGPokerokHoldem9MaxParser();
            Game game = parser.parseGame(fileContent);
            model.addAttribute("game", game);

            // Serialize game to JSON for JavaScript
            model.addAttribute("gameJson", objectMapper.writeValueAsString(game));

            model.addAttribute("heroWinLossFormatted", formatMoney(game.getHeroWinloss()));
            model.addAttribute("totalPotFormatted", formatMoney(game.getFinalPot()));
            model.addAttribute("playersPostFlop", game.getPreFlop().getPlayersAfterBetting().size());

            String potType = calculatePotType(game);
            model.addAttribute("potType", potType);

            model.addAttribute("formattedDate", formatDate(game.getDate()));

        } catch (Exception ex) {
            model.addAttribute("errorMessage", "File was incorrect, it must be a text file with a game from GGPokerok");
            return "hands";
        }

        return "view-hand";
    }

    private String calculatePotType(Game game) {
        if (pokerlibrary.analizer.GameAnalyzer.isPotUnRaised(game)) {
            return "unraised";
        } else if (pokerlibrary.analizer.GameAnalyzer.isPotSingleRaised(game)) {
            return "single raised";
        } else if (pokerlibrary.analizer.GameAnalyzer.isPot3Bet(game)) {
            return "3bet";
        } else if (pokerlibrary.analizer.GameAnalyzer.isPot4Bet(game)) {
            return "4bet";
        } else if (pokerlibrary.analizer.GameAnalyzer.isPot5PlusBet(game)) {
            return "5+ bet";
        }
        return "undefined";
    }

    private String formatMoney(double amount) {
        return decimalFormat.format(amount).replace(',', '.') + "$";
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        String[] s = date.toString().split(" ");
        return s[1] + " " + s[2] + " " + s[5];
    }
}