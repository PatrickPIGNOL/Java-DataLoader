
public class Location 
{
	private long aIDLocation;
	private String aLocation;
	
	public Location(long pIDLocation, String pLocation)
	{
		this.aIDLocation = pIDLocation;
		this.aLocation = pLocation;
	}
	
	public long mIDLocation()
	{
		return this.aIDLocation;
	}
	
	public String mLocation()
	{
		return this.aLocation;
	}
}
