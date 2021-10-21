package com.dlink.trans;

import com.dlink.executor.custom.CustomTableEnvironmentImpl;

import java.util.Arrays;
import java.util.List;

/**
 * AbstractOperation
 *
 * @author wenmo
 * @since 2021/6/14 18:18
 */
public class AbstractOperation {

    protected String statement;

    public AbstractOperation() {
    }

    public AbstractOperation(String statement) {
        this.statement = statement;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public boolean checkFunctionExist(CustomTableEnvironmentImpl stEnvironment,String key){
        String[] udfs = stEnvironment.listUserDefinedFunctions();
        List<String> udflist = Arrays.asList(udfs);
        if(udflist.contains(key.toLowerCase())){
            return true;
        }else {
            return false;
        }
    }

    public boolean noExecute(){
        return true;
    }
}
