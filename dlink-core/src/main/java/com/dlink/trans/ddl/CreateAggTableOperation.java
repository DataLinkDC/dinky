package com.dlink.trans.ddl;

import com.dlink.trans.Operation;

/**
 * TODO
 *
 * @author wenmo
 * @since 2021/6/13 19:24
 */
public class CreateAggTableOperation implements Operation{

    private String KEY_WORD = "CREATE AGGTABLE";

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public boolean canHandle(String key) {
        if(KEY_WORD.equalsIgnoreCase(key)){
            return true;
        }else {
            return false;
        }
    }


}
