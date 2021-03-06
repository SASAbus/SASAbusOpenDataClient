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

import java.util.ArrayList;

public class BusStation
{

   String                     ORT_NAME;
   transient private String[] nameItDe;

   ArrayList<BusStop>         busstops;

   ArrayList<Integer>         busLineIds;

   String                     ORT_GEMEINDE;

   transient String           routingName;

   public String findName_it()
   {
      this.splitName();
      return this.nameItDe[0];
   }

   public String findName_de()
   {
      this.splitName();
      return this.nameItDe[1];
   }

   public String getORT_NAME()
   {
      return this.ORT_NAME;
   }

   public String getRoutingName()
   {
      this.splitName();
      return this.routingName;
   }

   public BusStop[] getBusStops()
   {
      return this.busstops.toArray(new BusStop[this.busstops.size()]);
   }

   private void splitName()
   {
      if (this.nameItDe == null)
      {
         String gemIt = "";
         String gemDe = "";

         int pos = this.ORT_GEMEINDE.indexOf('-');

         gemIt = "(" + this.ORT_GEMEINDE.substring(0, pos).trim() + ")";
         gemDe = "(" + this.ORT_GEMEINDE.substring(pos + 1).trim() + ")";

         pos = this.ORT_NAME.indexOf('-');

         String it;
         String de;
         if (pos < 0) // Minigolf
         {
            it = de = this.ORT_NAME;
         }
         else
         {
            it = this.ORT_NAME.substring(0, pos).trim();
            de = this.ORT_NAME.substring(pos + 1).trim();
         }
         this.nameItDe = new String[] { it + " " + gemIt, de + " " + gemDe };
         this.routingName = gemIt + " " + it + " - " + gemDe + " " + de;

      }
   }

   public void addBusLine(int LI_NR)
   {
      if (this.busLineIds == null)
      {
         this.busLineIds = new ArrayList<Integer>();
      }
      if (!this.busLineIds.contains(LI_NR))
      {
         this.busLineIds.add(LI_NR);
      }

   }

   public Integer[] getBusLines()
   {
      if (this.busLineIds == null)
      {
         this.busLineIds = new ArrayList<Integer>();
      }
      return this.busLineIds.toArray(new Integer[this.busLineIds.size()]);
   }

}
