package ru.practicum.util;

import java.time.format.DateTimeFormatter;

public final class Constants {
    private Constants() {
    }

    public static final String VALUE_DISCOVERY_SERVICES_STATS_SERVER_ID = "${discovery.services.stats-server-id}";

    public static final String USER_BASE_PATH = "/admin/users";
    public static final String USER_PATH_USER_ID = "/{userId}";

    public static final String NAME_USER_SERVICE = "user-service";
    public static final String PATH_BOX_USERS = "/box/users";
    public static final String PATH_BOX_USERS_ID = "/{userId}";
    public static final String PATH_BOX_USERS_LIST = "/list";

    public static final String NAME_EVENT_SERVICE = "event-service";
    public static final String PATH_BOX_EVENTS = "/box/events";
    public static final String PATH_BOX_EVENTS_ID = "/{eventId}";

    public static final String NAME_REQUEST_SERVICE = "request-service";
    public static final String PATH_BOX_REQUESTS = "/box/requests";
    public static final String PATH_BOX_REQUEST_CONFIRMED_EVENTS_ID = "/confirmed/{eventId}";
    public static final String PATH_BOX_REQUEST_CONFIRMED_EVENTS_LIST = "/confirmed/list";
    public static final String PATH_BOX_REQUEST_STATUS_EVENTS_ID = "/status/{eventId}";
    public static final String PATH_BOX_REQUEST_EVENTS_ID = "/{eventId}";
    public static final String PATH_BOX_REQUEST_EVENTS_LIST = "/list";

    public static final String NAME_COMMENT_SERVICE = "comment-service";
    public static final String PATH_BOX_COMMENTS = "/box/comments";
    public static final String PATH_BOX_COMMENTS_ID = "/{commentId}";

    public static final String ERROR_MESSAGE_USER_SERVICE_UNAVAILABLE = "User-service не доступен. Попробуйте попытку получить пользователя позже.";
    public static final String ERROR_MESSAGE_EVENT_SERVICE_UNAVAILABLE = "Event-service не доступен. Попробуйте попытку получить событие позже.";

    public static final String PATTERN_FORMATE_DATE = "yyyy-MM-dd HH:mm:ss";

    public static final int LENGTH_NAME_CATEGORY_MIN = 1;
    public static final int LENGTH_NAME_CATEGORY_MAX = 50;

    public static final String PATH_VARIABLE_ID = "id";
    public static final String PATH_VARIABLE_USER_ID = "userId";
    public static final String PATH_VARIABLE_EVENT_ID = "eventId";

    public static final int LENGTH_DESCRIPTION_EVENT_MIN = 20;
    public static final int LENGTH_DESCRIPTION_EVENT_MAX = 7000;
    public static final int OFFSET_EVENT_DATE = 7200;
    public static final int LENGTH_TITLE_EVENT_MIN = 3;
    public static final int LENGTH_TITLE_EVENT_MAX = 120;
    public static final int LENGTH_ANNOTATION_EVENT_MIN = 20;
    public static final int LENGTH_ANNOTATION_EVENT_MAX = 2000;
    public static final String VALUE_SEND_TO_REVIEW = "SEND_TO_REVIEW";
    public static final String VALUE_CANCEL_REVIEW = "CANCEL_REVIEW";

    public static final String REQUEST_PARAM_FROM = "from";
    public static final String DEFAULT_VALUE_0 = "0";
    public static final String REQUEST_PARAM_SIZE = "size";
    public static final String DEFAULT_VALUE_REQUEST_PARAM_SIZE = "10";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
