package com.example.gateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class GatewayController {

    // Страница логина
    @GetMapping("/login")
    public String loginPage() {
        return "login";  // Имя JSP страницы login.jsp
    }

    // Страница dashboard
    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        // Здесь вы можете добавить логику для получения данных из микросервисов
        model.addAttribute("films", getFilmsFromService());
        return "dashboard";  // Имя JSP страницы dashboard.jsp
    }

    private List<String> getFilmsFromService() {
        // Получение данных от микросервисов, например, с использованием RestTemplate
        // Это пример, адаптировать под ваш реальный API
        return List.of("Film 1", "Film 2", "Film 3");
    }
}
