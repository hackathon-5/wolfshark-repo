--// setup postgres plugins
-- Migration SQL that makes the change goes here.

CREATE EXTENSION "uuid-ossp";

--//@UNDO
-- SQL to undo the change goes here.

DROP EXTENSION "uuid-ossp";
