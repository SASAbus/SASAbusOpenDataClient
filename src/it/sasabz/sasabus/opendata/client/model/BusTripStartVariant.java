/*
 * SASAbusOpenDataClient - Client and business logic for SASA bus open data
 *
 * BusTripStartVariant.java
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

public class BusTripStartVariant
{
   int                STR_LI_VAR;

   BusTripStartTime[] triplist;

   public BusTripStartTime[] getTriplist()
   {
      return this.triplist;
   }

   public int getVariantId()
   {
      return this.STR_LI_VAR;
   }
}
