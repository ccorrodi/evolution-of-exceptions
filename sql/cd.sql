SELECT count(t1.id), commit_timestamp FROM trycatchs t1
    JOIN commits t2 ON t2.id = t1.commit_id
    JOIN tcbexceptions t3 ON t3.tcb_id = t1.id
    WHERE project_id = 1 AND userdefined = false
    GROUP BY t2.commit_timestamp
    ORDER BY t2.commit_timestamp;
