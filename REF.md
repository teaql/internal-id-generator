```

protected String getNextId() {
    TransactionTemplate transactionTemplate =
        new TransactionTemplate(Beans.getBean(PlatformTransactionManager.class));
    transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
    IdSpace idSpace =
        transactionTemplate.execute(
            t -> {
              IdSpace current = null;
              try {
                current =
                    getJdbcTemplate()
                        .queryForObject(
                            "select * from id_space_data where type_name = ? for update",
                            new Object[] {getName()},
                            new IdSpaceMapper());
              } catch (EmptyResultDataAccessException e) {
                
              }
              if (current == null) {
                String SQL = "select max(id) from " + getName() + "_data";
                String maxId = null;
                try {
                  maxId = getJdbcTemplate().queryForObject(SQL, String.class);
                } catch (EmptyResultDataAccessException e) {

                }
                Long max = 0l;
                String prefix = getShortName(this.getName());
                if (maxId != null) {
                  Object ret[] = parse(maxId);
                  max = (Long) ret[1];
                  prefix = (String) ret[0];
                }
                getJdbcTemplate()
                    .update(
                        "insert into id_space_data values (?, ?, ?, ?, ?, ?, ?)",
                        new Object[] {getName(), getName(), prefix, 6, max + 1, "UD000001", 1});
                return new IdSpace()
                    .updateCurrent(max + 1)
                    .updatePrefix(prefix)
                    .updateInitDigitalLength(6);
              }

              getJdbcTemplate()
                  .update(
                      "update id_space_data set current = current + 1 where type_name = ? ",
                      new Object[] {getName()});
              current.increaseCurrent(1l);
              return current;
            });

    return idSpace.getPrefix()
        + NumberUtil.decimalFormat(
            StrUtil.repeat('0', idSpace.getInitDigitalLength()), idSpace.getCurrent());
  }


```

