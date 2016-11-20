SELECT count(t1.id), commit_timestamp FROM throws t1
    JOIN commits t2 ON t2.id = t1.commit_id
    WHERE project_id = 1 AND userdefined = false
    GROUP BY t2.commit_timestamp;