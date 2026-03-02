package ru.practicum.storage;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmedCount {
    Integer eventId;

    Integer cnt;
}
