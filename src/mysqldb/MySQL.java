package mysqldb;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de pilotage de MySQL
 * @author Patrick PIGNOL
 *
 */
public class MySQL 
{
	/* Connexion Ã  la base de donnÃ©es */
	private String aURL;
	private String aUser;
	private String aPassword;
	private Connection aConnexion = null;
	/**
	 * Instancie une nouvelle connection MySQL en enregistrant les paramêtres de la connection
	 * @param pServer	Adresse du serveur de base de données
	 * @param pPort		Port du serveur de base de données
	 * @param pDatabase	Nom de la base de donnée
	 * @param pUser		Nom de l'utilisateur à utiliser pour se connecter
	 * @param pPassword	Mot de passe de l'utilisateur à utiliser pour se connecter
	 */
	public MySQL(String pServer, String pPort, String pDatabase, String pUser, String pPassword)
	{
		this.aPassword = pPassword;
		this.aUser = pUser;
		this.aURL = "jdbc:mysql://" + pServer + ":" + pPort + "/" + pDatabase; 		
	}
	/**
	 * Se connecte à la base de donnée avec les informations transmisent au constructeur
	 */
	public void mConnect()
	{
		try
		{
			this.aConnexion = DriverManager.getConnection(this.aURL, this.aUser, this.aPassword);
		}
		catch ( SQLException e ) 
		{
			System.out.println("Erreur Ã  la connexion Ã  la base de donnÃ©e...");
			e.printStackTrace(System.err);
			this.mClose();
			System.exit(-1);
		}
	}
	/**
	 * mConnection Retourne la connection en cour 
	 * @return Connection Connection actuelle
	 */
	public Connection mConnection()
	{
		return this.aConnexion;
	}
	
	
	/**
	 * mClose Ferme la Connection actuelle
	 */
	public void mClose()
	{
		if(this.aConnexion != null)
		{
			try 
			{
			    /* Fermeture de la connexion */
				this.aConnexion.close();
			}
			catch ( SQLException ignore )
			{
			    /* Si une erreur survient lors de la fermeture, il suffit de l'ignorer. */
			}
		}
	}
}
