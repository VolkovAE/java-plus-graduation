package ru.practicum.util;

public final class StringConstants {
    private StringConstants() {
    }

    public static final String VALUE_DISCOVERY_SERVICES_STATS_SERVER_ID = "${discovery.services.stats-server-id}";

    public static final String USER_BASE_PATH = "/admin/users";
    public static final String USER_PATH_USER_ID = "/{userId}";

    public static final String NAME_USER_SERVICE = "user-service";
    public static final String PATH_BOX_USERS = "/box/users";
    public static final String PATH_BOX_USERS_ID = "/{userId}";
    public static final String PATH_BOX_USERS_LIST = "/list";

    public static final String ERROR_MESSAGE_USER_SERVICE_UNAVAILABLE = "User-service не доступен. Попробуйте попытку получить пользователя позже.";
}
