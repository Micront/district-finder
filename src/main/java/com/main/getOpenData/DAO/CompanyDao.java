package com.main.getOpenData.DAO;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

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
            "where (lng BETWEEN ?1 AND ?2) AND (lat BETWEEN  ?3 AND ?4) AND (id_type in ?5)")
    List<Company> findByRadius(double xLeft,  double xRight,
                                double yBottom,  double yTop, int[] typeIN);
}
