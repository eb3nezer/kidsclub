package kc.ebenezer.model;

import java.util.HashMap;
import java.util.Map;

public enum ImageSize {
    SMALL("50x50", 50),
    MEDIUM("100x100", 100),
    LARGE("250x250", 250),
    DEFAULT("original", null);

    private String code;
    private Integer maxWidth;

    private static Map<String, ImageSize> codeMap = new HashMap<>();

    static {
        for (ImageSize imageSize : ImageSize.values()) {
            codeMap.put(imageSize.code, imageSize);
        }
    }
    private ImageSize(String code, Integer maxWidth) {
        this.code = code;
        this.maxWidth = maxWidth;
    }

    public String getCode() {
        return code;
    }

    public Integer getMaxWidth() {
        return maxWidth;
    }

    public static ImageSize getForCode(String code) {
        return codeMap.get(code);
    }
}
