

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * 
 * @author Priya Ankush Kotwal
 * USC ID: 4336 080 755
 * Title: 585 Homework 2
 *
 */

public class SpatialProject {
	public static void main(String[] args) {
		Connection conn=null;
		try{
			/*for(int i =0;i< args.length;i++)
				System.out.println(i+": "+args[i]);*/
			conn = DriverManager.getConnection( "jdbc:oracle:thin:@localhost:1521:orcl", "sys as sysdba", "admin");
			Statement stmt = conn.createStatement();

			switch(args[2]){

			//If query type = window: we perform a window query in this case
			case "window":

				int args2=Integer.parseInt(args[4]);
				int args3=Integer.parseInt(args[5]);
				int args4=Integer.parseInt(args[6]);
				int args5=Integer.parseInt(args[7]);
				if(args[3].equalsIgnoreCase("firebuilding"))
				{
					String query="select b.bid from building b, firebuilding f where SDO_INSIDE(b.shape, SDO_GEOMETRY(2003, NULL, NULL,SDO_ELEM_INFO_ARRAY(1,1003,3), SDO_ORDINATE_ARRAY(" + args2 + "," + args3 + "," + args4 + "," + args5 + ")))='TRUE' and b.bid=f.fbid";
					ResultSet rs=stmt.executeQuery(query);
					int i=0;
					System.out.println("\nBuilding ids on fire");
					while(rs.next())
					{
						String building_id=rs.getString("bid");
						i++;
						System.out.println(i + ": " + "BUILDING ID : " + building_id);
					}
				}
				else if(args[3].equalsIgnoreCase("firehydrant"))
				{
					String query="select hydrantid from hydrant where SDO_INSIDE(hydrantloc, SDO_GEOMETRY(2003, NULL, NULL,SDO_ELEM_INFO_ARRAY(1,1003,3), SDO_ORDINATE_ARRAY(" + args2 + "," + args3 + "," + args4 + "," + args5 + ")))='TRUE'";
					ResultSet rs=stmt.executeQuery(query);
					int i=0;
					System.out.println("Firehydrant ids ");
					while(rs.next())
					{
						String building_id=rs.getString("hydrantid");
						i++;
						System.out.println(i + ") " + "Hydrant ID : " + building_id);
					}
				}
				else if(args[3].equalsIgnoreCase("building"))
				{
					//String query="select from building b where SDO_INSIDE(b.shape, SDO_GEOMETRY(2003, NULL, NULL,SDO_ELEM_INFO_ARRAY(1,1003,3), SDO_ORDINATE_ARRAY(" + args2 + "," + args3 + "," + args4 + "," + args5 + ")))='TRUE'";
					String query = "SELECT B.bid, B.bname FROM building B WHERE sdo_filter(B.shape, mdsys.sdo_geometry(2003,NULL,NULL, mdsys.sdo_elem_info_array(1,1003,3), mdsys.sdo_ordinate_array("+args2+","+args3+","+args4+","+args5+")), 'querytype=window') = 'TRUE'";
					ResultSet rs=stmt.executeQuery(query);
					int i=0;
					System.out.println("Building ids ");
					while(rs.next())
					{

						String building_id=rs.getString("bid");
						i++;
						System.out.println(i + ": " + "BUILDING ID : " + building_id);
					}
				}

				break;

				//If query type = within: we do a within-distance query in this case.
			case "within":

				int args_size=args.length;
				int no_of_objects=args_size-3;
				int index=2;
				ArrayList<String> obj= new ArrayList<String>();
				ArrayList<String> obj1=new ArrayList<String>();
				for(int i=0;i<no_of_objects;i++)
				{
					obj.add(args[index]);
					obj1.add(args[index]);
					index++;
				}
				String sub_query="";

				if(obj.size()==1)
				{
					sub_query="select * from building b1 where b1.bname = '" + obj.get(0) + "'";
				}
				else
				{
					sub_query="select * from building b1 where ";
					int j=0;
					for(j=0;j<obj.size()-1;j++)
					{
						sub_query=sub_query + "b1.bname = '" + obj.get(j) + "' or ";
					}
					sub_query=sub_query + "b1.bname = '" + obj.get(j) + "'";
				}
				if(args[3].equalsIgnoreCase("firehydrant"))
				{
					String query="SELECT distinct(f.hydrantid) FROM hydrant f, (" + sub_query + ") t WHERE SDO_WITHIN_DISTANCE(f.hydrantloc, t.shape, ";
					String args31=args[args_size-1];
					query=query+"'distance=" + args31 + "') = 'TRUE'";
					ResultSet rs=stmt.executeQuery(query);
					int j=0;
					System.out.println("Firehydrant ids ");
					while(rs.next())
					{
						String hydrant_id=rs.getString("hydrantid");
						j++;
						System.out.println(j + ": " + "FIREHYDRANT ID : " + hydrant_id);
					}
				}
				else if(args[3].equalsIgnoreCase("firebuilding"))
				{
					String query="SELECT distinct(b.fbid) FROM firebuilding b,building b1, (" + sub_query + ") t WHERE b.fbid=b1.bid and SDO_WITHIN_DISTANCE(b1.shape, t.shape, ";
					String args31=args[args_size-1];
					query=query+"'distance=" + args31 + "') = 'TRUE' and b.bname <> t.bname";
					//System.out.println(query);
					ResultSet rs=stmt.executeQuery(query);
					int j=0;
					System.out.println("Building ids which are on fire");
					while(rs.next())
					{
						String building_id=rs.getString("fbid");
						j++;
						System.out.println(j + ": " + "BUILDING ID : " + building_id);
					}
				}
				else if(args[3].equalsIgnoreCase("building"))
				{
					String query="SELECT distinct(b.bid) FROM building b, (" + sub_query + ") t WHERE SDO_WITHIN_DISTANCE(b.shape, t.shape, ";
					String args31=args[args_size-1];
					query=query+"'distance=" + args31 + "') = 'TRUE' and b.bname <> t.bname";
					//System.out.println(query);
					ResultSet rs=stmt.executeQuery(query);
					int j=0;
					System.out.println("Building ids ");
					while(rs.next())
					{
						String building_id=rs.getString("bid");
						j++;
						System.out.println(j + ") " + "BUILDING ID : " + building_id);
					}
				}

				break;

			case "nn":
				//If query type = nn: we do a nearest neighbor query in this case.
				String args211=args[4];
				if(args[3].equalsIgnoreCase("firehydrant"))
				{
					String query1="select f.hydrantid from hydrant f where SDO_NN(f.hydrantloc, (select b1.shape from building b1 where b1.bid='" + args211 + "'),";
					String args31=args[5];
					query1=query1+" 'sdo_num_res=" + args31 + "') = 'TRUE'";
					ResultSet rs1=stmt.executeQuery(query1);
					int j1=0;
					System.out.println("Firehydrant ids ");
					while(rs1.next())
					{
						//System.out.println(query1);
						String building_id=rs1.getString("hydrantid");
						j1++;
						System.out.println(j1 + ": " + "Hydrant ID : " + building_id);
					}
				}
				else if(args[3].equalsIgnoreCase("firebuilding"))
				{

					String query1="select x.fbid from firebuilding x where SDO_NN(x.shape, (select b1.shape from building b1 where b1.bid='" + args211 + "'), ";
					String a=args[5];
					int arg=Integer.parseInt(a);
					arg=arg+1;
					String args31="" + arg;
					query1=query1+"'sdo_num_res=" + args31 + "') = 'TRUE' AND fbid<>'"+args211+"'";
					System.out.println(query1);
					ResultSet rs1 = stmt.executeQuery(query1);

					int j1=1;
					System.out.println("Building ids which are on fire: ");

					while(rs1.next())
					{

						System.out.println("\n"+j1+" "+rs1 .getString("fbid"));
						j1++;
					}
				}
				else if(args[3].equalsIgnoreCase("building"))
				{

					String query1="select x.bid from (select * from building b) x where SDO_NN(x.shape, (select b1.shape from building b1 where b1.bid='" + args211 + "'), ";
					String a=args[5];
					int arg=Integer.parseInt(a);
					arg=arg+1;
					String args31="" + arg;
					query1=query1+"'sdo_num_res=" + args31 + "') = 'TRUE'";
					ResultSet rs1=stmt.executeQuery(query1);
					System.out.println(query1);
					int j1=0;
					System.out.println("Building ids ");
					while(rs1.next())
					{
						String building_id=rs1.getString("bid");
						String b_id=building_id.trim();
						if(!b_id.equalsIgnoreCase(args211) && j1<5)
						{	
							j1++;
							System.out.println(j1 + ": " + "BUILDING ID : " + building_id);
						}
					}
				}


				break;
			case "demo":
				//If query type = demo: in this case we print the results of the following hard-coded demos on the screen
				if(args[3].equalsIgnoreCase("1"))
				{
					String query="select b.bname from building b where b.BNAME LIKE 'S%' AND b.bid NOT IN (select fbid from firebuilding)";
					ResultSet rs=stmt.executeQuery(query);
					int i=0;
					System.out.println("\nDemo 1: Building names ");
					while(rs.next())
					{
						String building_id=rs.getString("bname");
						i++;
						System.out.println(i + ": " + "BUILDING NAME : " + building_id);
					}
				}
				else if(args[3].equalsIgnoreCase("2"))
				{
					String query="select distinct f.bname,h.HYDRANTID from firebuilding f, hydrant h, building b where f.fbid=b.bid and SDO_NN(h.HYDRANTLOC, b.SHAPE, 'sdo_num_res=5') = 'TRUE' order by f.bname";
					ResultSet rs=stmt.executeQuery(query);
					int i=0;
					ArrayList<String> f_id= new ArrayList<String>();
					ArrayList<String> b_id= new ArrayList<String>();
					System.out.println("\n Demo 2: Building names and corresponding firehydrant ids");
					while(rs.next())
					{
						String building_id=rs.getString("bname");
						if(!b_id.contains(building_id))
						{
							i++;
							System.out.println(i + ": " + "BUILDING NAME ON FIRE : " + building_id);
							b_id.add(building_id);
						}
						String hydrant_id=rs.getString("hydrantid");
						if(!f_id.contains(hydrant_id))
						{	
							f_id.add(hydrant_id);
							System.out.println(" FIREHYDRANT ID : " + hydrant_id);
						}
					}
				}
				else if(args[3].equalsIgnoreCase("3"))
				{
					String query1="SELECT hydrantid, COUNT(bid) FROM hydrant, building WHERE SDO_WITHIN_DISTANCE(hydrantloc,shape,'distance=120') = 'TRUE' GROUP BY hydrantid HAVING COUNT(bid)= (SELECT MAX(COUNT(bid)) FROM hydrant, building WHERE SDO_WITHIN_DISTANCE(hydrantloc,shape,'distance=120') = 'TRUE' GROUP BY hydrantid)";
					ResultSet rs1=stmt.executeQuery(query1);
					ArrayList<String> b_name=new ArrayList<String>();
					ArrayList<Integer> count=new ArrayList<Integer>();

					System.out.println("\nHydrant IDs and corresponding number of buildings");
					System.out.println("\nHydrantID Count ");
					while(rs1.next())
					{
						b_name.add(rs1.getString("hydrantid"));
						count.add(rs1.getInt("COUNT(bid)"));
						System.out.println("\n"+rs1.getString("hydrantid")+"	  "+rs1.getInt("COUNT(bid)"));

					}

				}
				else if(args[3].equalsIgnoreCase("4"))
				{
					String query="SELECT *FROM (SELECT hydrantid, COUNT(bid) FROM hydrant, building WHERE SDO_NN(HYDRANTLOC,SHAPE,'sdo_num_res=1')= 'TRUE' GROUP BY hydrantid ORDER BY COUNT(bid) desc)t WHERE ROWNUM<=5";
					ResultSet rs=stmt.executeQuery(query);
					System.out.println("\nHydrantID Count");
					while(rs.next())
					{
						System.out.println("\n"+rs.getString("hydrantid")+"         "+rs.getInt("COUNT(bid)"));
					}
				}
				else if(args[3].equalsIgnoreCase("5"))
				{
					String query1="SELECT X, Y FROM TABLE(SELECT SDO_UTIL.GETVERTICES(SDO_AGGR_MBR(shape)) FROM building WHERE bname LIKE '%HE')";
					ResultSet rs1=stmt.executeQuery(query1);
					System.out.println("\nX    Y");

					while(rs1.next())
						System.out.println("\n"+rs1.getInt("X")+" "+rs1.getInt("Y"));

				}

				break;
			default:
				System.out.println("default case");
				break;

			}

		}
		catch(SQLSyntaxErrorException e1)
		{
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
