-- Add indexes for statistic_hour and statistic_day columns to improve query performance

-- Hourly statistics indexes
CREATE INDEX idx_statistic_hour ON github_event_statistic_hourly(statistic_hour);
CREATE INDEX idx_statistic_hour ON github_repo_language_statistic_hourly(statistic_hour);
CREATE INDEX idx_statistic_hour ON github_repo_topic_statistic_hourly(statistic_hour);

-- Daily statistics indexes
CREATE INDEX idx_statistic_day ON github_event_statistic_daily(statistic_day);
CREATE INDEX idx_statistic_day ON github_repo_language_statistic_daily(statistic_day);
CREATE INDEX idx_statistic_day ON github_repo_topic_statistic_daily(statistic_day);