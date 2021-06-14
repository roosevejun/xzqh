package com.tongtu.xzqh.repository;

import com.tongtu.xzqh.entity.Updateurl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UpdateurlRepository extends JpaRepository<Updateurl, String>, JpaSpecificationExecutor<Updateurl> {

    @Query(value = "SELECT u.* FROM updateurl u WHERE u.isup ='f' ", nativeQuery = true)
    List<Updateurl> findUpdateurlByIsup();
}