package dataloader;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import CSV.CSVFile;
import MySQL.MySQL;


public class DataLoader 
{
	private MySQL aMySQL;
	private String aFilePath;
	private String aServer;
	private String aPort;
	private String aDatabase;
	private String aUser;
	private String aPassword;
	public DataLoader(String pServer, String pPort, String pDatabase, String pUser, String pPassword, String pFilePath)
	{
		this.aServer = pServer;
		this.aPort = pPort;
		this.aDatabase = pDatabase;
		this.aUser = pUser;
		this.aPassword = pPassword;
		this.aFilePath = pFilePath;
		this.aMySQL = null;
	}
	
	public void mLoadData()
	{
		this.aMySQL = new MySQL(this.aServer, this.aPort, this.aDatabase, this.aUser, this.aPassword);
		this.aMySQL.mConnect();
		this.mEmptyTables();
//		this.mF1_Status();
//		this.mF1_Country();
//		this.mF1_Locations();
//		this.mF1_Drivers();
//		this.mF1_Constructors();
//		this.mF1_Circuits();
//		this.mF1_Races();
//		this.mF1_Results();
//		this.mF1_LapTimes();
		
		this.mVerify_Status();
		
		this.aMySQL.mClose();
	}
	
	public void mVerify_Status()
	{
		/*int vTotalCount = 0;
		int vTotalUnique = 0;
		int vUniqueCount = 0;*/
		System.out.println("Filling table F1_Status... ");
		List<Map<String, String>> vStatusData = this.mReadFile("status.csv");
		//vTotalCount = vStatusData.size();
					
		if(vStatusData != null)
		{
			String vSQL = "SELECT * FROM F1_Status WHERE (IDStatus = ? AND Status = ?);";
			List<String> vStrings = new ArrayList<String>();
			try 
			{
				PreparedStatement vPreparedStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
			
				for(Map<String, String> vRow : vStatusData)
				{
					String vStatus = vRow.get("status");
					String vIDStatus = vRow.get("statusId");
					if((vStatus != null) && (vIDStatus != null))
					{
						if(!(vIDStatus.isEmpty() && vStatus.isEmpty()))
						{
							if(!vStrings.contains(vStatus))
							{
								//vTotalUnique++;						
								vStrings.add(vStatus);
								vPreparedStatement.setLong(1, Long.parseLong(vIDStatus));
								vPreparedStatement.setString(2, vStatus);
								ResultSet vResultSet = vPreparedStatement.executeQuery();
								while(vResultSet.next())
								{
									long vBDDIDStatus = vResultSet.getLong("IDStatus");
									String vBDDStatus = vResultSet.getString("Status");
									if((vBDDIDStatus == Long.parseLong(vIDStatus)) && (vBDDStatus.equals(vStatus)))
									{
										System.out.println("IDStatus : " + vIDStatus + " Status : " + vStatus + " has been integrated.");
									}
									else
									{
										System.err.println("IDStatus : " + vIDStatus + " Status : " + vStatus + " has NOT been integrated.");
									}
								}
							}
							else
							{
								System.out.println("IDStatus : " + vIDStatus + " Status : " + vStatus + " is already verified. This is a double entry.");
							}
						}
					}
				}
				vPreparedStatement.close();
			} 
            catch (SQLException e) 
            {
				e.printStackTrace();
			}
		}
	}
	
	public void mEmptyTables()
	{
		System.out.print("Emptying tables... ");
		try 
		{
			Statement vStatement = this.aMySQL.mConnection().createStatement();
			
			vStatement.addBatch("USE formation;");
			vStatement.addBatch("SET FOREIGN_KEY_CHECKS = 0;");

//			vStatement.addBatch("TRUNCATE TABLE F1_LapsTimes;");
//			vStatement.addBatch("ALTER TABLE F1_LapsTimes AUTO_INCREMENT 1;");
//			
//			vStatement.addBatch("TRUNCATE TABLE F1_Results;");
//			vStatement.addBatch("ALTER TABLE F1_Results AUTO_INCREMENT 1;");
//			
//			vStatement.addBatch("TRUNCATE TABLE F1_Races;");
//			vStatement.addBatch("ALTER TABLE F1_Races AUTO_INCREMENT 1;");
//
//			vStatement.addBatch("TRUNCATE TABLE F1_Constructors;");
//			vStatement.addBatch("ALTER TABLE F1_Constructors AUTO_INCREMENT 1;");			
//			
//			vStatement.addBatch("TRUNCATE TABLE F1_Circuits;");
//			vStatement.addBatch("ALTER TABLE F1_Circuits AUTO_INCREMENT 1;");
//			
//			vStatement.addBatch("TRUNCATE TABLE F1_DriverNationality;");
//			vStatement.addBatch("ALTER TABLE F1_DriverNationality AUTO_INCREMENT 1;");
//			
//			vStatement.addBatch("TRUNCATE TABLE F1_Drivers;");
//			vStatement.addBatch("ALTER TABLE F1_Drivers AUTO_INCREMENT 1;");
//			
//			vStatement.addBatch("TRUNCATE TABLE F1_Locations;");
//			vStatement.addBatch("ALTER TABLE F1_Locations AUTO_INCREMENT 1;");
//			
//			vStatement.addBatch("TRUNCATE TABLE F1_Country;");
//			vStatement.addBatch("ALTER TABLE F1_Country AUTO_INCREMENT 1;");
//			
//			vStatement.addBatch("TRUNCATE TABLE F1_Status;");
//			vStatement.addBatch("ALTER TABLE F1_Status AUTO_INCREMENT 1;");
			
			vStatement.addBatch("SET FOREIGN_KEY_CHECKS = 1;");
			
			vStatement.executeBatch();
			vStatement.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace(System.err);
			System.exit(-1);
		}
		System.out.println("Done !");
	}

	
	
