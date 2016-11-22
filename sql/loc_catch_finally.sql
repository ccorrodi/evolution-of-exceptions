SELECT sum(loc_catch_finally), commit_timestamp, commit_hash FROM trycatchs t1
	JOIN commits t2 ON t1.commit_id = t2.id
  WHERE project_id = 1
  GROUP BY commit_timestamp, commit_hash
  ORDER BY commit_timestamp;
