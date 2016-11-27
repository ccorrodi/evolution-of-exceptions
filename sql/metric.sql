SELECT b1.project_name, b1.commit_timestamp, b1.commit_hash, b1.code_lines as loc, mc, ms, ml, me, ma, cc, cs, cl, ca, fa, tc, ts, tl, ta, tf, uch, uunch, loc_catch, loc_finally, caught_exception
	FROM (SELECT project_name, commit_timestamp, commit_hash, code_lines,
        count(t1.id) as ma,
		count(CASE WHEN custom THEN 1 END) as mc,
  		count(CASE WHEN standard AND NOT custom THEN 1 END) as ms,
  		count(CASE WHEN library THEN 1 END) as ml,
  		count(CASE WHEN custom OR standard OR library THEN 1 END) as me FROM method_throws_declarations t1
	JOIN commits t2 ON t1.commit_id = t2.id
  	GROUP BY commit_timestamp, commit_hash, project_name, code_lines) b1
    JOIN (SELECT commit_timestamp, commit_hash,
	  count(CASE WHEN custom THEN 1 END) as cc,
    count(CASE WHEN standard AND NOT custom THEN 1 END) as cs,
    count(CASE WHEN library THEN 1 END) as cl,
    count(CASE WHEN custom OR standard OR library THEN 1 END) as ca,
    count(CASE WHEN finally_block THEN 1 END) as fa,
    sum(loc_catch) as loc_catch,
    sum(loc_finally) as loc_finally FROM trycatchs t1
	JOIN commits t2 ON t1.commit_id = t2.id
    GROUP BY commit_timestamp, commit_hash) b2 ON b1.commit_hash = b2.commit_hash
  JOIN
    (SELECT commit_timestamp, commit_hash,
    	count(CASE WHEN custom THEN 1 END) as tc,
      	count(CASE WHEN standard AND NOT custom THEN 1 END) as ts,
      	count(CASE WHEN library AND exception_class NOT LIKE '+++%' THEN 1 END) as tl,
				count(CASE WHEN exception_class LIKE '+++%' THEN 1 END) as tf,
				count(CASE WHEN standard AND string_arg THEN 1 END) as tsm,
      	count(CASE WHEN custom OR standard OR library THEN 1 END) as ta FROM throws t1
    	JOIN commits t2 ON t1.commit_id = t2.id
      	GROUP BY commit_timestamp, commit_hash) b3 ON b1.commit_hash = b3.commit_hash
  JOIN
    (SELECT count(CASE WHEN type = 'checked' THEN 1 END) as uch,
      count(CASE WHEN type = 'unchecked' THEN 1 END) as uunch,
      commit_hash FROM exception_classes t1
	     JOIN commits t2 ON t1.commit_id = t2.id
	      GROUP BY commit_hash) b4 ON b1.commit_hash = b4.commit_hash
  JOIN
    (SELECT count(CASE WHEN types = 'Exception,' THEN 1 END) as caught_exception, commit_hash FROM trycatchs t1
	   JOIN commits t2 ON t1.commit_id = t2.id
	    GROUP BY commit_hash) b5 ON b1.commit_hash = b5.commit_hash
  WHERE b1.commit_timestamp <> '1970-01-01 01:00:00'
  ORDER BY project_name, commit_timestamp;
