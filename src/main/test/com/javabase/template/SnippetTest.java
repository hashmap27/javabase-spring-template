package com.javabase.template;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 단위테스트 클래스
 *
 * @author Cheong SungHyun <hashmap27@gmail.com>
 */
public class SnippetTest {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    static {
        System.setProperty("logback.configurationFile", "runtimeEnv/logback-junit.xml");
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        if(StringUtils.isEmpty(System.getProperty("spring.profiles.acrive"))) {
            System.setProperty("spring.profiles.acrive", "local");
        }
    }

    @Test
    public void testCapitalize() {
        String temp = "i am FINE. Thank you and you";
        logger.debug("WordUitls: {}", WordUtils.capitalizeFully(temp));
        logger.debug("StringUtils: {}", StringUtils.capitalize(temp));
        logger.debug("capitalizeFully: {}", capitalizeFully(temp));
    }

    /** WordUtils.capitalizeFully의 deprecated로 해당 code 참고하여 private method로 변환 처리. */
    private String capitalizeFully(String str) {
        String target = str.toLowerCase();
        int targetLen = target.length();
        int[] newCodePoints = new int[targetLen];
        int outOffset = 0;

        boolean capitalizeNext = true;
        for(int index = 0; index < targetLen;) {
            final int codePoint = target.codePointAt(index);

            if(Character.isWhitespace(codePoint)) {
                capitalizeNext = true;
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            } else if(capitalizeNext) {
                int titleCaseCodePoint = Character.toTitleCase(codePoint);
                newCodePoints[outOffset++] = titleCaseCodePoint;
                index += Character.charCount(titleCaseCodePoint);
                capitalizeNext = false;
            } else {
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            }
        }
        return new String(newCodePoints, 0, outOffset);
    }

}
