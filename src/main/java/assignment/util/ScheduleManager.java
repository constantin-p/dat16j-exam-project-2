package assignment.util;

import assignment.model.Extra;
import assignment.model.Order;

import java.time.LocalDate;
import java.util.*;

public class ScheduleManager {

    private static List<Order> orderList = new ArrayList<>();
    //              year               dayInMonth       idType  idList
    private static HashMap<Integer, HashMap<Integer, HashMap<String, List<String>>>> schedule = new HashMap();

    public static void update(List<Order> orders) {
        orderList = orders;
        schedule = new HashMap();
        calculateYear(LocalDate.now().getYear());
        CacheEngine.markForUpdate("clients");
        CacheEngine.markForUpdate("extras");
        CacheEngine.markForUpdate("motorhomes");
    }

    private static boolean isFree(LocalDate date, String key, String ID) {
        int year = date.getYear();
        if (!schedule.containsKey(year)) {
            calculateYear(year);
        }

        HashMap<Integer, HashMap<String, List<String>>> yearSchedule = schedule.get(year);
        int dayIfYear = date.getDayOfYear();
        if (yearSchedule.containsKey(dayIfYear)) {
            return !yearSchedule.get(dayIfYear).get(key).contains(ID);
        }
        return true;
    }

    public static boolean isFree(LocalDate start, LocalDate end, String key, String ID) {
        int offset = 0;
        while (!start.plusDays(offset).isAfter(end)) {
            boolean isFree = isFree(start.plusDays(offset), key, ID);
            if (!isFree) {
                return false;
            }
            offset++;
        }
        return true;
    }

    public static boolean isClientFree(LocalDate date, String clientID) {
        return isFree(date, "clients", clientID);
    }

    public static boolean isClientFree(LocalDate start, LocalDate end, String clientID) {
        return isFree(start, end, "clients", clientID);
    }

    public static boolean isMotorhomeFree(LocalDate date, String motorhomeID) {
        return isFree(date, "motorhomes", motorhomeID);
    }

    public static boolean isMotorhomeFree(LocalDate start, LocalDate end, String motorhomeID) {
        return isFree(start, end, "motorhomes", motorhomeID);
    }

    public static boolean isExtraFree(LocalDate date, String extraID) {
        return isFree(date, "extras", extraID);
    }

    public static boolean isExtraFree(LocalDate start, LocalDate end, String extraID) {
        return isFree(start, end, "extras", extraID);
    }

    private static void calculateYear(int year) {
        HashMap<Integer, HashMap<String, List<String>>> yearSchedule = new HashMap<>();

        for (Order order: orderList) {
            if (order.startDate.getValue().getYear() == year ||
                    order.endDate.getValue().getYear() == year) {
                LocalDate start = order.startDate.getValue();
                LocalDate end = order.endDate.getValue();

                int offset = 0;
                boolean enter = false;
                while (!start.plusDays(offset).isAfter(end)) {
                    if (start.plusDays(offset).getYear() == year) {
                        enter = true;

                        int currentDay = start.plusDays(offset).getDayOfYear();
                        HashMap<String, List<String>> dayIDList = yearSchedule.get(currentDay);

                        if (dayIDList == null) {
                            dayIDList = new HashMap<>();

                            List<String> clientIDList = new ArrayList<>();
                            clientIDList.add(order.client.getValue().id);
                            dayIDList.put("clients", clientIDList);

                            List<String> motorhomeIDList = new ArrayList<>();
                            motorhomeIDList.add(order.motorhome.getValue().id);
                            dayIDList.put("motorhomes", motorhomeIDList);

                            List<String> extrasIDList = new ArrayList<>();
                            for (Map.Entry<Extra, Double> entry: order.extras) {
                                extrasIDList.add(entry.getKey().id);
                            }
                            dayIDList.put("extras", extrasIDList);
                        } else {
                            List<String> clientIDList = dayIDList.get("clients");
                            clientIDList.add(order.client.getValue().id);
                            dayIDList.put("clients", clientIDList);

                            List<String> motorhomeIDList = dayIDList.get("motorhomes");
                            motorhomeIDList.add(order.motorhome.getValue().id);
                            dayIDList.put("motorhomes", motorhomeIDList);

                            List<String> extrasIDList = dayIDList.get("extras");
                            for (Map.Entry<Extra, Double> entry: order.extras) {
                                extrasIDList.add(entry.getKey().id);
                            }
                            dayIDList.put("extras", extrasIDList);
                        }

                        yearSchedule.put(currentDay, dayIDList);
                    } else if(enter) {
                        break;
                    }
                    offset++;
                }

            }
        }

        schedule.put(year, yearSchedule);
    }
}
