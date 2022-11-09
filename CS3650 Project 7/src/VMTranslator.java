import java.io.*;
import java.util.Scanner;

public class VMTranslator {
	public static void main(String[] args) {
		//find the file to be read
		Scanner input = new Scanner(System.in);
	
		System.out.println("Enter a .vm file name:");
		String filename = input.nextLine();
		input.close();
		
		//create the parser
		Parser parser = new Parser(filename);
		
		//create the code writer
		String outputname = filename.substring(0, filename.indexOf(".vm")) + ".asm";
		
		try {
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
			//bulk of the code, handled by the codewriter
			while (parser.hasMoreLines())
			{
				parser.advance();
				if (parser.commandType() == "C_PUSH" || parser.commandType() == "C_POP")
					codewriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
				else if (parser.commandType() == "C_ARITHMETIC")
					codewriter.writeArithmetic(parser.arg1());
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
