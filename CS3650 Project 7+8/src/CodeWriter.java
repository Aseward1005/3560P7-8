import java.io.*;
import java.util.HashMap;

public class CodeWriter {
	
	File file = null;
	FileWriter writer = null;
	String workingfile = null;
	int fncalls;
	String currfn;
	HashMap<String, Integer> bases = new HashMap<String, Integer>();
	
	int eqs;
	int lts;
	int gts;
	
	
	CodeWriter(String filename)
	{
		try
		{
			file = new File(filename);
			writer = new FileWriter(file, true);//open the filewriter in append mode
			//so the stack pointer can be initialized outside the codewriter
		}
		catch (IOException e)
		{
			System.out.println("There was an error initialzing the file writer");
			e.printStackTrace();
		}
		
		//hold the current file name(default it to the name of the .asm file if not specified)
		//chapter 8 addition
		setFileName(file.getName()); 
		
		bases.put("local", 1);
		bases.put("argument", 2);
		bases.put("this", 3);
		bases.put("that", 4);
		
		eqs = 0;
		lts = 0;
		gts = 0;
		
		//chapter 8 addition
		fncalls = 0;
	}
	
	//chapter 7
	void writeArithmetic(String command)
	{
		try {
			writer.write("//" + command + "\n");
		} catch (IOException e) {
			System.out.println("problem when displaying arithmetic command");
			e.printStackTrace();
		}
		
		if (command.equals("add"))
		{
			//set D to the value popped from the top of the stack
			pop();
			//A is at the correct spot already(+1), all that needs to be done is add D
			try {
				writer.write("A=A-1\n");
				writer.write("M=M+D\n");
			} catch (IOException e) {
				System.out.println("problem when adding");
				e.printStackTrace();
			}
		}
		else if (command.equals("sub"))
		{
			//set D to the value popped from the top of the stack
			pop();
			//A is at the correct spot already(+1), all that needs to be done is sub d
			try {
				writer.write("A=A-1\n");
				writer.write("M=M-D\n");
			} catch (IOException e) {
				System.out.println("problem when subtracting");
				e.printStackTrace();
			}
		}
		else if (command.equals("neg"))
		{
			try {
				//point at the top of the stack
				writer.write("@SP\n");
				writer.write("A=M\n");
				writer.write("A=A-1\n");
				//negate that value
				writer.write("M=-M");
			} catch (IOException e) {
				System.out.println("problem when negating");
				e.printStackTrace();
			}
		}
		else if (command.equals("and"))
		{
			//set D to the value popped from the top of the stack
			pop();
			//A is at the correct spot already(+1), all that needs to be done is and D
			try {
				writer.write("A=A-1\n");
				writer.write("M=M&D\n");
			} catch (IOException e) {
				System.out.println("problem when anding");
				e.printStackTrace();
			}
		}
		else if (command.equals("or"))
		{
			//set D to the value popped from the top of the stack
			pop();
			//A is at the correct spot already(+1), all that needs to be done is and D
			try {
				writer.write("A=A-1\n");
				writer.write("M=M|D\n");
			} catch (IOException e) {
				System.out.println("problem when oring");
				e.printStackTrace();
			}
		}
		else if (command.equals("not"))
		{
			try {
				//point at the top of the stack
				writer.write("@SP\n");
				writer.write("A=M\n");
				writer.write("A=A-1\n");
				//negate that value
				writer.write("M=!M\n");
			} catch (IOException e) {
				System.out.println("problem when notting");
				e.printStackTrace();
			}
		}
		
		else if (command.equals("eq"))
		{
			//set D to the value popped from the top of the stack
			pop();
			try {
				//subtract from the value at A-1
				writer.write("A=A-1\n");
				writer.write("M=M-D\n");
				writer.write("D=M\n");
				writer.write("@EQ" + eqs + "\n");
				writer.write("D;JEQ\n"); //if zero, set to -1
				writer.write("@SP\n");
				writer.write("A=M\n");
				writer.write("A=A-1\n");
				writer.write("M=0\n"); //else, set to 0
				writer.write("@ENDEQ" + eqs + "\n");
				writer.write("0;JMP\n");
				writer.write("(EQ" + eqs + ")\n");
				writer.write("@SP\n");
				writer.write("A=M\n");
				writer.write("A=A-1\n");
				writer.write("M=-1\n");
				writer.write("(ENDEQ" + eqs + ")\n");
				
				eqs++;
			} catch (IOException e) {
				System.out.println("problem when calculating equality");
				e.printStackTrace();
			}
		}
		else if (command.equals("lt"))
		{
			//set D to the value popped from the top of the stack
			pop();
			try {
				//subtract from the value at A-1
				writer.write("A=A-1\n");
				writer.write("M=M-D\n");
				writer.write("D=M\n");
				writer.write("@LT" + lts + "\n");
				writer.write("D;JLT\n"); //if negative, set to -1
				writer.write("@SP\n");
				writer.write("A=M\n");
				writer.write("A=A-1\n");
				writer.write("M=0\n"); //else, set to 0
				writer.write("@ENDLT" + lts + "\n");
				writer.write("0;JMP\n");
				writer.write("(LT" + lts + ")\n");
				writer.write("@SP\n");
				writer.write("A=M\n");
				writer.write("A=A-1\n");
				writer.write("M=-1\n");
				writer.write("(ENDLT" + lts + ")\n");
				
				lts++;
			} catch (IOException e) {
				System.out.println("problem when calculating equality");
				e.printStackTrace();
			}
		}
		else if (command.equals("gt"))
		{
			//set D to the value popped from the top of the stack
			pop();
			try {
				//subtract from the value at A-1
				writer.write("A=A-1\n");
				writer.write("M=M-D\n");
				writer.write("D=M\n");
				writer.write("@GT" + gts + "\n");
				writer.write("D;JGT\n"); //if positive, set to -1
				writer.write("@SP\n");
				writer.write("A=M\n");
				writer.write("A=A-1\n");
				writer.write("M=0\n"); //else, set to 0
				writer.write("@ENDGT" + gts + "\n");
				writer.write("0;JMP\n");
				writer.write("(GT" + gts + ")\n");
				writer.write("@SP\n");
				writer.write("A=M\n");
				writer.write("A=A-1\n");
				writer.write("M=-1\n");
				writer.write("(ENDGT" + gts + ")\n");
				
				gts++;
			} catch (IOException e) {
				System.out.println("problem when calculating equality");
				e.printStackTrace();
			}
		}
	}
	
