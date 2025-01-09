package org.dinky.ltpa.token;

import org.dinky.ltpa.config.LtpaSetting;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

public final class LtpaToken {
    private static final java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("((?<==)|^)[^/=]*((?=/)|$)");
    private byte[] header = new byte[4];
    private byte[] creation = new byte[8];
    private byte[] expires = new byte[8];
    private byte[] user;
    private String canonicalUser;
    private byte[] digest = new byte[20];
    private Date creationDate, expiresDate;
    private String ltpaToken;
    private byte[] rawToken;

    public LtpaToken(String token) {
        ltpaToken = token;
        rawToken = base64decode(token);
        user = new byte[(rawToken.length) - 40];
        System.arraycopy(rawToken, 0, header, 0, header.length);    //4
        System.arraycopy(rawToken, 4, creation, 0, creation.length);    //8
        System.arraycopy(rawToken, 12, expires, 0, expires.length); //8
        System.arraycopy(rawToken, 20, user, 0, user.length);
        System.arraycopy(rawToken, rawToken.length - digest.length, digest, 0, digest.length);    //20

        creationDate = new Date(Long.parseLong(new String(creation), 16) * 1000);
        expiresDate = new Date(Long.parseLong(new String(expires), 16) * 1000);
        canonicalUser = new String(user);
    }

    private LtpaToken() {

    }

    public static String getCommonUser(String name) {
        java.util.regex.Matcher matcher = pattern.matcher(name);
        return matcher.find() ? matcher.group(0) : name;
    }

    public static boolean isValid(String ltpaToken, String secret) throws NoSuchAlgorithmException {
        LtpaToken ltpa = new LtpaToken(ltpaToken);
        return ltpa.isValid(secret);
    }

    public static LtpaToken generate(String canonicalUser, Date tokenCreation, Date tokenExpires, String secret) {
        LtpaToken ltpa = new LtpaToken();
        Calendar calendar = Calendar.getInstance();
        ltpa.header = new byte[]{0, 1, 2, 3};

        calendar.setTime(tokenCreation);
        ltpa.creation = Long.toHexString(calendar.getTimeInMillis() / 1000).toUpperCase().getBytes();
        calendar.setTime(tokenExpires);
        ltpa.expires = Long.toHexString(calendar.getTimeInMillis() / 1000).toUpperCase().getBytes();
        ltpa.user = canonicalUser.getBytes();
        ltpa.canonicalUser = canonicalUser;
        byte[] token = ltpa.header;
        token = concatenate(token, ltpa.creation);
        token = concatenate(token, ltpa.expires);
        token = concatenate(token, ltpa.user);

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(token);
        ltpa.digest = md.digest(base64decode(secret));
        token = concatenate(token, ltpa.digest);

        ltpa.ltpaToken = base64encode(token);
        ltpa.rawToken = token;
        ltpa.creationDate = tokenCreation;
        ltpa.expiresDate = tokenExpires;
        return ltpa;    //new LtpaToken(base64encode(token));
    }

    private static byte[] concatenate(byte[] a, byte[] b) {
        if (a == null) {
            return b;
        } else {
            byte[] bytes = new byte[a.length + b.length];

            System.arraycopy(a, 0, bytes, 0, a.length);
            System.arraycopy(b, 0, bytes, a.length, b.length);
            return bytes;
        }
    }

    public static byte[] base64decode(String src) {
        return java.util.Base64.getDecoder().decode(src.getBytes());
    }

    public static String base64encode(byte[] src) {
        return new String(java.util.Base64.getEncoder().encode(src));
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getExpiresDate() {
        return expiresDate;
    }

    public String getCanonicalUser() {
        return canonicalUser;   //new String(user);
    }

    public String getUser() {
        return LtpaToken.getCommonUser(canonicalUser);    //LtpaTokenHandler.getCommonUser(new String(user));
    }

    public boolean isValid(String secret) {
        Date now = new Date();
        if (!(now.after(creationDate) && now.before(expiresDate))) return false;

        byte[] bytes = header;
        bytes = concatenate(bytes, creation);
        bytes = concatenate(bytes, expires);
        bytes = concatenate(bytes, user);
        bytes = concatenate(bytes, base64decode(secret));
        try {
            return MessageDigest.isEqual(digest, MessageDigest.getInstance("SHA-1").digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String INVALID="Token签名错误.";
    public static final String INVALID_CREATE_TIME="Token创建时间错误.";
    public static final String INVALID_EXPIRATION="Token过期.";
    public void validate(LtpaSetting setting) {
        if (!this.isValid(setting.getSecret())) {
            throw new RuntimeException(INVALID);
        }
//        if (!setting.version.equals(this.getVersion())) {
//            throw new RuntimeException("Token版本号错误：" + this.getVersion());
//        }
        long timeCreation = this.getCreationDate().getTime();
        long timeExpiration = this.getExpiresDate().getTime();
        long now = System.currentTimeMillis();
        if (timeCreation > (now + setting.getTransition() * 1000)) {
            throw new RuntimeException(INVALID_CREATE_TIME);
        }
        if (timeExpiration < now || timeCreation > timeExpiration) {
            throw new RuntimeException(INVALID_EXPIRATION);
        }
    }

    public static LtpaToken generate(String canonicalUser, LtpaSetting setting) {
        long now = System.currentTimeMillis();
        return generate(canonicalUser, new Date(now - setting.getTransition() * 1000),
                new Date(now + setting.getExpiration() * 1000),
                setting.getSecret());
    }

    public String toString() {
        return ltpaToken;
    }

    public String getLtpaToken() {
        return ltpaToken;
    }

    public void setLtpaToken(String ltpaToken) {
        this.ltpaToken = ltpaToken;
    }

    public String getVersion() {
        return new String(this.header);
    }
}