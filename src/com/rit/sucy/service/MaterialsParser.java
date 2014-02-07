package com.rit.sucy.service;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Parses a given StringList into an Material array
 *
 * @author Diemex
 */
public class MaterialsParser
{
    /**
     * Parse a given List of Strings which represent Materials/Item Ids
     *
     * @param stringList of representing blocks
     *
     * @return the Materials which have been recognized
     */
    public static Material[] toMaterial(String[] stringList)
    {
        List<Material> materials = new ArrayList<Material>();

        for (String blockString : stringList)
        {
            /* Cut whitespace */
            Pattern whitespace = Pattern.compile("\\s"); //Includes tabs/newline characters
            if (whitespace.matcher(blockString).find())
            {
                blockString = whitespace.matcher(blockString).replaceAll("");
            }

            Pattern onlyNumbers = Pattern.compile("[^0-9]");
            Material material = Material.matchMaterial(blockString);

            /* couldn't be matched by enum constant */
            if (material == null)
            {
                /* try as number (blockId) */
                String tempId = onlyNumbers.matcher(blockString).replaceAll("");
                if (!tempId.isEmpty())
                {
                    material = Material.getMaterial(tempId);
                }
                /* still fail -> try as enum again but strip numbers */
                if (material == null)
                {
                    Pattern onlyLetters = Pattern.compile("[^a-zA-Z_]");
                    material = Material.matchMaterial(onlyLetters.matcher(blockString).replaceAll(""));
                }
            }
            if (material != null)
                materials.add(material);
            //If we want to switch to block ids
            /*String onlyNums = onlyNumbers.matcher(blockString).replaceAll("") .length() > 0
                    ? onlyNumbers.matcher(blockString).replaceAll("")
                    : "0";
            int blockNumber = material != null ? material.getId() : Integer.parseInt(onlyNums);*/

        }

        return materials.toArray(new Material[materials.size()]);
    }

    /**
     * Turn a List of Materials into a human readable form
     *
     * @param materials     to convert to strings
     * @return              an array of materialnames
     */
    public static String[] toStringArray (Material[] materials)
    {
        List <String> items = new ArrayList<String>();
        for (Material mat : materials)
            items.add(mat.name());

        return items.toArray(new String[items.size()]);
    }
}
