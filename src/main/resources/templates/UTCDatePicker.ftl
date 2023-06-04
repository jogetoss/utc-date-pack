<div class="form-cell" ${elementMetaData!}>
<#if element.properties.readonly! != 'true'>
    <#if !(request.getAttribute("org.joget.marketplace.form.lib.UTCDatePicker_EDITABLE")??)>
        <#if request.getAttribute("currentLocale")!?starts_with("zh") >
            <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.apps.form.lib.UTCDatePicker/js/jquery.ui.datepicker-zh-CN.js"></script>
        </#if>
        <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.apps.form.lib.DatePicker/js/jquery.placeholder.min.js"></script>
        <link rel="stylesheet" href="${request.contextPath}/plugin/org.joget.form.lib.DatePicker/css/datePicker.css" />
        <link rel="stylesheet" href="${request.contextPath}/plugin/org.joget.marketplace.form.lib.UTCDatePicker/css/jquery-ui-timepicker-addon.css" />
        <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.marketplace.form.lib.UTCDatePicker/js/jquery.ncustom.datepicker.js"></script>
        <script type="text/javascript" src="${request.contextPath}/plugin/org.joget.marketplace.form.lib.UTCDatePicker/js/jquery-ui-timepicker-addon.js"></script>
    </#if>
<script type="text/javascript">
    $(document).ready(function() {
        $("#${elementParamName!}_${element.properties.elementUniqueKey!}").ncdatepicker({
                        showOn: "button",
                        buttonImage: "${request.contextPath}/css/images/calendar.png",
                        buttonImageOnly: true,
                        changeMonth: true,
                        changeYear: true,
                        timeInput: true
                        <#if element.properties.format24hr! == ''>
                        ,timeFormat: "hh:mm tt"
                        </#if>
                        <#if element.properties.format! != ''>
                        ,dateFormat: "${element.properties.format}"
                        </#if>
                        <#if element.properties.yearRange! != ''>
                        ,yearRange: "${element.properties.yearRange}"
                        </#if>
                        <#if element.properties.startDateFieldId! != ''>
                        ,startDateFieldId: "${element.properties.startDateFieldId}"
                        </#if>
                        <#if element.properties.endDateFieldId! != ''>
                        ,endDateFieldId: "${element.properties.endDateFieldId}"
                        </#if>
                        <#if element.properties.currentDateAs! != ''>
                        ,currentDateAs: "${element.properties.currentDateAs}"
                        </#if>
                        <#if element.properties.datePickerType! != ''>
                        ,datePickerType: "${element.properties.datePickerType}"
                        </#if>
        });
    });
</script>
</#if>

    
    <#if (element.properties.readonly! == 'true' && element.properties.readonlyLabel! == 'true') >
        <label class="label">${element.properties.label} <span class="form-cell-validator">${decoration}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
        <span>${value!?html} <#if showUserTimeZone! == 'true' >(${userTimeZone!?html})</#if></span>
        <input id="${elementParamName!}" name="${elementParamName!}" type="hidden" value="${value!?html}" />
    <#else>
        <label class="label">${element.properties.label} <#if showUserTimeZone! == 'true' >(${userTimeZone!?html})</#if> <span class="form-cell-validator">${decoration}</span><#if error??> <span class="form-error-message">${error}</span></#if></label>
        <input id="${elementParamName!}_${element.properties.elementUniqueKey!}" name="${elementParamName!}" type="text" size="${element.properties.size!}" value="${value!?html}" class="${elementParamName!} <#if error??>form-error-cell</#if>" <#if (element.properties.allowManual! != 'true' || element.properties.readonly! == 'true')>readonly</#if> placeholder="<#if (element.properties.placeholder! != '')>${element.properties.placeholder}<#else>${displayFormat!?html}</#if>" />
    </#if>
</div>