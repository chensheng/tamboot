create table user_info (
  id bigint(20),
  version bigint(20),
  create_time date,
  creator bigint(20),
  modify_time date,
  modifier bigint(20),
  username varchar(256),
  password varchar(512)
);