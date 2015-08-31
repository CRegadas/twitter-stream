
DROP KEYSPACE IF EXISTS twitter_stream

CREATE KEYSPACE IF NOT EXISTS twitter_stream WITH REPLICATION = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
}

CREATE TABLE IF NOT EXISTS twitter_stream.tweet_word_count (
    word TEXT PRIMARY KEY,
    count COUNTER
)

TRUNCATE twitter_stream.tweet_word_count
