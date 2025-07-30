package me.dodo.readingnotes.util;

import eu.bitwalker.useragentutils.*;

// @Component는 static 메서드만 있으면 필요 없긴 함.
// 사용할 때도 의존성 주입 필요 없음
public class DeviceInfoParser {
    public static String extractDeviceInfo(String userAgentString){
        if (userAgentString == null || userAgentString.isEmpty()){
            return "Unknown";
        }

        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        OperatingSystem os = userAgent.getOperatingSystem();
        Browser browser = userAgent.getBrowser();
        Version browserVersion = userAgent.getBrowserVersion();

        String osName = os != null ? os.getName() : "Unknown OS"; // Android, iOS 등
        String deviceType = os != null ? os.getDeviceType().getName() : "Unknown DeviceType"; // Mobile, Tablet, Computer 등
        String browserName = browser != null ? browser.getName() : "Unknown Browser";
        String browserVer = browserVersion != null ? browserVersion.getVersion() : "Unknown Browser Version";

        return String.format("%s / %s / %s %s", osName, deviceType, browserName, browserVer);
    }
}
