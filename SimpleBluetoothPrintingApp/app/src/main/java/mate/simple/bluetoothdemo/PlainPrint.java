package mate.simple.bluetoothdemo;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class PlainPrint {
	int pageWidth = 53,itemColumnRank=0,serialColumnRank=0,qtyCoulmnRank=0,rateCoulmnRank=0,maxColumnsPerRow=2;//currently next column of item is taken as qty,rate
	int maxChars = 50,leftHalfMaxChars=25,rightHalfMaxChars=25,columnsCount=0,equalCharacters=10,tttequalCharactersNoItem=10,qtyColumnCharacters=10,itemColumnCharacters=15,serialColumnCharacters=15,multiRowEntriesCount = 2;;
	String content = "",fullContent = "";//content gets changing at every stage for bluetooth printing, fullContent is full text from start to end
	private static SharedPreferences pref = null;
	boolean itemColumnsSet = true, multiRowEntries = false, showSerialNo = false, oddColumnsEntries = false,doubleColumnPresent=false,lastColRightAligned=false;
	Context context;
	int minCharsPerFieldEntry = 8,doubleColRank=0;;
	ArrayList<String> columnEntries = new ArrayList<String>();
	char space = ' '; //remove from right side to improve fast performance it is taken for testing purpose
	//some bugs in center line
	public PlainPrint(Context con,int paperWidth,int no){
		pref = PreferenceManager.getDefaultSharedPreferences(con);
		context = con;
		content = "";
		fullContent = "";
		minCharsPerFieldEntry = no;
		multiRowEntries = false;
		maxChars = (int) (paperWidth*0.67);
		maxChars--;
		if(maxChars>0){
			if(maxChars%2==0){
				leftHalfMaxChars = (int) (maxChars/2);
				rightHalfMaxChars = (int) (maxChars/2);
			}
			else{
				leftHalfMaxChars = (int) (maxChars-1)/2;
				rightHalfMaxChars = (int) maxChars - leftHalfMaxChars;
			}
		}
		Log.v("plaintext maxChars ",maxChars+"");
	}
/*	public void setMinChars(int no){
		minCharsPerFieldEntry = no;
	}*/
	public void setLastColRightAlign(){
		lastColRightAligned = true;
	}
	public void addNewLine(){
		content = content + "\n";
		fullContent = fullContent + "\n";
	}
	private void addWhiteSpaces(int no){
		for(int i=0;i<no;i++){
			content = content + space;
			fullContent = fullContent + space;
		}
	}
	public void addText(String str){
		content = content + str;
		fullContent = fullContent + str;
	}
	public void addDashesFullLine(){
		addNewLine();
		for(int i=0;i<maxChars;i++){
			content = content + "-";
			fullContent = fullContent + "-";
		}
	}
	public String getDashesFullLine(){
		String str = "";
		for(int i=0;i<maxChars;i++)
			str = str + "-";
		return str;
	}
	public String getStarsFullLine(){
		String str = "";
		for(int i=0;i<maxChars;i++)
			str = str + "*";
		return str;
	}
	public void addTextLeftAlign(String str){
		if(str==null)
			str = "";
		int length = str.length();
		if(length>0){
			addNewLine();
			content = content + str;
			fullContent = fullContent + str;
		}
	}
	public void addTextCenterAlign(String str,boolean newLine){
		if(str==null)
			str = "";
		int length = str.length();
		if(length>0){
			if(newLine)
				addNewLine();
			if(str.contains("\n")){
				String[] arr = str.split("\n");
				for(int i=0;i<arr.length;i++){
					if(arr[i].length()==0)
						addNewLine();
					else {
						if(i!=0)
							addNewLine();
						addTextCenterAlign(arr[i],false);
					}
				}
			}
			else{
				if(length==maxChars)
					addText(str);
				else if (length<maxChars){
					int remainingChars = maxChars - length;
					if(remainingChars==1)
						addText(str);
					else {
						int remainingEqualChars = 0;
						if(remainingChars%2==0)
							remainingEqualChars = remainingChars/2;
						else
							remainingEqualChars = (remainingChars/2) - 1;
						addWhiteSpaces(remainingEqualChars);
						addText(str);
					}
				}
				else if (length>maxChars){
					String str1 = str.substring(0,maxChars);
					String str2 = str.substring(maxChars);
					addText(str1);
					addTextCenterAlign(str2,true);
				}
			}
		}
	}
	public void addTextDoubleWidthCenterAlign(String str,boolean newLine){
		if(str==null)
			str = "";
		int length = str.length()*2;

		if(length>0){
			if(newLine)
				addNewLine();
			if(str.contains("\n")){
				String[] arr = str.split("\n");
				for(int i=0;i<arr.length;i++){
					if(arr[i].length()==0)
						addNewLine();
					else {
						if(i!=0)
							addNewLine();
						addTextDoubleWidthCenterAlign(arr[i],false);
					}
				}
			}
			else{
				if(length==maxChars)
					addText(str);
				else if (length<maxChars){
					int remainingChars = maxChars - length;
					if(remainingChars==1)
						addText(str);
					else {
						int remainingEqualChars = 0;
						if(remainingChars%2==0)
							remainingEqualChars = remainingChars/2;
						else
							remainingEqualChars = (remainingChars-1)/2;
						addWhiteSpaces((int) (remainingEqualChars/2));
						addText(str);
						Log.v("PlainPrint","length "+length+" remainingEqualChars "+remainingEqualChars);
					}
				}
				else if (length>maxChars){
					int tempChars = maxChars;
					if(maxChars%2==0)
						tempChars = maxChars/2;
					else
						tempChars = (maxChars-1)/2;
					String str1 = str.substring(0,tempChars);
					String str2 = str.substring(tempChars);
					String lastStr1 = str1.substring(str1.length()-1);
					if(lastStr1.equals(" ")){
						addText(str1);
						addTextDoubleWidthCenterAlign(str2,true);
					}
					else{
						int endIndex = str1.lastIndexOf(" ");
						if (endIndex != -1)
						{
							String newstr = str1.substring(0, endIndex);
							String newstr1 = str1.substring(endIndex+1)+str2;
							addTextDoubleWidthCenterAlign(newstr+"\n"+newstr1,false);
						}
						else{
							addText(str1);
							addTextDoubleWidthCenterAlign(str2,true);
						}
					}			
				}
			}
		}
	}
	public void getTextRightAlign(String str,boolean newLine){
		if(str==null)
			str = "";
		int length = str.length();
		if(length>0){
			if(newLine)
				addNewLine();
			if(str.contains("\n")){
				String[] arr = str.split("\n");
				for(int i=0;i<arr.length;i++){
					if(arr[i].length()==0)
						addNewLine();
					else {
						if(i!=0)
							addNewLine();
						getTextRightAlign(arr[i],false);
					}
				}
			}
			else{
				if(length==maxChars)
					addText(str);
				else if (length<maxChars){
					int remainingChars = maxChars - length;
					if(remainingChars==1){
						addWhiteSpaces(1);
						addText(str);
					}
					else {
						addWhiteSpaces(remainingChars);
						addText(str);
					}
				}
				else if (length>maxChars){
					String str1 = str.substring(0,maxChars);
					String str2 = str.substring(maxChars);
					addText(str1);
					getTextRightAlign(str2,true);
				}
			}
		}
	}
	public void addLeftRightTextJustified(String leftText,String rightText,boolean newLine){
		//not done for '\n'. Do when necessary
		//temperory
		leftText = leftText.replaceAll("\n", " ");
		rightText = rightText.replaceAll("\n", " ");
		int leftTextLength = leftText.length(),rightTextLength = rightText.length();
		if(leftTextLength>0||rightTextLength>0){
			if(newLine)
				addNewLine();
			if(leftText.contains("\n")||rightText.contains("\n")){
				//do when needed
			}
			else{
				if((leftTextLength+rightTextLength)<=maxChars){
					int remainingLength = maxChars - (leftTextLength+rightTextLength);
					addText(leftText);
					addWhiteSpaces(remainingLength);
					addText(rightText);
				}
				else{
					addText(rightText);
				}
			}
		}
	}
	public void addLeftRightText(String leftText,String rightText,boolean newLine){
		//not done for '\n'. Do when necessary
		//temperory
		leftText = leftText.replaceAll("\n", " ");
		rightText = rightText.replaceAll("\n", " ");
		int leftTextLength = leftText.length(),rightTextLength = rightText.length();
		if(leftTextLength>0||rightTextLength>0){
			if(newLine)
				addNewLine();
			if(leftText.contains("\n")||rightText.contains("\n")){
				//do when needed
			}
			else{
				if(leftTextLength<=leftHalfMaxChars&&rightTextLength<=rightHalfMaxChars){
					int remainingleftTextLength = leftHalfMaxChars-leftTextLength;
					addText(leftText);
					addWhiteSpaces(remainingleftTextLength);
					addText(rightText);
				}
				else if(leftTextLength>leftHalfMaxChars&&rightTextLength<=rightHalfMaxChars){
					if(rightTextLength==0){
						addText(leftText);
					}
					else{
						String strleft1 = leftText.substring(0,leftHalfMaxChars);
						String strleft2 = leftText.substring(leftHalfMaxChars);
						addText(strleft1+rightText);				
						if(strleft2.trim().length()!=0){
							addLeftRightText(strleft2, "",true);
						}
					}
				}
				else if(leftTextLength<=leftHalfMaxChars&&rightTextLength>rightHalfMaxChars){
					int remainingleftTextLength = leftHalfMaxChars-leftTextLength;
					String strrightt1 = rightText.substring(0,rightHalfMaxChars);
					String strrightt2 = rightText.substring(rightHalfMaxChars);
					addText(leftText);
					addWhiteSpaces(remainingleftTextLength);
					addText(strrightt1);
					if(strrightt2.trim().length()!=0){
						addLeftRightText("",strrightt2,true);
					}
				}
				else if(leftTextLength>leftHalfMaxChars&&rightTextLength>rightHalfMaxChars){
					String strleft1 = leftText.substring(0,leftHalfMaxChars);
					String strleft2 = leftText.substring(leftHalfMaxChars);
					String strrightt1 = rightText.substring(0,rightHalfMaxChars);
					String strrightt2 = rightText.substring(rightHalfMaxChars);
					addText(strleft1+strrightt1);
					if(strleft2.trim().length()!=0||strrightt2.trim().length()!=0){
						addLeftRightText(strleft2,strrightt2,true);
					}
				}
			}
		}
	}
	public void prepareTabularForm(int colsCount,int itColRank,int rtColRank, boolean itCol){
		//itColRank & seColRank start with 0
		columnsCount = colsCount;
		itemColumnRank = itColRank;
		qtyCoulmnRank = 1000;
		rateCoulmnRank = rtColRank;
		maxColumnsPerRow = 2;
		//equalCharactersNoItem = (int) maxChars/columnsCount;
		if(!itCol)
			equalCharacters = (int) maxChars/columnsCount;
		else{
			equalCharacters = (int) maxChars/(columnsCount+1);
			doubleColumnPresent = true;
			doubleColRank = itemColumnRank;
		}	
/*		if(!itCol){
			int tp = (equalCharacters+1)*colsCount;
			if(tp<=maxChars)
				equalCharacters++;
			Log.v("pl",equalCharacters+" eq" );
		}*/
		
		itemColumnCharacters = maxChars - (equalCharacters*(columnsCount-1));
		if(equalCharacters<minCharsPerFieldEntry)
			multiRowEntries = true;
		Log.v("PlainPrint","multiRowEntries "+multiRowEntries);
		if(multiRowEntries){
			//mutiple rows
			oddColumnsEntries = false;

			if(columnsCount%2!=0)
				oddColumnsEntries = true;

			int tempcolumnsCount = columnsCount;//default

			if(oddColumnsEntries)
				tempcolumnsCount = (int) (columnsCount+1)/2;
			else
				tempcolumnsCount = (int) (columnsCount+2)/2;

			int tempequalCharacters;
			tempequalCharacters = (int) (maxChars/tempcolumnsCount);
			if(tempequalCharacters<minCharsPerFieldEntry){
				multiRowEntriesCount = 3;
				int roundedColsCount = columnsCount;
				if(columnsCount%3==0)
					roundedColsCount = columnsCount + 3;
				else if(columnsCount%3==1)
					roundedColsCount = columnsCount + 2;
				if(columnsCount%3==2)
					roundedColsCount = columnsCount + 1;
				maxColumnsPerRow = roundedColsCount/3;
				equalCharacters = (int) (maxChars/maxColumnsPerRow);
				itemColumnCharacters = maxChars - (equalCharacters*(maxColumnsPerRow-2));
				
			}
			else{
				multiRowEntriesCount = 2;
				equalCharacters = tempequalCharacters;
				//equalCharactersNoItem = tempequalCharactersNoItem;
				maxColumnsPerRow = tempcolumnsCount;
				itemColumnCharacters = maxChars - (equalCharacters*(tempcolumnsCount-2));
				Log.v("PlainPrint","equalCharacters "+equalCharacters+" maxColumnsPerRow "+maxColumnsPerRow+" itemColumnCharacters "+itemColumnCharacters);
				
			}
		}
		else{
			maxColumnsPerRow = columnsCount;
			
		}
	}
	private String getStringWhiteSpacesSingleLine(String str,int charsCount, boolean rightAlign, boolean addSpaceRight4LeftAlign){
		Log.v("plaintext setStringWhiteSpacesSingleLine", str);
		String fstr = "";
		int len = str.length();
		if(len==charsCount)
			fstr = str;
		else if(len<charsCount){
			int remLen = charsCount - len;
			if(rightAlign){
				for(int i=0;i<remLen;i++){
					fstr = fstr + space;
				}
				fstr = fstr + str;
			}
			else{
				fstr = str;
				if(addSpaceRight4LeftAlign){
					for(int i=0;i<remLen;i++){
						fstr = fstr + space;
					}
				}
			}
			Log.v("plaintext setStringWhiteSpacesSingleLine len", len+" remLen "+remLen);
		}
		else{
			fstr = str.substring(0,charsCount);
		}
		return fstr;
	}

	private void addItemTitlesSingleColumn(ArrayList<String> titlesList){
		String fstr = "";
		itemColumnsSet= true;
		for(int i=0;i<titlesList.size();i++){
			Log.v("plainText columns",titlesList.get(i));
			int myCharsCount = equalCharacters;
			boolean rightAlign = false;
			if(i==serialColumnRank)
				myCharsCount = serialColumnCharacters;
			else if(i==itemColumnRank)
				myCharsCount = itemColumnCharacters;
			else if(i==qtyCoulmnRank)
				myCharsCount = qtyColumnCharacters;
			else if(i==rateCoulmnRank)
				rightAlign = true;
			Log.v("pl itcon",doubleColumnPresent+" i:"+i+" doubleColRank:"+doubleColRank+" itemColumnCharacters:"+itemColumnCharacters);
			if(doubleColumnPresent&&i==itemColumnRank){
				myCharsCount = itemColumnCharacters;
			}				
			else
				myCharsCount = equalCharacters;
			Log.v("pl itcon1","myCharsCount: "+myCharsCount);
			boolean addSpaceRight4LeftAlign = true;
			if(!rightAlign&&i==(titlesList.size()-1))
				addSpaceRight4LeftAlign = false;
			String str = getStringWhiteSpacesSingleLine(titlesList.get(i), myCharsCount, rightAlign, addSpaceRight4LeftAlign);
			Log.v("pl itemTitles",str.length()+" len");
			fstr = fstr + str;
		}
		if(!fstr.equals("")){
			addNewLine();
			content = content + fstr;
			fullContent = fullContent + fstr;
		}
	}
	public void addItemTiles(ArrayList<String> titlesList){
		Log.v("PlainPrint addItemTiles"," multiRowEntries "+multiRowEntries+" multiRowEntriesCount "+multiRowEntriesCount);
		if(columnsCount==0)
			return;
		if(multiRowEntries){
			if(multiRowEntriesCount==2){
				Log.v("PlainPrint itemTitles","equalCharacters "+equalCharacters+" maxColumnsPerRow "+maxColumnsPerRow+" itemColumnCharacters "+itemColumnCharacters);
				int firstColCountIndex = maxColumnsPerRow - 1;
				ArrayList<String> myList = new ArrayList<String>();
				ArrayList<String> myList1 = new ArrayList<String>();
				for(int i=0;i<firstColCountIndex;i++)
					myList.add(titlesList.get(i));
				for(int j=firstColCountIndex;j<columnsCount;j++)
					myList1.add(titlesList.get(j));

				addItemTitlesSingleColumn(myList);//item should be in first listing
				Log.v("plainText myList size ",myList.size()+"");
				int tempequalcharacters = equalCharacters, tempitemColumnRank = itemColumnRank, tempserialColumnRank = serialColumnRank, tempQtyColumnRank = qtyCoulmnRank, tempRateColumnRank = rateCoulmnRank, myequalCharacters = equalCharacters;
				itemColumnRank = 100;
				serialColumnRank = 150;
				qtyCoulmnRank = 140;
				rateCoulmnRank = 140;

				//if(!oddColumnsEntries){
				for(int m=0;m<7;m++){
					int eq1Chars = myList1.size()*(equalCharacters+1);
					if(eq1Chars<=maxChars)
						equalCharacters = equalCharacters + 1;
				}

				Log.v("plainText equalCharacters ",equalCharacters+"");
				addItemTitlesSingleColumn(myList1);
				itemColumnRank = tempitemColumnRank;
				serialColumnRank = tempserialColumnRank;
				qtyCoulmnRank = tempQtyColumnRank;
				rateCoulmnRank = tempRateColumnRank;
				equalCharacters = tempequalcharacters;
			}
			else if(multiRowEntriesCount==3){
				Log.v("PlainPrint itemTitles","equalCharacters "+equalCharacters+" maxColumnsPerRow "+maxColumnsPerRow+" itemColumnCharacters "+itemColumnCharacters);
				int firstColCountIndex = maxColumnsPerRow - 1;
				int secondColCountIndex = firstColCountIndex + maxColumnsPerRow;
				ArrayList<String> myList = new ArrayList<String>();
				ArrayList<String> myList1 = new ArrayList<String>();
				ArrayList<String> myList2 = new ArrayList<String>();
				for(int i=0;i<firstColCountIndex;i++)
					myList.add(titlesList.get(i));
				for(int j=firstColCountIndex;j<secondColCountIndex;j++)
					myList1.add(titlesList.get(j));
				for(int k=secondColCountIndex;k<columnsCount;k++)
					myList2.add(titlesList.get(k));
				addItemTitlesSingleColumn(myList);//item should be in first listing
				Log.v("plainText myList size ",myList.size()+"");
				int tempequalcharacters = equalCharacters,tempitemColumnRank = itemColumnRank, tempserialColumnRank = serialColumnRank, tempQtyColumnRank = qtyCoulmnRank, tempRateColumnRank = rateCoulmnRank,myequalCharacters = equalCharacters;
				itemColumnRank = 100;
				serialColumnRank = 150;
				qtyCoulmnRank = 150;
				rateCoulmnRank = 150;
				if(myList1.size()!=maxColumnsPerRow){
					int eq1Chars = myList1.size()*(equalCharacters+1);
					if(eq1Chars<=maxChars)
						equalCharacters = equalCharacters + 1;
					int eq2Chars = myList1.size()*(equalCharacters+2);
					if(eq2Chars<=maxChars)
						equalCharacters = equalCharacters + 2;
					int eq3Chars = myList1.size()*(equalCharacters+3);
					if(eq3Chars<=maxChars)
						equalCharacters = equalCharacters + 3;
				}
				addItemTitlesSingleColumn(myList1);
				equalCharacters = tempequalcharacters;
				if(myList2.size()!=maxColumnsPerRow){
					int eq1Chars = myList2.size()*(equalCharacters+1);
					if(eq1Chars<=maxChars)
						equalCharacters = equalCharacters + 1;
					int eq2Chars = myList2.size()*(equalCharacters+2);
					if(eq2Chars<=maxChars)
						equalCharacters = equalCharacters + 2;
					int eq3Chars = myList2.size()*(equalCharacters+3);
					if(eq3Chars<=maxChars)
						equalCharacters = equalCharacters + 3;
				}
				addItemTitlesSingleColumn(myList2);
				itemColumnRank = tempitemColumnRank;
				serialColumnRank = tempserialColumnRank;
				qtyCoulmnRank = tempQtyColumnRank;
				rateCoulmnRank = tempRateColumnRank;
				equalCharacters = tempequalcharacters;
			}
		}
		else{
			addItemTitlesSingleColumn(titlesList);
		}
	}
	private void addItemContentSingleColumn(ArrayList<String> itemsList){
		Log.v("plainText itcn columnsCount",columnsCount+" itemsList size "+itemsList.size());
		ArrayList<String> nextItemsList = new ArrayList<String>();
		String itstr = "";
		for(int k=0;k<itemsList.size();k++)
			itstr = itstr + itemsList.get(k);
		Log.v("pt occ",itstr.trim().length()+"");
		int rk = 0;
		nextItemsList.clear();
		if(itemColumnsSet&occurenceCount(itstr, space)!=itstr.length()){
			String fstr = "";
			boolean contentAgain = false;
			Log.v("plaintext addcontent"," : "+itemColumnRank+" columnsCount "+columnsCount + " itemColumnCharacters "+itemColumnCharacters+" serialcolchars "+serialColumnCharacters);
			for(int i=0;i<itemsList.size();i++){
				String myItem = itemsList.get(i);
				if(i>1&&i!=itemColumnRank&&equalCharacters<6)
					myItem = myItem.replaceAll("\\.00", "");
				Log.v("pt i",i+" equalCharacters "+equalCharacters);
				Log.v("pt colcm"," wowad item "+myItem);
				int myCharsCount = equalCharacters;
				boolean rightAlign = false;
				if(i==itemColumnRank)
					myCharsCount = itemColumnCharacters;
				else if(i==serialColumnRank)
					myCharsCount = serialColumnCharacters;
				else if(i==qtyCoulmnRank)
					myCharsCount = qtyColumnCharacters;
				else if(i==rateCoulmnRank)
					rightAlign = true;
				if(doubleColumnPresent&&i==itemColumnRank){
					myCharsCount = itemColumnCharacters;
				}				
				else	
					myCharsCount = equalCharacters;
				if(myItem.length()<=myCharsCount){
					Log.v("pt mycol","item column1");
					String str = getStringWhiteSpacesSingleLine(myItem, myCharsCount, rightAlign, true);//tempppppppp
					fstr = fstr + str;
					String nxtr = "";
					for(int j=0;j<myCharsCount;j++)
						nxtr = nxtr + space;
					Log.v("pt rk",(rk++)+" ad");
					nextItemsList.add(nxtr);
				}
				else{
					Log.v("pt mycol","item column2");
					contentAgain = true;
					String str1 = myItem.substring(0,myCharsCount);
					String str2 = myItem.substring(myCharsCount);

					String lastStr1 = str1.substring(str1.length()-1);
					//Log.v("pt item column2 lastChar",str+" str:lastchar "+lastChar+"");
					if(lastStr1.equals(" ")){
						fstr = fstr + str1;
						nextItemsList.add(str2);
					}
					else{
						int endIndex = str1.lastIndexOf(" ");
						if (endIndex != -1)
						{
							String newstr = str1.substring(0, endIndex);
							Log.v("newstr",newstr+" len "+newstr.length());
							fstr = fstr + newstr;
							for(int j=0;j<(myCharsCount-newstr.length());j++)
								fstr = fstr + space;
							String newstr1 = str1.substring(endIndex+1)+str2;
							Log.v("pt rk",(rk++)+" ad2");
							nextItemsList.add(newstr1);
						}
						else{
							fstr = fstr + str1;
							nextItemsList.add(str2);
						}
					}
				}
			}
			if(!fstr.equals("")){
				addNewLine();
				Log.v("plain text", fstr +" new line");
				addText(fstr);
			}
			Log.v("plainText nextItemsList size",nextItemsList+" "+rk);
			if(contentAgain)
				addItemContentSingleColumn(nextItemsList);
		}
	}
	public void addItemContent(ArrayList<String> itemsList){
		if(columnsCount==0)
			return;
		if(multiRowEntries){
			if(multiRowEntriesCount==2){
				int firstColCountIndex = maxColumnsPerRow - 1;
				ArrayList<String> myList = new ArrayList<String>();
				ArrayList<String> myList1 = new ArrayList<String>();
				for(int i=0;i<firstColCountIndex;i++)
					myList.add(itemsList.get(i));
				for(int j=firstColCountIndex;j<columnsCount;j++)
					myList1.add(itemsList.get(j));

				addItemTitlesSingleColumn(myList);//item should be in first listing
				Log.v("plainText myList size ",myList.size()+"");
				int tempequalcharacters = equalCharacters, tempitemColumnRank = itemColumnRank, tempserialColumnRank = serialColumnRank, tempQtyColumnRank = qtyCoulmnRank, tempRateColumnRank = rateCoulmnRank, myequalCharacters = equalCharacters;
				itemColumnRank = 100;
				serialColumnRank = 150;
				qtyCoulmnRank = 140;
				rateCoulmnRank = 140;

				for(int m=0;m<7;m++){
					int eq1Chars = myList1.size()*(equalCharacters+1);
					if(eq1Chars<=maxChars)
						equalCharacters = equalCharacters + 1;
				}
				addItemTitlesSingleColumn(myList1);
				itemColumnRank = tempitemColumnRank;
				serialColumnRank = tempserialColumnRank;
				qtyCoulmnRank = tempQtyColumnRank;
				rateCoulmnRank = tempRateColumnRank;
				equalCharacters = tempequalcharacters;
			}
			else if(multiRowEntriesCount==3){
				int firstColCountIndex = maxColumnsPerRow - 1;
				int secondColCountIndex = firstColCountIndex + maxColumnsPerRow;
				ArrayList<String> myList = new ArrayList<String>();
				ArrayList<String> myList1 = new ArrayList<String>();
				ArrayList<String> myList2 = new ArrayList<String>();
				for(int i=0;i<firstColCountIndex;i++)
					myList.add(itemsList.get(i));
				for(int j=firstColCountIndex;j<secondColCountIndex;j++)
					myList1.add(itemsList.get(j));
				for(int k=secondColCountIndex;k<columnsCount;k++)
					myList2.add(itemsList.get(k));
				addItemTitlesSingleColumn(myList);//item should be in first listing
				Log.v("plainText myList size ",myList.size()+"");
				int tempequalcharacters = equalCharacters,tempitemColumnRank = itemColumnRank, tempserialColumnRank = serialColumnRank, tempQtyColumnRank = qtyCoulmnRank, tempRateColumnRank = rateCoulmnRank,myequalCharacters = equalCharacters;
				itemColumnRank = 100;
				serialColumnRank = 150;
				qtyCoulmnRank = 150;
				rateCoulmnRank = 150;
				if(myList1.size()!=maxColumnsPerRow){
					int eq1Chars = myList1.size()*(equalCharacters+1);
					if(eq1Chars<=maxChars)
						equalCharacters = equalCharacters + 1;
					int eq2Chars = myList1.size()*(equalCharacters+2);
					if(eq2Chars<=maxChars)
						equalCharacters = equalCharacters + 2;
					int eq3Chars = myList1.size()*(equalCharacters+3);
					if(eq3Chars<=maxChars)
						equalCharacters = equalCharacters + 3;
				}
				addItemTitlesSingleColumn(myList1);
				equalCharacters = tempequalcharacters;
				if(myList2.size()!=maxColumnsPerRow){
					int eq1Chars = myList2.size()*(equalCharacters+1);
					if(eq1Chars<=maxChars)
						equalCharacters = equalCharacters + 1;
					int eq2Chars = myList2.size()*(equalCharacters+2);
					if(eq2Chars<=maxChars)
						equalCharacters = equalCharacters + 2;
					int eq3Chars = myList2.size()*(equalCharacters+3);
					if(eq3Chars<=maxChars)
						equalCharacters = equalCharacters + 3;
				}
				addItemTitlesSingleColumn(myList2);
				itemColumnRank = tempitemColumnRank;
				serialColumnRank = tempserialColumnRank;
				qtyCoulmnRank = tempQtyColumnRank;
				rateCoulmnRank = tempRateColumnRank;
				equalCharacters = tempequalcharacters;
			}
		}
		else{
			addItemContentSingleColumn(itemsList);
		}
	}
	public int occurenceCount (String s,char c){
		int counter = 0;
		for( int i=0; i<s.length(); i++ ) {
			if( s.charAt(i) == c ) {
				counter++;
			}
		}
		return counter;
	}
	public void startAddingContent4printFields(){
		content = "";
	}
	public String getContent4PrintFields(){
		return content.replaceFirst("\n", "");
	}
	public String getFinalFullText(){
		return fullContent;
	}
	/*public void startTempContent(String str){
		tempContent = str;
	}
	public String getTempContent(){
		return tempContent;
	}*/
}
