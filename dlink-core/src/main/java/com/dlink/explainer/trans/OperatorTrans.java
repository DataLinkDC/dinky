package com.dlink.explainer.trans;

import com.dlink.utils.MapParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OperatorTrans
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class OperatorTrans extends AbstractTrans implements Trans {

    private List<Field> select;
    private List<String> fields;
    private List<String> joinType;
    private String where;
    private List<String> leftInputSpec;
    private List<String> rightInputSpec;

    public final static String TRANS_TYPE = "Operator";
    private final static String FIELD_AS = " AS ";

    public List<Field> getSelect() {
        return select;
    }

    public List<String> getFields() {
        return fields;
    }

    public List<String> getJoinType() {
        return joinType;
    }

    public String getWhere() {
        return where;
    }

    public List<String> getLeftInputSpec() {
        return leftInputSpec;
    }

    public List<String> getRightInputSpec() {
        return rightInputSpec;
    }

    @Override
    public String getHandle() {
        return TRANS_TYPE;
    }

    @Override
    public boolean canHandle(String pact) {
        return TRANS_TYPE.equals(pact);
    }


    @Override
    public void translate() {
        name = pact;
        Map map = MapParseUtils.parseForSelect(contents);
        translateSelect((ArrayList<String>) map.get("select"));
        fields = (ArrayList<String>) map.get("fields");
        joinType = (ArrayList<String>) map.get("joinType");
        where = map.containsKey("where") ? map.get("where").toString() : null;
        leftInputSpec = (ArrayList<String>) map.get("leftInputSpec");
        rightInputSpec = (ArrayList<String>) map.get("rightInputSpec");
    }

    private void translateSelect(ArrayList<String> fieldStrs) {
        if (fieldStrs != null && fieldStrs.size() > 0) {
            select = new ArrayList<>();
            for (int i = 0; i < fieldStrs.size(); i++) {
                String fieldStr = fieldStrs.get(i).trim();
                if (fieldStr.toUpperCase().contains(FIELD_AS)) {
                    String[] fieldNames = fieldStr.split(FIELD_AS);
                    if (fieldNames.length == 2) {
                        select.add(new Field(fieldNames[0].trim(), fieldNames[1].trim()));
                    } else if (fieldNames.length == 1) {
                        select.add(new Field(fieldNames[0].trim()));
                    } else {
                        List<String> fieldNameList = new ArrayList<>();
                        for (int j = 0; j < fieldNames.length - 1; j++) {
                            fieldNameList.add(fieldNames[j]);
                        }
                        select.add(new Field(StringUtils.join(fieldNameList, FIELD_AS).trim(), fieldNames[fieldNames.length - 1].trim()));
                    }
                } else {
                    select.add(new Field(fieldStr));
                }
            }
        }
    }

    @Override
    public String asSummaryString() {
        return null;
    }
}