	public void mF1_LapTimes()
	{
		System.out.println("Filling table F1_LapsTimes... ");
		//System.out.print("\tRetriving data... ");
		CSVFile vCSVFile = new CSVFile(this.aFilePath+ "\\" + "lapTimes.csv", 1);
		try
		{
			vCSVFile.mOpen();
		} 
		catch(CsvValidationException | IOException e1)
		{
			e1.printStackTrace(System.err);
			System.exit(-1);
		}
				
		if(vCSVFile != null)
		{
			Map<String, String> vLapTime = null;
			String vSQL = "INSERT INTO F1_LapsTimes "
					+ "("
						+ "IDDriver, "
						+ "IDRace, "
						+ "Lap, "
						+ "PositionLap, "
						+ "TimeLap, "
						+ "Milliseconds"
					+ ") "
					+ "VALUES "
					+ "("
						+ "?, ?, ?, ?, ?, ?"
					+ ")";
			try 
			{
				PreparedStatement vPreparedStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
				while((vLapTime = vCSVFile.mReadLine()) != null)
				{
					int vCurentLine = vCSVFile.mCurrentLine();
					if(vCurentLine % 10 == 0)
					{
						System.out.println("Line : " + vCSVFile.mCurrentLine());
					}
					if(vLapTime != null)
					{
						String vRaceID 			= vLapTime.get("raceId");
						String vDriverID		= vLapTime.get("driverId");
						String vLap				= vLapTime.get("lap");
						String vPosition		= vLapTime.get("position");
						String vTime			= vLapTime.get("time");
						String vMilliseconds	= vLapTime.get("milliseconds");
						try
						{
							vPreparedStatement.setLong(1, Long.parseLong(vDriverID));
							vPreparedStatement.setLong(2, Long.parseLong(vRaceID));
							vPreparedStatement.setInt(3, Integer.valueOf(vLap));
							vPreparedStatement.setInt(4, Integer.valueOf(vPosition));
							vPreparedStatement.setDouble(5, this.mTimeToDouble(vTime));
							vPreparedStatement.setLong(6, Long.parseLong(vMilliseconds));
							vPreparedStatement.addBatch();					
						} 
						catch (Exception e)
						{
							System.err.println("Error LapTime on line : " + vCSVFile.mCurrentLine() + "\n\tIDDriver : " + vDriverID + "\n\tIDRace : " + vRaceID + "\n\tLap : " + vLap);
							e.printStackTrace(System.err);
						}
					}
				}
				vPreparedStatement.executeBatch();
				vPreparedStatement.close();	
			} 
			catch (Exception e) 
			{
				e.printStackTrace(System.err);
			}
		}
		System.out.println("Done !");
	}
	
	public void mF1_Results()
	{
		System.out.println("Filling table F1_Results... ");
		List<Map<String, String>> vResultData = this.mReadFile("results.csv");
		if(vResultData != null)
		{
			try
			{
				String vSQL = "INSERT INTO F1_Results "
						+ "("
							+ "IDResult, "
							+ "IDRace, "
							+ "IDDriver, "
							+ "IDConstructor, "
							+ "Number, "
							+ "Grid, "
							+ "PositionValue, "
							+ "PositionText, "
							+ "PositionOrder, "
							+ "Points, "
							+ "Laps, "
							+ "TimeResult, "
							+ "Miliseconds, "
							+ "FastestsLap, "
							+ "RankResult, "
							+ "FastestLapTime, "
							+ "FastestLapSpeed, "
							+ "IDStatus "
						+ ") "
						+ "Values "
						+ "("
							+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"
						+ ");";
				PreparedStatement vPreparedStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
				int vSize = vResultData.size();
				int vCounter = 0;
				for(Map<String, String> vResultRow : vResultData)
				{
					this.mShowProgression(vCounter, vSize, 10);
					vCounter++;
					String vResultID        = vResultRow.get("resultId");
					String vRaceID          = vResultRow.get("raceId");
					String vDriverID        = vResultRow.get("driverId");
					String vConstructorID   = vResultRow.get("constructorId");
					String vNumber          = vResultRow.get("number");
					String vGrid            = vResultRow.get("grid");
					String vPosition        = vResultRow.get("position");
					String vPositionText    = vResultRow.get("positionText");
					String vPositionOrder   = vResultRow.get("positionOrder");
					String vPoints          = vResultRow.get("points");
					String vLaps            = vResultRow.get("laps");
					String vTime            = vResultRow.get("time");
					String vMilliseconds    = vResultRow.get("milliseconds");
					String vFastestLap      = vResultRow.get("fastestLap");
					String vRank            = vResultRow.get("rank");
					String vFastestLapTime  = vResultRow.get("fastestLapTime");
					String vFastestLapSpeed = vResultRow.get("fastestLapSpeed");
					String vStatusId        = vResultRow.get("statusId");
					
					if
					(
							(vResultID        != null) &&
							(vRaceID          != null) &&
							(vDriverID        != null) &&
							(vConstructorID   != null) &&
							(vNumber          != null) &&
							(vGrid            != null) &&
							(vPosition        != null) &&
							(vPositionText    != null) &&
							(vPositionOrder   != null) &&
							(vPoints          != null) &&
							(vLaps            != null) &&
							(vTime            != null) &&
							(vMilliseconds    != null) &&
							(vFastestLap      != null) &&
							(vRank            != null) &&
							(vFastestLapTime  != null) &&
							(vFastestLapSpeed != null) &&
							(vStatusId        != null)
					)
					{
						try
						{						
							vPreparedStatement.setLong(1, Long.parseLong(vResultID));
							vPreparedStatement.setLong(2, Long.parseLong(vRaceID));
							vPreparedStatement.setLong(3, Long.parseLong(vDriverID));
							vPreparedStatement.setLong(4, Long.parseLong(vConstructorID));
							if(vNumber.trim().isEmpty())
							{
								vPreparedStatement.setNull(5, Types.INTEGER);
							}
							else
							{
								vPreparedStatement.setInt(5, Integer.parseInt(vNumber));
							}
							vPreparedStatement.setInt(6, Integer.parseInt(vGrid));
							if(vPosition.trim().isEmpty())
							{
								vPreparedStatement.setNull(7, Types.INTEGER);
							}
							else
							{
								vPreparedStatement.setInt(7, Integer.parseInt(vPosition));
							}
							vPreparedStatement.setString(8, vPositionText);
							vPreparedStatement.setInt(9, Integer.parseInt(vPositionOrder));
							vPreparedStatement.setDouble(10, Double.parseDouble(vPoints));
							vPreparedStatement.setInt(11, Integer.parseInt(vLaps));
							if(vTime.trim().isEmpty())
							{
								vPreparedStatement.setNull(12, Types.DOUBLE);
							}
							else
							{
								vPreparedStatement.setDouble(12, this.mTimeToDouble(vTime));
							}
							if(vMilliseconds.trim().isEmpty())
							{
								vPreparedStatement.setNull(13, Types.INTEGER);
							}
							else
							{
								vPreparedStatement.setLong(13, Long.parseLong(vMilliseconds));
							}
							if(vFastestLap.trim().isEmpty())
							{
								vPreparedStatement.setNull(14, Types.INTEGER);
							}
							else
							{
								vPreparedStatement.setInt(14, Integer.valueOf(vFastestLap));
							}
							
							if(vRank.trim().isEmpty())
							{
								vPreparedStatement.setNull(15, Types.INTEGER);
							}
							else
							{
								vPreparedStatement.setInt(15, Integer.parseInt(vRank));
							}
							
							if(vFastestLapTime.trim().isEmpty())
							{
								vPreparedStatement.setNull(16, Types.DOUBLE);
							}
							else
							{
								vPreparedStatement.setDouble(16, this.mTimeToDouble(vFastestLapTime));
							}
							
							if(vFastestLapSpeed.trim().isEmpty())
							{
								vPreparedStatement.setNull(17, Types.DOUBLE);
							}
							else
							{
								
								vPreparedStatement.setDouble(17, this.mTimeToDouble(vFastestLapSpeed));
							}
							
							vPreparedStatement.setLong(18, Long.parseLong(vStatusId));						
							
							vPreparedStatement.addBatch();
						}
						catch (Exception /* SQLException*/ e)
						{
							System.err.println("Error Result :\n\tID : " + vResultID + "\n\tConstructor : " + vConstructorID);
							e.printStackTrace(System.err);
						}
					}
				}
				vPreparedStatement.executeBatch();
				vPreparedStatement.close();
			}
			catch (Exception /* SQLException*/ e)
			{
				e.printStackTrace(System.err);
			}
		}
		System.out.println("Done !");
	}
	
