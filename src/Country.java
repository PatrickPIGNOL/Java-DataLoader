
public class Country 
{
	private long aIDCountry;
	private String aCountry;
	private String aNationality;
	public Country(long pIDCountry, String pCountry, String pNationality)
	{
		this.aIDCountry = pIDCountry;
		this.aCountry = pCountry;
		this.aNationality = pNationality;
	}
	
	public long mIDCountry()
	{
		return this.aIDCountry;
	}
	
	public String mCountry()
	{
		return this.aCountry;
	}
	public String mNationality()
	{
		return this.aNationality;
	}
}
