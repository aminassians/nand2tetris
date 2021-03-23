import java.util.*;
import java.io.*;
import java.util.HashMap;

public class myCode {

	static HashMap <String, String> mMap = new HashMap <String, String>();
	static int counter = 0;
	static	int m = 16;
	
	static String codeLine = "";
		
		
	public static void main(String[] args) {
			
		if(args.length > 0){	
				
			char [] binaryArr = new char[16];
			String binaryNum;
			File newFile = new File(args[0]);
			try(Scanner scan = new Scanner(newFile);)
			{
				String binString = "";
				String binCheck = "";
				while(scan.hasNextLine())
				{
					binString = scan.nextLine().trim();
					if ((binString.length() > 0) && (binString.charAt(0)=='('))
					{			
							binCheck = binString.substring(1, binString.length()-1);
								mMap.put(binCheck, Integer.toString(counter));
						}
		
					binString = binString.trim();
					if((binString.length() > 0) && (binString.charAt(0) != '/')&& (binString.charAt(0) != '('))
					{						
						counter++;
						codeLine = binString;
						}
					}	
				}catch(IOException x){
					System.err.format("IOException: %s%n", x);
				}
				try(Scanner fileScan = new Scanner(newFile);){
					String line = "";
					String hackFile = args[0].substring(0, args[0].indexOf(".asm")) + ".hack";
				
					File outFile = new File(hackFile);
					FileOutputStream streamOut = new FileOutputStream(outFile);
					BufferedWriter fileBuff = new BufferedWriter(new OutputStreamWriter(streamOut));	
					while(fileScan.hasNextLine())
					{
						line = fileScan.nextLine();
						line = line.trim();
						if(line.indexOf('/') != -1)
						{
						line = line.substring(0,line.indexOf('/'));
						}

						if ((line.length() > 0) && ((line.charAt(0) == '@') || (line.charAt(0)=='(')))
						{
							if (line.charAt(0)=='('){
							}
							else{
							binaryArr[0] = '0';
							String binCheck = line.substring(1);
						
							String numLine = symbolTable(binCheck);
										
							binaryNum = convertToBin(numLine);
							int count = 0;
							int index = 1;
							while(count < binaryNum.length()){
								binaryArr[index] = binaryNum.charAt(count);
								index++;
								count++;
							}
						 	for(int i=0; i < 16; i++){
								System.out.print(binaryArr[i]);
								fileBuff.write(binaryArr[i]);	
							}
							fileBuff.newLine();
							System.out.println();
						}}
						else if((line.length() > 0) && ((line.indexOf('D')!=-1) || (line.indexOf('A')!=-1) || (line.indexOf('M')!=-1)
							|| (line.indexOf('=')!=-1) || (line.indexOf(';')!=-1)) && (line.charAt(0) != '/') && (line.indexOf('(') == -1)){//C-INSTRUCTION


							for(int i=0; i < 3; i++){
								binaryArr[i] = '1';
							}
							if (line.indexOf('=') > -1){
								binaryNum = replaceWith(line);
								int count = 0;
								int index = 3;
								while(count < binaryNum.length()){
									binaryArr[index] = binaryNum.charAt(count);
									index++;
									count++;
								}
								for(int i=0; i<16; i++){
									System.out.print(binaryArr[i]);
									fileBuff.write(binaryArr[i]);	
								}
								fileBuff.newLine();
								System.out.println();
							}
							else if(line.indexOf(';') > -1){

								binaryNum = jumpInst(line);
								int count = 0;
								int index = 3;
								while (count < binaryNum.length()){
									binaryArr[index] = binaryNum.charAt(count);
									index++;
									count++;
								}
								for(int i=0; i<16; i++){
									System.out.print(binaryArr[i]);
									fileBuff.write(binaryArr[i]);	
								}
								fileBuff.newLine();
								System.out.println();
							}
						}
						
					}fileBuff.close();
				
				}catch(IOException x){
					System.err.format("IOException: %s%n", x);
				}
			}
			else{
			System.out.println("No file argument");
			}
		}

	public static String convertToBin(String decimal){
		String binaryString = "";
		int replace = Integer.parseInt(decimal);
		int binaryPower;
		double doublePow;
		double power = 14;
		double two = 2;
		while(!(power < 0)){
			doublePow = Math.pow(two, power);
			binaryPower = (int) doublePow;
			if(replace >= binaryPower){
		
				replace = replace - binaryPower;
				binaryString = binaryString.concat("1");
				power--;
			}
			else{

				binaryString = binaryString.concat("0");
				power--;
			}
		}

		return binaryString;
	}

