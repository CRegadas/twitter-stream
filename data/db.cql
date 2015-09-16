
DROP KEYSPACE IF EXISTS twitter_stream;

CREATE KEYSPACE IF NOT EXISTS twitter_stream WITH REPLICATION = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};


CREATE TABLE IF NOT EXISTS twitter_stream.tweet_hashtag_count (
    hashtag TEXT PRIMARY KEY,
    count COUNTER
);

CREATE TABLE IF NOT EXISTS twitter_stream.top_hashtag (
    hashtag TEXT PRIMARY KEY,
    position INT
);

TRUNCATE twitter_stream.tweet_hashtag_count;

TRUNCATE twitter_stream.top_hashtag;