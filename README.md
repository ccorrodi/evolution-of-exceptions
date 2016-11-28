# evolution-of-exceptions

### Dependencies
Java 8 Required
cloc
postgresql http://postgresapp.com / https://www.pgadmin.org

#### Install brew
```
brew install cloc
```

### Setup database
```
CREATE USER seminar;
CREATE DATABASE seminar;
GRANT ALL PRIVILEGES ON DATABASE seminar to seminar;
```

### Compile the Project
```
mvn assembly:assembly -DdescriptorId=jar-with-dependencies
```

### Configure the Interval
The interval can be configured inside run.sh with the variable interval_months.

### Repositories
Add the git repositories to repo_list.txt

### Run
./run.sh repo_list.txt

### Measures
The sql query for the metrics is found inside sql/metric.sql