	void writePushPop(String command, String segment, int index)
	{	
		if (command.equals("C_PUSH"))
		{
			try {
				writer.write("//push " + segment + " " + index + "\n");
			
				//set D
				if (segment.equals("constant"))
				{
					consthelper(index);
					writer.write("D=A\n");
				}
				else if (segment.equals("static"))
				{
					statichelper(index);
					writer.write("D=M\n");
				}
				else if (segment.equals("temp"))
				{
					temphelper(index);
					writer.write("D=M\n");
				}
				else if (segment.equals("pointer"))
				{
					pointerhelper(index);
					writer.write("D=M\n");
				}
				else if (bases.containsKey(segment))
				{
					basehelper(segment, index);
					writer.write("D=M\n");
				}
				else
					throw new IOException();
			}
			catch (IOException e) {
				System.out.println("error while pushing");
				e.printStackTrace();
			}
			//push D
			push();		
		}
		
		else if (command.equals("C_POP"))
		{	
			try {
				writer.write("//pop " + segment + " " + index + "\n");
				//pop D
				pop();
			
				//access and store
				if (segment.equals("static"))
					statichelper(index);
				else if (segment.equals("temp"))
					temphelper(index);
				else if (segment.equals("pointer"))
					pointerhelper(index);
				else if (bases.containsKey(segment))
					basehelper(segment, index);
				else
					throw new IOException();
				
				writer.write("M=D\n");
			}
			catch (IOException e) {
				System.out.println("error while popping");
				e.printStackTrace();
			}
		}
	}
	
	void close()
	{
		try {
			writer.close();
		} catch (IOException e) {
			System.out.println("There was an error closing the file");
			e.printStackTrace();
		}
	}
	
	//chapter 8 additions
	void setFileName(String filename)
	{
		//System.out.println(filename);
		workingfile = filename.substring(0, filename.indexOf("."));
	}
	
	//remember SOLID, this will ONLY write whatever label is passed in
	//it doesn't worry about how the label is formatted, only writes exactly what it's told
	//the writer doesn't have much logic, it just does what you tell it
	//the translator holds all the logic
	void writeLabel(String label)
	{
		try {
			writer.write("//label " + label + "\n");
			writer.write("(" + label + ")\n");
		} catch (IOException e) {
			System.out.println("error when writing label");
			e.printStackTrace();
		}
	}
	
	//again, this will ONLY go to exactly what label is passed in, it doesn't think about what that means
	void writeGoto(String label)
	{
		try {
			writer.write("//goto " + label + "\n");
			writer.write("@" + label + "\n");
			writer.write("0;JMP\n");
		} catch (IOException e) {
			System.out.println("error when writing label");
			e.printStackTrace();
		}
	}
	