		public static String symbolTable(String str1){
			String lineNum = "";

			switch(str1.trim()){
			case "R0":
			case "SP": lineNum = "0";
			break;
			case "R1":
			case "LCL": lineNum = "1";
			break;
			case "R2":
			case "ARG": lineNum = "2";
			break;
			case "R3":
			case "THIS": lineNum = "3";
			break;
			case "R4":
			case "THAT": lineNum = "4";
			break;
			case "R5": lineNum = "5";
			break;
			case "R6": lineNum = "6";
			break;
			case "R7": lineNum = "7";
			break;
			case "R8": lineNum = "8";
			break;
			case "R9": lineNum = "9";
			break;
			case "R10": lineNum = "10";
			break;
			case "R11": lineNum = "11";
			break;
			case "R12": lineNum = "12";
			break;
			case "R13": lineNum = "13";
			break;
			case "R14": lineNum = "14";
			break;
			case "R15": lineNum = "15";
			break;
			case "SCREEN": lineNum = "16384";
			break;
			case "KBD": lineNum = "24576";
			break;
			default: lineNum = "";
			}

			if(lineNum == ""){
				if (Character.isLetter(str1.charAt(0))){

					if (mMap.containsKey(str1)){
								
						lineNum = mMap.get(str1);
					}
					else{
					
						mMap.put(str1, Integer.toString(m));
						m++;
						lineNum = mMap.get(str1);
					}
				}
				else{
					lineNum = str1;
				}
			}

		
			return lineNum; 
		


		}	

		public static String jumpInst(String jumpLine){
			int indexSemi = jumpLine.indexOf(';');
			String subcomp = jumpLine.substring(0, indexSemi);
			String subJump = jumpLine.substring(indexSemi+1);
			String comp = "";
			String noDest = "";
			String Dest = "";
			String Jump = "";
			String total = "";
			jumpLine = jumpLine.trim();	
			comp = checkTrans(subcomp);
			Dest = destDict(noDest);
			
			switch(subJump.trim()){
			case "JGT": Jump = "001";
			break;
			case "JEQ": Jump = "010";
			break;
			case "JGE": Jump = "011";
			break;
			case "JLT": Jump = "100";
			break;
			case "JNE": Jump = "101";
			break;
			case "JLE": Jump = "110";
			break;
			case "JMP": Jump = "111";
			//	System.out.println("IN JMP");
			break;
			default: System.out.println("ERROR IN JUMP");
			}

			total = comp + Dest + Jump;
			return total;

			


		}


		public static String checkTrans(String lineComp){
			lineComp = lineComp.trim();
			String binComp = "";
			String oneA = "";
		
			if(lineComp.indexOf('A') != -1){
				oneA = "0";
			}
			else if(lineComp.indexOf('M') != -1){
				oneA = "1";
			}
			else{
				oneA = "0";
			}
			

			switch(lineComp.trim()){
			case "0": binComp = "101010"; 
			break;
			case "1": binComp = "111111";
			break;
			case "-1": binComp = "111010";
			break;
			case "D": binComp = "001100";
			break;
			case ("A"):
			case ("M"): binComp = "110000";
			break;
			case "!D": binComp = "001101";
			break;
			case ("!A"):
			case ("!M"): binComp = "110001";
			break;
			case "-D": binComp = "001111";
			break;
			case ("-A"):
			case ("-M"): binComp = "110011";
			break;
			case "D+1": binComp = "011111";
			break;
			case ("A+1"):
			case ("M+1"): binComp = "110111";
			break;
			case "D-1": binComp = "001110";
			break;
			case ("A-1"):
			case ("M-1"): binComp = "110010";
			break;
			case ("D+A"):
			case ("D+M"): binComp = "000010";
			break;
			case ("D-A"):
			case ("D-M"): binComp = "010011";
			break;
			case ("A-D"):
			case ("M-D"): binComp = "000111";
			break;
			case ("D&A"):
			case ("D&M"): binComp = "000000";
			break;
			case ("D|A"):
			case ("D|M"): binComp = "010101";
			break;
			default: System.out.println("ERROR");
			}

			String Comp = oneA + binComp;
			return Comp;
		}

		public static String destDict(String destLine){
			String binDest = "";		


			if (destLine.indexOf("A") != -1){
				binDest = binDest.concat("1");
			}
			else{
				binDest = binDest.concat("0");
			}
			if (destLine.indexOf("D") != -1){
				binDest = binDest.concat("1");
			}
			else{
				binDest = binDest.concat("0");
			}
			if (destLine.indexOf("M") != -1){
				binDest = binDest.concat("1");
			}
			else{
				binDest = binDest.concat("0");
			}

			return binDest;
		}
		
		public static String replaceWith(String newLine){
			newLine = newLine.trim();
			int indexEqual = newLine.indexOf('=');
			String subStr = newLine.substring(0, indexEqual);
			String subcomp = newLine.substring(indexEqual+1);
			String binDest = ""; 
			String Comp = "";
			String remainderLine = ""; 
			String Jump = "000";
			

			Comp = checkTrans(subcomp);
			String messageComp = "COMP IS: " + Comp;


			binDest = destDict(subStr);

			String messageDest = "DEST IS: " + binDest;


			remainderLine = Comp + binDest + Jump;
			String messageLine = "REMAINING LINE IS: " + remainderLine;
			return remainderLine;
		
		}



	}


