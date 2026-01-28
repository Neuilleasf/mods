/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.codec.Codec
 *  com.hypixel.hytale.codec.KeyedCodec
 *  com.hypixel.hytale.codec.builder.BuilderCodec
 *  com.hypixel.hytale.codec.builder.BuilderCodec$Builder
 */
package org.zuxaw.plugin.gui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class StatsGUIEventData {
    public static final BuilderCodec<StatsGUIEventData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(StatsGUIEventData.class, StatsGUIEventData::new).append(new KeyedCodec("StatName", (Codec)Codec.STRING), (data, value) -> {
        data.statName = value != null ? value : "";
    }, data -> data.statName != null ? data.statName : "").add()).append(new KeyedCodec("Action", (Codec)Codec.STRING), (data, value) -> {
        data.action = value != null ? value : "";
    }, data -> data.action != null ? data.action : "").add()).append(new KeyedCodec("Amount", (Codec)Codec.STRING), (data, value) -> {
        if (value == null || value.isEmpty()) {
            data.amount = 0;
        } else {
            try {
                data.amount = Integer.parseInt(value);
            }
            catch (NumberFormatException e) {
                data.amount = 0;
            }
        }
    }, data -> data.amount != null ? String.valueOf(data.amount) : "0").add()).append(new KeyedCodec("NavBar", (Codec)Codec.STRING), (data, value) -> {
        data.navBar = value != null ? value : "";
    }, data -> data.navBar != null ? data.navBar : "").add()).build();
    public String statName;
    public String action;
    public Integer amount;
    public String navBar;
}
