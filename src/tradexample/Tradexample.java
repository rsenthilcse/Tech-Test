/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tradexample;

/**
 *
 * @author India
 */
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Tradexample {

    /**
     * @param args the command line arguments
     */
    
	static SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
    public static void main(String[] args) {
        // TODO code application logic here
        
        
		// Create sell and buys array list to get the list of 
		// buyers and sellers 
		ArrayList<Trade> sells = new ArrayList<Trade>(); 
		ArrayList<Trade> buys = new ArrayList<Trade>();
	 
		Map<String, Double> sellMap = new HashMap<String, Double>(); 
		Map<String, Double> buyMap = new HashMap<String, Double>();
		
		double price = 0; 
		double exchange = 0;
		int units = 0;
		File file=new File("E:\\Project\\trade.txt");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		// since we are using files put the execution code in try..catch 
		try { 
			// create a file object by passing it to scanner class 
            Scanner input=new Scanner(file);
				
			// read the lines from the text file 
			while (input.hasNextLine()) { 
				Trade t = new Trade();
				//if ("".equals(input.nextLine())) continue;
					
				String data[]= input.nextLine().toString().split("\\|");
				t.setEntity(data[0]);
				t.setType(data[1]);
				t.setFxRate(new Double(data[2]));
				t.setCurrency(data[3]);
				t.setInstructionDate((Date)sdf.parse(data[4].replace(" ", "-")));
				t.setSettleDate((Date)sdf.parse(data[5].replace(" ", "-")));
				t.setUnits(Integer.valueOf(data[6]));
				t.setPrice(new Double(data[7]));
				
				if ("B".equalsIgnoreCase(t.getType())) {
					buys.add(t);
					buyMap.put(t.getEntity(), (t.getUnits() * t.getPrice() * t.getFxRate()));
				}else if ("S".equalsIgnoreCase(t.getType())) {
					sells.add(t);
					sellMap.put(t.getEntity(), (t.getUnits() * t.getPrice() * t.getFxRate()));
				}	 
			} 
			
			HashMap<Date, Double> buyTotal = getTotalUSD(buys);
			HashMap<Date, Double> sellTotal = getTotalUSD(sells);
			
			System.out.println("********Amount in USD settled incoming everyday*********");
			printData(sellTotal);	
			System.out.println();
			System.out.println("******** Incoming Ranking*********");
			dispalyRanks(sellMap);
			
			System.out.println();
			System.out.println();
			System.out.println("********Amount in USD settled outgoing everyday*********");
			printData(buyTotal);
			System.out.println();
			System.out.println("******** Outgoing Ranking*********");
			dispalyRanks(buyMap);
			System.out.println();
			
		}catch(Exception e) {
			System.out.println("Exception --->"+e.getMessage());
		}finally {

		}	
	  }
	
	  private static void dispalyRanks(Map<String, Double> map) {
		  System.out.println("---------------------------------" );
		  System.out.println("Ranking  |   Entity   |  Amount  " );
		  System.out.println("---------------------------------" );
		  
		  Map<String, Double> sorted = map
			        .entrySet()
			        .stream()
			        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
			        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,LinkedHashMap::new));
		  Iterator it = sorted.entrySet().iterator();
		  int counter=1;
		  double amt=0;
		  while (it.hasNext()) {
		       Map.Entry pair = (Map.Entry)it.next();
		       
		       if(amt == (Double)pair.getValue()) {
		    	   counter--;
		    	   System.out.println(counter +"        | "+pair.getKey()+"       | "+amt );
		       }else {
		    	   System.out.println(counter+"        | "+pair.getKey()+"       | "+pair.getValue() );
		    	      
		       }	   
		       counter++;
		       amt=(Double)pair.getValue();
		  }
	  }
	  private static void printData(HashMap<Date, Double> total) {
		  System.out.println();
		  System.out.println("----------------------------" );
		  System.out.println("Settlement Date | Toal USD  " );
		  System.out.println("----------------------------" );
		  Iterator it = total.entrySet().iterator();
		  
		  while (it.hasNext()) {
		       Map.Entry pair = (Map.Entry)it.next();
		       String strDate= formatter.format(pair.getKey());
		       System.out.println(strDate+"     | "+pair.getValue() );
		  }
	  }	
	  private static HashMap<Date, Double> getTotalUSD(ArrayList<Trade> tradeList){
		  HashMap<Date, Double> map = new HashMap<Date, Double>();
		  double totalUSD=0;
		  for(Trade tr : tradeList) {
			  totalUSD = tr.getUnits() * tr.getPrice() * tr.getFxRate();
			  Date tmpDate=tr.getSettleDate();
			  Calendar c = Calendar.getInstance();
	          c.setTime(tmpDate);
				
	          while(!isBankHoliday(c.getTime(), c)) {
		          c.add(Calendar.DAY_OF_WEEK, 1);
		      }
	          Date date=c.getTime();
			  
			  if(map.containsKey(date)){
				  map.put(date, map.get(date)+totalUSD);
			  }else {
				  map.put(date, totalUSD);
			  }
		  }
		  return map;
	  }	
	  
	  public static boolean isBankHoliday(Date date, Calendar calendar) {
		  calendar.setTime(date);
          int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
          // check if it is Saturday(day=7) or Sunday(day=1)
          if ((dayOfWeek == 7) || (dayOfWeek == 1)) {
              return false;
          }
          return true;

    }
    
}
