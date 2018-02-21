
import com.healthmarketscience.jackcess.*;
import java.io.*;
import java.text.*;
//import sun.jdbc.odbc.JdbcOdbcDriver;

import java.util.*;
import static java.util.Arrays.asList;

public class ABCDatabase {

    private static String date;
    private static String printDate;
    private static int attendance;
    private static int total = 0;
    private static final List<String> keywords = new ArrayList<>(asList("guest", "sell wristband", "sell sticker", "entry 15", "pay wages", "pay rent", "free entry",
            "cancel guest", "cancel wristband", "cancel sticker", "cancel entry 15", "cancel rent", "cancel wages", "cancel free entry", "cancel entry"));
    private static int newMembers;
    private static int bandsSold;
    private static int StickersSold;
    private static int guests;
    private static int rentPaid; 
    private static int wagesPaid;

    private static String message = "";
    private static String messageBuffer = "";

    public static void main(String[] args) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = dateFormat.format(new Date());
        dateFormat = new SimpleDateFormat("yyyy - MMMM");
        printDate = dateFormat.format(new Date());
        System.out.println(printDate);
        reload();
        runProgram(loadMembers());
    }

    public static Member[] loadMembers() {
        int line = 0;
        try {
            Database db = DatabaseBuilder.open(new File("ABCDatabase.accdb"));
            Table table = db.getTable("Members");
            //System.out.println("Table: " + table);
            int numRows = table.getRowCount();
            System.out.println("\tNumber of rows in table: " + numRows);
          //  System.out.println(table.getColumns());
            Member[] members = new Member[numRows];
            int index = 0;

            for (Row row : table) {
                Member m = new Member();

                m.setMembershipNumber(String.format("%04d", Integer.parseInt(row.get("Membership Number").toString())));
                        
                m.setForename(row.get("Forename").toString());

                if (row.get("Nickname") != null) {
                    m.setNickname(row.get("Nickname").toString());
                }
                
                

                members[index++] = m;
                line++;
            }
            db.close();
            //System.out.println("\tMembers loaded successfully");
            return members;

        } catch (Exception e) {
            System.out.println("Error loading members: " + e + "\nAt Line: " + line);
            System.out.println(e.fillInStackTrace());
            return null;
        }

    }

    public static void runProgram(Member[] members) {
        String entry = "";
        Scanner kybd = new Scanner(System.in);
        clearScreen();
        System.out.println("\tPlease press Enter to start!");
        while (!entry.equalsIgnoreCase("Exit")) {

            System.out.print("\n\t -->  ");
            entry = kybd.nextLine();
            try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Logs\\"+printDate+".txt", true)))) {
                out.println(entry);
            }catch (IOException e) {
            }

            messageBuffer = "";
            message = "";
            //      if(entry == null)
            //        {
            //            entry = "";
            //         }

            boolean flag = false;
            boolean specialEntry = false;
            if (listContains(entry, keywords)) {
                specialEntry = true;
                specialEntry(entry);
                message += "\n";
                if (newMembers > 0) {
                    message += "\tNew Members: " + newMembers + "\n";
                }
                if (guests > 0) {
                    message += "\tGuests: " + guests + "\n";
                }
                if (StickersSold > 0) {
                    message += "\tStickers Sold: " + StickersSold + "\n";
                }
                if (bandsSold > 0) {
                    message += "\tWristbands Sold: " + bandsSold + "\n";
                }

            } else {
                message += "\n";
                if (newMembers > 0) {
                    message += "\tNew Members: " + newMembers + "\n";
                }
                if (guests > 0) {
                    message += "\tGuests: " + guests + "\n";
                }
                if (StickersSold > 0) {
                    message += "\tStickers Sold: " + StickersSold + "\n";
                }
                if (bandsSold > 0) {
                    message += "\tWristbands Sold: " + bandsSold + "\n";
                }
                for (Member m : members) {
                    if(m == null) { break; }

                    try {

                        switch (entry.length()) {
                            case 1:
                                entry = "000" + entry;
                                break;
                            case 2:
                                entry = "00" + entry;
                                break;
                            case 3:
                                entry = "0" + entry;
                                break;
                        }
                        if (m.getMembershipNumber().equals(entry)) { 
                            flag = true;
                            String nickname = m.getNickname(); 
                            message += "\n\t" + m.getMembershipNumber() + "\t" + m.getForename();
                            if (nickname != null) {
                                message += "\t(" + nickname + ")";
                            }
                            message += "\n";

                            try {
                                Database db = DatabaseBuilder.open(new File("ABCDatabase.accdb"));
                                Table table = db.getTable("Members");
                                
                                for (Row row : table) {
                                    countVisits(m, table, row);  
                                    
                                }
                                
                                db.close();
                                
                                
                                for (Row row : table) {
                                    if (padNumber(row.get("Membership Number").toString()).equals(m.getMembershipNumber())) {
                                        if (row.get(date) != null) {
                                            if (row.get(date).equals(false)) {
                                                message += "\n\tAttendance Marked!\n\n";
                                                attendance++;
                                                row.put(date, true);
                                                table.updateRow(row);
                                                total += 10;

                                                Table record = db.getTable("Summary");
                                                
                                                for (Row r : record) {
                                                    if (r.get("ABC Date").toString().equals(date)) {
                                                        int i = Integer.parseInt(r.get("£10 Entries").toString()) + 1;
                                                        r.put("£10 Entries", i);

                                                        Double d = Double.parseDouble(r.get("£10 Total").toString()) + 10;
                                                        r.put("£10 Total", d);

                                                        d = Double.parseDouble(r.get("Total").toString()) + 10;
                                                        r.put("Total", d);

                                                        record.updateRow(r);

                                                        break;
                                                    }
                                                }

                                            } else {
                                                message += "\n\t" + m.getForename() + " has already been marked in!\n\n";
                                            }
                                        }
                                        countVisits(m, table, row);
                                        break;
                                    }
                                }

                                db.close();

                            } catch (IOException e) {
                                System.out.println(e + " TestT");
                            }

                        }
                    } catch (Exception e) {
                        System.out.println(e.toString() + " Failed line 196  - "+ m + "  Previous = ");
                        break;
                    }
                }
            }
            if (!flag) {
                try {
                    int n = Integer.parseInt(entry);
                    clearScreen();
                    System.out.print("\tThe current date is: " + date + "\n\t" + attendance + " people have been marked in today.\n" + message + messageBuffer);
                    System.out.print("\n\tCreate new member: #" + padNumber(entry) + "?\n\t --> ");
                    String confirm = kybd.nextLine();
                    if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
                        createMember(n);
                        messageBuffer += ("\tNew member added!\n");
                    }
                } catch (NumberFormatException e) {
                    if (!specialEntry) {
                        messageBuffer += "\tPlease enter a number!\n";
                    }
                }
            }

            clearScreen();
            String totalMessage = "";
            totalMessage += "\tTotal: " + total + "\tWages Paid: ";
            if (wagesPaid > 0) {
                totalMessage += "" + wagesPaid;
            } else {
                totalMessage += "No";
            }

            totalMessage += "\t\tRent Paid: ";

            if (rentPaid > 0) {
                totalMessage += "" + rentPaid + "\n";
            } else {
                totalMessage += "No\n";
            }

            System.out.print("\tThe current date is: " + date + "\n\t#" + attendance + " people have been marked in today.\n" + totalMessage
                    + message + messageBuffer);

            members = loadMembers();
        }
    }

    private static void createMember(int entry) {
        try {
            Database db = DatabaseBuilder.open(new File("ABCDatabase.accdb"));
            Table table = db.getTable("Members");
            System.out.println("Table Loaded");

            table.addRow(   entry, null, null, null, null, null, null, null, null, null, 
                            null, null, null, null, null, null, null, null, null, null, 
                            null, null, null, null, null, null, null, null, null, null,
                            null, null, null, null, null, null, null, null, null, null,
                            null, null, null, null, null, null, null, null, null, null,
                            null, null, null, null, null, null, null, null, null, null,
                            null, null, null, null, null, null, null, null, null, null, null);
            
            System.out.println("Row added");
            for (Row r : table) { 
                if(r == null) { break; } 
                
                if (r.get("Forename") == null) {
                    System.out.println(entry + "  :   "  + date);
                    System.out.println(r);
                    r.put("Membership Number", entry); 
                    r.put("Forename", "UPDATE"); 
                    r.put("Date Issued", date); 
                    r.put("Issued By", "Nigel"); 
                    r.put("Paid", "£15"); 
                    r.put(date, true);

                    table.updateRow(r); 

                    message += "\n\t#" + padNumber("" + entry) + " has been added and marked in.\n";
                    attendance++;
                    newMembers += 1;
                    total += 15;
                    break;
                    
                }

            }
            Table record = db.getTable("Summary");
            for (Row r : record) {
                if (r.get("ABC Date").toString().equals(date)) {
                    int i = Integer.parseInt(r.get("£15 Entries").toString()) + 1;
                    r.put("£15 Entries", i);

                    Double d = Double.parseDouble(r.get("£15 Total").toString()) + 15;
                    r.put("£15 Total", d);

                    d = Double.parseDouble(r.get("Total").toString()) + 15;
                    r.put("Total", d);

                    record.updateRow(r);

                    break;
                }
            }
            db.close();
        } catch (Exception e) {
            System.out.println("Error creating member: " + e.toString() + "\nAt line: " + entry);

        }

    }

    private static String padNumber(String number) {
        switch (number.length()) {
            case 1:
                return "000" + number;
            case 2:
                return "00" + number;
            case 3:
                return "0" + number;
        }
        return number;
    }

    private static void clearScreen() {
        for (int i = 0; i < 15; i++) {
            System.out.println();
        }
    }

    private static void countVisits(Member m, Table t, Row r) {

        int visits = 0;
        String lastVisit = "";
        String num = "";
        for (Column c : t.getColumns()) {                
            num = r.get("Membership Number").toString();
            if (c.getColumnIndex() > 10) {
                if (r.get(c.getName()).equals(true)) {
                    visits++;
                    if (!c.getName().equals(date)) {
                        lastVisit = c.getName();
                    }
                }
            }
        }
   //     System.out.println("Before: " + r.get("Visits"));
        r.put("Visits", visits);
    //    System.out.println("After: " + r.get("Visits"));
        
        r.put("Last Visit", lastVisit);
        try{
            t.updateRow(r);
        }
        catch(Exception e){}
 //       if (visits == 1) {
  //          message += "\tThis is " + m.getForename() + "'s first visit!\n";
 //       } else {
    //        message += "\tThis is visit #" + visits + " for " + m.getForename() + ". Welcome back!\n\tLast visit date: " + lastVisit + "\n";
          //  message += num + " - " + visits + " - " + lastVisit + "\n";
 //       }

    }

    private static boolean listContains(String s, List<String> l) {
        boolean flag = false;

        for (String string : l) {
            if (string.equalsIgnoreCase(s)) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    private static void specialEntry(String entry) {
        //System.out.println("Special Entry - " + entry);
        try {
            Database db = DatabaseBuilder.open(new File("ABCDatabase.accdb"));
            Table t = db.getTable("Summary");
            for (Row r : t) {
                if (r.get("ABC Date").toString().equals(date)) {

                    if (entry.equalsIgnoreCase("guest")) {
                        int i = Integer.parseInt(r.get("Guest Entries").toString()) + 1;
                        r.put("Guest Entries", i);
                        t.updateRow(r);
                        messageBuffer += "\n\tGuest has been marked in!\n";
                        guests = i;
                        attendance++;
                    }

                    if (entry.equalsIgnoreCase("cancel guest")) {
                        int i = Integer.parseInt(r.get("Guest Entries").toString()) - 1;
                        r.put("Guest Entries", i);
                        t.updateRow(r);
                        messageBuffer += "\n\tGuest has been unmarked!\n";
                        guests = i;
                        attendance--;
                    }

                    if (entry.equalsIgnoreCase("free entry")) {
                        int i = Integer.parseInt(r.get("Guest Entries").toString()) + 1;
                        r.put("Guest Entries", i);
                        
                        guests = i;

                        i = Integer.parseInt(r.get("£10 Entries").toString()) - 1;
                        r.put("£10 Entries", i);

                        Double d = Double.parseDouble(r.get("£10 Total").toString()) - 10;
                        r.put("£10 Total", d);

                        t.updateRow(r);
                        messageBuffer += "\n\tFree Entry has been marked in!\n";
                        
                        total -= 10;

                    }

                    if (entry.equalsIgnoreCase("cancel free entry")) {
                        int i = Integer.parseInt(r.get("Guest Entries").toString()) - 1;
                        r.put("Guest Entries", i);
                        
                        guests = i;

                        i = Integer.parseInt(r.get("£10 Entries").toString()) + 1;
                        r.put("£10 Entries", i);

                        Double d = Double.parseDouble(r.get("£10 Total").toString()) + 10;
                        r.put("£10 Total", d);

                        t.updateRow(r);
                        messageBuffer += "\n\tFree Entry has been canceled!\n";
                        
                        total += 10;
                    }

                    if (entry.equalsIgnoreCase("sell sticker")) {
                        Double d = Double.parseDouble(r.get("Stickers Sold").toString()) + 1;
                        r.put("Stickers Sold", d);

                        Double x = Double.parseDouble(r.get("Total").toString()) + 1;
                        r.put("Total", x);

                        t.updateRow(r);
                        messageBuffer += "\n\tsticker has been marked as sold.\n";
                        StickersSold = d.intValue();
                        total += 1;

                    }

                    if (entry.equalsIgnoreCase("cancel sticker")) {
                        Double d = Double.parseDouble(r.get("Stickers Sold").toString()) - 1;
                        r.put("Stickers Sold", d);

                        Double x = Double.parseDouble(r.get("Total").toString()) - 1;
                        r.put("Total", x);

                        t.updateRow(r);
                        messageBuffer += "\n\tsticker has been unmarked as sold.\n";
                        StickersSold = d.intValue();
                        total -= 1;
                    }

                    if (entry.equalsIgnoreCase("pay wages")) {
                        Double d = Double.parseDouble(r.get("Wages").toString()) - 40;
                        r.put("Wages", d);

                        Double x = Double.parseDouble(r.get("Total").toString()) - 40;
                        r.put("Total", x);

                        t.updateRow(r);
                        messageBuffer += "\n\tWages have been paid.\n";
                        wagesPaid += 40;
                        total -= 40;

                    }

                    if (entry.equalsIgnoreCase("cancel wages")) {
                        Double d = Double.parseDouble(r.get("Wages").toString()) + 40;
                        r.put("Wages", d);

                        Double x = Double.parseDouble(r.get("Total").toString()) + 40;
                        r.put("Total", x);

                        t.updateRow(r);
                        messageBuffer += "\n\tWages have been unpaid.\n";
                        wagesPaid -= 40;
                        total += 40;

                    }

                    if (entry.equalsIgnoreCase("pay rent")) {
                        Double d = Double.parseDouble(r.get("Rent").toString()) - 50;
                        r.put("Rent", d);

                        Double x = Double.parseDouble(r.get("Total").toString()) - 50;
                        r.put("Total", x);

                        t.updateRow(r);
                        messageBuffer += "\n\tRent has been paid.\n";
                        rentPaid += 50;
                        total -= 50;

                    }

                    if (entry.equalsIgnoreCase("cancel rent")) {
                        Double d = Double.parseDouble(r.get("Rent").toString()) + 50;
                        r.put("Rent", d);

                        Double x = Double.parseDouble(r.get("Total").toString()) + 50;
                        r.put("Total", x);

                        t.updateRow(r);
                        messageBuffer += "\n\tRent has been unpaid.\n";
                        rentPaid -= 50;
                        total += 50;

                    }

                    if (entry.equalsIgnoreCase("sell wristband")) {
                        int i = Integer.parseInt(r.get("Band Sales").toString()) + 1;
                        r.put("Band Sales", i);
                        Double d = Double.parseDouble(r.get("Band Total").toString()) + 4;
                        r.put("Band Total", d);

                        Double x = Double.parseDouble(r.get("Total").toString()) + 4;
                        r.put("Total", x);

                        t.updateRow(r);

                        messageBuffer += "\n\tWristband as been marked as sold.\n";
                        bandsSold = i;
                        total += 4;

                    }

                    if (entry.equalsIgnoreCase("cancel wristband")) {
                        int i = Integer.parseInt(r.get("Band Sales").toString()) - 1;
                        r.put("Band Sales", i);
                        Double d = Double.parseDouble(r.get("Band Total").toString()) - 4;
                        r.put("Band Total", d);

                        Double x = Double.parseDouble(r.get("Total").toString()) - 4;
                        r.put("Total", x);

                        t.updateRow(r);

                        messageBuffer += "\n\tWristband as been unmarked as sold.\n";
                        bandsSold = i;
                        total -= 4;

                    }

                    if (entry.equalsIgnoreCase("entry 15")) {
                        int i = Integer.parseInt(r.get("£15 Entries").toString()) - 1;
                        r.put("£15 Entries", i);
                        Double d = Double.parseDouble(r.get("£15 Total").toString()) - 15;
                        r.put("£15 Total", d);

                        newMembers = i;

                        i = Integer.parseInt(r.get("£10 Entries").toString()) + 1;
                        r.put("£10 Entries", i);
                        d = Double.parseDouble(r.get("£10 Total").toString()) + 10;
                        r.put("£10 Total", d);

                        Double x = Double.parseDouble(r.get("Total").toString()) - 5;
                        r.put("Total", x);

                        t.updateRow(r);

                        messageBuffer += "\n\tReturning member has been registered.\n";

                        total += 5;

                    }

                    if (entry.equalsIgnoreCase("cancel entry 15")) {
                        int i = Integer.parseInt(r.get("£15 Entries").toString()) + 1;
                        r.put("£15 Entries", i);
                        Double d = Double.parseDouble(r.get("£15 Total").toString()) + 15;
                        r.put("£15 Total", d);

                        newMembers = i;

                        i = Integer.parseInt(r.get("£10 Entries").toString()) - 1;
                        r.put("£10 Entries", i);
                        d = Double.parseDouble(r.get("£10 Total").toString()) - 10;
                        r.put("£10 Total", d);

                        Double x = Double.parseDouble(r.get("Total").toString()) + 5;
                        r.put("Total", x);

                        t.updateRow(r);

                        messageBuffer += "\n\tReturning member has been un-registered.\n";

                        total -= 5;

                    }

                    if (entry.equalsIgnoreCase("cancel entry")) {
                        int i = Integer.parseInt(r.get("£10 Entries").toString()) - 1;
                        r.put("£10 Entries", i);
                        Double d = Double.parseDouble(r.get("£10 Total").toString()) - 10;
                        r.put("£10 Total", d);

                        Double x = Double.parseDouble(r.get("Total").toString()) - 10;
                        r.put("Total", x);

                        t.updateRow(r);

                        messageBuffer += "\n\tCancelled £10 entry.\n";
                        attendance--;

                        total -= 10;

                    }
                    if (entry.equalsIgnoreCase("cancel entry 15")) {
                        int i = Integer.parseInt(r.get("£15 Entries").toString()) - 1;
                        r.put("£15 Entries", i);
                        Double d = Double.parseDouble(r.get("£15 Total").toString()) - 15;
                        r.put("£15 Total", d);

                        Double x = Double.parseDouble(r.get("Total").toString()) - 15;
                        r.put("Total", x);

                        t.updateRow(r);

                        messageBuffer += "\n\tCancelled £10 entry.\n";
                        attendance--;
                        newMembers-=1;

                        total -= 15;

                    }

                }
            }
            db.close();

        } catch (Exception e) {
            System.out.println(e + "TEST");
        }
    }

    private static void reload() {
        try {
            Database db = DatabaseBuilder.open(new File("ABCDatabase.accdb"));
            Table t = db.getTable("Summary");
            for (Row r : t) {
                if (r.get("ABC Date").toString().equals(date)) {

                    int i = Integer.parseInt(r.get("£10 Entries").toString());

                    attendance += i;

                    i = Integer.parseInt(r.get("Guest Entries").toString());

                    attendance += i;
                    guests += i;

                    i = Integer.parseInt(r.get("£15 Entries").toString());

                    attendance += i;
                    newMembers += i;

                    i = Integer.parseInt(r.get("Band Sales").toString());

                    bandsSold += i;

                    Double d = Double.parseDouble(r.get("Rent").toString());

                    rentPaid = d.intValue();

                    if (rentPaid < 0) {
                        rentPaid = rentPaid * -1;
                    }

                    d = Double.parseDouble(r.get("Wages").toString());

                    wagesPaid = d.intValue();
                    if (wagesPaid < 0) {
                        wagesPaid = wagesPaid * -1;
                    }

                    d = Double.parseDouble(r.get("Stickers Sold").toString());

                    StickersSold = d.intValue();

                    d = Double.parseDouble(r.get("Total").toString());

                    total = d.intValue();

                    break;

                }
            }

            db.close();
        } catch (Exception e) {
            System.out.println(e + " Failed on Reload.");
        }
    }
}
