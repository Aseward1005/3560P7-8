import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class VMTranslator {
	public static void main(String[] args) {
		//find the file to be read
		Scanner input = new Scanner(System.in);
	
		System.out.println("Enter a .vm file name or directory:");
		String filename = input.nextLine();
		File file = new File(filename);
		input.close();
		
		String outputname = "";
		//create the parser(s)
		//make parsers into a list so it's iterable
		ArrayList<Parser> parsers = new ArrayList<Parser>();
		if (file.isFile()) //if it is a file, do what we've been doing
		{
			parsers.add(new Parser(filename));
			outputname = filename.substring(0, filename.indexOf(".vm")) + ".asm";
		}
		else //otherwise, make a list of parsers for each .vm in the directory
		{
			outputname = filename + "\\" + file.getName() + ".asm";
			for (File f : file.listFiles())
			{
				if (f.getName().endsWith(".vm"))
					parsers.add(new Parser(f.getAbsolutePath()));
			}
		}
		
		//keep track of the current function name
		String functionName = "";
		try {
			//bootstrap code
			//initialize the stack pointer
			FileWriter out = new FileWriter(outputname);
			int spbase = 256;
			out.write("@" + spbase + "\n");
			out.write("D=A\n");
			out.write("@SP\n");
			out.write("M=D\n");
			out.close();
			
			//create the code writer
			CodeWriter codewriter = new CodeWriter(outputname);
			//call sys.init
			codewriter.writeCall("Sys.init", 0);
			//bulk of the code, handled by the codewriter
			for (Parser parser : parsers)
			{
				codewriter.setFileName(parser.file.getName());
				//System.out.println(parser.file.getName());
				while (parser.hasMoreLines())
				{
					parser.advance();
					//System.out.println(parser.command);
					if (parser.commandType() == "C_PUSH" || parser.commandType() == "C_POP")
						codewriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
					else if (parser.commandType() == "C_ARITHMETIC")
						codewriter.writeArithmetic(parser.arg1());
					else {
						//otherwise its a control flow command
						String comm = parser.command;
						if (comm.equals("return"))
							codewriter.writeReturn();
						else
						{
							String label = comm.substring(comm.indexOf(" ") + 1);
							comm = comm.substring(0, comm.indexOf(" "));
					
							if (comm.equals("label"))
							{
								//assemble the label to be passed in
								label = functionName + "$" + label;
					
								//write the label to the file
								codewriter.writeLabel(label);
							}
							else if (comm.equals("if-goto"))
							{
								//assemble the label to be passed in
								label = functionName + "$" + label;
							
								//write the branch instruction to the file
								codewriter.writeIf(label);
							}
							else if (comm.equals("goto"))
							{
								//assemble the label to be passed in
								label = functionName + "$" + label;
						
								//write the jump
								codewriter.writeGoto(label);
							}
							else if (comm.equals("function"))
							{
								//parse the instruction into its parts
								functionName = label.substring(0, label.indexOf(" "));
								int num = Integer.parseInt(label.substring(label.indexOf(" ") + 1));
								
								codewriter.writeFunction(functionName, num);
							}
							else if (comm.equals("call"))
							{
								String function = label.substring(0, label.indexOf(" "));
								int num = Integer.parseInt(label.substring(label.indexOf(" ") + 1));
								
								codewriter.writeCall(function, num);
							}
						}
					}
				}
			}
			
			//close the code writer
			codewriter.close();
			//append the infinite loop
			out = new FileWriter(outputname, true);
			out.write("//infinite loop\n");
			out.write("(END)\n");
			out.write("@END\n");
			out.write("0;JMP");
			out.close();
		}
		catch (IOException e){
			System.out.println("there was a problem initializing the stack pointer");
			e.printStackTrace();
		}
		
		System.out.println("Done");
	}
}
