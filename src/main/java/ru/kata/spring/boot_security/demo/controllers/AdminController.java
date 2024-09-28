package ru.kata.spring.boot_security.demo.controllers;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;


    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping("/users")
    public String showUsers(Model model) {
        model.addAttribute("users", userService.getUsers());
        return "admin";
    }

    @GetMapping("/newUser")
    public String showNewUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.findAll()); // добавлено для получения всех ролей
        return "newUser";
    }

    @PostMapping("/newUser")
    public String create(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleService.findAll());
            return "newUser";
        }
        userService.createUser(user);
        return "redirect:/admin/users";
    }


    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }


    @GetMapping("/update/{id}")
    public String updateUserForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(id);

        if (user.getId() == null) {

            redirectAttributes.addFlashAttribute("msgError", "User with ID " + id + " is not found.");
            return "redirect:/admin/users";
        }

        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.findAll());

        return "userupdate";
    }


    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id, @Valid @ModelAttribute User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.findAll());
            return "userupdate";
        }
        userService.updateUser(id, user);
        return "redirect:/admin/users";
    }
}
