package org.joget.marketplace.datalist.lib;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListColumn;
import org.joget.apps.datalist.model.DataListColumnFormatDefault;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.TimeZoneUtil;
import static org.joget.commons.util.TimeZoneUtil.getTimeZoneByGMT;
import org.joget.workflow.model.service.WorkflowUserManager;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.i18n.LocaleContextHolder;
import org.joget.directory.model.User;

public class UTCDateDatalistFormatter extends DataListColumnFormatDefault {
    
    public String getName() {
        return "UTC Date Formatter";
    }

    public String getVersion() {
        return "7.0.0";
    }

    public String getDescription() {
        return "To convert date time from UTC to local timezone";
    }

    public String getLabel() {
        return "UTC Date Formatter";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/datalist/UTCDateFormatter.json", null, true, "messages/datalist/UTCDateFormatter");
    }

    public String format(DataList dataList, DataListColumn column, Object row, Object value) {
        if (value != null) {
            try {
                String dataFormat = getPropertyString("dataFormat");
                String displayFormat = getPropertyString("displayFormat");
                if (displayFormat.isEmpty()) {
                    displayFormat = dataFormat;
                } 
                
                String mode = getPropertyString("mode");

                SimpleDateFormat sdf = new SimpleDateFormat(dataFormat);
                if ("storeInUTC".equalsIgnoreCase(mode)) {
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                }else if("convertToUTC".equalsIgnoreCase(mode)) {
                    if(column.getName().equalsIgnoreCase("dateCreated") || column.getName().equalsIgnoreCase("dateModified")  ){
                        //LocaleContextHolder.getTimeZone().getID() is NOT returning the current user's correct timezone, it is getting server timezone
                        //sdf.setTimeZone(TimeZone.getTimeZone(LocaleContextHolder.getTimeZone().getID()));
                        //use this instead
                        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
                        User user = workflowUserManager.getCurrentUser();
                        sdf.setTimeZone(TimeZone.getTimeZone(TimeZoneUtil.getTimeZoneByGMT(user.getTimeZone())));
                    }else{
                        sdf.setTimeZone(TimeZone.getTimeZone(TimeZoneUtil.getServerTimeZoneID()));
                    }
                }
                
                Date date = sdf.parse(value.toString());
                
                if ("storeInUTC".equalsIgnoreCase(mode)) {
                    return TimeZoneUtil.convertToTimeZone(date, null, displayFormat);
                }else if ("convertToUTC".equalsIgnoreCase(mode)) {
                    SimpleDateFormat outputFormat = new SimpleDateFormat(displayFormat);
                    outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    return outputFormat.format(date);
                }else {
                    sdf = new SimpleDateFormat(displayFormat);
                    return sdf.format(date);
                }
            } catch (Exception e) {
                LogUtil.error(this.getClassName(), e, "Error in parsing");
            }
        }
        return "";
    }
}
