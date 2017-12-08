create table if not exists plugin (
  id int not null auto_increment primary key,
  intellij_plugin_id varchar(255) not null,
  name varchar(255) not null,
  short_description varchar(255),
  description clob,
  version_number intellij_version not null,
  since_intellij_build intellij_version not null,
  until_intellij_build intellij_version not null,
  downloads int not null,
  size int not null,
  vendor varchar(255),
  vendor_email varchar(255),
  vendor_url varchar(255),
  constraint unique_plugin_version unique (intellij_plugin_id, version_number)
);

create unique index unique_plugin_version_idx
  on plugin (intellij_plugin_id, version_number);