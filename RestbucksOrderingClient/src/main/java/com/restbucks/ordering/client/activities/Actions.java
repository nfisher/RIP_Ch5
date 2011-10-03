package com.restbucks.ordering.client.activities;

import java.util.ArrayList;

public class Actions extends ArrayList<Activity> {

    
    private static final long serialVersionUID = 7455318429430311610L;

    public boolean has(Class clazz) {
        for(Activity act : this) {
            if(act.getClass() == clazz) {
                return true;
            }
        }
        return false;
    }

    public <T extends Activity> T get(Class clazz) {
        
        for(Activity act : this) {
            if(act.getClass() == clazz) {
                return (T) clazz.cast(act);
            }
        }
        
        return null;
    }
}
