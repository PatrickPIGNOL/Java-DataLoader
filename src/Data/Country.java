package Data;

/**
 * Classe Country représentant un enregistrement Pays en base
 */
public class Country 
{
	private long aIDCountry;
	private String aCountry;
	private String aNationality;
	
	/**
	 * Country Constructeur
	 * @param pIDCountry : Index du pays dans la base
	 * @param pCountry : nom du pays
	 * @param pNationality : nationalité associé au pays
	 */
	public Country(long pIDCountry, String pCountry, String pNationality)
	{
		this.aIDCountry = pIDCountry;
		this.aCountry = pCountry;
		this.aNationality = pNationality;
	}
	
	/**
	 * mIDCountry Retourne L'ID en base du pays
	 * @return ID du pays
	 */
	public long mIDCountry()
	{
		return this.aIDCountry;
	}
	
	/**
	 * mCountry Retourne le nom du pays
	 * @return nom du pays
	 */	
	public String mCountry()
	{
		return this.aCountry;
	}
	
	/**
	 * mNationality Retourne la nationalité associé au pays
	 * @return nationalité associé au pays
	 */
	public String mNationality()
	{
		return this.aNationality;
	}
}
