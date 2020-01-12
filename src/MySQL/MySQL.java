package MySQL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MySQL 
{
	/* Connexion à la base de données */
	private String aURL;
	private String aUser;
	private String aPassword;
	private Connection aConnexion = null;
	
	public MySQL(String pServer, String pPort, String pDatabase, String pUser, String pPassword)
	{
		this.aPassword = pPassword;
		this.aUser = pUser;
		this.aURL = "jdbc:mysql://" + pServer + ":" + pPort + "/" + pDatabase; 		
	}
	
	public void mConnect()
	{
		try
		{
			this.aConnexion = DriverManager.getConnection(this.aURL, this.aUser, this.aPassword);
		}
		catch ( SQLException e ) 
		{
			System.out.println("Erreur à la connexion à la base de donnée...");
			e.printStackTrace(System.err);
			this.mClose();
			System.exit(-1);
		}
	}
	
	public Connection mConnection()
	{
		return this.aConnexion;
	}
	
	
	
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
