package org.imemorize.android.model;

/**
 * Created by briankurzius on 10/12/13.
 */

import com.google.analytics.tracking.android.Logger.LogLevel;

public class Consts {
    public final static String ACTION = "action";
    public final static String ACTION_EDIT_QUOTE = "action_edit_quote";
    public final static String ACTION_ADD_QUOTE = "action_add_quote";

    public final static String USER_QUOTE_PREFIX = "U_";

    public static final String GA_ANALYTICS_ID = "UA-2194207-8";
    public static final int GA_DISPATCH_PERIOD = 30;
    public static final boolean GA_IS_DRY_RUN = false;

    public static final LogLevel GA_LOG_VERBOSITY = LogLevel.INFO;

    public static final String TRACK_SCREEN_HOME = "home_view";
    public static final String TRACK_SCREEN_CATEGORY = "category_view";
    public static final String TRACK_SCREEN_QUOTES = "quotes_view";
    public static final String TRACK_SCREEN_MEMORIZE = "memorize_view";
    public static final String TRACK_SCREEN_ADD_QUOTE_VIEW = "add_quote";

    public static final String TRACK_EVENT_TYPE = "Event";
    public static final String TRACK_EVENT_TYPE_SEARCH = "Event_search";
    public static final String TRACK_EVENT_TYPE_SHARE = "Event_share";
    public static final String TRACK_EVENT_TYPE_ADD_QUOTE = "Event_add_quote";
    public static final String TRACK_EVENT_TYPE_DELETE_QUOTE = "Event_delete_quote";
    public static final String TRACK_EVENT_TYPE_MEMORIZE = "Event_memorize";
    public static final String TRACK_EVENT_TYPE_SELECT_CATEGORY = "Event_select_category";
    public static final String TRACK_EVENT_TYPE_ADD_FAVORITE = "Event_add_favorite";
    public static final String TRACK_EVENT_TYPE_ADD_MEMORIZED = "Event_add_memorized";

    public static final String TRACK_EVENT_NAME_SHARE = "UserQuote_share";
    public static final String TRACK_EVENT_NAME_ADD_QUOTE = "UserQuote_add";
    public static final String TRACK_EVENT_NAME_DELETE_QUOTE = "UserQuote_delete";
    public static final String TRACK_EVENT_NAME_EDIT_QUOTE = "UserQuote_edit";



    public final static int DEFAULT_LANGUAGE_INDEX = 14;




}
