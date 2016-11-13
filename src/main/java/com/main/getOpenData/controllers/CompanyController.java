package com.main.getOpenData.controllers;

import com.main.getOpenData.DAO.Company;
import com.main.getOpenData.DAO.CompanyDao;
import com.main.getOpenData.DAO.CompanyType;
import com.main.getOpenData.DAO.CompanyTypeDao;
import com.main.getOpenData.DataYandex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

@Controller
public class CompanyController {

    @RequestMapping(value = "/kind")
    public String kind() {
        String queryText = "детский сад";
        DataYandex dataYandex = new DataYandex(queryText, companyDao,companyTypeDao);
        boolean success = dataYandex.writeDataToBD();

//        String queryText = "Детский са";
//        String queryTextEng = "hospitals";
//        int companyTypeId = 6;
//      /*  Iterator<CompanyType> iterator = companyTypeDao.findAll().iterator();
//        while (iterator.hasNext()) {
//            CompanyType companyType = iterator.next();
//            if (queryTextEng.equals(companyType.getName())) {
//                queryText = translate(queryTextEng);
//                companyTypeId = companyType.getId();
//                break;
//            }
//        }*/

//        DataYandex dataYandex = new DataYandex(queryText, companyTypeId);
//        List<Company> list = dataYandex.getCompanies();

        /*for (Company x : list) {
            companyDao.save(x);
        }*/
        if (success) return "success";
        else return "Problems!";
    }

//    private String translate(String textEng) {
//        String[][] translation = {{"parks", "парк"}, {"malls", "торговый центр"}, {"schools", "школа"}, {"sportCenters", "спортивный центр"},
//                {"rest", "отдых"}, {"hospitals", "больница"}, {"kindergarten", "детский сад"}};
//        for (String[] x : translation) {
//            if (textEng.equals(x[0])) {
//                return x[1];
//            }
//        }
//        return null;
//    }

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private CompanyTypeDao companyTypeDao;
}
