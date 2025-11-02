package org.example.farmapigateway;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import java.util.Map;
@RestController
@RequestMapping
public class Controller {
@GetMapping("/auth")
    public String profile(Model model, @AuthenticationPrincipal OidcUser principal) {
    return "index";
    }

    ///  geting user profile
    public String home(Model model, @AuthenticationPrincipal OidcUser principal) {
        if (principal!=null){
            model.addAttribute("profile", principal.getClaims());
        }
        return "index";
    }

}
