package com.main.getOpenData.DAO;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface CompanyDao extends CrudRepository<Company,Long>{
//    @Query(value = "\n" +
//            "select address,name,id_type,coordinateY,coordinateX from Company\n" +
//            "where (id_type in (5,6)) and\n" +
//            "(coordinateX BETWEEN 30.239744 AND 30.239746) AND \n" +
//            "(coordinateY BETWEEN  59.93010 AND 59.93012)")
    @Query(value = "select c from Company c " +
            "where (coordinateX BETWEEN ?1 AND ?2) AND (coordinateY BETWEEN  ?3 AND ?4) AND (id_type in ?5)")
    List<Company> findByRadius(@Param("xLeft") double xLeft, @Param("xRight") double xRight,
                               @Param("yBottom") double yBottom, @Param("yTop") double yTop,
                               @Param("typeIN") int[] typeIN);
}
