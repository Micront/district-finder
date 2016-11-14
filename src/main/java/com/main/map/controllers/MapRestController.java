package com.main.map.controllers;

import com.main.getOpenData.DAO.CompanyDao;
import com.main.map.models.AreaInformation;
import com.main.map.models.EstimateParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class MapRestController {
    @Autowired
    private CompanyDao companyDao;

    @PostMapping(value  = "/get_info")
    public String PostEstimateParam(@RequestBody String jsonQueryStr){
        EstimateParam e = new EstimateParam();
        String info = e.getInfo(jsonQueryStr);
        return info;
    }

    @PostMapping(value = "/get_query")
    public String PostAreaInformation(@RequestBody String jsonQueryStr) {
        AreaInformation areaInformation = new AreaInformation();
        return areaInformation.requestHandling(jsonQueryStr, companyDao);
    }
}
