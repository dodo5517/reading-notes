package me.dodo.readingnotes.controller;

import jakarta.validation.Valid;
import me.dodo.readingnotes.dto.LoginRequest;
import me.dodo.readingnotes.dto.LoginResponse;
import me.dodo.readingnotes.dto.UserRequest;
import me.dodo.readingnotes.dto.UserResponse;
import me.dodo.readingnotes.service.UserService;
import me.dodo.readingnotes.domain.User;
import org.slf4j.Logger; // java.util.logging.Logger 보다 세부 설정 가능.
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
            this.userService = userService; // controller에 service 의존성 주입
    }

    // post(일반 회원가입)
    @PostMapping
    public UserResponse registerUser(@RequestBody @Valid UserRequest request){ // @Valid는 유효성 검사를 해줌.
        log.debug("회원가입 요청(request): {}", request.toString());

        User savedUser = userService.registerUser(request.toEntity());
        log.debug("user: {}", savedUser.toString());

        return new UserResponse(savedUser);
    }

    // 일반 로그인
    @PostMapping("/login")
    public LoginResponse loginUser(@RequestBody @Valid LoginRequest request){
        log.debug("로그인 요청(request): {}", request.toString());

        User user = userService.loginUser(request.getEmail(), request.getPassword());

        return new LoginResponse("로그인 성공", new UserResponse(user));
    }

    // 특정 유저 조희
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id){
        User user = userService.findUserById(id);
        return new UserResponse(user);
    }

    // 모든 유저 조희
    @GetMapping
    public List<UserResponse> getAllUsers(){
        List<User> users = userService.findAllUsers();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            userResponses.add(new UserResponse(user));
        }
        return userResponses;
    }

}
