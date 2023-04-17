package org.dinky.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlUtilTest {

    @Test
    void removeNote() {
        String sql = "-- a \n" +
                " --- b \n" +
                " --- -- \n" +
                " # abc \n" +
                " # \n" +
                " /* efg */ \n" +
                " /** xyz*/ \n" +
                " /**pdf*/ \n" +
                " 'ccc" +
                "--'a\n" +
                "'" +
                "kcf/*df/*/ \n" +
                " where is '--abc'";
        String result = SqlUtil.removeNote(sql);
        System.out.println(result);
    }
}
