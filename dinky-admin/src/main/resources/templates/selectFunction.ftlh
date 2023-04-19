<#macro fieldFunction fo>
    <@compress single_line=true>
        <#if fo.functionName??>${fo.functionName}(</#if>
        <#list fo.parameters as parameter>
            <#if instanceOf(parameter, "com.zdpx.coder.operator.FieldFunction")>
                <@fieldFunction parameter/>
            <#elseif parameter?is_number>
                ${parameter?c}
            <#else>
                ${parameter?no_esc}
            </#if>
            <#sep>${fo.delimiter!","}
        </#list>
        <#if fo.functionName??>)</#if>
    </@compress>
</#macro>

<#macro fieldsProcess fos single=true>
    <@compress single_line=single>
        <#list fos as fo>
            <#assign x><@fieldFunction fo/></#assign>
            <#if x?has_content> ${x} AS</#if> ${fo.outName}<#sep>,</#sep>
        </#list>
    </@compress>
</#macro>

<#macro cepFunction cep>
    SELECT *
    FROM ${cep.inputTableName}
    MATCH_RECOGNIZE(
    <#if cep.partition??>PARTITION BY ${cep.partition} </#if>
    <#if cep.orderBy??>ORDER BY ${cep.orderBy} </#if>
    MEASURES
    <@fieldsProcess cep.measures false/>

    ONE ROW PER MATCH
    AFTER MATCH SKIP ${cep.skipStrategy}
    PATTERN (<#list cep.patterns as pattern>${pattern}<#sep> </#sep><#rt></#list>)
    DEFINE
    <#list cep.defines as define>
        ${define}<#sep>,</#sep>
    </#list>
    )
</#macro>
