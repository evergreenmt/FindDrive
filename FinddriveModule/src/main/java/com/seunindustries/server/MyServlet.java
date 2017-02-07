/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.seunindustries.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

//import javax.servlet.http.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MyServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Please use the form to POST to this url");
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        StringBuffer jb = new StringBuffer();
        String line = null;
        String result = null;

        Gson gson = new Gson();

        try {
            BufferedReader reader = req.getReader();

            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
            result = jb.toString();

            DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

            Type type = new TypeToken<List<RawData>>(){}.getType();
            ArrayList<RawData> RawDataList = gson.fromJson(result, type);

            RawData inpList = RawDataList.get(0);

            if (inpList.getCmd().equals("PUT")) {
                int nSize = RawDataList.size();

                for (int i = 0; i < nSize; i++) {
                    RawData data = RawDataList.get(i);
                    Entity entityRawData = new Entity("RawData");

                    entityRawData.setProperty("DestID", data.getDestID());
                    entityRawData.setProperty("DestName", data.getDestName());
                    entityRawData.setProperty("Lat", data.getLat());
                    entityRawData.setProperty("Lgt", data.getLgt());
                    entityRawData.setProperty("Addr", data.getAddr());
                    entityRawData.setProperty("ListID", data.getListID());
                    entityRawData.setProperty("ListName", data.getListName());

                    ds.put(entityRawData);
                }

                Entity entityListData = new Entity("ListData");
                entityListData.setProperty("ListID", inpList.getListID());
                entityListData.setProperty("ListName", inpList.getListName());

                ds.put(entityListData);


                String job = gson.toJson(RawDataList, type);
                resp.setContentType("application/json");
                resp.getWriter().println(inpList.getCmd());//job);

            } else if (inpList.getCmd().equals("GETLIST")) {
                ArrayList<RawData> ListData = new ArrayList<RawData>();

                //result = "1111";
                Query q = new Query("ListData").addSort("ListID", Query.SortDirection.ASCENDING);

                PreparedQuery pq = ds.prepare(q);

                //result = "count:" + pq.countEntities();

                for (Entity entityRawData : pq.asIterable()) {
                    RawData rawData = new RawData();
                    rawData.setCmd(inpList.getCmd());
                    rawData.setListID(Integer.parseInt(entityRawData.getProperty("ListID").toString()));
                    rawData.setListName(entityRawData.getProperty("ListName").toString());

                    ListData.add(rawData);
                }
                //result = result + "\n3333";
                String job = gson.toJson(ListData, type);
                resp.setContentType("application/json");
                resp.getWriter().println(job);

            } else if (inpList.getCmd().equals("GETDEST")) {
                ArrayList<RawData> ListData = new ArrayList<RawData>();
/*
                Query q = new Query("RawData").addFilter("ListID", Query.FilterOperator.EQUAL, inpList.getListID() + "")
                        .addSort("DestID", Query.SortDirection.ASCENDING);
*/
                String SearchID = inpList.getListID() + "";
                Query q = new Query("RawData").addFilter("ListID", FilterOperator.EQUAL, inpList.getListID());
                //q.addSort("DestID", Query.SortDirection.ASCENDING);

                PreparedQuery pq = ds.prepare(q);

                //result = "count:" + pq.countEntities();

                for (Entity entityRawData : pq.asIterable()) {

                    RawData rawData = new RawData();
                    rawData.setCmd(inpList.getCmd());
                    rawData.setDestID(Integer.parseInt(entityRawData.getProperty("DestID").toString()));
                    rawData.setDestName(entityRawData.getProperty("DestName").toString());
                    rawData.setLat(Double.parseDouble(entityRawData.getProperty("Lat").toString()));
                    rawData.setLgt(Double.parseDouble(entityRawData.getProperty("Lgt").toString()));
                    rawData.setAddr(entityRawData.getProperty("Addr").toString());
                    rawData.setListID(Integer.parseInt(entityRawData.getProperty("ListID").toString()));
                    rawData.setListName(entityRawData.getProperty("ListName").toString());

                    ListData.add(rawData);
                }
                //result = result + "\n3333";

                String job = gson.toJson(ListData, type);
                resp.setContentType("application/json");
                resp.getWriter().println(job);

            }

        } catch (Exception e) {
            result = "Server Error : "+e.getStackTrace();
        }
    }
}
