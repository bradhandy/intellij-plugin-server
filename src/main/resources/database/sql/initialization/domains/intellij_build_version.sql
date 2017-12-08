create domain if not exists intellij_version as varchar(255)
  check is_intellij_version(value)