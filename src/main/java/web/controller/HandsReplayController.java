package web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pokerlibrary.exceptions.IncorrectBoardException;
import pokerlibrary.exceptions.IncorrectCardException;
import pokerlibrary.exceptions.IncorrectHandException;
import pokerlibrary.models.*;
import pokerlibrary.parsers.gg.GGPokerokHoldem9MaxParser;
import web.persistence.models.GameEntity;
import web.persistence.models.UserEntity;
import web.service.GameService;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("/hands")
@RequiredArgsConstructor
public class HandsReplayController {

    private final GameService gameService;
    private final GGPokerokHoldem9MaxParser parser;

    // ===== PAGE: List Hands =====
    @GetMapping
    public String handsPage(@AuthenticationPrincipal UserEntity user, Model model) {
        var games = gameService.findAllByUser(user);
        model.addAttribute("games", games);

        model.addAttribute("totalHands", games.size());
        model.addAttribute("winningHands", games.stream()
                .filter(g -> g.getHeroWinloss() != null && g.getHeroWinloss().doubleValue() > 0)
                .count());
        model.addAttribute("totalWinloss", games.stream()
                .mapToDouble(g -> g.getHeroWinloss() != null ? g.getHeroWinloss().doubleValue() : 0)
                .sum());
        model.addAttribute("recentGames", games.stream().limit(5).toList());

        return "hands";
    }

    // ===== ACTION: Upload Hand =====
    @PostMapping("/upload")
    public String uploadHand(@RequestParam("fileToUpload") MultipartFile file,
                             @AuthenticationPrincipal UserEntity user,
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/hands";
        }

