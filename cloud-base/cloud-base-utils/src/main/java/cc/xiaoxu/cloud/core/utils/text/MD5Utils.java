package cc.xiaoxu.cloud.core.utils.text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 工具类
 * <p>
 * 2019/12/23 10:40
 *
 * @author Xiao Xu
 */
public class MD5Utils {

    private static final char[] yT = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};

    private MD5Utils() {
    }

    public static String p(byte[] paramArrayOfByte) throws NoSuchAlgorithmException {
        MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
        localMessageDigest.update(paramArrayOfByte);
        return toHexString(localMessageDigest.digest());
    }

    public static String toHexString(byte[] paramArrayOfByte) {
        if (paramArrayOfByte == null) {
            return null;
        }
        StringBuilder localStringBuilder = new StringBuilder(paramArrayOfByte.length * 2);
        int i = 0;
        while (true) {
            if (i >= paramArrayOfByte.length) {
                return localStringBuilder.toString();
            }
            localStringBuilder.append(yT[((paramArrayOfByte[i] & 0xF0) >>> 4)]);
            localStringBuilder.append(yT[(paramArrayOfByte[i] & 0xF)]);
            i += 1;
        }
    }

    public static String toMd5(String paramString) {
        if (paramString == null) {
            return null;
        }
        try {
            paramString = p(paramString.getBytes(StandardCharsets.UTF_8));
            return paramString;
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 计算文件 MD5
     *
     * @param file 文件
     * @return 文件 MD5
     */
    public static String toMd5(File file) {
        try (InputStream stream = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
            return toMd5(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String toMd5(InputStream stream) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int len;
            while ((len = stream.read(buf)) > 0) {
                digest.update(buf, 0, len);
            }
            return toHexString(digest.digest());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}