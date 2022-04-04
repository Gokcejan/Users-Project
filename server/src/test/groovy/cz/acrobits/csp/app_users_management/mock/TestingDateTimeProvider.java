package cz.acrobits.csp.app_users_management.mock;

import cz.acrobits.csp.core.common.datetime.CurrentDateTimeProvider;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static cz.acrobits.csp.core.common.mapper.DateTimeMappingUtils.UTC;

public class TestingDateTimeProvider implements CurrentDateTimeProvider {

    @Getter
    @Setter
    private LocalDateTime returnedDateTime;

    @Override
    public LocalDateTime getCurrentDateTime() {
        if (returnedDateTime == null) {
            return LocalDateTime.now(ZoneId.of(UTC));
        } else {
            return returnedDateTime;
        }
    }
}
