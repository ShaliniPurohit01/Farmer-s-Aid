package dbconnection;
import java.sql.*;
import java.util.Scanner;
import java.math.BigDecimal;

public class PostgreSQLJDBC 
{
	public static void main(String args[]) 
	{
		int choice=0;
		Connection c = null;
		try
		{
			// Load Postgresql Driver class
			Class.forName("org.postgresql.Driver");
			// Using Driver class connect to databased on localhost, port=5432, database=postgres, user=postgres, password=postgres. If cannot connect then exception will be generated (try-catch block)
			c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/farming_db","postgres", "rucha");
			System.out.println("Opened database successfully");
			
			// Create instance of this class to call other methods
			PostgreSQLJDBC p = new PostgreSQLJDBC();
			
			c.setAutoCommit(false);
			System.out.println("1.Get Suggestion based on area:");
			System.out.println("2.Get Suggestion based on farm:");
			System.out.println("3.Get Suggestion based on crop:");
			System.out.println("4.Get Suggestion based on season:");
			System.out.println("5.Display crop having high demands:");
			System.out.println("6.Give feedback:");
			Scanner sc=new Scanner(System.in);
			choice=sc.nextInt();
			//sc.close();
			switch(choice)
			{
			case 1: 
				p.queryGetSuggestion(c);
				break;
			case 2:
				p.queryGetSuggestionByFarm(c);
				break;
			case 3:
				p.queryGetSuggestionByCrop(c);
				break;
			case 4:
				p.queryGetSuggestionBySeason(c);
				break;
			case 5:
				p.queryDisplayCropByDemand(c);
				break;
			case 6:
				p.queryGiveFeedback(c);
				c.setAutoCommit(true);	
				break;
				
			default:
				System.out.println("Invalid Choice");
			}
			
			c.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);
		}
	}
	

	void queryGiveFeedback(Connection c)
	{
		int rating=0,farmerid=0;
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter ratings:");
		rating=sc.nextInt();
		System.out.println("Enter farmer ID:");
		farmerid=sc.nextInt();
		PreparedStatement stmt = null;
		String sql = "INSERT INTO feedback(feedbackid,rating,farmerid)VALUES(DEFAULT,?, ?)";
		try
		{
			stmt = c.prepareStatement(sql);
			stmt.setInt(1, rating);
			stmt.setInt(2, farmerid);
			stmt.execute();
			stmt.close();
			System.out.println("Data Inserted successfully");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);
		}
	}
	
	void queryGetSuggestion(Connection c)
	{
		PreparedStatement stmt = null;
		
		try
		{
			String state="";
			System.out.println("Enter state: ");
			Scanner sc=new Scanner(System.in);
			state=sc.next();
			String sql="select distinct c.cropname from crop_area as c join area as a on (a.areaid=c.areaid) where a.state=?";
			stmt = c.prepareStatement(sql);
			stmt.setString(1,state);
			ResultSet rs=stmt.executeQuery();
			if(rs.next())
			{
			System.out.println("You can grow:");	
			while(rs.next())
			{
				String cropname="";
				cropname=rs.getString("cropname");
				System.out.println(cropname);	
			}
			System.out.println("in your area.");
			}
			else
				System.out.println("Invalid Area.");
				
			stmt.close();
			//System.out.println("Table Queried successfully");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);
		}

   }
	void queryDisplayCropByDemand(Connection c)
	{
		PreparedStatement stmt = null;
		
		try
		{
			String state="";
			System.out.println("Enter state: ");
			Scanner sc=new Scanner(System.in);
			state=sc.next();
			//String sql="select distinct croptypename from croptype where cropdemand='High'";
			String sql="select distinct ca.cropname from crop_area as ca join area as a on(a.areaid=ca.areaid) join crop as c on(c.cropname=ca.cropname)join croptype as ct on(ct.croptypename=c.croptype) where ct.cropdemand='high' and a.state=?";
			stmt = c.prepareStatement(sql);
			stmt.setString(1,state);
			ResultSet rs=stmt.executeQuery();
			if(rs.next())
			{
			System.out.println("Crop with high demand:");	
			while(rs.next())
			{
				String cropname="";
				cropname=rs.getString("cropname");
				System.out.println(cropname);	
			}
			System.out.println("in your area.");
			}
			else
				System.out.println("Invalid Area.");
				
			stmt.close();
			}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);
		}

   }
	
	
	void queryGetSuggestionBySeason(Connection c)
	{
		PreparedStatement stmt = null;
		
		try
		{
			Scanner sc=new Scanner(System.in);
			
			String seasonname="",area="";
			String cropname="";
			
			System.out.println("Enter your area: ");
			area=sc.next();
			System.out.println("Enter season: ");
			seasonname=sc.next();
			//String sql="select cropname from crop where seasonname = ?";
			String sql="select ca.cropname from crop_area as ca join area as a on(ca.areaid=a.areaid) join crop as c on(c.cropname=ca.cropname) where c.seasonname=? and a.state=?;";
			stmt = c.prepareStatement(sql);
			stmt.setString(1,seasonname);
			stmt.setString(2, area);
			ResultSet rs=stmt.executeQuery();
			if(rs.next())
			{
			System.out.println("You can grow:");
			//System.out.println(area+" "+seasonname);
			while(rs.next())
			{
				cropname=rs.getString("cropname");
				System.out.println(cropname);	
			}
			System.out.println("in "+seasonname);
			}
			else
				System.out.println("Invalid Season.");
				
			stmt.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);
		}

   }

	void queryGetSuggestionByFarm(Connection c)
	{
		PreparedStatement stmt = null;
		
		try
		{
			int farmid=0;
			System.out.println("Enter farmid: ");
			Scanner sc=new Scanner(System.in);
			farmid=sc.nextInt();
			String sql="select distinct c.cropname from crop_area as c join farm as f on (f.areaid=c.areaid) where f.farmid=?";
			stmt = c.prepareStatement(sql);
			stmt.setInt(1,farmid);
			ResultSet rs=stmt.executeQuery();
			if(rs.next())
			{
				System.out.println("You can grow:");
			while(rs.next())
			{
				String cropname="";
				cropname=rs.getString("cropname");
				
				System.out.println(cropname);
				}
			System.out.println("in your farm.");
			}
			else
				System.out.println("Invalid farm ID");
			
			stmt.close();
			}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);
		}

   }
   
	void queryGetSuggestionByCrop(Connection c)
	{
		PreparedStatement stmt = null;
		
		try
		{
			String crop="";
			int farmid=0,flag=0;
			System.out.println("Enter crop: ");
			Scanner sc=new Scanner(System.in);
			crop=sc.next();
			System.out.println("Enter farmid: ");
			farmid=sc.nextInt();
			String sql="select distinct c.cropname from crop_area as c join farm as f on (f.areaid=c.areaid) where f.farmid=?";
			stmt = c.prepareStatement(sql);
			stmt.setInt(1,farmid);
			ResultSet rs=stmt.executeQuery();
			while(rs.next())
			{
				String cropname="";
				cropname=rs.getString("cropname");
				
				if(cropname.equalsIgnoreCase(crop))
				flag++;
			}
			if(flag>0)
				System.out.println("You can grow "+crop);
			else
				System.out.println("You cannot grow "+crop+" in this area.");
			
				
			stmt.close();
			}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);
		}

   }
}