package org.dinky.gateway.config;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 *
 */
class ActionTypeTest {

    @ParameterizedTest
    @CsvSource({
            "savepoint, SAVEPOINT",
            "cancel, CANCEL"
    })
    void get(String value, ActionType actionType) {
        assertThat(ActionType.get(value), equalTo(actionType));
    }
}