	public void mF1_Constructors()
	{
		System.out.print("Filling table F1_Country... ");
		List<Map<String, String>> vConstructorData = this.mReadFile("constructors.csv");
		List<Country> vCountries = this.mCountries();
		if(vConstructorData != null)
		{
			int vSize = vConstructorData.size();
			int vCounter = 0;	
			String vSQL = "INSERT INTO F1_Constructors "
				+ "("
				+ "IDConstructor, "
				+ "RefConstructor, "
				+ "Name, "
				+ "URL, "
				+ "IDCountry"
			+ ") "
			+ "VALUES "
			+ "("
				+ "?,?,?,?,?"
			+ ")";
			try 
			{
				PreparedStatement vPreparedStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
				for(Map<String, String> vConstructor : vConstructorData)
				{
					this.mShowProgression(vCounter, vSize, 10);
					vCounter++;
					String vConstructorID = vConstructor.get("constructorId");
					String vConstructorRef = vConstructor.get("constructorRef");
					String vConstructorName = vConstructor.get("name");
					String vConstructorNationality = vConstructor.get("nationality");
					String vConstructorURL = vConstructor.get("url");
					if
					(
						(vConstructorID          != null) &&
						(vConstructorRef         != null) &&
						(vConstructorName        != null) &&
						(vConstructorNationality != null) &&
						(vConstructorURL         != null) 
					)
					{
						vConstructorNationality = this.mPurifyNationalityName(vConstructorNationality);
					
						for(Country vCountry : vCountries)
						{
							if(vCountry.mNationality().equals(vConstructorNationality) || (JaroWinkler.similarity(vCountry.mNationality(), vConstructorNationality) > 0.95d))
							{
								try
								{
									vPreparedStatement.setLong(1, Long.parseLong(vConstructorID));
									vPreparedStatement.setString(2, vConstructorRef);
									vPreparedStatement.setString(3, vConstructorName);
									vPreparedStatement.setString(4, vConstructorURL);
									vPreparedStatement.setLong(5, vCountry.mIDCountry());
									vPreparedStatement.addBatch();
									//System.out.println("Inserted Constructor :\n\tID : " + vConstructorID + "\n\tvConstructorNationality : " + vConstructorNationality +"\n\tCountryID : " + vCountry.mIDCountry());
									break;
								}
								catch (SQLException e)
								{
									System.err.println("Error Constructor :\n\tID : " + vConstructorID);
									e.printStackTrace(System.err);
								}
							}
						}
					}
				}
				vPreparedStatement.executeBatch();
				vPreparedStatement.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace(System.err);
			}
		}
		System.out.println("Done !");
	}
	
