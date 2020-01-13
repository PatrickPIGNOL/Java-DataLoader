package csv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

/***
 * CSVFile : CSVFile manipulator that use OpenCSV for data reading and writing
 * 
 * @author Patrick PIGNOL
 *
 */

public class CSVFile 
{
	private CSVReader aCSVReader;
	private int aHeaderLine;
	private int aLineCount;
	private String[] aHeader;
	private String aCSVFilePath;
	
	/***
	 * CSVFile Constructor 
	 * @param pCSVFilePath : Path of the csv file to handle
	 * @param pHeaderLine : Number of the header line. < 1 => no header line.
	 */
	public CSVFile(String pCSVFilePath, int pHeaderLine)
	{
		this.aCSVFilePath = pCSVFilePath;
		this.aHeaderLine = pHeaderLine - 1;
	}
	
	/***
	 * mReadFile() Return the list of lines of the CSVFile Mapped with header
	 * @return A list of lines Mapped with header
	 * @throws IOException
	 * @throws CsvException
	 */
	public List<Map<String, String>> mReadFile() throws IOException, CsvException
	{
		CSVReader vCSVReader = new CSVReader(new FileReader(this.aCSVFilePath));
	    List<String[]> vValues = vCSVReader.readAll();
	    String[] vHeader = null;
	    if(this.aHeaderLine > -1)
		{
	    	vHeader = vValues.get(this.aHeaderLine);
		}
	    List<Map<String, String>> vResult = new ArrayList<Map<String, String>>();
	    int vSize = vValues.size();
		int vCounter = 0;
	    for(int vIndex = 0; vIndex < vSize; vIndex++)
	    {
	    	this.mShowProgression(vCounter, vSize, 100);
			vCounter++;
	    	if(vIndex == this.aHeaderLine)
	    	{
	    		continue;
	    	}
	    	String[] vLine = vValues.get(vIndex);
    		Map<String, String> vRow = new HashMap<String, String>();
	    	for(int vColumn = 0; vColumn < vLine.length; vColumn++)
	    	{
	    		String vColumnName = "";
	    		if(vColumn > vHeader.length - 1)
	    		{
	    			vColumnName = "Column" + vIndex;
	    		}
	    		else
	    		{
	    			vColumnName = vHeader[vColumn];
	    		}
	    		vRow.put(vColumnName, vLine[vColumn]);
	    	}
    		vResult.add(vRow);
	    }
	    vCSVReader.close();
		return vResult;
	}

	/**
	 * mOpen() Open a file
	 * @return A list of lines Mapped with header
	 * @throws IOException
	 * @throws CsvException
	 */
	public void mOpen() throws CsvValidationException, IOException
	{
		this.aCSVReader = new CSVReader(new FileReader(this.aCSVFilePath));
		this.aHeader = null;
		if(this.aHeaderLine > -1)
		{
			this.aLineCount = 0;
			while(this.aLineCount <= this.aHeaderLine)
			{
				this.aHeader = this.aCSVReader.readNext();				
				this.aLineCount++;
			}
		}
	}
	/**
	 * mReadLine read next line
	 * @return Map<String, String> if there is a line to read else null
	 * @throws IOException
	 * @throws CsvException
	 */
	public Map<String, String> mReadLine() throws IOException, CsvException
	{
		Map<String, String> vRow = null;
		String[] vValues = this.aCSVReader.readNext();			
		this.aLineCount++;
		if(vValues != null)
		{
		    vRow = new HashMap<String, String>();
		    for(int vIndex = 0; vIndex < vValues.length; vIndex++)
		    {
		    	String vHeader = null;
				if(this.aHeader == null)
				{
					vHeader = "Collumn" + vIndex;
				}
				else
				{
					if(vIndex > this.aHeader.length)
					{
						vHeader = "Collumn" + vIndex;
					}
					else
					{
						vHeader = this.aHeader[vIndex];
					}
				}
				vRow.put(vHeader, vValues[vIndex]);
		    }
		}
		return vRow;
	}
	/**
	 * mClose close any opened file as needed
	 */
	public void mClose()
	{
		if(this.aCSVFilePath != null)
		{
		    try 
		    {
				this.aCSVReader.close();
			}
		    catch (IOException e) 
		    {
		    	e.printStackTrace(System.err);
			}
		    this.aCSVFilePath = null;
		}
	}

	/**
	 * mShowProgression Show progression
	 *
	 * @param pCounter	Counter current value of monitored value
	 * @param pSize 	Maximum count of monitored value
	 * @param pModulo	Modulo to refresh
	 */
	private void mShowProgression(int pCounter, int pSize, int pModulo)
	{
		if((pCounter % pModulo == 0) || (pCounter >= pSize - 1))
		{
			System.out.println(pCounter + " / " + (pSize - 1));
		}
		pCounter++;
	}
	
	/**
	 * mCurrentLine return index of current readed line 
	 * @return index of current readed line 
	 */
	public int mCurrentLine()
	{
		return this.aLineCount;
	}
}
