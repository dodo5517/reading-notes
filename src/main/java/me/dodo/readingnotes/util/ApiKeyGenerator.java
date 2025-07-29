package me.dodo.readingnotes.util;

import java.util.UUID; // Universally Unique Identifier
// 128비트로 구성된 고유 ID 문자열을 생성함.
public class ApiKeyGenerator {
    public static String generate(){
        return UUID.randomUUID().toString();
    }
}
