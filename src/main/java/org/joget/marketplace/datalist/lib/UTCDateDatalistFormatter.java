package org.joget.marketplace.datalist.lib;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListColumn;
import org.joget.apps.datalist.model.DataListColumnFormatDefault;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.TimeZoneUtil;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.directory.model.User;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONArray;

public class UTCDateDatalistFormatter extends DataListColumnFormatDefault implements PluginWebSupport {

    public String getName() {
        return "UTC Date Formatter";
    }

    public String getVersion() {
        return "7.0.1";
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
                } else if ("convertToUTC".equalsIgnoreCase(mode)) {
                    if (column.getName().equalsIgnoreCase("dateCreated") || column.getName().equalsIgnoreCase("dateModified")) {
                        WorkflowUserManager workflowUserManager = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
                        User user = workflowUserManager.getCurrentUser();
                        sdf.setTimeZone(TimeZone.getTimeZone(TimeZoneUtil.getTimeZoneByGMT(user.getTimeZone())));
                    } else {
                        sdf.setTimeZone(TimeZone.getTimeZone(TimeZoneUtil.getServerTimeZoneID()));
                    }
                } else if ("storeInUTC2".equalsIgnoreCase(mode)) {
                    String customTimezone = getPropertyString("Timezone");
                    if (!customTimezone.isEmpty()) {
                        TimeZone timeZone = TimeZone.getTimeZone(customTimezone);
                        sdf.setTimeZone(timeZone);
                    }
                } 

                Date date = sdf.parse(value.toString());

                if ("storeInUTC".equalsIgnoreCase(mode)) {
                    return TimeZoneUtil.convertToTimeZone(date, null, displayFormat);
                } else if ("convertToUTC".equalsIgnoreCase(mode)) {
                    SimpleDateFormat outputFormat = new SimpleDateFormat(displayFormat);
                    outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    return outputFormat.format(date);
                } else if ("storeInUTC2".equalsIgnoreCase(mode)) {
                    String customTimezone = getPropertyString("Timezone");
                    if (!customTimezone.isEmpty()) {
                        sdf.setTimeZone(TimeZone.getTimeZone(customTimezone));
                        Date localDate = sdf.parse(value.toString());

                        TimeZone customTimeZone = TimeZone.getTimeZone(customTimezone);
                        int timeDifference = customTimeZone.getOffset(localDate.getTime());

                        long utcTime = localDate.getTime() + timeDifference;
                        Date utcDate = new Date(utcTime);

                        SimpleDateFormat outputFormat = new SimpleDateFormat(displayFormat);
                        outputFormat.setTimeZone(TimeZone.getTimeZone(customTimezone));
                        return outputFormat.format(utcDate);
                    }
                } else {
                    sdf = new SimpleDateFormat(displayFormat);
                    return sdf.format(date);
                }
            } catch (Exception e) {
                LogUtil.error(this.getClassName(), e, "Error in parsing");
            }
        }
        return "";
    }

    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("getTimezone".equals(action)) {
            try {
                JSONArray jsonArray = new JSONArray();

                String [] ids = TimeZone.getAvailableIDs();
                
                for (String id:ids) {
                    TimeZone zone = TimeZone.getTimeZone(id);
                    int offset = zone.getRawOffset()/1000;
                    int hour = offset/3600;
                    int minutes = (offset % 3600)/60;

                    Map<String, String> option = new HashMap<String, String>();
                    option.put("value", id);
                    option.put("label", id);
                    jsonArray.put(option);
                }

                jsonArray.write(response.getWriter());
            } catch (Exception ex) {
                LogUtil.error(this.getClass().getName(), ex, "Get Timezone Error!");
            }
        }
    }

}
