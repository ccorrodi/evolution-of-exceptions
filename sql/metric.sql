SELECT b1.project_name, b1.commit_timestamp, b1.commit_hash, b1.code_lines as loc, throws_declarations_custom, throws_declarations_standard, throws_declarations_library, throws_declarations_total, total_methods, catch_custom, catch_standard, catch_library, catch_total, finally_blocks, throw_custom, throw_standard, throw_library, throw_total, throw_unknown, throw_standard_with_string_literal, checked, unchecked, loc_catch, loc_finally, caught_exception
	FROM (SELECT project_name, commit_timestamp, commit_hash, code_lines,
        count(t1.id) as total_methods,
		count(CASE WHEN custom THEN 1 END) as throws_declarations_custom,
  		count(CASE WHEN standard AND NOT custom THEN 1 END) as throws_declarations_standard,
  		count(CASE WHEN library THEN 1 END) as throws_declarations_library,
  		count(CASE WHEN custom OR standard OR library THEN 1 END) as throws_declarations_total FROM method_throws_declarations t1
	JOIN commits t2 ON t1.commit_id = t2.id
  	GROUP BY commit_timestamp, commit_hash, project_name, code_lines) b1
    JOIN (SELECT commit_timestamp, commit_hash,
	  count(CASE WHEN custom THEN 1 END) as catch_custom,
    count(CASE WHEN standard AND NOT custom THEN 1 END) as catch_standard,
    count(CASE WHEN library THEN 1 END) as catch_library,
    count(CASE WHEN custom OR standard OR library THEN 1 END) as catch_total,
    count(CASE WHEN finally_block THEN 1 END) as finally_blocks,
    sum(loc_catch) as loc_catch,
    sum(loc_finally) as loc_finally FROM trycatchs t1
	JOIN commits t2 ON t1.commit_id = t2.id
    GROUP BY commit_timestamp, commit_hash) b2 ON b1.commit_hash = b2.commit_hash
  JOIN
    (SELECT commit_timestamp, commit_hash,
    	count(CASE WHEN custom THEN 1 END) as throw_custom,
      	count(CASE WHEN standard AND NOT custom THEN 1 END) as throw_standard,
      	count(CASE WHEN library AND exception_class NOT LIKE '+++%' THEN 1 END) as throw_library,
		count(CASE WHEN exception_class LIKE '+++%' THEN 1 END) as throw_unknown,
		count(CASE WHEN standard AND string_arg THEN 1 END) as throw_standard_with_string_literal,
      	count(CASE WHEN custom OR standard OR library THEN 1 END) as throw_total FROM throws t1
    	JOIN commits t2 ON t1.commit_id = t2.id
      	GROUP BY commit_timestamp, commit_hash) b3 ON b1.commit_hash = b3.commit_hash
  JOIN
    (SELECT count(CASE WHEN type = 'checked' THEN 1 END) as checked,
      count(CASE WHEN type = 'unchecked' THEN 1 END) as unchecked,
      commit_hash FROM exception_classes t1
	     JOIN commits t2 ON t1.commit_id = t2.id
	      GROUP BY commit_hash) b4 ON b1.commit_hash = b4.commit_hash
  JOIN
    (SELECT count(CASE WHEN types = 'Exception,' THEN 1 END) as caught_exception, commit_hash FROM trycatchs t1
	   JOIN commits t2 ON t1.commit_id = t2.id
	    GROUP BY commit_hash) b5 ON b1.commit_hash = b5.commit_hash
  WHERE b1.commit_timestamp <> '1970-01-01 01:00:00'
  ORDER BY project_name, commit_timestamp;
