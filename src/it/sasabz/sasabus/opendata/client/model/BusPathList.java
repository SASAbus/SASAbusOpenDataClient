/*
 * SASAbusOpenDataClient - Client and business logic for SASA bus open data
 *
 * BusPathList.java
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

import java.util.HashMap;

public class BusPathList
{

   BusPathLine[]                           list;

   transient HashMap<Integer, BusPathLine> cache = null;

   public BusPathLine findBusPathLine(int LI_NR)
   {
      if (cache == null)
      {
         cache = new HashMap<Integer, BusPathLine>();
         for (BusPathLine busPathLine : list)
         {
            cache.put(busPathLine.getLI_NR(), busPathLine);
         }
      }
      return cache.get(LI_NR);
   }

   public BusPathLine[] getList()
   {
      return list;
   }

}