        try {
            byte[] bytes = file.getBytes();
            String rawText = new String(bytes, StandardCharsets.UTF_8);
            gameService.saveGame(user, rawText);
            redirectAttributes.addFlashAttribute("successMessage", "Hand uploaded successfully!");
            return "redirect:/hands";

        } catch (IncorrectHandException | IncorrectBoardException | IncorrectCardException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid hand format: " + e.getMessage());
            return "redirect:/hands";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/hands";
        }
    }

    // ===== PAGE: View/Replay Hand =====
    @GetMapping("/view/{gameId}")
    public String viewHand(@PathVariable String gameId,
                           @AuthenticationPrincipal UserEntity user,
                           Model model) {
        try {
            GameEntity gameEntity = gameService.findByUserAndGameId(user, gameId);

            // ✅ Parse using your injected parser
            Game pokerGame = parser.parseGame(gameEntity.getRawHandText());

            // ✅ Convert to JSON-safe Map using ACTUAL fields from your models
            Map<String, Object> gameJson = convertGameToSerializableMap(pokerGame);

            model.addAttribute("game", gameEntity);
            model.addAttribute("gameJson", gameJson);
            model.addAttribute("potType", gameEntity.getPotType());
            // Format date
            if (gameEntity.getDatePlayed() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                model.addAttribute("formattedDate", gameEntity.getDatePlayed().format(formatter));
            } else {
                model.addAttribute("formattedDate", "Unknown");
            }

            // Format money
            model.addAttribute("heroWinLossFormatted", formatMoney(gameEntity.getHeroWinloss()));
            model.addAttribute("totalPotFormatted", formatMoney(gameEntity.getFinalPot()));

            // Players post-flop count
            int playersPostFlop = (pokerGame.getFlop() != null)
                    ? pokerGame.getFlop().getPlayersAfterBetting().size()
                    : 0;
            model.addAttribute("playersPostFlop", playersPostFlop);

            return "replay";

        } catch (IllegalArgumentException e) {
            return "redirect:/hands?error=game-not-found";
        } catch (IncorrectHandException | IncorrectBoardException | IncorrectCardException e) {
            return "redirect:/hands?error=parse-error&msg=" + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/hands?error=internal-error";
        }
    }

    // ===== ACTION: Delete Hand =====
    @PostMapping("/delete/{id}")
    public String deleteHand(@PathVariable Long id,
                             @AuthenticationPrincipal UserEntity user,
                             RedirectAttributes redirectAttributes) {
        try {
            gameService.deleteGame(user, id);
            redirectAttributes.addFlashAttribute("successMessage", "Hand deleted successfully!");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Not authorized");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting: " + e.getMessage());
        }
        return "redirect:/hands";
    }

    // ===== SERIALIZATION: Game → JSON-safe Map =====

    /**
     * Convert pokerlibrary Game object to Map for safe Thymeleaf JS inlining
     * Uses ONLY actual fields/methods from your provided model classes
     */
    private Map<String, Object> convertGameToSerializableMap(Game game) {
        Map<String, Object> json = new LinkedHashMap<>();

        // Basic fields (using actual getters)
        json.put("gameId", game.getGameId());
        json.put("finalPot", game.getFinalPot());
        json.put("bigBlindSize$", game.getBigBlindSize$());
        json.put("gameType", game.getGameType() != null ? game.getGameType().name() : "UNKNOWN");
        json.put("heroWinloss", game.getHeroWinloss());
        json.put("date", game.getDate() != null ? game.getDate().toString() : null);

        // Community cards from Board
        List<Map<String, String>> board = new ArrayList<>();
        if (game.getRiver() != null && game.getRiver().getBoard() != null) {
            // Use river board (contains all community cards)
            for (Card card : game.getRiver().getBoard().getCards()) {
                if (card != null && card.getRank() != null && card.getSuit() != null) {
                    board.add(Map.of(
                            "rank", card.getRank().name(),
                            "suit", card.getSuit().name()
                    ));
                }
            }
        } else if (game.getTurn() != null && game.getTurn().getBoard() != null) {
            for (Card card : game.getTurn().getBoard().getCards()) {
                if (card != null && card.getRank() != null && card.getSuit() != null) {
                    board.add(Map.of(
                            "rank", card.getRank().name(),
                            "suit", card.getSuit().name()
                    ));
                }
            }
        } else if (game.getFlop() != null && game.getFlop().getBoard() != null) {
            for (Card card : game.getFlop().getBoard().getCards()) {
                if (card != null && card.getRank() != null && card.getSuit() != null) {
                    board.add(Map.of(
                            "rank", card.getRank().name(),
                            "suit", card.getSuit().name()
                    ));
                }
            }
        }
        json.put("board", board);
        json.put("communityCards", board);

        // Players (only expose Hero's hole cards)
        List<Map<String, Object>> players = new ArrayList<>();
        if (game.getPlayers() != null) {
            for (PlayerInGame player : game.getPlayers().values()) {
                if (player != null) {
                    Map<String, Object> p = new LinkedHashMap<>();
                    p.put("id", player.getId());
                    p.put("position", player.getPosition() != null ? player.getPosition().name() : "");
                    p.put("balance", player.getBalance());

                    // Only include hole cards for Hero
                    if ("Hero".equals(player.getId()) && player.getHand() != null) {
                        List<Map<String, String>> cards = new ArrayList<>();
                        if (player.getHand().getCard1() != null) {
                            cards.add(Map.of(
                                    "rank", player.getHand().getCard1().getRank().name(),
                                    "suit", player.getHand().getCard1().getSuit().name()
                            ));
                        }
                        if (player.getHand().getCard2() != null) {
                            cards.add(Map.of(
                                    "rank", player.getHand().getCard2().getRank().name(),
                                    "suit", player.getHand().getCard2().getSuit().name()
                            ));
                        }
                        p.put("cards", cards);
                    }
                    players.add(p);
                }
            }
        }
        json.put("players", players);

        // ✅ Extract actions from all streets using actual methods
        List<Map<String, Object>> actions = extractActionsFromGame(game);
        json.put("actions", actions);
        json.put("handActions", actions);

        return json;
    }

    /**
     * Extract ALL actions from PreFlop, Flop, Turn, River in chronological order
     * Uses ONLY actual getters from your StreetDescription and Action classes
     */
    private List<Map<String, Object>> extractActionsFromGame(Game game) {
        List<Map<String, Object>> actions = new ArrayList<>();
        AtomicInteger sequence = new AtomicInteger();

        // Helper lambda to extract actions from a street
        java.util.function.BiConsumer<StreetDescription, String> extractFromStreet = (street, streetName) -> {
            if (street != null && street.getAllActions() != null) {
                for (Action action : street.getAllActions()) {
                    if (action != null) {
                        Map<String, Object> actionMap = new LinkedHashMap<>();
                        actionMap.put("sequence", sequence.getAndIncrement());
                        actionMap.put("actor", action.getPlayerId());  // ← actual getter
                        actionMap.put("actionType", action.getActionType() != null ? action.getActionType().name() : "UNKNOWN");
                        actionMap.put("amount", action.getAmount());    // ← actual getter
                        actionMap.put("potBeforeAction", action.getPotBeforeAction());  // ← actual getter
                        actionMap.put("street", streetName);

                        // Include shown cards if this is a SHOW-type action (we infer from context)
                        // Note: Your Action.ActionType doesn't have SHOW/WIN, so we handle winners separately
                        actions.add(actionMap);
                    }
                }
            }
        };

        // Extract in chronological order
        if (game.getPreFlop() != null) extractFromStreet.accept(game.getPreFlop(), "PREFLOP");
        if (game.getFlop() != null) extractFromStreet.accept(game.getFlop(), "FLOP");
        if (game.getTurn() != null) extractFromStreet.accept(game.getTurn(), "TURN");
        if (game.getRiver() != null) extractFromStreet.accept(game.getRiver(), "RIVER");

        // Add WIN actions from game winners (using actual getWinners() method)
        if (game.getWinners() != null) {
            for (Map.Entry<String, Double> winner : game.getWinners().entrySet()) {
                Map<String, Object> winAction = new LinkedHashMap<>();
                winAction.put("sequence", sequence.getAndIncrement());
                winAction.put("actor", winner.getKey());
                winAction.put("actionType", "WIN");
                winAction.put("amount", winner.getValue());
                winAction.put("street", "SHOWDOWN");
                actions.add(winAction);
            }
        }

        return actions;
    }

    /**
     * Format money with $ prefix and +/- sign for win/loss
     */
    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "$0.00";
        double value = amount.doubleValue();
        String sign = value >= 0 ? "+" : "";
        return sign + "$" + String.format("%.2f", Math.abs(value));
    }

    // ===== DEBUG ENDPOINT (remove in production) =====
    @GetMapping("/debug/{gameId}")
    @ResponseBody
    public Map<String, Object> debugGame(@PathVariable String gameId,
                                         @AuthenticationPrincipal UserEntity user) throws IncorrectHandException, IncorrectBoardException, IncorrectCardException {
        GameEntity entity = gameService.findByUserAndGameId(user, gameId);
        Game pokerGame = parser.parseGame(entity.getRawHandText());
        return convertGameToSerializableMap(pokerGame);
    }
}