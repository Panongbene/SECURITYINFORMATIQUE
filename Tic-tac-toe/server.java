import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.lang.String;

public class server
    {
/**********************************************************************************************************************/
	//variable we need
    //flag message for the game
    final static int FYI=0x01;
	final static int MYM=0x02;
	final static int END=0x03;
	final static int TXT=0x04;
	final static int MOV=0x05;
	final static int LFT=0x06;

    //ip and port serveur
	private static int port_server=8000;
	private static String ip_server="127.0.0.1";

	//buffer
	final static int taille = 4096; 
    final static byte buffer[] = new byte[taille];


    //customer data
    public static int number_client_connected=0;
    static int port_gamer1=0;
    static String ip_adress_gamer1="";
    static int port_gamer2=0;
    static String ip_adress_gamer2="";
    static int number_free_space=9;//for to know the number of free box in the array

    //this array represente the game
	private static int[][] the_game= new int[3][3];

	//the datagram socket server
	private static DatagramSocket listen_socket=null;

	//this flag is used for to determinate the end of game
	private static int ENDD=-1;
	private static int winner=0;
/**********************************************************************************************************************/
    //function for to send data at the client
    public static void send(final String payload,int port,String ip_adresse)
    	 {
		  byte[] data = payload.getBytes();

		  try 
		    {
		     DatagramPacket datagramme= new DatagramPacket(data,data.length,InetAddress.getByName(ip_adresse),port);
		     listen_socket.send(datagramme);
			}
		  catch (SocketException e) 
		    {
             e.printStackTrace();
            } 
          catch (IOException e) 
            {
             e.printStackTrace();
            }

    	 }
/**********************************************************************************************************************/
	public static void process_receive_data(DatagramPacket datagram_gammer)
		 {
		  int port_gamer=datagram_gammer.getPort();
    	  String ip_adress_gamer=new String(datagram_gammer.getAddress().getHostAddress());		  
    	  String payload=new String(datagram_gammer.getData());
System.out.println("RECEIVE SERVEUR ==== "+payload);
    	  if(number_client_connected<2)
    	  	{
             if(number_client_connected==0)
             	{
             	 String tampon= new String(Integer.toString(TXT)+"welcomẹ!\0");
             	 if(tampon.equals(payload)==false)
             	 	{
             	 	 port_gamer1=port_gamer;
             	 	 ip_adress_gamer1=new String(ip_adress_gamer);
             	 	 number_client_connected=1;
             	 	 send(tampon,port_gamer,ip_adress_gamer);
             	 	}
             	}
             else
             	{
             	 String tampon= new String(Integer.toString(TXT)+"welcomẹ!\0");
             	 if(port_gamer==port_gamer1 && (ip_adress_gamer.equals(ip_adress_gamer1)==false))
             	 	 send(tampon,port_gamer,ip_adress_gamer);
             	 else
             	    {
             	     if(tampon.equals(payload)==false)
             	     	{
             	     	 port_gamer2=port_gamer;
             	 	 	 ip_adress_gamer2=new String(ip_adress_gamer);
             	 	 	 number_client_connected=2;
             	 	 	 send(tampon,port_gamer2,ip_adress_gamer2);

             	 	 	 String tampon1= new String(Integer.toString(FYI)+transformer_array_string());
             	 	 	 send(tampon1,port_gamer1,ip_adress_gamer1);
             	     	 send(tampon1,port_gamer2,ip_adress_gamer2);

             	     	 //the game begin
             	     	 String tampon3= new String(Integer.toString(MYM));
             	     	 send(tampon3,port_gamer1,ip_adress_gamer1);
             	     	}	
             	    }
             	}
            }
           else
            {
             if(port_gamer!=port_gamer1 || (ip_adress_gamer.equals(ip_adress_gamer1)==false))
                 {
                  if(port_gamer!=port_gamer2 || ip_adress_gamer.equals(ip_adress_gamer2)==false)
                  	{
                  	 int winner1=0xFF;
                  	 String tampon= new String(Integer.toString(END));
                  	 tampon+=new String(Integer.toString(winner1));
                  	 send(tampon,port_gamer,ip_adress_gamer);
                  	}
                  else
                    {
                     int flagg=convertSafe(payload.substring(0,1));//extract the flag

                     if(flagg==MOV)
                     	{
                     	 int lin=convertSafe(payload.substring(1,2));
                     	 int col=convertSafe(payload.substring(2,3));

                     	 if(the_game[lin][col]==0)
                     	 	{
                     	 	 the_game[lin][col]=2;
                     	 	 number_free_space-=1;
                     	 	 winner=the_game_winner();//determinate the winner
                     	 	 if(winner!=0 || number_free_space==0)//if there are an winner or the thame finished
                     	 	 	ENDD=1;                     	 	 
                     	 	 else
                     	 	    {
                     	 	     String tampon1= new String(Integer.toString(FYI)+transformer_array_string());
             	 	 	 		 send(tampon1,port_gamer1,ip_adress_gamer1);
             	     	 		 send(tampon1,port_gamer2,ip_adress_gamer2);

                     	 	     String tampon3= new String(Integer.toString(MYM));
             	     	 		 send(tampon3,port_gamer1,ip_adress_gamer1);//it's player 1's turn
                     	 	    }                     	 	 		
                     	 	}
                     	 else
                     	    {
                     	     String tampon1= new String(Integer.toString(LFT)+" INVALID CHOICE");
             	 	 	 	 send(tampon1,port_gamer2,ip_adress_gamer2);
                     	     String tampon3= new String(Integer.toString(MYM));
             	     	 	 send(tampon3,port_gamer2,ip_adress_gamer2);//it's player 1's turn
                     	    }
                     	}
                    }
                 }
              else
                 {
                  int flagg=convertSafe(payload.substring(0,1));//extract the flag

                  if(flagg==MOV)
                    {
                     int lin=convertSafe(payload.substring(1,2));
                     int col=convertSafe(payload.substring(2,3));

                     if(the_game[lin][col]==0)
                     	 {
                     	  the_game[lin][col]=1;
                     	  number_free_space-=1;
                     	  winner=the_game_winner();//determinate the winner
                     	  if(winner!=0 || number_free_space==0)//if there are an winner or the thame finished
                     	 	 ENDD=1;
                     	  else
                     	 	{
                     	 	 String tampon1= new String(Integer.toString(FYI)+transformer_array_string());
             	 	 	     send(tampon1,port_gamer1,ip_adress_gamer1);
             	     	 	 send(tampon1,port_gamer2,ip_adress_gamer2);

                     	 	 String tampon3= new String(Integer.toString(MYM));
             	     	 	 send(tampon3,port_gamer2,ip_adress_gamer2);//it's player 2's turn
                     	 	 }                   	 	 		
                     	  }
                      else
                     	  {
                     	   String tampon1= new String(Integer.toString(LFT)+" INVALID CHOICE");
             	 	 	   send(tampon1,port_gamer1,ip_adress_gamer1);
                     	   String tampon3= new String(Integer.toString(MYM));
             	     	   send(tampon3,port_gamer1,ip_adress_gamer1);//it's player 1's turn
                     	  }
                    }
                 }

            }

    	 }
/**********************************************************************************************************************/
    //function for to convert string in integer
    private static int convertSafe(String s) 
   	    {
    	 try 
      	    {
      	 	 return Integer.parseInt(s);
      	    } 
    	 catch (NumberFormatException e) 
      	   { 
       		return -1;
      	   }
  	    }
/**********************************************************************************************************************/
    //to begin the part by putting 0 in the array game
    private static void the_game_begin()
        {
         for (int i = 0;i <3; i++) 
           {
            for (int j = 0;j<3;j++) 
              {
               the_game[i][j] =0;
              }
           }
        }
/**********************************************************************************************************************/
    //using this function print the array of game
    private static void the_game_print()
       {
       	for (int i = 0;i <3; i++) 
          {
           for (int j = 0;j<3;j++) 
             {
              System.out.print(the_game[i][j]+"    ");
              }
            System.out.println();
           }
        //to do
       }
/**********************************************************************************************************************/
    //this function determines the winner : if the value returned is 1 (2) the winner is the gammer 1 (gammer 2) else no winner 
    private static int the_game_winner()
       {

        //the first line
        if (the_game[0][0]==the_game[0][1] && the_game[0][0]==the_game[0][2])
        	if(the_game[0][0]==1)
        		return 1;
        	else
        		if(the_game[0][0]==2)
        			return 2;
        
        //the second line
        if (the_game[1][0]==the_game[1][1] && the_game[1][0]==the_game[1][2])
        	if(the_game[1][0]==1)
        		return 1;
        	else
        		if(the_game[1][0]==2)
        			return 2; 

        //the third line
        if (the_game[2][0]==the_game[2][1] && the_game[2][0]==the_game[2][2])
        	if(the_game[2][0]==1)
        		return 1;
        	else
        		if(the_game[2][0]==2)
        			return 2;

        //the first column 
        if (the_game[0][0]==the_game[1][0] && the_game[0][0]==the_game[2][0])
        	if(the_game[0][0]==1)
        		return 1;
        	else
        		if(the_game[0][0]==2)
        			return 2;

        //the second column
		if (the_game[0][1]==the_game[1][1] && the_game[0][1]==the_game[2][1])
        	if(the_game[0][1]==1)
        		return 1;
        	else
        		if(the_game[0][1]==2)
        			return 2;
        
        //the third column
        if (the_game[0][2]==the_game[1][2] && the_game[0][2]==the_game[2][2])
        	if(the_game[0][2]==1)
        		return 1;
        	else
        		if(the_game[0][2]==2)
        			return 2;

        //the first diagonal
        if (the_game[0][0]==the_game[1][1] && the_game[0][0]==the_game[2][2])
        	if(the_game[0][0]==1)
        		return 1;
        	else
        		if(the_game[0][0]==2)
        			return 2;

        //the second diagonal
        if (the_game[0][2]==the_game[1][1] && the_game[0][2]==the_game[2][0])
        	if(the_game[0][2]==1)
        		return 1;
        	else
        		if(the_game[0][2]==2)
        			return 2;

        return 0;
       }
/**********************************************************************************************************************/
//this function print the end of the game
	private static void the_game_over_print()
	   {
	   	System.out.println("-------------------------------THE GAME IS OVER-------------------------------");
	   	//to do
	   }
/**********************************************************************************************************************/
	private static String transformer_array_string()
	   {
	   	int tap=9-number_free_space;
	   	String tampon=new String(Integer.toString(tap));
	   	for (int i = 0;i <3; i++) 
          {
           for (int j = 0;j<3;j++) 
           	if(the_game[i][j]!=0)
            	tampon+=new String(Integer.toString(the_game[i][j])+Integer.toString(i)+Integer.toString(j));
          }

        return tampon;
	   }
/**********************************************************************************************************************/ 
/**********************************************************************************************************************/
/**********************************************************************************************************************/
/**********************************************************************************************************************/
/**********************************************************************************************************************/
	public static void main(String argv[]) 
       { 
    	//if you give a file 
    	if (argv.length==2)
            {
             ip_server=argv[0];
             port_server=convertSafe(argv[1]);
            }
        System.out.println("IP SERVER   === "+ip_server+"\nPORT SERVER === "+port_server); 

    	//to create the server socket
    	 try 
    	   {
      	    listen_socket=new DatagramSocket(port_server,InetAddress.getByName(ip_server));
		  
      	 	while(true) 
      	  		{ 
      	  		 int taille = 4096; 
    			 byte buffer[] = new byte[taille];
        	 	 
        	 	 DatagramPacket datagram_gammer = new DatagramPacket(buffer,buffer.length); 
        	 	 listen_socket.receive(datagram_gammer);

        	 	 //process
        	 	 process_receive_data(datagram_gammer);

        	 	 //reset the datagram for new reception
        	 	 datagram_gammer.setLength(buffer.length);

        	 	 //the end of the game
        	 	 if(ENDD==1)
        	 		{
        	 		 String tampon1= new String(Integer.toString(FYI)+transformer_array_string());
             	 	 send(tampon1,port_gamer1,ip_adress_gamer1);
             	     send(tampon1,port_gamer2,ip_adress_gamer2);

                     String tampon3= new String(Integer.toString(END)+Integer.toString(winner));
             	     send(tampon3,port_gamer1,ip_adress_gamer1);
             	     send(tampon3,port_gamer2,ip_adress_gamer2);

        	 	 	 the_game_over_print();
        	 	 	 break;
        	 		}
      			}
      	   } 
      	 catch (SocketException e) 
      	   {
            e.printStackTrace();
           } 
         catch (IOException e) 
           {
            e.printStackTrace();
           } 
       }
/**********************************************************************************************************************/
    }
