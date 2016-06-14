package cn.com.taiji.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.com.taiji.doman.Logs;

public interface LogsDao extends 
 JpaRepository<Logs, String>, JpaSpecificationExecutor<Logs>{

}
