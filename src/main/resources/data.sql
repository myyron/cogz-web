INSERT INTO user (id,username,password,first_name,last_name,enabled) 
VALUES (1,'altrax','$2a$10$oh/P3K..0rAHq85ZhhNlIuNnlEZKz8sv.h6CmHKfNtxQiKR3elwfC','Myyron','Latorilla',true);

INSERT INTO user_role (id,user_id,role,enabled) VALUES (1,1,'SYS_ADMIN',true);
