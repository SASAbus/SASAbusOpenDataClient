/*
 * SASAbusOpenDataClient - Client and business logic for SASA bus open data
 *
 * BusLineWaitTimeAtStopList.java
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

import bz.davide.dmxmljson.util.IntIntHashMap;

public class BusLineWaitTimeAtStopList
{
   BusLineWaitTimeAtStop[]                        list;

   transient IntIntHashMap<BusLineWaitTimeAtStop> cache = null;

   public BusLineWaitTimeAtStop findBusLineWaitTimeAtStop(int LI_NR, int STR_LI_VAR, int FGR_NR, int LI_LFD_NR)
   {
      if (cache == null)
      {
         cache = new IntIntHashMap<BusLineWaitTimeAtStop>();
         for (BusLineWaitTimeAtStop busLineWaitTimeAtStop : list)
         {
            cache.put(Integer.parseInt(busLineWaitTimeAtStop.LI_NR),
                      Integer.parseInt(busLineWaitTimeAtStop.STR_LI_VAR),
                      Integer.parseInt(busLineWaitTimeAtStop.FGR_NR),
                      Integer.parseInt(busLineWaitTimeAtStop.LI_LFD_NR),
                      busLineWaitTimeAtStop);
         }
      }
      return cache.get(LI_NR, STR_LI_VAR, FGR_NR, LI_LFD_NR);
   }
}
