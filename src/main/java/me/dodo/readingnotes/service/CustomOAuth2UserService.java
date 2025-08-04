package me.dodo.readingnotes.service;

import me.dodo.readingnotes.domain.User;
import me.dodo.readingnotes.repository.UserRepository;
import me.dodo.readingnotes.util.ApiKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements
        OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // DefaultOAuth2UserService = 유저 정보 로딩하는 서비스
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        // userRequest에 있는 액세스 토큰으로 사용자 정보를 요청하여 OAuth2User 형태로 받아옴. (attributes에 email,name 등이 담김)
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 로그인할 서비스 구분(ex. google, naver, kakao)
        // registrasion은 application-oauth.properties에 등록한 소셜 로그인 ID
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // OAuth2 로그인 진행 시 기본키가 되는 필드 = Primary Key, 구글은 sub
        String userNameAttribute = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // atrributes 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // 일반 회원가입한 이메일로 로그인 시 소셜 로그인 거부
        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.getProvider() == null || !user.getProvider().equals(registrationId)) {
                throw new IllegalArgumentException("이미 일반 가입된 이메일입니다. 소셜 로그인 불가");
            }
        });

        // api_key 생성
        String api_key = ApiKeyGenerator.generate();
        if (api_key != null){
            log.info("api_key:" + api_key.substring(0,8));
        } else{
            log.warn("api_key가 null임.");
        }

        // DB에 저장
        userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.fromSocial(email, name, "google",(String) attributes.get("sub"), api_key)
                ));

        // 객체로 만들어 전달
        return new DefaultOAuth2User(
                // 권한 지정 = ROLE_USER
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                // 불러온 사용자 정보
                attributes,
                // 식별자로 삼을 key
                userNameAttribute
        );
    }
}
