package com.example.blog.captcha;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码服务
 * 生成图片验证码，存储答案到本地缓存，支持过期自动清理
 */
@Service
public class CaptchaService {

    /** 验证码字符集（排除易混淆字符 O/0/1/l） */
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;
    /** 过期时间：5 分钟 */
    private static final long EXPIRY_MS = 5 * 60 * 1000L;

    /** 存储 token -> { answer, createTime } */
    private final Map<String, CaptchaEntry> store = new ConcurrentHashMap<>();
    private final Random random = new Random();

    /**
     * 启动后台线程，每分钟清理过期验证码
     */
    @PostConstruct
    public void startCleanup() {
        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60000);
                    long now = System.currentTimeMillis();
                    store.entrySet().removeIf(e ->
                        now - e.getValue().createTime > EXPIRY_MS);
                } catch (InterruptedException ignored) {
                    break;
                }
            }
        }, "captcha-cleaner");
        cleaner.setDaemon(true);
        cleaner.start();
    }

    /**
     * 生成验证码
     * @return 包含 token、base64 图片和过期时间的 Map
     */
    public Map<String, Object> generate() {
        String code = generateCode();
        String token = UUID.randomUUID().toString().replace("-", "");

        store.put(token, new CaptchaEntry(code, System.currentTimeMillis()));

        // 生成图片并转为 base64
        String base64Image = generateImage(code);

        return Map.of(
            "token", token,
            "image", "data:image/png;base64," + base64Image,
            "expireIn", EXPIRY_MS
        );
    }

    /**
     * 验证码验证
     * @param token  前端传来的 token
     * @param code   用户输入的验证码（不区分大小写）
     * @return true=验证通过，false=失败
     */
    public boolean verify(String token, String code) {
        if (token == null || code == null) return false;
        CaptchaEntry entry = store.remove(token);  // 一次性使用
        if (entry == null) return false;
        // 检查是否过期
        if (System.currentTimeMillis() - entry.createTime > EXPIRY_MS) return false;
        return entry.answer.equalsIgnoreCase(code.trim());
    }

    /** 生成随机验证码字符串 */
    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * 生成验证码图片（带干扰线和扭曲效果）
     */
    private String generateImage(String code) {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        // 抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 填充背景色（随机浅色）
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制干扰线
        g.setColor(new Color(200, 200, 200));
        for (int i = 0; i < 6; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制干扰点
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.setColor(new Color(
                150 + random.nextInt(100),
                150 + random.nextInt(100),
                150 + random.nextInt(100)));
            g.fillRect(x, y, 2, 2);
        }

        // 逐个绘制字符（随机旋转、颜色、大小）
        char[] chars = code.toCharArray();
        int startX = 10;
        for (int i = 0; i < chars.length; i++) {
            // 随机颜色
            g.setColor(new Color(
                random.nextInt(100),
                random.nextInt(100),
                random.nextInt(150) + 50));

            // 随机字体大小
            int fontSize = 22 + random.nextInt(8);
            Font font = new Font("Arial", Font.BOLD + Font.ITALIC, fontSize);
            g.setFont(font);

            // 随机旋转
            AffineTransform old = g.getTransform();
            double angle = (random.nextDouble() - 0.5) * 0.6;  // ±0.3 rad
            g.rotate(angle, startX + 10, HEIGHT / 2 + 5);

            g.drawString(String.valueOf(chars[i]), startX + random.nextInt(4), 25 + random.nextInt(10));
            g.setTransform(old);

            startX += 20 + random.nextInt(8);
        }

        g.dispose();

        // 转为 base64
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("验证码图片生成失败", e);
        }
    }

    /** 验证码缓存条目 */
    private record CaptchaEntry(String answer, long createTime) {}
}