	public void mF1_Country() 
	{
		System.out.println("Filling table F1_Country... ");
		Long vIDCountry = null;
		
		try
		{
			String vSQL = "SELECT MAX(IDCountry) AS IDCountry FROM F1_Country";
			
			PreparedStatement vIndexPreparedStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
			ResultSet vResultSet = vIndexPreparedStatement.executeQuery();
			while(vResultSet.next())
			{
				vIDCountry = vResultSet.getLong("IDCountry");
			}
			vResultSet.close();
			if(vIDCountry == null)
			{
				vIDCountry = 0L;
			}
			vIndexPreparedStatement.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace(System.err);
			System.exit(-1);
		}
		List<Map<String, String>> vDriversData = this.mReadFile("drivers.csv");
		List<Map<String, String>> vConstructorData = this.mReadFile("constructors.csv");
		List<Map<String, String>> vCircuitsData = this.mReadFile("circuits.csv");
		List<Map<String, String>> vCountriesData = this.mReadFile("Countries.csv");
		
		if((vCountriesData != null) && (vCircuitsData != null) && (vDriversData != null))
		{
			List<String> vCountryStrings = new ArrayList<String>();
			int vSize = vConstructorData.size();
			int vCounter = 0;	
			for(Map<String, String> vCircuitRow : vCircuitsData)
			{
				this.mShowProgression(vCounter, vSize, 10);
				vCounter++;
				String vCircuitCountry = vCircuitRow.get("country");
				if(vCircuitCountry != null)
				{
					vCircuitCountry = this.mPurifyCountryName(vCircuitCountry);
					if(!vCountryStrings.contains(vCircuitCountry))
					{
						for(Map<String, String> vCountryRow : vCountriesData)
						{
							String vCountry = vCountryRow.get("Country");
							vCountry = this.mPurifyCountryName(vCountry);
							String vNationality = vCountryRow.get("Nationality1");
							vNationality = this.mPurifyNationalityName(vNationality);
							if((vCountry != null)&&(vNationality != null))
							{
								if(vCircuitCountry.equals(vCountry))
								{
									vIDCountry++;
									vCountryStrings.add(vCircuitCountry);
									String vSQL = "INSERT INTO F1_Country (IDCountry , Country, NationalityCountry) Values (?, ?, ?);";
									try
									{
										PreparedStatement vPreparedStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
										vPreparedStatement.setLong(1, vIDCountry);
										vPreparedStatement.setString(2, vCountry);
										vPreparedStatement.setString(3, vNationality);
										vPreparedStatement.execute();
										vPreparedStatement.close();
										//System.out.println("Inserted Country :\n\tID : " + vIDCountry + ";\n\tCountry : " + vCountry + ";\n\t" + vNationality + ";");
										break;
									}
									catch (SQLException e)
									{
										System.err.println("Error Country :\n\tID : " + vIDCountry + ";\n\tCountry : " + vCountry + ";\n\t" + vNationality + ";");
										e.printStackTrace(System.err);
									}
								}
							}
						}
					}
				}
			}
			vSize = vDriversData.size();
			vCounter = 0;				
			for(Map<String, String> vDriverRow : vDriversData)
			{
				this.mShowProgression(vCounter, vSize, 10);
				vCounter++;
				String vDriverNationality = vDriverRow.get("nationality");
				if(vDriverNationality != null)
				{
					vDriverNationality = this.mPurifyNationalityName(vDriverNationality);	
					for(Map<String, String> vCountryRow : vCountriesData)
					{
						String vCountry = vCountryRow.get("Country");
						String vNationality = vCountryRow.get("Nationality1");
						if((vCountry != null) && (vNationality != null))
						{
							vCountry = this.mPurifyCountryName(vCountry);
							if(!vCountryStrings.contains(vCountry))
							{
								if(vNationality.equals(vDriverNationality) || (JaroWinkler.similarity(vNationality, vDriverNationality) > 0.9d))
								{
									vIDCountry++;
									vCountryStrings.add(vCountry);
									String vSQL = "INSERT INTO F1_Country (IDCountry , Country, NationalityCountry) Values (?, ?, ?);";
									try
									{
										PreparedStatement vPreparedStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
										vPreparedStatement.setLong(1, vIDCountry);
										vPreparedStatement.setString(2, vCountry);
										vPreparedStatement.setString(3, vNationality);
										vPreparedStatement.execute();
										vPreparedStatement.close();
										//System.out.println("Inserted Country :\n\tID : " + vIDCountry + ";\n\tCountry : " + vCountry + ";\n\t" + vNationality + ";");
										break;
									}
									catch (SQLException e)
									{
										System.err.println("Error Country :\n\tID : " + vIDCountry + ";\n\tCountry : " + vCountry + ";\n\t" + vNationality + ";");
										e.printStackTrace(System.err);
									}
								}
							}
						}
					}
				}
			}
			vSize = vConstructorData.size();
			vCounter = 0;	
			for(Map<String, String> vConstructorRow : vConstructorData)
			{
				this.mShowProgression(vCounter, vSize, 10);
				vCounter++;
				String vConstructorNationality = vConstructorRow.get("nationality");
				if(vConstructorNationality != null)
				{
					vConstructorNationality = this.mPurifyNationalityName(vConstructorNationality);
					for(Map<String, String> vCountryRow : vCountriesData)
					{
						String vCountry = vCountryRow.get("Country");
						String vNationality = vCountryRow.get("Nationality1");
						if((vCountry != null) && (vNationality != null))
						{
							vCountry = this.mPurifyCountryName(vCountry);
							vNationality = this.mPurifyNationalityName(vNationality);
							if(!vCountryStrings.contains(vCountry))
							{
								if(vNationality.equals(vConstructorNationality) || (JaroWinkler.similarity(vNationality, vConstructorNationality) > 0.95d))
								{
									try
									{		
										vIDCountry++;
										vCountryStrings.add(vCountry);
										String vSQL = "INSERT INTO F1_Country (IDCountry , Country, NationalityCountry) Values (?, ?, ?);";
										PreparedStatement vPreparedStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
										vPreparedStatement.setLong(1, vIDCountry);
										vPreparedStatement.setString(2, vCountry);
										vPreparedStatement.setString(3, vNationality);
										vPreparedStatement.execute();
										vPreparedStatement.close();
										//System.out.println("Inserted Country :\n\tID : " + vIDCountry + ";\n\tCountry : " + vCountry + ";\n\t" + vNationality + ";");
										break;
									}
									catch (SQLException e)
									{
										System.err.println("Error Country :\n\tID : " + vIDCountry + ";\n\tCountry : " + vCountry + ";\n\t" + vNationality + ";");
										e.printStackTrace(System.err);
									}
								}
							}
						}
					}
				}
			}
		}
		System.out.println("Done !");
	}
	
