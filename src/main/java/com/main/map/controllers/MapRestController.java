package com.main.map.controllers;

import com.google.gson.*;
import com.main.getOpenData.DAO.Company;
import com.main.getOpenData.DAO.CompanyDao;
import com.main.getOpenData.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import static com.main.map.models.EstimateParam.getRaiting;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
public class MapRestController {
    final int TYPES_NUMBER = 7;


    @PostMapping(value  = "/get_info")
    public String PostEstimateParam(@RequestBody String jsonQueryStr){
        String rating = getRaiting(jsonQueryStr);
        return "";
    }

    @PostMapping(value = "/get_query")
    public String PostAreaInformation(@RequestBody String jsonQueryStr) {
        System.out.println("Json:" + jsonQueryStr);
        /*JSONObject outputJsonObj = new JSONObject();
        outputJsonObj.put("output", output);

        return outputJsonObj.toString();*/


        JsonObject rootObject = (new JsonParser()).parse(jsonQueryStr).getAsJsonObject(); // чтение главного объекта

        JsonArray geometry = rootObject.getAsJsonObject("target").getAsJsonArray("coordinates");
        double[] coordinates = {geometry.get(0).getAsDouble(), geometry.get(1).getAsDouble()};// координаты клика
        geometry = rootObject.getAsJsonArray("northPoint");
        double[] coordinatesRadius = {geometry.get(0).getAsDouble(), geometry.get(1).getAsDouble()};// координаты радиуса клика

        //double radius = rootObject.getAsJsonPrimitive("radius").getAsDouble();// радиус круга вокруг клика


        JsonArray estimateParams = rootObject.getAsJsonArray("estimateParams");
        Iterator it = estimateParams.iterator();
        JsonObject param;
        StringBuilder typeINstr = new StringBuilder();
        double[] importances = new double[TYPES_NUMBER];//важность параметра, номер параметра = индекс
        while (it.hasNext()) {
            param = (JsonObject) it.next();
            int type = param.getAsJsonPrimitive("type").getAsInt();
            double importance = param.getAsJsonPrimitive("importance").getAsDouble();
            importances[type] = importance;
            if (importance > 0) typeINstr.append(type).append(" ");
        }

        int[] typeIN = null;

        if(!typeINstr.toString().equals("")) {
            String[] str = typeINstr.toString().split(" ");
            typeIN = new int[str.length];//       в БД нумерация типов начинается с 1:
            for (int i = 0; i < str.length; i++) typeIN[i] = Integer.parseInt(str[i]) + 1;
        }

        //  ------------------------------------
        //  ответ
        //  ------------------------------------
        JsonObject answerRootObject = new JsonObject(); // ответ


        JsonPrimitive estimate = new JsonPrimitive("4.5");// заглушка на оценку
        answerRootObject.add("estimate", estimate);


        JsonObject geoObject = parseDataForGeoObject(getYandexGeocodeJSON(coordinates));
        double[] point = getPoint(geoObject);   //положение(координаты) найденного объекта, по версии Yandex


        JsonObject target = new JsonObject();
        JsonPrimitive address = new JsonPrimitive(getAddress(geoObject));
        target.add("address", address);


        JsonArray coordinatesJson = new JsonArray();
        coordinatesJson.add(point[0]);
        coordinatesJson.add(point[1]);
        target.add("coordinates", coordinatesJson);


        answerRootObject.add("target", target);


        JsonArray infrastructure = new JsonArray(); // для ответа - массив объектов в радиусе

        if(typeIN!=null) {
            //int[] typeIN = {1,2,3,4,5,6,7};
            Point p0 = new Point(coordinates[1], coordinates[0]), pRad = new Point(coordinatesRadius[1], coordinatesRadius[0]);
            List<Company> list = getInRadius(p0, pRad, typeIN, companyDao);

            JsonObject company;
            for (Company com : list) {
                company = new JsonObject();
                JsonPrimitive companyAddress = new JsonPrimitive(com.getAddress());
                company.add("address", companyAddress);
                JsonPrimitive companyName = new JsonPrimitive(com.getName());
                company.add("name", companyName);
                JsonPrimitive companyType = new JsonPrimitive(com.getIdType());
                company.add("type", companyType);
                JsonArray companyCoordinates = new JsonArray();//coordinates
                companyCoordinates.add(com.getCoordinateY());
                companyCoordinates.add(com.getCoordinateX());
                company.add("coordinates", companyCoordinates);

                infrastructure.add(company);
            }
        }
        answerRootObject.add("infrastructure", infrastructure);

        System.out.println(answerRootObject.toString()); //ответ готов
        return answerRootObject.toString();
    }

    @Autowired
    private CompanyDao companyDao;


    public String getAddress(JsonObject geoObject) {
        return geoObject.getAsJsonPrimitive("name").getAsString();
    }

    public double[] getPoint(JsonObject geoObject) {
        String[] coord = geoObject.getAsJsonObject("Point").getAsJsonPrimitive("pos").getAsString().split(" ");
        double c[] = {Double.parseDouble(coord[1]), Double.parseDouble(coord[0])};
        return c;
    }


    public String getYandexGeocodeJSON(double[] coordinates) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        StringBuilder result = new StringBuilder();
        try {
            url = new URL("https://geocode-maps.yandex.ru/1.x/?format=json;geocode=" + coordinates[1] + "," + coordinates[0]);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();
    }


    public JsonObject parseDataForGeoObject(String strJson) {
        JsonObject rootObject = (new JsonParser()).parse(strJson).getAsJsonObject(); // чтение главного объекта
//        return rootObject.getAsJsonObject("response")
//                .getAsJsonObject("GeoObjectCollection")
//                .getAsJsonArray("featureMember").get(0).getAsJsonObject()
//                .getAsJsonObject("GeoObject")
//                .getAsJsonObject("metaDataProperty")
//                .getAsJsonObject("GeocoderMetaData")
//                .getAsJsonObject("AddressDetails")
//                .getAsJsonObject("Country")
//                .getAsJsonPrimitive("AddressLine").getAsString();
        return rootObject.getAsJsonObject("response")
                .getAsJsonObject("GeoObjectCollection")
                .getAsJsonArray("featureMember").get(0).getAsJsonObject()
                .getAsJsonObject("GeoObject");
    }




    public List<Company> getInRadius(Point centreCoor, Point upCoor, int[] typeIN, CompanyDao companyDao) {
        double radius = upCoor.getY() - centreCoor.getY();
        double radiusSquared = Math.pow(radius, 2);
        double xLeft = centreCoor.getX() - radius;
        double xRight = centreCoor.getX() + radius;
        double yBottom = centreCoor.getY() - radius;
        double yTop = centreCoor.getY() + radius;

        Iterator<Company> iterator = companyDao.findByRadius(xLeft, xRight, yBottom, yTop, typeIN).iterator();
        List<Company> listCompany = new ArrayList<>(15);
        while (iterator.hasNext()) {
            Company company = iterator.next();
            if (Math.pow(company.getCoordinateX() - centreCoor.getX(), 2) +
                    Math.pow(company.getCoordinateY() - centreCoor.getY(), 2) < radiusSquared) {
                listCompany.add(company);
            }
        }
        // listCompany.forEach(item -> System.out.println(item.getName()));
        return listCompany;
    }
}
