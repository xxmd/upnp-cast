/*
 * net/balusc/webapp/FileServlet.java
 *
 * Copyright (C) 2009 BalusC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.xxmd.util;

import android.content.Context;
import android.content.ContextWrapper;

import androidx.appcompat.app.AppCompatActivity;

import javax.servlet.http.HttpServlet;

public class ContextUtil extends HttpServlet {
    public static AppCompatActivity parseContextToAppCompatActivity(Context context) {
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextWrapper) {
            return (AppCompatActivity) ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}