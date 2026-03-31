package web.controller;

import web.model.dto.RegistrationRequest;
import web.persistence.models.UserEntity;
import web.service.UserService;
import web.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final GameService gameService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "register";
    }

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserEntity user, Model model) {
        model.addAttribute("user", user);

        // Calculate user statistics
        var games = gameService.findAllByUser(user);
        long totalHands = games.size();
        long winningHands = games.stream()
                .filter(g -> g.getHeroWinloss() != null && g.getHeroWinloss().doubleValue() > 0)
                .count();
        double totalWinloss = games.stream()
                .mapToDouble(g -> g.getHeroWinloss() != null ? g.getHeroWinloss().doubleValue() : 0)
                .sum();
        double winRate = totalHands > 0 ? (winningHands * 100.0 / totalHands) : 0;

        model.addAttribute("totalHands", totalHands);
        model.addAttribute("winningHands", winningHands);
        model.addAttribute("totalWinloss", BigDecimal.valueOf(totalWinloss));
        model.addAttribute("winRate", Math.round(winRate * 10.0) / 10.0);

        // Check subscription status
        boolean hasSubscription = user.getRoles().stream()
                .anyMatch(r -> r.getName() == web.persistence.models.RoleEntity.RoleName.ROLE_USER_SUBSCRIBED);
        model.addAttribute("hasSubscription", hasSubscription);

        // Recent games
        model.addAttribute("recentGames", games.stream().limit(5).toList());

        return "profile";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}