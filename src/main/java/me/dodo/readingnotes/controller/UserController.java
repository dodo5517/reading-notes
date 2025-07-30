package me.dodo.readingnotes.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import me.dodo.readingnotes.dto.*;
import me.dodo.readingnotes.repository.UserRepository;
import me.dodo.readingnotes.service.UserService;
import me.dodo.readingnotes.domain.User;
import me.dodo.readingnotes.util.JwtTokenProvider;
import org.slf4j.Logger; // java.util.logging.Logger 보다 세부 설정 가능.
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    public UserController(UserService userService,
                          JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
            this.userService = userService; // controller에 service 의존성 주입
            this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    // post(일반 회원가입)
    @PostMapping
    public UserResponse registerUser(@RequestBody @Valid UserRequest request){ // @Valid는 유효성 검사를 해줌.
        log.debug("회원가입 요청(request): {}", request.toString());

        User savedUser = userService.registerUser(request.toEntity());
        log.debug("user: {}", savedUser.toString());

        return new UserResponse(savedUser);
    }

    // 로그인한 유저의 정보 조회
    @GetMapping("me")
    public UserResponse getMe(HttpServletRequest request){
        String token = jwtTokenProvider.extractToken(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
        return new UserResponse(user);
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
