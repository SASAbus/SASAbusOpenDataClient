/*
 * SASAbusOpenDataClient - Client and business logic for SASA bus open data
 *
 * BusTripCalculator.java
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

package it.sasabz.sasabus.opendata.client.logic;

import it.sasabz.sasabus.opendata.client.SASAbusOpenDataLocalStorage;
import it.sasabz.sasabus.opendata.client.model.BusDefaultWaitTimeAtStop;
import it.sasabz.sasabus.opendata.client.model.BusDefaultWaitTimeAtStopList;
import it.sasabz.sasabus.opendata.client.model.BusExceptionTimeBetweenStops;
import it.sasabz.sasabus.opendata.client.model.BusLineWaitTimeAtStop;
import it.sasabz.sasabus.opendata.client.model.BusLineWaitTimeAtStopList;
import it.sasabz.sasabus.opendata.client.model.BusPathList;
import it.sasabz.sasabus.opendata.client.model.BusStandardTimeBetweenStops;
import it.sasabz.sasabus.opendata.client.model.BusStandardTimeBetweenStopsList;
import it.sasabz.sasabus.opendata.client.model.BusTripBusStopTime;
import it.sasabz.sasabus.opendata.client.model.BusTripStartTime;
import it.sasabz.sasabus.opendata.client.model.BusWaitTimeAtStop;

import java.io.IOException;
import java.util.ArrayList;

public class BusTripCalculator
{
   public static BusTripBusStopTime[] calculateBusStopTimes(int LI_NR,
                                                            int STR_LI_VAR,
                                                            BusTripStartTime busTripStartTime,
                                                            SASAbusOpenDataLocalStorage localStorage) throws IOException
   {

      long start = System.currentTimeMillis();

      BusPathList busPathList = localStorage.getBusPathList();
      Integer[] busStops = busPathList.findBusPathLine(LI_NR).findBusPathVariant(STR_LI_VAR).getBusStops();
      BusStandardTimeBetweenStopsList standardTimes = localStorage.getBusStandardTimeBetweenStopsList();
      BusDefaultWaitTimeAtStopList defaultWaitTimeAtStopList = localStorage.getBusDefaultWaitTimeAtStopList();
      BusLineWaitTimeAtStopList busLineWaitTimeAtStopList = localStorage.getBusLineWaitTimeAtStopList();
      ArrayList<BusTripBusStopTime> stopTimes = new ArrayList<BusTripBusStopTime>();
      int currentTime = busTripStartTime.getSeconds();
      for (int i = 0; i < busStops.length; i++)
      {
         if (i > 0) // Calculate departure adding the time from the last bus stop and wait time
         {
            // There is defined an exceptional time between the last bus stop and the current for this trip?
            BusExceptionTimeBetweenStops excepetionTime = localStorage.getBusExceptionTimeBetweenStopsList().findBusExceptionTimeBetweenStops(busTripStartTime.getId(),
                                                                                                                                              busStops[i - 1]);
            if (excepetionTime != null)
            {
               currentTime += excepetionTime.getSeconds();
            }
            else
            // Use standard time!
            {
               BusStandardTimeBetweenStops standardTime = standardTimes.findBusStandardTimeBetweenStops(busStops[i - 1],
                                                                                                        busTripStartTime.getGroupId(),
                                                                                                        busStops[i]);
               currentTime += standardTime.getSeconds();
            }

            if (i < busStops.length - 1) // Wait time isn't used for the first and last stop.
            {
               // There is an exceptional wait time for this trip?
               BusWaitTimeAtStop waitTime = localStorage.getBusWaitTimeAtStopList().findBusWaitTimeAtStop(busTripStartTime.getId(),
                                                                                                          busStops[i]);
               if (waitTime != null)
               {
                  currentTime += waitTime.getSeconds();
               }
               else
               {
                  // This line has a wait time at this stop?
                  BusLineWaitTimeAtStop busLineWaitTime = busLineWaitTimeAtStopList.findBusLineWaitTimeAtStop(LI_NR,
                                                                                                              STR_LI_VAR,
                                                                                                              busTripStartTime.getGroupId(),
                                                                                                              i + 1);

                  if (busLineWaitTime != null)
                  {
                     currentTime += busLineWaitTime.getSeconds();
                  }
                  else
                  {
                     // There is a default wait time at this busstop?
                     BusDefaultWaitTimeAtStop defaultWaitTime = defaultWaitTimeAtStopList.findBusWaitTimeAtStop(busTripStartTime.getGroupId(),
                                                                                                                busStops[i]);
                     if (defaultWaitTime != null)
                     {
                        currentTime += defaultWaitTime.getTime();
                     }
                  }
               }
            }
         }

         // Only for debug!
         String busStopName = localStorage.getBusStations().findBusStop(busStops[i]).getBusStation().getName_it();
         String debugTime = formatSeconds(currentTime);

         BusTripBusStopTime busStopTime = new BusTripBusStopTime();
         busStopTime.setBusStop(busStops[i]);
         busStopTime.setSeconds(currentTime);
         stopTimes.add(busStopTime);

      }

      long stop = System.currentTimeMillis();

      return stopTimes.toArray(new BusTripBusStopTime[0]);
   }

   public static String formatSeconds(long seconds)
   {
      long sec = seconds % 60;
      long min = seconds / 60 % 60;
      long hour = seconds / 3600;
      return "" + twoDigits(hour) + ":" + twoDigits(min);
   }

   public static String twoDigits(long num)
   {
      String ret = "0" + num;
      ret = ret.substring(ret.length() - 2);
      return ret;
   }
}
