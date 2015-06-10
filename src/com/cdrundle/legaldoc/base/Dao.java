package com.cdrundle.legaldoc.base;

import org.springframework.data.jpa.repository.JpaRepository;

public interface Dao<T extends LongIdEntity> extends JpaRepository<T, Long>{

}
