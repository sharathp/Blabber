package com.sharathp.blabber.repositories;

import com.sharathp.blabber.models.Tweet;
import com.sharathp.blabber.models.TweetWithUser;
import com.sharathp.blabber.models.User;
import com.yahoo.squidb.sql.Query;
import com.yahoo.squidb.support.SquidSupportCursorLoader;

import java.util.Collection;

public interface TwitterDAO {

    // even though this seems unnecessary, this helps keep track of all clients easily..
    SquidSupportCursorLoader<TweetWithUser> getTweets(Query query);

    Tweet getLatestTweet();

    Tweet getEarliestTweet();

    boolean checkAndInsertUsers(Collection<User> users);

    boolean checkAndInsertTweets(Collection<Tweet> tweets);

    boolean deleteExistingTweets();
}
