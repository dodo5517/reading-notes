package me.dodo.readingnotes.service;

// jakarta.transaction.Transactional 보다 밑에가 spring framework 전용으로 연동 잘 됨.
import me.dodo.readingnotes.repository.RefreshTokenRepository;
import me.dodo.readingnotes.util.ApiKeyGenerator;
import me.dodo.readingnotes.util.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import me.dodo.readingnotes.domain.User;
import me.dodo.readingnotes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepositorys, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 유저 회원가입
    @Transactional // 트랜젝션 처리
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())){
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 이름입니다.");
        }

        // 비밀번호 암호화(해싱)
        String encodedPw = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPw);

        // api_key
        user.setApiKey(ApiKeyGenerator.generate()); // api_key 생성
        if (user.getApiKey() != null){
            log.info("api_key:" + user.getApiKey().substring(0,8));
        } else{
            log.warn("api_key가 null임.");
        }

        return userRepository.save(user);
    }

    // api_key 재발급
    @Transactional
    public String reissueApiKey(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        String newApiKey = ApiKeyGenerator.generate(); // 랜덤 키 생성 로직
        user.setApiKey(newApiKey); // apiKey 갱신
        userRepository.save(user);

        return maskApiKey(newApiKey); // 마스킹된 키 반환
    }
    // api_key 마스킹
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 4) return "****";
        int visibleCount = 4;
        int maskCount = apiKey.length() - visibleCount;
        return "*".repeat(maskCount) + apiKey.substring(maskCount);
    }

    // api_key 전체(마스킹 안 된) 조회
    @Transactional(readOnly = true)
    public String getRawApiKey(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        return user.getApiKey();
    }


    // 전체 유저 조회
    public List<User> findAllUsers() {
        // 필요하면 탈퇴 유저는 제외하고 보도록 추가 해야함.
        return userRepository.findAll(); }

    // ID로 유저 조회
    public User findUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 없습니다."));
    }

    // 유저 삭제
    public String deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("해당 ID의 유저가 없습니다."));
        // 삭제
        user.setIsDeleted(true);
        userRepository.save(user);

        // 삭제 완료 메시지
        log.info("is_deleted:" + user.getIsDeleted());
        return "탈퇴 처리가 완료되었습니다.";
    }
}
