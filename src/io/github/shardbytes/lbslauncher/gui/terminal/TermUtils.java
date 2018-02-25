package io.github.shardbytes.lbslauncher.gui.terminal;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi.Attribute;
import com.diogonunes.jcdp.color.api.Ansi.BColor;
import com.diogonunes.jcdp.color.api.Ansi.FColor;

public class TermUtils{
	
	private static ColoredPrinter cp;
	
	public static void init(){
		if(System.getProperty("os.name").startsWith("Win")){
			//Do nothing
		}else{
			cp = new ColoredPrinter.Builder(1, false).foreground(FColor.WHITE).background(BColor.BLACK).build();
		}
		
	}
	
	public static void println(String text){
		if(System.getProperty("os.name").startsWith("Win")){
			System.out.println(text);
		}else{
			cp.print(cp.getDateFormatted(), Attribute.NONE, FColor.CYAN, BColor.BLACK);
			cp.println(" " + text);
		}
		
	}
	
	public static void printerr(String text){
		if(System.getProperty("os.name").startsWith("Win")){
			System.err.println(text);
		}else{
			cp.print(cp.getDateFormatted(), Attribute.NONE, FColor.RED, BColor.BLACK);
			cp.println(" " + text, Attribute.NONE, FColor.RED, BColor.BLACK);
		}
		
	}
	
}
