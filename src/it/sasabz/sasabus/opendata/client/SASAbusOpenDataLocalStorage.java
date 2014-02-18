/*
 * SASAbusOpenDataClient - Client and business logic for SASA bus open data
 *
 * SASAbusOpenDataLocalStorage.java
 *
 * Created: Jan 27, 2014 10:34:00 AM
 *
 * Copyright (C) 2011-2014 Paolo Dongilli, Markus Windegger, Davide Montesin
 *
 * This file is part of SASAbus.
 *
 * SASAbus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SASAbus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with SASAbus.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.sasabz.sasabus.opendata.client;

import it.sasabz.sasabus.opendata.client.model.BusDayType;
import it.sasabz.sasabus.opendata.client.model.BusDayTypeList;
import it.sasabz.sasabus.opendata.client.model.BusDefaultWaitTimeAtStopList;
import it.sasabz.sasabus.opendata.client.model.BusExceptionTimeBetweenStopsList;
import it.sasabz.sasabus.opendata.client.model.BusLine;
import it.sasabz.sasabus.opendata.client.model.BusLineList;
import it.sasabz.sasabus.opendata.client.model.BusLineWaitTimeAtStopList;
import it.sasabz.sasabus.opendata.client.model.BusPathLine;
import it.sasabz.sasabus.opendata.client.model.BusPathList;
import it.sasabz.sasabus.opendata.client.model.BusPathVariant;
import it.sasabz.sasabus.opendata.client.model.BusStandardTimeBetweenStopsList;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.opendata.client.model.BusStationList;
import it.sasabz.sasabus.opendata.client.model.BusStop;
import it.sasabz.sasabus.opendata.client.model.BusTripStartLine;
import it.sasabz.sasabus.opendata.client.model.BusTripStartList;
import it.sasabz.sasabus.opendata.client.model.BusTripStartVariant;
import it.sasabz.sasabus.opendata.client.model.BusWaitTimeAtStopList;
import it.sasabz.sasabus.opendata.client.model.SASAbusOpenDataMarshaller;
import it.sasabz.sasabus.opendata.client.model.SASAbusOpenDataUnmarshaller;
import it.sasabz.sasabus.opendata.client.model.StartDateList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import bz.davide.dmxmljson.marshalling.json.JSONStructure;
import bz.davide.dmxmljson.unmarshalling.IOUtil;
import bz.davide.dmxmljson.unmarshalling.Structure;
import bz.davide.dmxmljson.unmarshalling.json.AsyncJSONDownloader;
import bz.davide.dmxmljson.unmarshalling.json.AsyncJSONDownloaderCallback;
import bz.davide.dmxmljson.unmarshalling.json.JSONParser;

public abstract class SASAbusOpenDataLocalStorage
{

   SASAbusOpenDataUnmarshaller      unmarshaller;

   BusStationList                   REC_ORTCache        = null;
   BusLineList                      REC_LIDCache        = null;
   BusDayTypeList                   FIRMENKALENDERCache = null;
   BusPathList                      LID_VERLAUFCache    = null;
   BusStandardTimeBetweenStopsList  SEL_FZT_FELDCache   = null;
   BusExceptionTimeBetweenStopsList REC_FRT_FZTCache    = null;
   BusWaitTimeAtStopList            REC_FRT_HZTCache    = null;
   BusDefaultWaitTimeAtStopList     ORT_HZTCache        = null;
   BusLineWaitTimeAtStopList        REC_LIVAR_HZTCache  = null;

   //HashMap<Integer, BusTripStartLine> busTripStartLineCache = new HashMap<Integer, BusTripStartLine>();

   JSONParser                       jsonParser          = null;

   public SASAbusOpenDataLocalStorage(JSONParser jsonParser)
   {
      this.jsonParser = jsonParser;
      this.unmarshaller = new SASAbusOpenDataUnmarshaller();
   }

   public String getVersionDateIfExists() throws IOException
   {
      try
      {
         String json = this.getData("BASIS_VER_GUELTIGKEIT");
         if (json == null)
         {
            return null;
         }
         StartDateList list = new StartDateList();
         this.unmarshallSASAbusOpenData(json, list);
         String ret = list.getList()[0].getStartDate();
         return ret;
      }
      catch (Exception exxx)
      {
         throw IOUtil.wrapIntoIOException(exxx);
      }
   }

   public BusStationList getBusStations() throws IOException
   {
      if (this.REC_ORTCache != null)
      {
         return this.REC_ORTCache;
      }
      String json = this.getData("REC_ORT");
      this.REC_ORTCache = new BusStationList();
      long start1 = System.currentTimeMillis();
      this.unmarshallSASAbusOpenData(json, this.REC_ORTCache);
      long start2 = System.currentTimeMillis();
      ArrayList<BusStop> busStops = new ArrayList<BusStop>();
      for (BusStation busStation : this.REC_ORTCache.getList())
      {
         for (BusStop busStop : busStation.getBusStops())
         {
            busStop.setBusStation(busStation);
            busStops.add(busStop);
         }
      }
      this.REC_ORTCache.setBusStopsReferences(busStops.toArray(new BusStop[0]));
      return this.REC_ORTCache;
   }

   public BusLineList getBusLines() throws IOException
   {
      if (this.REC_LIDCache != null)
      {
         return this.REC_LIDCache;
      }
      String json = this.getData("REC_LID");
      this.REC_LIDCache = new BusLineList();
      this.unmarshallSASAbusOpenData(json, this.REC_LIDCache);
      return this.REC_LIDCache;
   }

   public BusTripStartVariant[] getBusTripStarts(int busLineId, int dayTypeId) throws IOException
   {
      String json = this.getData("REC_FRT_LI_NR_" + busLineId + "_TAGESART_NR_" + dayTypeId);
      BusTripStartList list = new BusTripStartList();
      this.unmarshallSASAbusOpenData(json, list);
      BusTripStartLine[] busTripStartLines = list.getList();
      if (busTripStartLines.length == 0)
      {
         return new BusTripStartVariant[0];
      }
      return busTripStartLines[0].getDayTypes()[0].getVariants();
   }

   public BusDayTypeList getBusDayTypeList() throws IOException
   {
      if (this.FIRMENKALENDERCache != null)
      {
         return this.FIRMENKALENDERCache;
      }
      String json = this.getData("FIRMENKALENDER");
      this.FIRMENKALENDERCache = new BusDayTypeList();
      this.unmarshallSASAbusOpenData(json, this.FIRMENKALENDERCache);
      return this.FIRMENKALENDERCache;

   }

   public BusPathList getBusPathList() throws IOException
   {
      if (this.LID_VERLAUFCache != null)
      {
         return this.LID_VERLAUFCache;
      }
      String json = this.getData("LID_VERLAUF");
      this.LID_VERLAUFCache = new BusPathList();
      this.unmarshallSASAbusOpenData(json, this.LID_VERLAUFCache);
      return this.LID_VERLAUFCache;

   }

   public BusStandardTimeBetweenStopsList getBusStandardTimeBetweenStopsList() throws IOException
   {
      if (this.SEL_FZT_FELDCache != null)
      {
         return this.SEL_FZT_FELDCache;
      }
      String json = this.getData("SEL_FZT_FELD");
      this.SEL_FZT_FELDCache = new BusStandardTimeBetweenStopsList();
      this.unmarshallSASAbusOpenData(json, this.SEL_FZT_FELDCache);
      return this.SEL_FZT_FELDCache;

   }

   public BusExceptionTimeBetweenStopsList getBusExceptionTimeBetweenStopsList() throws IOException
   {
      if (this.REC_FRT_FZTCache != null)
      {
         return this.REC_FRT_FZTCache;
      }
      String json = this.getData("REC_FRT_FZT");
      this.REC_FRT_FZTCache = new BusExceptionTimeBetweenStopsList();
      this.unmarshallSASAbusOpenData(json, this.REC_FRT_FZTCache);
      return this.REC_FRT_FZTCache;
   }

   public BusWaitTimeAtStopList getBusWaitTimeAtStopList() throws IOException
   {
      if (this.REC_FRT_HZTCache != null)
      {
         return this.REC_FRT_HZTCache;
      }
      String json = this.getData("REC_FRT_HZT");
      this.REC_FRT_HZTCache = new BusWaitTimeAtStopList();
      this.unmarshallSASAbusOpenData(json, this.REC_FRT_HZTCache);
      return this.REC_FRT_HZTCache;
   }

   public BusDefaultWaitTimeAtStopList getBusDefaultWaitTimeAtStopList() throws IOException
   {
      if (this.ORT_HZTCache != null)
      {
         return this.ORT_HZTCache;
      }
      String json = this.getData("ORT_HZT");
      this.ORT_HZTCache = new BusDefaultWaitTimeAtStopList();
      this.unmarshallSASAbusOpenData(json, this.ORT_HZTCache);
      return this.ORT_HZTCache;
   }

   public BusLineWaitTimeAtStopList getBusLineWaitTimeAtStopList() throws IOException
   {
      if (this.REC_LIVAR_HZTCache != null)
      {
         return this.REC_LIVAR_HZTCache;
      }
      String json = this.getData("REC_LIVAR_HZT");
      this.REC_LIVAR_HZTCache = new BusLineWaitTimeAtStopList();
      this.unmarshallSASAbusOpenData(json, this.REC_LIVAR_HZTCache);
      return this.REC_LIVAR_HZTCache;
   }

   void setDataEmptyCache(HashMap<String, String> key_data) throws IOException
   {
      this.REC_ORTCache = null;
      this.REC_LIDCache = null;
      this.FIRMENKALENDERCache = null;
      this.LID_VERLAUFCache = null;
      this.SEL_FZT_FELDCache = null;
      this.REC_FRT_FZTCache = null;
      this.REC_FRT_HZTCache = null;
      this.ORT_HZTCache = null;
      this.REC_LIVAR_HZTCache = null;
      //busTripStartLineCache.clear();

      //this.setData(key_data);
   }

   protected abstract void setData(String key, String data) throws IOException;

   protected abstract String getData(String key) throws IOException;

   void unmarshallSASAbusOpenData(String json, final Object list) throws IOException
   {
      try
      {

         long time1 = System.currentTimeMillis();
         Structure structure = this.jsonParser.parse(json);
         long time2 = System.currentTimeMillis();

         this.unmarshaller.unmarschall(structure, list);

         long time3 = System.currentTimeMillis();

         final long counterJSON = time2 - time1;
         final long counterUnmarshalling = time3 - time2;

         
      }
      catch (Exception exxx)
      {
         exxx.printStackTrace();
         throw IOUtil.wrapIntoIOException(exxx);

      }
   }

   final private static String[] TABLES = new String[]{
            "REC_LID",
            "LID_VERLAUF",
            "REC_ORT",
            "FIRMENKALENDER",
            "BASIS_VER_GUELTIGKEIT",
            //"REC_FRT",
            "SEL_FZT_FELD",
            "REC_FRT_FZT",
            "REC_FRT_HZT",
            "ORT_HZT",
            "REC_LIVAR_HZT"             };

   public void asyncReadRemoteVersionDate(String baseUrl,
                                          AsyncJSONDownloader jsonDownloader,
                                          final RemoteVersionDateReady callback) throws IOException
   {
      String url = baseUrl + "?type=BASIS_VER_GUELTIGKEIT";
      jsonDownloader.download(url, new AsyncJSONDownloaderCallback()
      {
         @Override
         public void ready(String json)
         {
            try
            {
               json = "{ \"list\": " + json + "}";
               StartDateList list = new StartDateList();
               SASAbusOpenDataLocalStorage.this.unmarshallSASAbusOpenData(json, list);
               String ret = list.getList()[0].getStartDate();
               callback.ready(ret);
            }
            catch (Exception exxx)
            {
               callback.exception(IOUtil.wrapIntoIOException(exxx));
            }
         }

         @Override
         public void exception(IOException ioxxx)
         {
            callback.exception(ioxxx);
         }
      });
   }

   public void asyncDownloadSASAbusOpenDataToLocalStore(final String baseUrl,
                                                        final AsyncJSONDownloader jsonDownloader,
                                                        final SASAbusOpenDataDownloadCallback progressCallback) throws IOException
   {

      ArrayList<String> tables = new ArrayList<String>(Arrays.asList(TABLES));
      this.downloadTable(baseUrl, jsonDownloader, tables, progressCallback);

   }

   private void downloadTable(final String baseUrl,
                              final AsyncJSONDownloader jsonDownloader,
                              final ArrayList<String> tables,
                              final SASAbusOpenDataDownloadCallback progressCallback) throws IOException
   {
      final String table = tables.remove(0);
      progressCallback.progress(TABLES.length - tables.size(), TABLES.length, table);
      String url = baseUrl + "?type=" + table;
      jsonDownloader.download(url, new AsyncJSONDownloaderCallback()
      {
         @Override
         public void ready(String json)
         {
            try
            {
               json = "{ \"list\": " + json + "}"; // Create an obect for the array to easier unmarshalling to java!

               if (table.equals("REC_ORT")) // Precalculate lines of a bus stations
               {
                  SASAbusOpenDataLocalStorage.this.setData(table, json);
                  json = SASAbusOpenDataLocalStorage.this.precalculateBusLinesOfBusStation();
               }

               SASAbusOpenDataLocalStorage.this.setData(table, json);

               if (tables.size() > 0)
               {
                  SASAbusOpenDataLocalStorage.this.downloadTable(baseUrl,
                                                                 jsonDownloader,
                                                                 tables,
                                                                 progressCallback);
               }
               else
               {
                  SASAbusOpenDataLocalStorage.this.downloadStep1Complete(baseUrl,
                                                                         jsonDownloader,
                                                                         progressCallback);
               }
            }
            catch (IOException e)
            {
               progressCallback.exception(e);
            }
         }

         @Override
         public void exception(IOException ioxxx)
         {
            progressCallback.exception(ioxxx);
         }
      });
   }

   private String precalculateBusLinesOfBusStation() throws IOException
   {
      BusStationList busStationList = this.getBusStations();
      BusPathList busPathList = this.getBusPathList();
      for (BusPathLine busPathLine : busPathList.getList())
      {
         for (BusPathVariant busPathVariant : busPathLine.getVariants())
         {
            for (int busStopId : busPathVariant.getBusStops())
            {
               busStationList.findBusStop(busStopId).getBusStation().addBusLine(busPathLine.getLI_NR());
            }
         }
      }
      JSONStructure jsonStructure = new JSONStructure(0);
      try
      {
         new SASAbusOpenDataMarshaller().marschall(busStationList, jsonStructure);
         String json = jsonStructure.toJSON(0);
         return json;
      }
      catch (Exception exxx)
      {
         throw IOUtil.wrapIntoIOException(exxx);
      }

   }

   void downloadStep1Complete(final String baseUrl,
                              final AsyncJSONDownloader jsonDownloader,
                              final SASAbusOpenDataDownloadCallback progressCallback) throws IOException
   {
      BusDayTypeList dayTypeList = this.getBusDayTypeList();
      HashMap<Integer, Void> uniqueDayTypeMap = new HashMap<Integer, Void>();
      for (BusDayType busDayType : dayTypeList.getList())
      {
         uniqueDayTypeMap.put(busDayType.getDayTypeId(), null);
      }
      Integer[] uniqueDayTypeArray = uniqueDayTypeMap.keySet().toArray(new Integer[0]);
      Arrays.sort(uniqueDayTypeArray);

      BusLineList busLineList = this.getBusLines();
      HashMap<Integer, Void> uniqueBusLineMap = new HashMap<Integer, Void>();
      for (BusLine busLine : busLineList.getList())
      {
         uniqueBusLineMap.put(busLine.getLI_NR(), null);
      }
      Integer[] uniqueBusLineArray = uniqueBusLineMap.keySet().toArray(new Integer[0]);
      Arrays.sort(uniqueBusLineArray);

      final ArrayList<String> parameters = new ArrayList<String>();

      for (int busLineId : uniqueBusLineArray)
      {
         for (int dayTypeId : uniqueDayTypeArray)
         {
            parameters.add("REC_FRT&LI_NR=" + busLineId + "&TAGESART_NR=" + dayTypeId);
         }
      }
      this.downloadStep2(baseUrl, jsonDownloader, parameters, parameters.size(), progressCallback);
   }

   void downloadStep2(final String baseUrl,
                      final AsyncJSONDownloader jsonDownloader,
                      final ArrayList<String> parameters,
                      final int total,
                      final SASAbusOpenDataDownloadCallback progressCallback) throws IOException
   {
      final String table = parameters.remove(0);
      progressCallback.progress(total - parameters.size(), total, table);
      String url = baseUrl + "?type=" + table;
      jsonDownloader.download(url, new AsyncJSONDownloaderCallback()
      {
         @Override
         public void ready(String json)
         {
            try
            {
               json = "{ \"list\": " + json + "}"; // Create an obect for the array to easier unmarshalling to java!
               String fileName = table.replaceAll("[&=]", "_");
               SASAbusOpenDataLocalStorage.this.setData(fileName, json);

               if (parameters.size() > 0)
               {
                  SASAbusOpenDataLocalStorage.this.downloadStep2(baseUrl,
                                                                 jsonDownloader,
                                                                 parameters,
                                                                 total,
                                                                 progressCallback);
               }
               else
               {
                  progressCallback.complete();
               }
            }
            catch (IOException e)
            {
               progressCallback.exception(e);
            }
         }

         @Override
         public void exception(IOException ioxxx)
         {
            progressCallback.exception(ioxxx);
         }
      });
   }
}
