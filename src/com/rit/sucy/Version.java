/**
 * EnchantmentAPI
 * com.rit.sucy.Version
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.rit.sucy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version
{
    private static String pack;

    private static void init()
    {
        if (pack == null) {
            Matcher matcher = Pattern.compile("v\\d+_\\d+_R\\d+").matcher(Bukkit.getServer().getClass().getPackage().getName());
            if (matcher.find())
            {
                pack = matcher.group();
            }
            else pack = "._.";
        }
    }

    /**
     * Gets the package for the server version
     *
     * @return package ID
     */
    public static String getPackage()
    {
        return pack;
    }

    /**
     * Gets the list of online players, handling differences with the new
     * 1.9 changes.
     *
     * @return array of online players
     */
    public static Player[] getOnlinePlayers() {
        init();
        if (pack.startsWith("v1_9")) {
            ArrayList<Player> list = new ArrayList<Player>();
            Collection<? extends Player> online = Bukkit.getOnlinePlayers();
            for (Object player : online) {
                if (player instanceof Player) {
                    list.add((Player)player);
                }
            }
            return list.toArray(new Player[list.size()]);
        }
        else {
            try
            {
                return (Player[])Bukkit.class.getMethod("getOnlinePlayers").invoke(null);
            }
            catch (Exception ex) {
                return new Player[0];
            }
        }
    }
}