	//and once more, this ONLY jumps to exactly what label is passed in
	void writeIf(String label)
	{
		try {
			writer.write("//if-goto " + label + "\n");
			//pop the value at the top of the stack
			pop();
			//if not equal to 0, jump
			writer.write("@" + label + "\n");
			writer.write("D;JLT\n");
			writer.write("D;JGT\n");
		} catch (IOException e) {
			System.out.println("There was an error writing the if-goto statement");
			e.printStackTrace();
		}
	}
	
	void writeFunction(String functionName, int nVars)
	{
		try {
			writer.write("//function " + functionName + " " + nVars + "\n");
			//write the label
			writer.write("(" + functionName + ")\n");
			
			//initialize the local variables to 0
			for (int i = 0; i < nVars; i++)
			{
				writer.write("D=0\n");
				push();
			}
			
		} catch (IOException e) {
			System.out.println("error when writing start of function");
			e.printStackTrace();
		}
		currfn = functionName;
		fncalls = 0;
	}
	
	void writeReturn()
	{
		try {
			writer.write("//return\n");
			//frame = LCL, use R13 as frame
			writer.write("@" + bases.get("local") + "\n");
			writer.write("D=M\n"); //d=lcl
			writer.write("@R13\n");
			writer.write("M=D\n"); //frame=lcl
			
			//retAddr = *(frame-5), use R14 as retaddr
			writer.write("@5\n"); 
			writer.write("D=A\n"); //D = 5
			writer.write("@R13\n");
			writer.write("D=M-D\n"); //D = frame-5
			writer.write("A=D\n"); //*(frame-5)
			writer.write("D=M\n"); //D = *(frame-5)
			writer.write("@R14\n");
			writer.write("M=D\n"); //retaddr = *(frame-5)
			
			//*ARG = pop()
			pop(); //D=pop
			writer.write("@" + bases.get("argument") + "\n");
			writer.write("A=M\n"); 
			writer.write("M=D\n"); //*arg = pop
			
			//SP = ARG+1
			writer.write("@" + bases.get("argument") + "\n");
			writer.write("D=M\n"); //d=ARG
			writer.write("D=D+1\n"); //d=ARG+1
			writer.write("@SP\n");
			writer.write("M=D\n"); //SP=arg+1
			
			//THAT = *(frame-1)
			writer.write("@1\n");
			writer.write("D=A\n"); //D = 1
			writer.write("@R13\n");
			writer.write("D=M-D\n"); //D=(frame-1)
			writer.write("A=D\n");
			writer.write("D=M\n"); //D = *(frame-1)
			writer.write("@" + bases.get("that") + "\n");
			writer.write("M=D\n"); //that = *(frame-1)
			
			//THIS = *(frame-2)
			writer.write("@2\n");
			writer.write("D=A\n"); //D = 2
			writer.write("@R13\n");
			writer.write("D=M-D\n"); //D=(frame-2)
			writer.write("A=D\n");
			writer.write("D=M\n"); //D = *(frame-2)
			writer.write("@" + bases.get("this") + "\n");
			writer.write("M=D\n"); //this = *(frame-2)
			
			//ARG = *(frame-3)
			writer.write("@3\n");
			writer.write("D=A\n"); //D = 3
			writer.write("@R13\n");
			writer.write("D=M-D\n"); //D=(frame-3)
			writer.write("A=D\n");
			writer.write("D=M\n"); //D = *(frame-3)
			writer.write("@" + bases.get("argument") + "\n");
			writer.write("M=D\n"); //this = *(frame-3)
			
			//LCL = *(frame-4)
			writer.write("@4\n");
			writer.write("D=A\n"); //D = 4
			writer.write("@R13\n");
			writer.write("D=M-D\n"); //D=(frame-4)
			writer.write("A=D\n");
			writer.write("D=M\n"); //D = *(frame-4)
			writer.write("@" + bases.get("local") + "\n");
			writer.write("M=D\n"); //this = *(frame-4)
			
			//goto retaddr
			writer.write("@R14\n");
			writer.write("A=M\n");
			writer.write("0;JMP\n");
			
		} catch (IOException e) {
			System.out.println("problem writing return");
			e.printStackTrace();
		}
	}
	
