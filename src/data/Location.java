package data;

/**
 * Classe Location represente une ville en base
 * @author Patrick PIGNOL
 */
public class Location 
{
	private long aIDLocation;
	private String aLocation;
	/**
	 * Location Constructeur
	 * @param pIDLocation : ID de la ville en base 
	 * @param pLocation : Nom de la ville
	 */
	public Location(long pIDLocation, String pLocation)
	{
		this.aIDLocation = pIDLocation;
		this.aLocation = pLocation;
	}
	
	/**
	 * mIDLocation retourne l'ID en base de la ville
	 * @return ID en base de la ville
	 */
	public long mIDLocation()
	{
		return this.aIDLocation;
	}

	/**
	 * mIDLocation retourne le nom de la ville
	 * @return nom de la ville
	 */
	public String mLocation()
	{
		return this.aLocation;
	}
}
