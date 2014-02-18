/*
 * SASAbusOpenDataClient - Client and business logic for SASA bus open data
 *
 * BusPathLine.java
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

public class BusPathLine
{
   String LI_NR;

   BusPathVariant[] varlist;

   transient HashMap<Integer, BusPathVariant> cache = null;

   public BusPathVariant findBusPathVariant(int STR_LI_VAR)
   {
      if (cache == null)
      {
         cache = new HashMap<Integer, BusPathVariant>();
         for (BusPathVariant busPathVariant: varlist)
         {
            cache.put(busPathVariant.STR_LI_VAR, busPathVariant);
         }
      }
      return cache.get(STR_LI_VAR);
   }

   public BusPathVariant[] getVariants()
   {
      return varlist;
   }

   public int getLI_NR()
   {
      return Integer.parseInt(LI_NR);
   }
}
