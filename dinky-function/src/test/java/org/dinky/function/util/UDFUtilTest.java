package org.dinky.function.util;

import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UDFUtilTest {

    @Test
    void isUdfStatement() {
        Pattern pattern = Pattern.compile(UDFUtil.FUNCTION_SQL_REGEX, Pattern.CASE_INSENSITIVE);

        Function<String, Boolean> c = (s) -> UDFUtil.isUdfStatement(pattern, s);

        assertTrue(c.apply("create function a as 'abc'"));
        assertTrue(c.apply("create  function  a  as  'abc'  language python"));
        assertTrue(c.apply("create  function  a  as  'abc'  using jar 'path'"));
        assertTrue(c.apply("create  function  a  as  'abc'  using jar 'path', jar 'path/2'"));
        assertTrue(c.apply("create tempOrary function  a  as  'abc'  using jar 'path', jar 'path/2'"));
        assertTrue(c.apply("create tempOrary system function  a  as  'abc'  using jar 'path', jar 'path/2'"));
        assertTrue(c.apply("create tempOrary system function  a  as  'abc'  using jar 'path',jar 'path/2'"));
        assertFalse(c.apply(" create tempOrary system function  a  as  'abc'  using jar 'path', jar 'path/2'"));
        assertFalse(c.apply("create tempOrary system function  a  as  abc  using jar 'path', jar 'path/2'"));

    }
}
