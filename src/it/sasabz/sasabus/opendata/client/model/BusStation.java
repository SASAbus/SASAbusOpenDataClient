/*
 * SASAbusOpenDataClient - Client and business logic for SASA bus open data
 *
 * BusStation.java
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

package it.sasabz.sasabus.opendata.client.model;

import java.io.Serializable;
import java.util.ArrayList;

public class BusStation implements Serializable
{

   String             ORT_NAME;

   ArrayList<BusStop> busstops;

   ArrayList<Integer> busLineIds;

   public String getName_it()
   {
      return ORT_NAME;
   }

   public String getName_de()
   {
      return ORT_NAME;
   }

   public BusStop[] getBusStops()
   {
      return busstops.toArray(new BusStop[busstops.size()]);
   }

   public void addBusLine(int LI_NR)
   {
      if (!busLineIds.contains(LI_NR))
      {
         this.busLineIds.add(LI_NR);
      }

   }

   public Integer[] getBusLines()
   {
      return busLineIds.toArray(new Integer[busLineIds.size()]);
   }

}