	void writeCall(String functionName, int nArgs)
	{
		try {
			writer.write("//call " + functionName + " " + nArgs + "\n");
			//find and push returnAddress
			writer.write("@" + currfn + "$ret" + fncalls + "\n");
			writer.write("D=A\n");
			push();
			
			//push local
			writer.write("@" + bases.get("local") + "\n");
			writer.write("D=M\n");
			push();
			
			//push arg
			writer.write("@" + bases.get("argument") + "\n");
			writer.write("D=M\n");
			push();
			
			//push this
			writer.write("@" + bases.get("this") + "\n");
			writer.write("D=M\n");
			push();
			
			//push that
			writer.write("@" + bases.get("that") + "\n");
			writer.write("D=M\n");
			push();
			
			//arg = sp - 5 - nargs
			writer.write("@SP\n");
			writer.write("D=M\n"); //D = SP
			writer.write("@" + bases.get("argument") + "\n");
			writer.write("M=D\n"); //arg = sp
			writer.write("@5\n");
			writer.write("D=A\n"); //D=5
			writer.write("@" + bases.get("argument") + "\n");
			writer.write("M=M-D\n"); //arg = sp-5
			writer.write("@" + nArgs + "\n");
			writer.write("D=A\n"); //d = nargs
			writer.write("@" + bases.get("argument") + "\n");
			writer.write("M=M-D\n"); //arg = sp-5-nargs
			
			//lcl = sp
			writer.write("@SP\n");
			writer.write("D=M\n"); //D = sp
			writer.write("@" + bases.get("local") + "\n");
			writer.write("M=D\n"); //local = sp
			
			//goto f
			writer.write("@" + functionName + "\n");
			writer.write("0;JMP\n");
			
			//inject returnAddress
			writer.write("(" + currfn + "$ret" + fncalls + ")\n");
			//increment the number of calls
			fncalls++;
		} catch (IOException e) {
			System.out.println("problem when calling");
			e.printStackTrace();
		}
	}
	//access helpers
	private void pointerhelper(int index)
	{
		try {
			writer.write("@" + (bases.get("this") + index) + "\n");
		} catch (IOException e) {
			System.out.println("There was an error in a pointer instruction");
			e.printStackTrace();
		}
	}
	private void consthelper(int index)
	{
		try {
			writer.write("@" + index + "\n");
		} catch (IOException e) {
			System.out.println("problem with constant access");
			e.printStackTrace();
		}
	}
	private void statichelper(int index)
	{
		//assemble the variable name
		String name = workingfile + "." + index;
		
		try {
			writer.write("@" + name + "\n");
		} catch (IOException e) {
			System.out.println("problem with static access");
			e.printStackTrace();
		}
	}
	private void temphelper(int index)
	{
		try {
			//save the popped value at r13
			writer.write("@R13\n");
			writer.write("M=D\n");
			
			//create the address to jump to
			//temp = 5 and idk what the pointer name is
			writer.write("@5\n");
			writer.write("D=A\n");
			writer.write("@R14\n");
			writer.write("M=D\n");
			writer.write("@" + index + "\n");
			writer.write("D=A\n");
			writer.write("@R14\n");
			writer.write("M=M+D\n");
			
			//restore D
			writer.write("@R13\n");
			writer.write("D=M\n");
			
			//jump to the correct spot in memory
			writer.write("@R14\n");
			writer.write("A=M\n");
			
		} catch (IOException e) {
			System.out.println("problem accessing temp");
			e.printStackTrace();
		}
	}
	private void basehelper(String segment, int index)
	{
		try {
			//save the popped value at r13
			writer.write("@R13\n");
			writer.write("M=D\n");
			
			//create the address to jump to
			writer.write("@" + bases.get(segment) + "\n");
			writer.write("D=M\n");
			writer.write("@R14\n");
			writer.write("M=D\n");
			writer.write("@" + index + "\n");
			writer.write("D=A\n");
			writer.write("@R14\n");
			writer.write("M=M+D\n");
			
			//restore D
			writer.write("@R13\n");
			writer.write("D=M\n");
			
			//jump to the correct spot in memory
			writer.write("@R14\n");
			writer.write("A=M\n");
		} catch (IOException e) {
			System.out.println("problem with base/offset access");
			e.printStackTrace();
		}
	}
	
	//push/pop helpers
	private void push()
	{
		String sp = "@SP\n";
		try {
			//ram[sp++] = D
			writer.write(sp);
			writer.write("A=M\n");
			writer.write("M=D\n");
			writer.write(sp);
			writer.write("M=M+1\n");
		} catch (IOException e) {
			System.out.println("There was an error during push");
			e.printStackTrace();
		}
	}
	private void pop()
	{
		try {
			//D = ram[--sp]
			writer.write("@SP\n");
			writer.write("M=M-1\n");
			writer.write("A=M\n");
			writer.write("D=M\n");
		} catch (IOException e) {
			System.out.println("There was an error during push");
			e.printStackTrace();
		}
	}
}
