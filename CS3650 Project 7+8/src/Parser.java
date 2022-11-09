import java.io.*;
import java.util.Scanner;
import java.util.HashSet;

public class Parser {
	File file = null;
	Scanner reader = null;
	String command = null;
	HashSet<String> ops = new HashSet<String>();
	
	Parser(String filename)
	{
		try 
		{
			file = new File(filename);
			reader = new Scanner(file);
		} 
		catch (FileNotFoundException e) {
			System.out.println("File could not be found");
			e.printStackTrace();
		}
		
		//define the list of accepted operations
		ops.add("add");
		ops.add("sub");
		ops.add("neg");
		ops.add("eq");
		ops.add("gt");
		ops.add("lt");
		ops.add("and");
		ops.add("or");
		ops.add("not");
	}
	
	boolean hasMoreLines()
	{
		return reader.hasNextLine();
	}
	
	void advance()
	{
		String comm = reader.nextLine();
		if (comm == "" || comm.startsWith("//")) //skip whitespace and comments
		{
			if (reader.hasNextLine())
				advance();
			else
				command = null;
		}
		else
			command = comm;
		
		//remove comments
		if (command.contains("//"))
		{
			command = command.substring(0, command.indexOf("//"));
		}
		
		//we actually DO want the whitespace in this case, but we want to normalize it to make processing easier
		command = command.replaceAll("\\s",  " "); //change all whitespace to a singular space
		
		//and we want the line to start with the command, meaning we should remove the whitespace at the beginning
		//if (command.startsWith(" "))
		//{
			//command = command.substring(1);
		//}
		//we can just trim, it should work better
		command = command.trim();
		
		//System.out.println(command);
	}
	
	String commandType()
	{		
		if (ops.contains(command))
			return "C_ARITHMETIC";
		if (command.startsWith("push"))
			return "C_PUSH";
		if (command.startsWith("pop"))
			return "C_POP";
		
		return "";
	}
	
	String arg1()
	{
		//funny idea, a hashmap with a string key and a function value
		if (commandType() == "C_ARITHMETIC")
			return command;
		else
		{
			//return the middle portion (everything should be separated by whitespace)
			String second = command.substring(command.indexOf(" ") + 1);
			//System.out.println(second.substring(0,second.indexOf(" ")));
			return second.substring(0,second.indexOf(" "));
		}
	}
	
	int arg2()
	{
		String second = command.substring(command.indexOf(" ") + 1);
		//System.out.println(second.substring(second.indexOf(" ") + 1));
		return Integer.parseInt(second.substring(second.indexOf(" ") + 1));
	}
}
