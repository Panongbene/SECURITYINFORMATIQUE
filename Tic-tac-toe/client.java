import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.lang.String;

import java.net.Socket;


import java.util.Scanner;


/**********************************************************************************************************************/
public class client
    {
/**********************************************************************************************************************/
    //the flag of game need
    final static int FYI=0x01;
	final static int MYM=0x02;
	final static int END=0x03;
	final static int TXT=0x04;
	final static int MOV=0x05;
	final static int LFT=0x06;

	//this array represente the game
	static int[][] the_game= new int[3][3];
    static int number_free_space=9;//for to know the number of free box in the array


    //this flag is used for to determinate the end of game
	private static int ENDD=-1;
	private static int winner=0;
	private static int FAILED=-1;
	
	//ip and port server
	private static int port_server=0;
    private static String ip_server="";


	//buffer
	//final static int taille = 4096; 
    //final static byte buffer[] = new byte[taille];

	//the datagram socket gamer
	private static DatagramSocket gammer_socket=null;
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
         //line 1
         for (int i = 0;i <11; i++) 
           {
		    if(i==3 || i==7)
		   	   System.out.print(" | ");
		   	else
		   	   System.out.print("   ");
		   }
		 System.out.println("");
		 //line 2
         for (int i = 0;i <11; i++) 
           {
			if(i==1)
			  {
			   if (the_game[0][0]==1)
              	 System.out.print(" X ");
               if (the_game[0][0]==2)
              	 System.out.print(" 0 ");
               if (the_game[0][0]==0)
              	 System.out.print("   ");
              }			
		   	if(i==4)
		   	  {
			   if (the_game[0][1]==1)
              	 System.out.print(" X ");
               if (the_game[0][1]==2)
              	 System.out.print(" 0 ");
               if (the_game[0][1]==0)
              	 System.out.print("   ");
              }
            if(i==7)
		   	  {
			   if (the_game[0][2]==1)
              	 System.out.print(" X ");
               if (the_game[0][2]==2)
              	 System.out.print(" 0 ");
               if (the_game[0][2]==0)
              	 System.out.print("   ");
              }			   	   		    
		    if(i==2 || i==5)
		   	   System.out.print(" | ");
		   	else
		   	   System.out.print("   ");
		   }
		  System.out.println("");
         //line 3
		 for (int i = 0;i <11; i++) 
           {
		    if(i==3 || i==7)
		   	   System.out.print(" | ");
		   	else
		   	   System.out.print("   ");
		   }
		 System.out.println("");
		 //line 4
		 for (int i = 0;i <11; i++)
		 	System.out.print(" - ");
		 System.out.println("");
/*****************************************/
		//line 5
         for (int i = 0;i <11; i++) 
           {
		    if(i==3 || i==7)
		   	   System.out.print(" | ");
		   	else
		   	   System.out.print("   ");
		   }
		 System.out.println("");
		 //line 6
         for (int i = 0;i <11; i++) 
           {
           	if(i==1)
			  {
			   if (the_game[1][0]==1)
              	 System.out.print(" X ");
               if (the_game[1][0]==2)
              	 System.out.print(" 0 ");
               if (the_game[1][0]==0)
              	 System.out.print("   ");
              }			
		   	if(i==4)
		   	  {
			   if (the_game[1][1]==1)
              	 System.out.print(" X ");
               if (the_game[1][1]==2)
              	 System.out.print(" 0 ");
               if (the_game[1][1]==0)
              	 System.out.print("   ");
              }
            if(i==7)
		   	  {
			   if (the_game[1][2]==1)
              	 System.out.print(" X ");
               if (the_game[1][2]==2)
              	 System.out.print(" 0 ");
               if (the_game[1][2]==0)
              	 System.out.print("   ");
              }		   	   		    

		    if(i==2 || i==5)
		   	   System.out.print(" | ");
		   	else
		   	   System.out.print("   ");
		   }
		  System.out.println("");
         //line 7
		 for (int i = 0;i <11; i++) 
           {
		    if(i==3 || i==7)
		   	   System.out.print(" | ");
		   	else
		   	   System.out.print("   ");
		   }
		 System.out.println("");
		 //line 8
		 for (int i = 0;i <11; i++)
		 	System.out.print(" - ");
		 System.out.println("");
/***********************/
		 //line 9
         for (int i = 0;i <11; i++) 
           {
		    if(i==3 || i==7)
		   	   System.out.print(" | ");
		   	else
		   	   System.out.print("   ");
		   }
		 System.out.println("");
		 //line 10
         for (int i = 0;i <11; i++) 
           {
           	if(i==1)
			  {
			   if (the_game[2][0]==1)
              	 System.out.print(" X ");
               if (the_game[2][0]==2)
              	 System.out.print(" 0 ");
               if (the_game[2][0]==0)
              	 System.out.print("   ");
              }			
		   	if(i==4)
		   	  {
			   if (the_game[2][1]==1)
              	 System.out.print(" X ");
               if (the_game[2][1]==2)
              	 System.out.print(" 0 ");
               if (the_game[2][1]==0)
              	 System.out.print("   ");
              }
            if(i==7)
		   	  {
			   if (the_game[2][2]==1)
              	 System.out.print(" X ");
               if (the_game[2][2]==2)
              	 System.out.print(" 0 ");
               if (the_game[2][2]==0)
              	 System.out.print("   ");
              }

	        if(i==2 || i==5)
		   	   System.out.print(" | ");
		   	else
		   	   System.out.print("   ");
		   }
		  System.out.println("");
         //line 11
		 for (int i = 0;i <11; i++) 
           {
		    if(i==3 || i==7)
		   	   System.out.print(" | ");
		   	else
		   	   System.out.print("   ");
		   }
		 
		 System.out.println("");
/*********************/
       }
