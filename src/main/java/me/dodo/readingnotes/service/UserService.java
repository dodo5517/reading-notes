package me.dodo.readingnotes.service;

// jakarta.transaction.Transactional 보다 밑에가 spring framework 전용으로 연동 잘 됨.
import me.dodo.readingnotes.util.ApiKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

    // 유저 저장
    @Transactional // 트랜젝션 처리
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())){
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 이름입니다.");
        }

        // api_key
        user.setApiKey(ApiKeyGenerator.generate()); // api_key 생성
        if (user.getApiKey() != null){
            log.info("api_key:" + user.getApiKey().substring(0,8));
        } else{
            log.warn("api_key가 null임.");
        }

        return userRepository.save(user);
    }

    // 전체 유저 조회
    public List<User> findAllUsers() {
        // 필요하면 탈퇴 유저는 제외하고 보도록 추가 해야함.
        return userRepository.findAll(); }

    // ID로 유저 조회
    public User findUserById(Long id) { return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 없습니다."));
    }

    // 유저 삭제
    public String deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("해당 ID의 책이 없습니다."));
        // 삭제
        user.setIsDeleted(true);
        userRepository.save(user);

        // 삭제 완료 메시지
        log.info("is_deleted:" + user.getIsDeleted());
        return "탈퇴 처리가 완료되었습니다.";
    }
}
