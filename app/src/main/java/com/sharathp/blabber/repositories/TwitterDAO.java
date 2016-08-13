package com.sharathp.blabber.repositories;

import com.sharathp.blabber.models.HomeTimeline;
import com.sharathp.blabber.models.HomeTimelineWithUser;
import com.sharathp.blabber.models.Mentions;
import com.sharathp.blabber.models.MentionsWithUser;
import com.sharathp.blabber.models.Tweet;
import com.sharathp.blabber.models.User;
import com.sharathp.blabber.models.UserTimeLineTweetWithUser;
import com.sharathp.blabber.models.UserTimeline;
import com.yahoo.squidb.sql.Query;
import com.yahoo.squidb.support.SquidSupportCursorLoader;

import java.util.Collection;
import java.util.List;

public interface TwitterDAO {

    // even though this seems unnecessary, this helps keep track of all clients easily..
    SquidSupportCursorLoader<HomeTimelineWithUser> getHomeTimeline(Query query);

    SquidSupportCursorLoader<UserTimeLineTweetWithUser> getUserTimeline(Query query);

    SquidSupportCursorLoader<MentionsWithUser> getMentions(Query query);

    HomeTimelineWithUser getLatestHomeTimeline();

    HomeTimelineWithUser getEarliestHomeTimeline();

    MentionsWithUser getLatestMention();

    MentionsWithUser getEarliestMention();

    UserTimeLineTweetWithUser getLatestUserTimeline(Long userId);

    UserTimeLineTweetWithUser getEarliestUserTimeline(Long userId);

    boolean checkAndInsertUsers(Collection<User> users);

    boolean checkAndInsertTweets(Collection<Tweet> tweets);

    boolean checkAndInsertMentions(Collection<Mentions> mentions);

    boolean checkAndInsertUserTimelines(List<UserTimeline> userTimelines);

    boolean checkAndInsertHomeTimelines(List<HomeTimeline> homeTimelines);

    boolean deleteExistingTweets();
}