	private void mF1_Drivers() 
	{
		System.out.println("Filling table F1_Drivers... ");
		List<Map<String, String>> vDriversData = this.mReadFile("drivers.csv");
		List<Country> vCountries = this.mCountries();
		if(vDriversData != null)
		{
			int vSize = vDriversData.size();
			int vCounter = 0;	
			for(Map<String, String> vDriverRow : vDriversData)
			{
				this.mShowProgression(vCounter, vSize, 10);
				vCounter++;
				String vDriverId = vDriverRow.get("driverId");
				String vDriverRef= vDriverRow.get("driverRef");
				String vNumber = vDriverRow.get("number");
				String vCode = vDriverRow.get("code");
				String vFirstName  = vDriverRow.get("forename");
				String vLastName = vDriverRow.get("surname");
				String vBirthDate = vDriverRow.get("dob");
				String vNationality = vDriverRow.get("nationality");
				String vURL = vDriverRow.get("url");
				
				String vSQL = "INSERT INTO F1_Drivers "
							+ "("
								+ "IDDriver, "
								+ "RefDriver,"
								+ "NumberDriver,"
								+ "CodeDriver,"
								+ "FirstNameDriver,"
								+ "LastNameDriver,"
								+ "BirthDateDriver,"
								+ "URLDriver"									
							+ ")"
							+ "VALUES"
							+ "("
								+ "?,?,?,?,?,?,?,?"
							+ ")";
				try
				{	
					PreparedStatement vPreparedStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
					vPreparedStatement.setLong(1, Long.parseLong(vDriverId));
					vPreparedStatement.setString(2, vDriverRef);
					if(vNumber.trim().isEmpty())
					{
						vPreparedStatement.setNull(3, Types.INTEGER);
					}
					else
					{
						vPreparedStatement.setInt(3, Integer.parseInt(vNumber));
					}
					vPreparedStatement.setString(4, vCode);
					vPreparedStatement.setString(5, vFirstName);
					vPreparedStatement.setString(6, vLastName);
					if(vBirthDate.trim().isEmpty())
					{
						vPreparedStatement.setNull(7, Types.DATE);
					}
					else
					{
						if(vBirthDate.contains("-"))
						{
							vPreparedStatement.setDate(7, Date.valueOf(LocalDate.parse(vBirthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
						}
						else
						{
							vPreparedStatement.setDate(7, Date.valueOf(LocalDate.parse(vBirthDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
						}
					}
					vPreparedStatement.setString(8, vURL);
					vPreparedStatement.execute(); 
					vPreparedStatement.close();
					//System.out.println("Inserted Drivers :\n\tID : " + vDriverId + "\n\tDriverRef : " + vDriverRef + "FirstName : " + vFirstName + "\n\tLastName : " + vLastName);
				}
				catch (SQLException e)
				{
					System.err.println("Error Drivers :\n\tID : " + vDriverId + "\n\tDriverRef : " + vDriverRef + "FirstName : " + vFirstName + "\n\tLastName : " + vLastName);
					e.printStackTrace(System.err);
				}
				
				vSQL = "INSERT INTO F1_DriverNationality (IDDriver, IDCountry) VALUES (?,?)";
				if(vNationality.contains("-"))
				{
					String[] vNationalities = vNationality.split("-");
					for(String vNationalityName : vNationalities)
					{
						for(Country vCountry : vCountries)
						{
							vNationalityName = this.mPurifyNationalityName(vNationalityName);
							if(vCountry.mNationality().contains(vNationalityName))
							{
								try
								{		
									PreparedStatement vNationalitiesStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
									vNationalitiesStatement.setLong(1, Integer.parseInt(vDriverId));
									vNationalitiesStatement.setLong(2, vCountry.mIDCountry());
									vNationalitiesStatement.execute();
									vNationalitiesStatement.close();
									//System.out.println("Inserted Driver's Nationality :\n\tID : " + vDriverId + ";\n\tCountry : " + vCountry.mIDCountry() + ";");
									break;
								}
								catch (SQLException e)
								{
									System.err.println("Error Driver's Nationality :\n\tID : " + vDriverId + ";\n\tCountry : " + vCountry.mIDCountry() + ";");
									e.printStackTrace(System.err);
								}
								break;
							}
						}
					}
				}
				else
				{
					for(Country vCountry : vCountries)
					{	
						if(vCountry.mNationality().contains(vNationality))
						{
							try
							{	
								PreparedStatement vNationalitiesStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
								vNationalitiesStatement.setLong(1, Integer.parseInt(vDriverId));
								vNationalitiesStatement.setLong(2, vCountry.mIDCountry());
								vNationalitiesStatement.execute();
								vNationalitiesStatement.close();
								//System.out.println("Inserted Driver's Nationality :\n\tID : " + vDriverId + ";\n\tCountry : " + vCountry.mIDCountry() + ";");
								break;
							}
							catch (SQLException e)
							{
								System.err.println("Error Driver's Nationality :\n\tID : " + vDriverId + ";\n\tCountry : " + vCountry.mIDCountry() + ";");
								e.printStackTrace(System.err);
							}
						}
					}
				}
			}
		}
		System.out.println("Done !");
	}

	private void mShowProgression(int pCounter, int pSize, int pModulo)
	{
		if((pCounter % pModulo == 0) || (pCounter >= pSize - 1))
		{
			System.out.println(pCounter + " / " + (pSize - 1));
		}
		pCounter++;
	}
	
	public void mF1_Locations() 
	{
		System.out.print("Filling table F1_Locations... ");
		Long vIDLocation = null;
		
		try
		{
			String vSQL = "SELECT MAX(IDLocation) AS IDLocation FROM F1_Locations";
			
			PreparedStatement vIndexPreparedStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
			ResultSet vResultSet = vIndexPreparedStatement.executeQuery();
			while(vResultSet.next())
			{
				vIDLocation = vResultSet.getLong("IDLocation");
			}
			vResultSet.close();
			if(vIDLocation == null)
			{
				vIDLocation = 0L;
			}
			vIndexPreparedStatement.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace(System.err);
			System.exit(-1);
		}
		
		List<Map<String, String>> vCircuitsData = this.mReadFile("circuits.csv");
		List<Country> vCountries = this.mCountries();
		List<String> vLocationList = new ArrayList<String>();
		
		if((vCircuitsData != null) && (vCountries != null))
		{
			int vSize = vCircuitsData.size();
			int vCounter = 0;
			for(Map<String, String> vCircuitRow : vCircuitsData)
			{
				this.mShowProgression(vCounter, vSize, 10);
				vCounter++;
				String vCircuitLocation = vCircuitRow.get("location");
				String vCircuitCountry = vCircuitRow.get("country");
				if((vCircuitLocation != null) && (vCircuitCountry != null))
				{
					vCircuitCountry = this.mPurifyCountryName(vCircuitCountry);
					vCircuitLocation = this.mPurifyLocationName(vCircuitLocation);
					//System.out.println(vCircuitCountry);
					//System.out.println(vCircuitLocation);					
					if(!vLocationList.contains(vCircuitLocation))
					{
						for(Country vCountry : vCountries)
						{
							if(vCountry != null)
							{
								if(vCountry.mCountry().equals(vCircuitCountry))
								{
									vIDLocation++;
									vLocationList.add(vCircuitLocation);
									String vSQL = "INSERT INTO F1_Locations "
											+ "("
												+ "IDLocation, "
												+ "Location,"
												+ "IDCountry"									
											+ ")"
											+ "VALUES"
											+ "("
												+ "?,?,?"
											+ ")";

									try
									{
										PreparedStatement vPreparedStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
										vPreparedStatement.setLong(1, vIDLocation);
										vPreparedStatement.setString(2, vCircuitLocation);
										vPreparedStatement.setLong(3, vCountry.mIDCountry());
										vPreparedStatement.execute();
										vPreparedStatement.close();
										//System.out.println("Inserted Location :\n\tID : " + vIDLocation + "\n\tLocation : " + vCircuitLocation + "\n\tIDCountry : " + vCountry.mIDCountry() + "");
										break;
									} 
									catch (SQLException e) 
									{
										System.err.println("Error Location :\n\tID : " + vIDLocation + "\n\tLocation : " + vCircuitLocation + "\n\tIDCountry : " + vCountry.mIDCountry() + "");
										e.printStackTrace();
									}
									break;
								}
							}
						}
					}
				}
			}
		}
		System.out.println("Done !");
	}
	
	public void mF1_Status()
	{		
		System.out.println("Filling table F1_Status... ");
		List<Map<String, String>> vStatusData = this.mReadFile("status.csv");
					
		if(vStatusData != null)
		{
			String vSQL = "INSERT INTO F1_Status (IDStatus , Status) Values (?,?);";
			List<String> vStrings = new ArrayList<String>();
			int vSize = vStatusData.size();
			int vCounter = 0;
			for(Map<String, String> vRow : vStatusData)
			{
				this.mShowProgression(vCounter, vSize, 10);
				vCounter++;
				String vStatus = vRow.get("status");
				String vIDStatus = vRow.get("statusId");
				if(!(vIDStatus.isEmpty() && vStatus.isEmpty()))
				{
					if(!vStrings.contains(vStatus))
					{
						vStrings.add(vStatus);
						try
						{	
							PreparedStatement vPreparedStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
							vPreparedStatement.setLong(1, Integer.parseInt(vIDStatus));
							vPreparedStatement.setString(2, vStatus);
							vPreparedStatement.execute();
							vPreparedStatement.close();
							//System.out.println("Inserted Status :\n\tID : " + vIDStatus + "\n\tStatus : " + vStatus);
						}
						catch (SQLException e)
						{
							System.err.println("Error Status :\n\tID : " + vIDStatus + "\n\tStatus : " + vStatus);
							e.printStackTrace(System.err);
						}
					}
				}
			}
		}
		System.out.println("Done !");
	}
	
	
	
	public void mF1_Circuits()
	{
		System.out.print("Filling table F1_Circuits... ");
		List<Map<String, String>> vCircuitsData = this.mReadFile("circuits.csv");
		List<Location> vLocations = this.mLocations();
				
		if(vCircuitsData != null)
		{
			String vSQL = "INSERT INTO F1_Circuits "
					+ "("
						+ "IDCircuit, "
						+ "CircuitRefCircuit, "
						+ "NameCircuit, "
						+ "LatitudeCircuit, "
						+ "LongitudeCircuit, "
						+ "Altitude, "
						+ "URL, "
						+ "IDLocation"
					+ ") "
					+ "Values "
					+ "("
						+ "?,?,?,?,?,?,?,?"
					+ ");";
			int vSize = vCircuitsData.size();
			int vCounter = 0;	
			for(Map<String, String> vRow : vCircuitsData)
			{
				this.mShowProgression(vCounter, vSize, 10);
				vCounter++;
				String vCircuitID = vRow.get("circuitId");
				String vCircuitRef = vRow.get("circuitRef");
				String vCircuit = vRow.get("name");
				String vCircuitLocation = vRow.get("location");
				String vCircuitCountry = vRow.get("country");
				String vCircuitLatitude = vRow.get("lat");
				String vCircuitLongitude = vRow.get("lng");
				String vCircuitAltitude = vRow.get("alt");
				String vCircuitURL = vRow.get("url");
				
				if
				(
					(vCircuitID != null) 
					&& 
					(vCircuitRef != null) 
					&& 
					(vCircuit != null) 
					&& 
					(vCircuitLocation != null) 
					&& 
					(vCircuitCountry != null) 
					&& 
					(vCircuitLatitude != null) 
					&& 
					(vCircuitLongitude != null) 
					&& 
					(vCircuitAltitude != null) 
					&& 
					(vCircuitURL != null)
				)
				{
					vCircuit = this.mPurifyCircuitsNames(vCircuit);
					vCircuitLocation = this.mPurifyLocationName(vCircuitLocation);
					vCircuitCountry = this.mPurifyCountryName(vCircuitCountry);
					for(Location vLocation : vLocations)
					{
						if(vLocation.mLocation().equals(vCircuitLocation) || JaroWinkler.similarity(vCircuitLocation, vLocation.mLocation()) > 0.95d)
						{
							try
							{
								PreparedStatement vPreparedStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
								vPreparedStatement.setLong(1, Long.parseLong(vCircuitID));
								vPreparedStatement.setString(2, vCircuitRef);
								vPreparedStatement.setString(3, vCircuit);
								vPreparedStatement.setDouble(4, Double.parseDouble(vCircuitLatitude));
								vPreparedStatement.setDouble(5, Double.parseDouble(vCircuitLongitude));
								if(!vCircuitAltitude.trim().isEmpty())								
								{
									vPreparedStatement.setDouble(6, Double.parseDouble(vCircuitAltitude));
								}
								else
								{
									vPreparedStatement.setNull(6, Types.DOUBLE);
								}
								vPreparedStatement.setString(7, vCircuitURL);
								vPreparedStatement.setLong(8, vLocation.mIDLocation());
								vPreparedStatement.execute();
								vPreparedStatement.close();
								//System.out.println("Inserted Circuit :\n\tID : " + vCircuitID + ";\n\tCircuitRef : " + vCircuitRef + ";\n\tCircuit : " + vCircuit + ";\n\tLatituede : " + vCircuitLatitude + "");
								break;
							}
							catch (SQLException e)
							{
								System.err.println("Error Circuit :\n\tID : " + vCircuitID + ";\n\tCircuitRef : " + vCircuitRef + ";\n\tCircuit : " + vCircuit + ";\n\tLatituede : " + vCircuitLatitude + "");
								e.printStackTrace(System.err);
							}
						}
					}
					
				}
			}
		}
		System.out.println("Done !");
	}
	
	public void mF1_Races()
	{
		System.out.print("Filling table F1_Races... ");
		List<Map<String, String>> vRacesData =  mReadFile("races.csv");
		if(vRacesData != null)
		{
			String vSQL = "INSERT INTO F1_Races "
			+ "("
			+ "IDRace, "
			+ "YearRace, "
			+ "RoundRace, "
			+ "NameRace, "
			+ "DateRace, "
			+ "TimeRace, "
			+ "URLRace, "
			+ "IDCircuit"
			+ ") "
			+ "Values "
			+ "("
			+ "?, ?, ?, ?, ?, ?, ?, ?"
			+ ");";
			int vSize = vRacesData.size();
			int vCounter = 0;
			for(Map<String, String> vRow : vRacesData)
			{			
				this.mShowProgression(vCounter, vSize, 10);
				vCounter++;
				String vIDRace		= vRow.get("raceId"); 
				String vYearRace	= vRow.get("year"); 
				String vRoundRace	= vRow.get("round"); 
				String vNameRace	= vRow.get("name"); 
				String vDateRace	= vRow.get("date"); 
				String vTimeRace	= vRow.get("time"); 
				String vURLRace		= vRow.get("url"); 
				String vIDCircuit	= vRow.get("circuitId"); 
				
				if
				(
					!
					(
						vIDRace.isEmpty() 
						&& 
						vYearRace.isEmpty()
						&&
						vRoundRace.isEmpty()
						&&
						vNameRace.isEmpty()
						&&
						vDateRace.isEmpty() 
						&&
						vTimeRace.isEmpty() 
						&&
						vURLRace.isEmpty()
						&&
						vIDCircuit.isEmpty()
					)
				)
				{		
					try
					{
						PreparedStatement vPreparedStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
						vPreparedStatement.setLong(1, Integer.parseInt(vIDRace));
						vPreparedStatement.setLong(2, Integer.parseInt(vYearRace));
						vPreparedStatement.setLong(3, Integer.parseInt(vRoundRace));
						vPreparedStatement.setString(4, vNameRace);
						vPreparedStatement.setDate(5, Date.valueOf(LocalDate.parse(vDateRace, DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
						if(vTimeRace.indexOf(":") > -1)
						{
							vPreparedStatement.setTime(6, Time.valueOf(LocalTime.parse(vTimeRace, DateTimeFormatter.ofPattern("HH:mm:ss"))));
						}
						else
						{
							vPreparedStatement.setNull(6, Types.TIME);
						}
						vPreparedStatement.setString(7, vURLRace);
						vPreparedStatement.setLong(8, Integer.parseInt(vIDCircuit));
						vPreparedStatement.execute();
						vPreparedStatement.close();
						//System.out.println("Inserted Race :\n\tID : " + vIDRace + "\n\tYear : " + vYearRace + "\n\tRound : " + vRoundRace + "\n\tName : " + vNameRace + "\n\tDate : " + vDateRace + "\n\tTime : " + vTimeRace + "\n\tURL : " + vURLRace + "\n\tIDCircuit : " + vIDCircuit);
					}
					catch (SQLException e)
					{
						System.err.println("Error Race :\n\tID : " + vIDRace + "\n\tYear : " + vYearRace + "\n\tRound : " + vRoundRace + "\n\tName : " + vNameRace + "\n\tDate : " + vDateRace + "\n\tTime : " + vTimeRace + "\n\tURL : " + vURLRace + "\n\tIDCircuit : " + vIDCircuit);
						e.printStackTrace(System.err);
					}
				}
			}
		}
		System.out.println("Done !");
	}
	
	private String mPurifyNationalityName(String pNationalityName)
	{
		if(pNationalityName.contains("Hong Kong"))
		{
			pNationalityName = "Hongkonger";
		}
		if(pNationalityName.contains("Belgium"))
		{
			pNationalityName = "Belgian";
		}
		if(pNationalityName.contains("East German"))
		{
			pNationalityName = "German";
		}
		pNationalityName = pNationalityName.replace("*", "");
		pNationalityName = pNationalityName.trim();
		return pNationalityName;
	}
	
	private String mPurifyLocationName(String pLocationName)
	{
		pLocationName = pLocationName.trim();
		if(pLocationName.contains("Montmel"))
		{
			pLocationName = "Montmeló";
		}
		if(pLocationName.contains("rburg"))
		{
			pLocationName = "Nürburg";
		}
		if(pLocationName.contains("o Paulo"))
		{
			pLocationName = "São Paulo";
		}
		return pLocationName;
	}

	private String mPurifyCircuitsNames(String pCircuitName)
	{
		if(pCircuitName.contains("Carlos Pace"))
		{
			pCircuitName = "Autódromo José Carlos Pace";
		}
		if(pCircuitName.contains("rburgring"))
		{
			pCircuitName = "Nürburgring";
		}
		if(pCircuitName.contains("dromo do Estoril"))
		{
			pCircuitName = "Autódromo do Estoril";
		}
		if(pCircuitName.contains("dromo Juan y Oscar G"))
		{
			pCircuitName = "Autódromo Juan y Oscar Gálvez";
		}
		if(pCircuitName.contains("dromo Hermanos Rodr"))
		{
			pCircuitName = "Autódromo Hermanos Rodríguez";
		}
		if(pCircuitName.contains("dromo Internacional Nelson Piquet"))
		{
			pCircuitName = "Autódromo Internacional Nelson Piquet";
		}
		if(pCircuitName.contains("Montju"))
		{
			pCircuitName = "Montjuïc";
		}
		
		return pCircuitName;
	}
	
	private String mPurifyCountryName(String pCountryName)
	{
		pCountryName = pCountryName.trim();
		pCountryName = pCountryName.replace("(The)", "");
		pCountryName = pCountryName.replace("*", "");
		if(pCountryName.contains("UK"))
		{
			pCountryName = "United Kingdom";
		}
		pCountryName = pCountryName.replace("USA", "United States");
		pCountryName = pCountryName.replace("UAE", "United Arab Emirates");
		pCountryName = pCountryName.trim();
		return pCountryName;
	}
	
	private String mPurifyTime(String pTime)
	{
		if(pTime.contains("sec"))
		{
			pTime = pTime.replace("sec", "").trim();
		}
		if(pTime.contains("s"))
		{
			pTime = pTime.replace("s", "").trim();
		}
		if(pTime.contains("+"))
		{
			pTime = pTime.replace("+", "").trim();
		}
		return pTime;
	}
	
	private Double mTimeToDouble(String pTime)
	{
		Double vResult = null;
		pTime = this.mPurifyTime(pTime);
		
		if(pTime.contains(":"))
		{
			vResult = Double.valueOf(pTime.split(":")[0]) * 60.0 + Double.valueOf(pTime.split(":")[1]);
		}
		else
		{
			vResult = Double.valueOf(pTime);
		}
		return vResult;		
	}
	
	private List<Location> mLocations()
	{
		List<Location> vLocations = new ArrayList<Location>();
		String vSQL = "SELECT * FROM F1_Locations";
		try 
		{
			PreparedStatement vStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
			
			ResultSet vResultSet = vStatement.executeQuery();
			while(vResultSet.next())
			{
				long vIDLocation = vResultSet.getLong("IDLocation");
				String vLocation = vResultSet.getString("Location");
				vLocations.add(new Location(vIDLocation, vLocation));
			}
			vResultSet.close();
			vStatement.close();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace(System.err);
			System.exit(-1);
		}
		return vLocations;
	}
	
	private List<Country> mCountries()
	{
		List<Country> vCountries = new ArrayList<Country>();
		String vSQL = "SELECT * FROM F1_Country";
		try 
		{
			PreparedStatement vStatement = this.aMySQL.mConnection().prepareStatement(vSQL);
			
			ResultSet vResultSet = vStatement.executeQuery();
			while(vResultSet.next())
			{
				long vIDCountry = vResultSet.getLong("IDCountry");
				String vCountry = vResultSet.getString("Country");
				String vNationality = vResultSet.getString("NationalityCountry");
				vCountries.add(new Country(vIDCountry, vCountry, vNationality));
			}
			vResultSet.close();
			vStatement.close();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace(System.err);
			System.exit(-1);
		}
		return vCountries;
	}
	
	public List<Map<String, String>> mReadFile(String pFile)
	{
        String vFilePath = "";
        String vOS = System.getProperty("os.name");
        if(vOS.contains("Windows"))
        {
		    vFilePath = this.aFilePath + "\\" + pFile;
        }
        if(vOS.contains("Linux"))
        {
		    vFilePath = this.aFilePath + "/" + pFile;
        }
		CSVFile vFileCSV = new CSVFile(vFilePath, 1);
		List<Map<String, String>> vFileData = null;
		
		try 
		{
			vFileData = vFileCSV.mReadFile();
		} 
		catch (IOException | CsvException e) 
		{
			e.printStackTrace(System.err);
			System.exit(-1);
		}
		return vFileData;
	}
}
