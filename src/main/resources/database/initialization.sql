create schema plugin
//
create table if not exists plugin.plugin (
  id int not null auto_increment primary key,
  intellij_id varchar(255) not null,
  name varchar(255) not null,
  short_description varchar(255),
  description clob,
  constraint unique_intellij_id unique (intellij_id)
)
//
create table if not exists plugin.plugin_version (
  id int not null auto_increment primary key,
  plugin_id int not null,
  version_number varchar(255) not null,
  since_intellij_build intellij_version not null,
  until_intellij_build intellij_version not null,
  downloads int not null,
  size int not null,
  vendor varchar(255),
  vendor_email varchar(255),
  vendor_url varchar(255),
  available_date timestamp,
  discontinue_date timestamp,
  constraint unique_plugin_version unique (plugin_id, version_number)
)
