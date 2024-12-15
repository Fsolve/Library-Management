package com.ensam.library.authen.service.controller;

import com.ensam.library.authen.service.dto.RegisterRequest;
import com.ensam.library.authen.service.model.User;
import com.ensam.library.authen.service.security.JwtTokenProvider;
import com.ensam.library.authen.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserViewController {

    private final UserService userService;

    @Autowired
    public UserViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            User user = (User) auth.getPrincipal();
            model.addAttribute("user", user);
        }
        return "index";
    }
    @GetMapping("/login")
    public String loginPage(Model model, String error, String logout) {
        if (error != null) {
            model.addAttribute("error", "Identifiants invalides.");
        }
        if (logout != null) {
            model.addAttribute("message", "Vous avez été déconnecté avec succès.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @GetMapping("/profile")
    public String profilePage(Model model) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println(auth);
//        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
//            return "redirect:/login";
//        }

//        User currentUser = (User) auth.getPrincipal();
//        System.out.println(currentUser);
//        model.addAttribute("user", currentUser);
        return "user/profile";
    }

    @GetMapping("/logout")
    public String logoutPage(Model model) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
//            return "redirect:/login";
//        }
        return "auth/logout";
    }

//    @GetMapping("/users/{userId}")
//    public String userDetails(@PathVariable Long userId, Model model, RedirectAttributes redirectAttributes) {
//        try {
//            UserDto user = userService.getUserById(userId);
//            model.addAttribute("user", user);
//            return "user/details";
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Utilisateur non trouvé");
//            return "redirect:/";
//        }
//    }
//
//    @GetMapping("/dashboard")
//    public String dashboard(Model model) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
//            return "redirect:/login";
//        }
//
//        User currentUser = (User) auth.getPrincipal();
//        model.addAttribute("user", currentUser);
//
//        // Ajoutez ici la logique pour récupérer les statistiques ou les informations
//        // nécessaires pour le tableau de bord
//        return "dashboard";
//    }
//
//    @GetMapping("/access-denied")
//    public String accessDenied() {
//        return "error/access-denied";
//    }
//
//    // Méthode utilitaire pour vérifier si l'utilisateur est connecté
//    private boolean isAuthenticated() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        return auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
//    }
}
