package com.dlink.assertion;

import com.dlink.exception.JobException;

/**
 * Asserts
 *
 * @author wenmo
 * @since 2021/7/5 21:57
 */
public class Asserts {

    public static void checkNull(String key,String msg) {
        if (key == null||"".equals(key)) {
            throw new JobException(msg);
        }
    }

}
