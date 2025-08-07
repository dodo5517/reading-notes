package me.dodo.readingnotes.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import me.dodo.readingnotes.dto.*;
import me.dodo.readingnotes.repository.UserRepository;
import me.dodo.readingnotes.service.S3Service;
import me.dodo.readingnotes.service.UserService;
import me.dodo.readingnotes.domain.User;
import me.dodo.readingnotes.util.JwtTokenProvider;
import org.slf4j.Logger; // java.util.logging.Logger 보다 세부 설정 가능.
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;
    private final JwtTokenProvider jwtTokenProvider;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    public UserController(UserService userService,
                          S3Service s3Service,
                          JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
            this.userService = userService; // controller에 service 의존성 주입
            this.s3Service = s3Service;
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
    @GetMapping("/me")
    public UserResponse getMe(HttpServletRequest request){
        String token = jwtTokenProvider.extractToken(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        User user = userService.findUserById(userId);
        return new UserResponse(user);
    }

    // 유저 프로필 사진
    @PostMapping("/{id}/profile-image")
    public ResponseEntity<String> uploadProfileImage(@PathVariable Long id,
                                                     @RequestParam("image") MultipartFile image) throws Exception {
        String fileName = "user-" + id + "_" + UUID.randomUUID();
        String imageUrl = s3Service.uploadProfileImage(image, fileName);

        userService.updateProfileImage(id, imageUrl); // DB에 URL 저장

        return ResponseEntity.ok(imageUrl);
    }

    // 유저 이름 수정
    @PatchMapping("/me/username")
    public ResponseEntity<Void> updateUsername(@RequestBody @Valid UpdateUsernameRequest request,
                                               HttpServletRequest httpRequest){
        log.debug("유저이름 수정 요청");
        log.debug("수정할 이름: {}", request.getNewUsername());

        // 토큰에서 userId 추출
        String token = jwtTokenProvider.extractToken(httpRequest);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // 유저 이름 수정
        Boolean result = userService.updateUsername(userId, request.getNewUsername());

        // 수정 확인
        log.debug("updateUsername: {}", result);

        // 상태코드만 반환 (204)
        return ResponseEntity.noContent().build();
    }

    // 유저 비밀번호 수정
    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatePasswordRequest request,
                                               HttpServletRequest httpRequest){
        log.debug("비밀번호 수정 요청");
        
        // 토큰에서 userId 추출
        String token = jwtTokenProvider.extractToken(httpRequest);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // 유저 비밀번호 수정
        Boolean result = userService.updatePassword(userId, request.getCurrentPassword(), request.getNewPassword());

        // 수정 확인
        log.debug("updatePassword: {}", result);

        // 상태코드만 반환 (204)
        return ResponseEntity.noContent().build();
    }

    // 특정 유저 조희
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id){
        User user = userService.findUserById(id);
        return new UserResponse(user);
    }

    // api_key 재발급
    @PostMapping("/api-key/reissue")
    public ResponseEntity<MaskedApiKeyResponse> reissueApiKey(HttpServletRequest request) {
        log.debug("API Key 재발급 요청");

        String token = jwtTokenProvider.extractToken(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        String maskedApiKey = userService.reissueApiKey(userId);
        return ResponseEntity.ok(new MaskedApiKeyResponse("API Key가 새로 발급되었습니다.", maskedApiKey));
    }

    // api_key 전체(마스킹 안 된) 조회
    @GetMapping("/api-key")
    public ResponseEntity<ApiKeyResponse> getApiKey(HttpServletRequest request) {
        log.debug("API Key 전체 조회 요청");
        
        String token = jwtTokenProvider.extractToken(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        String apiKey = userService.getRawApiKey(userId);
        return ResponseEntity.ok(new ApiKeyResponse("API Key 조회 성공", apiKey));
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
