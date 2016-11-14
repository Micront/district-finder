package com.main.map.models;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.main.getOpenData.DAO.Company;
import com.main.getOpenData.DAO.CompanyDao;
import com.main.getOpenData.Point;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AreaInformation {
    private final int TYPES_NUMBER = 7;
    private double[] importances;
    private double[] coordinates;
    private double[] coordinatesRadius;
    private int[] typeIN;
    private double estimate;

    public String requestHandling(String jsonQueryStr, CompanyDao companyDao) {
        parsingJsonQueryStr(jsonQueryStr);
        calculateEstimate();
        return createAnswerJson(companyDao);
    }

    private void parsingJsonQueryStr(String jsonQueryStr) {
        System.out.println("Json:" + jsonQueryStr);
        /*JSONObject outputJsonObj = new JSONObject();
        outputJsonObj.put("output", output);

        return outputJsonObj.toString();*/

        JsonObject rootObject = (new JsonParser()).parse(jsonQueryStr).getAsJsonObject(); // чтение главного объекта

        JsonArray geometry = rootObject.getAsJsonObject("target").getAsJsonArray("coordinates");
        coordinates = new double[]{geometry.get(0).getAsDouble(), geometry.get(1).getAsDouble()};// координаты клика
        geometry = rootObject.getAsJsonArray("northPoint");
        coordinatesRadius = new double[]{geometry.get(0).getAsDouble(), geometry.get(1).getAsDouble()};// координаты радиуса клика

        //double radius = rootObject.getAsJsonPrimitive("radius").getAsDouble();// радиус круга вокруг клика

        JsonArray estimateParams = rootObject.getAsJsonArray("estimateParams");
        Iterator it = estimateParams.iterator();
        JsonObject param;
        StringBuilder typeINstr = new StringBuilder();
        importances = new double[TYPES_NUMBER];
        while (it.hasNext()) {
            param = (JsonObject) it.next();
            int type = param.getAsJsonPrimitive("type").getAsInt();
            double importance = param.getAsJsonPrimitive("importance").getAsDouble();
            importances[type] = importance;
            if (importance > 0) typeINstr.append(type).append(" ");
        }

        if (!typeINstr.toString().equals("")) {
            String[] str = typeINstr.toString().split(" ");
            typeIN = new int[str.length];//       в БД нумерация типов начинается с 1:
            for (int i = 0; i < str.length; i++) typeIN[i] = Integer.parseInt(str[i]) + 1;
        }
    }

    private void calculateEstimate() {
        double result = 0;
        for (double x : importances) {
            result += x;
        }
        result /= importances.length;
        result = Math.round(result*100);
        estimate = result/100;
    }

    private String createAnswerJson(CompanyDao companyDao) {
        JsonObject answerRootObject = new JsonObject();
        answerRootObject.add("estimate", new JsonPrimitive(estimate));

        JsonObject districtRating = new JsonObject();
        districtRating.add("safety", new JsonPrimitive(1));
        districtRating.add("life_quality", new JsonPrimitive(1));
        districtRating.add("transport_quality", new JsonPrimitive(1));
        districtRating.add("rest_availability", new JsonPrimitive(1));
        districtRating.add("parks_availability", new JsonPrimitive(1));
        answerRootObject.add("district-rating", districtRating);

        JsonArray arrayMetro = new JsonArray();
        JsonObject metro = new JsonObject();
        metro.add("name", new JsonPrimitive("Невский проспект"));
        metro.add("distance", new JsonPrimitive("1.5 км"));
        metro.add("color", new JsonPrimitive(1));
        arrayMetro.add(metro);
        JsonObject metro2 = new JsonObject();
        metro2.add("name", new JsonPrimitive("Парк победы"));
        metro2.add("distance", new JsonPrimitive("4.8 км"));
        metro2.add("color", new JsonPrimitive(2));
        arrayMetro.add(metro2);
        answerRootObject.add("metro", arrayMetro);

//        JsonObject geoObject = parseDataForGeoObject(getYandexGeocodeJSON(coordinates));
//        double[] point = getPoint(geoObject);   //положение(координаты) найденного объекта, по версии Yandex
//
//        JsonObject target = new JsonObject();
//        JsonPrimitive address = new JsonPrimitive(getAddress(geoObject));
//        target.add("address", address);

//        JsonArray coordinatesJson = new JsonArray();
//        coordinatesJson.add(point[0]);
//        coordinatesJson.add(point[1]);
//        target.add("coordinates", coordinatesJson);
//
//        answerRootObject.add("target", target);

        JsonArray infrastructure = new JsonArray(); // для ответа - массив объектов в радиусе

        if (typeIN != null) {
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
                companyCoordinates.add(com.getLatitude());
                companyCoordinates.add(com.getLongitude());
                company.add("coordinates", companyCoordinates);

                infrastructure.add(company);
            }
        }
        answerRootObject.add("infrastructure", infrastructure);

        System.out.println(answerRootObject.toString()); //ответ готов
        return answerRootObject.toString();
    }

    private String getAddress(JsonObject geoObject) {
        return geoObject.getAsJsonPrimitive("name").getAsString();
    }

    private double[] getPoint(JsonObject geoObject) {
        String[] coord = geoObject.getAsJsonObject("Point").getAsJsonPrimitive("pos").getAsString().split(" ");
        double c[] = {Double.parseDouble(coord[1]), Double.parseDouble(coord[0])};
        return c;
    }


    private String getYandexGeocodeJSON(double[] coordinates) {
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


    private JsonObject parseDataForGeoObject(String strJson) {
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


    private List<Company> getInRadius(Point centreCoor, Point upCoor, int[] typeIN, CompanyDao companyDao) {
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
            if (Math.pow(company.getLongitude() - centreCoor.getX(), 2) +
                    Math.pow(company.getLatitude() - centreCoor.getY(), 2) < radiusSquared) {
                listCompany.add(company);
            }
        }
        // listCompany.forEach(item -> System.out.println(item.getName()));
        return listCompany;
    }

}

