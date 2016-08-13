package com.sharathp.blabber.repositories;

import com.sharathp.blabber.models.HomeTimeline;
import com.sharathp.blabber.models.HomeTimelineWithUser;
import com.sharathp.blabber.models.Like;
import com.sharathp.blabber.models.LikeWithUser;
import com.sharathp.blabber.models.Mentions;
import com.sharathp.blabber.models.MentionsWithUser;
import com.sharathp.blabber.models.Tweet;
import com.sharathp.blabber.models.User;
import com.sharathp.blabber.models.UserTimeLineTweetWithUser;
import com.sharathp.blabber.models.UserTimeline;
import com.yahoo.squidb.support.SquidSupportCursorLoader;

import java.util.Collection;
import java.util.List;

public interface TwitterDAO {

    // even though this seems unnecessary, this helps keep track of all clients easily..
    SquidSupportCursorLoader<HomeTimelineWithUser> getHomeTimeline();

    SquidSupportCursorLoader<UserTimeLineTweetWithUser> getUserTimeline(Long userId);

    SquidSupportCursorLoader<LikeWithUser> getUserLikes(Long userId);

    SquidSupportCursorLoader<MentionsWithUser> getMentions();

    HomeTimelineWithUser getLatestHomeTimeline();

    HomeTimelineWithUser getEarliestHomeTimeline();

    MentionsWithUser getLatestMention();

    MentionsWithUser getEarliestMention();

    UserTimeLineTweetWithUser getLatestUserTimeline(Long userId);

    UserTimeLineTweetWithUser getEarliestUserTimeline(Long userId);

    LikeWithUser getLatestUserLike(final Long userId);

    LikeWithUser getEarliestUserLike(final Long userId);

    boolean checkAndInsertUsers(Collection<User> users);

    boolean checkAndInsertTweets(Collection<Tweet> tweets);

    boolean checkAndInsertMentions(Collection<Mentions> mentions);

    boolean checkAndInsertUserTimelines(long userId, List<UserTimeline> userTimelines);

    boolean checkAndInsertHomeTimelines(List<HomeTimeline> homeTimelines);

    boolean checkAndInsertLikes(Long userId, Collection<Like> likes);

    boolean deleteExistingTweets();

    User getUser(Long userId);

    boolean updateTweet(Tweet tweet);

    Tweet getTweet(Long tweetId);
}
