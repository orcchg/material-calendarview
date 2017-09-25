package com.prolificinteractive.materialcalendarview;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;

import java.util.LinkedHashMap;

public class TypefaceStore {

    LinkedHashMap<String, Typeface> fonts = new LinkedHashMap<>();

    private static TypefaceStore INSTANCE;

    public static TypefaceStore getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new TypefaceStore(context);
        }
        return INSTANCE;
    }

    TypefaceStore(Context applicationContext) {
//            fonts.put("LucidaGrande", initTypeface(applicationContext, "LucidaGrande"));
//            fonts.put("LucidaGrande-Bold", initTypeface(applicationContext, "LucidaGrande-Bold"));
        fonts.put("Montserrat-Bold", initTypeface(applicationContext, "Montserrat-Bold"));
        fonts.put("Montserrat-Light", initTypeface(applicationContext, "Montserrat-Light"));
//            fonts.put("Montserrat-Medium", initTypeface(applicationContext, "Montserrat-Medium"));
        fonts.put("Montserrat-Regular", initTypeface(applicationContext, "Montserrat-Regular"));
    }

    Typeface get(String fontAsset) {
        if (fonts.containsKey(fontAsset)) {
            return fonts.get(fontAsset);
        }
        return defaultTypeface();
    }

    Typeface defaultTypeface() {
        return fonts.get("Montserrat-Regular");
    }

    Typeface initTypeface(Context context, String fontAsset) {
        String path = "fonts/" + (TextUtils.isEmpty(fontAsset) ? "Montserrat-Regular" : fontAsset) + ".ttf";
        return Typeface.createFromAsset(context.getAssets(), path);
    }
}
