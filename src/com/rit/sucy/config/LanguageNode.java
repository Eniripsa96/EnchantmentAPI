package com.rit.sucy.config;

import com.rit.sucy.service.ConfigNode;

import java.util.ArrayList;
import java.util.List;

public enum LanguageNode implements ConfigNode {

    ANVIL_COMPONENT ("Anvil Component", VarType.LIST, new ArrayList<String>(){{
        add("&2Anvil Components");
        add("&dPlace components");
        add("&dover here!");
    }}),

    ANVIL_SEPARATOR ("Anvil Separator", VarType.LIST, new ArrayList<String>(){{
        add("&2Anvil Book");
        add("&d<- Components");
        add("&8------------");
        add("&dResults ->");
    }}),

    ANVIL_RESULT ("Anvil Result", VarType.LIST, new ArrayList<String>(){{
        add("&2Anvil Result");
        add("&dResults will");
        add("&dshow up over");
        add("&dhere!");
    }}),

    TABLE_ENCHANTABLE ("Table Enchantable", VarType.LIST, new ArrayList<String>(){{
        add("&2Placeholder");
        add("&dEnchantable");
    }}),

    TABLE_UNENCHANTABLE ("Table Unenchantable", VarType.LIST, new ArrayList<String>(){{
        add("&2Placeholder");
        add("&4Unenchantable");
    }}),

    NAME_FORMAT ("Name Format", VarType.LIST, new ArrayList<String>(){{
        add("{adjective} {weapon} of {suffix}");
    }});

    final String path;
    final VarType type;
    final List<String> value;

    private LanguageNode(String path, VarType type, List<String> value) {
        this.type = type;
        this.path = path;
        this.value = value;
    }

    @Override
    public String getPath() {
        return path;
    }

    public String getFullPath() {
        return RootConfig.baseNode + "Language." + getPath();
    }

    @Override
    public VarType getVarType() {
        return type;
    }

    @Override
    public SubType getSubType() {
        return null;
    }

    @Override
    public Object getDefaultValue() {
        return value;
    }
}
