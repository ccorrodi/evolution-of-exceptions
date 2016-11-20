-- change the project_id
SELECT count(DISTINCT source), commit_timestamp FROM method_throws_declarations t1
	JOIN method_throws_declaration_types t2 ON t1.id = t2.method_throws_declaration_id
  JOIN commits t3 ON t3.id = t1.commit_id
  WHERE project_id = 1 AND userdefined = true
  GROUP BY t3.commit_timestamp, userdefined;