/**********************************************************************************************************************/
//this function print the end of the game
private static void the_game_over_print()
	   {
	   	System.out.println("-----------------------------THE GAME IS OVER------------------------------");
	   	//to do
	   }
/**********************************************************************************************************************/
//this function print the end of the game
private static void the_winner_game_print(int winner)
	   {
	   	if(winner>0)
	   		System.out.println("----->>>>>>>>>>>>>>>>THE WINNER IS THE GAMMER=="+winner+"<<<<<<<<<<<<<<<<<<<<<<-----");
	   	else
	   	 	System.out.println("------------------>>>>>>>>>>>>>>NO WINNER<<<<<<<<<<<<<<<<-----------------------");
	   	//to do
	   }
/**********************************************************************************************************************/
   //function for to send data at the server
    public static void send(final String payload,int port,String ip_adresse)
    	{
		  byte[] data = payload.getBytes();
		  try 
		    {
		     DatagramPacket datagramme= new DatagramPacket(data,data.length,InetAddress.getByName(ip_adresse),port);
		     gammer_socket.send(datagramme);

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
	
	public static void process_receive_data(DatagramPacket datagram_server)
	    {
	     int port=datagram_server.getPort();
    	 String ip_adress=new String(datagram_server.getAddress().getHostAddress());		  
    	 String payload12=new String(datagram_server.getData());
    	 //verify if the datagram come from server

         if(port!=port_server || ip_adress.equals(ip_server)==false)       
         	 return;

         //extract flag
		 int flagg=convertSafe(payload12.substring(0,1));
         if(flagg==TXT)
         	{
         	 System.out.print("TXT == "+payload12.substring(1));       
			 return;
			}

		 if(flagg==FYI)
         	{
         	 update_the_game(payload12.substring(1));
         	 System.out.println("\n\n\n------------------------------THE ARRAY GAME-------------------------------");
         	 the_game_print();
			 return;
			}

		 if(flagg==END)
         	{
         	 winner=convertSafe(payload12.substring(1,2));
         	 int winner123=convertSafe(payload12.substring(1,3));
         	 if(winner123==25)
         	 	{
         	 	 System.out.println("----------------------------GAME CONNECTION FAILED-----------------------");
         	 	 FAILED=1;
         	     ENDD=1;
         		}
         	 else
         	 	ENDD=1;
			 return;
			}

		 if(flagg==MYM)
         	{
         	 int x=0,y=0;
         	 Scanner sc = new Scanner(System.in);

         	 System.out.println("--------------------------CHOOSE POSITION----------------------------------");
         	 do
         	   {
         	   	System.out.print("CHOISE X=");
         	   	x=sc.nextInt();
         	   	if(x<1 || x>3)
         	   		System.out.println("------INCORRECTE CHOICE---GOOD CHOICE IS INTEGER BETWEEN 1 TO 3----RETRY------");
         	   }
         	 while(x<1 || x>3);
         	 x=x-1;
         	 
         	 do
         	   {
         	   	System.out.print("CHOISE Y=");
         	   	y=sc.nextInt();
         	   	if(y<1 || y>3)
         	   		System.out.println("------INCORRECTE CHOICE---GOOD CHOICE IS INTEGER BETWEEN 1 TO 3----RETRY------");
         	   }
         	 while(y<1 || y>3);
         	 y=y-1;

         	 String tampon=new String(Integer.toString(MOV)+Integer.toString(x)+Integer.toString(y));

         	 send(tampon,port_server,ip_server);
         	 return;
			}

		  if(flagg==LFT)
		  	{
		  	 System.out.println("----------------------------INVALID CHOICE---------------------------------");

		  	}		 
	    }

/**********************************************************************************************************************/
	private static void update_the_game(String payload1)
		{
		 int n=convertSafe(payload1.substring(0,1));

         number_free_space=10-n;
         String payload=new String(payload1.substring(1));
         int index=0;

         
         while(index<n*3)
         	{
         	 the_game[convertSafe(payload.substring(index+1,index+2))][convertSafe(payload.substring(index+2,index+3))]=convertSafe(payload.substring(index,index+1));
         	 index+=3;
         	}
         return;
		}
/**********************************************************************************************************************/
/**********************************************************************************************************************/
/**********************************************************************************************************************/
/**********************************************************************************************************************/
/**********************************************************************************************************************/
/**********************************************************************************************************************/
    public static void main(String argv[]) 
        { 
         

         //check if a correct number of arguments has been generated
         if (argv.length<2)
            {
             System.out.println("ERRROR == PORT or IP_SERVER MISSEED");
             System.exit(1);
            }

        //port and ip server
         ip_server=argv[0];
         port_server=convertSafe(argv[1]);

         //to begin the game
         the_game_begin();
         //the_game_print();

         try 
    	   {
      	    gammer_socket=new DatagramSocket();
		    
      	    //send the first paquet
			String payload=new String(Integer.toString(TXT)+"welcomẹ!\0");
			send(payload,port_server,ip_server);



		    while(true) 
      	  		{ 
      	  		 int taille = 4096; 
    			 byte buffer[] = new byte[taille];
        	 	 DatagramPacket datagram_server = new DatagramPacket(buffer,buffer.length); 
        	
        	 	 //wait the packet
        	 	 gammer_socket.receive(datagram_server);

        	 	 //process
        	 	 process_receive_data(datagram_server);

        	 	 //reset the datagram for new reception
        	 	 datagram_server.setLength(buffer.length);

        	 	 if(FAILED==1)
        	 	 	return;

        	 	 //the end of the game
        	 	 if(ENDD==1)
        	 		{
        	 		 the_winner_game_print(winner);
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
