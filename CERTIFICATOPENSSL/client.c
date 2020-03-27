//gcc -std=c99 -Wall -W -pedantic client.c -o client -lcrypto

#include <netdb.h> 
#include <stdio.h> 
#include <stdlib.h> 
#include <string.h> 
#include <sys/socket.h>
/***************************************************************************************************************/
#include <openssl/rand.h>
#include <openssl/des.h>
#include <openssl/evp.h>
#include <openssl/rsa.h>
#include <openssl/pem.h>
#include <openssl/aes.h>

typedef unsigned char uchar;
#define NUM_BITS  2048
#define PADDING   RSA_PKCS1_PADDING
/***************************************************************************************************************/ 
#define MAX 36000 
//#define PORT 8080 
#define SA struct sockaddr
/**************************************************************************************************************/
//variable for encrypt
char name_file[MAX];//="sawadogo.txt";//namefile
char public_keys[MAX];
char private_keys[MAX];
char symetric_keys[MAX];
char buff[MAX];
char tampon[MAX];
char data[MAX];
/**************************************************************************************************************/
void generate_key(char* symetric_keys)
    {


    }
/**************************************************************************************************************/
char *encrypte_symetric_data(char *data,char *key)
	{



   return "sawadogo";	  	
	}
/**************************************************************************************************************/
char *decrypte_symetric_data(char *data,char *key)
	{
     	
	}
/**************************************************************************************************************/
char *encrypte_asymetric_data(char *data,char *key)
	{
   int lengh_data=strlen(data);
   char encrypte[MAX];
   bzero(encrypte, MAX); 

   RSA * rsa = createRSA(key,1);
   int result = RSA_public_encrypt(lengh_data,data,encrypte,rsa,padding);
    
   if(result==-1)
    {
     printf("----------------ERROR ENCRYPT ASYMETRIC---------------------\n");
     exit(-1);
    }

   return encrypte;
	}
/**************************************************************************************************************/
void save_data(char* name_file,char* data_sava)
  {
   FILE* fichier=NULL;

   //create file 
   fichier=fopen(name_file,"w+");

   if(fichier==NULL)
      {
        printf("--------------ERROR SAVE FILE-------------------\n");
        exit(EXIT_FAILURE);
      }

    //save data in a file
    fputs(data_sava,fichier);

   //Close the file
   if(fclose(fichier)!=0)
      {
       printf("--------------ERROR CLOSE FILE-------------------\n");
       exit(EXIT_FAILURE); 
      }
  }
/**************************************************************************************************************/
void func(int sockfd) 
   { 
     
    int flags=0;
    generate_key(symetric_keys);
   
    for (;;) 
       { 
        bzero(buff, sizeof(buff)); 
        
        //Send hello at server
        if(flags==0)
          {
           strcpy(buff,"HELLO");
           write(sockfd, buff, sizeof(buff)); 
           flags=1;
           continue;
          }
        //
        bzero(buff, sizeof(buff)); 
        read(sockfd, buff, sizeof(buff)); 
        printf("From Server ==== %s", buff); //just for see a packet
/************************************************/        
        //the first connection
        if (flags==1 && strncmp(buff, "PUBLIC_KEY=", 11) == 0 )
           {
            strcpy(public_keys,buff+11);
            bzero(data, sizeof(data));
            strcpy(data,"SYMETRIC_KEY=");
            strcat(data,symetric_keys);
            bzero(tampon, sizeof(tampon));
            strcpy(tampon,encrypte_asymetric_data(data,public_keys));
            write(sockfd, tampon, sizeof(tampon)); 
            flags=2;
            continue;
           }

        if(flags==2)
          {
           bzero(data, sizeof(data));
           strcpy(data,decrypte_symetric_data(buff,symetric_keys));
           //to send the name file
           if ((strncmp(data, "DATA_NAME=", 10)) == 0)
               {
                bzero(tampon, MAX); 

                strcpy(tampon,"DATA_NAME=");
                strcat(tampon,name_file);
                bzero(buff, MAX);
                strcpy(buff,encrypte_symetric_data(tampon,symetric_keys));                
                write(sockfd, buff, sizeof(buff));
                flags=3;
                continue;  
               }
           continue;
          }
        
        if(flags==3)
          {
           //decrypte data
           bzero(data, sizeof(data));
           strcpy(data,decrypte_symetric_data(buff,symetric_keys));

           if(strncmp(data, "DATA=", 5)==0)
             {
              //data is received
              printf("%s\n",data);//print data
              save_data(name_file,data+5);//save data
              bzero(tampon, MAX); 
              strcpy(tampon,"RECEIVE_SUCCES=");
              bzero(buff, MAX);
              strcpy(buff,encrypte_symetric_data(tampon,symetric_keys));                
              write(sockfd, buff, sizeof(buff));
              break;
             }
           }

         if (strncmp(buff, "EXIT=", 4) == 0) 
            {
             printf("---------------------CONNECTION IS FINISH--------------------\n"); 
             break; 
            }

         bzero(tampon, MAX); 
         strcpy(tampon,"EXIT=");             
         printf("---------------------CONNECTION IS FINISH--------------------\n"); 
         write(sockfd, tampon, sizeof(tampon));
         break;
       }
  
  }
/***************************************/        
    
/**************************************************************************************************************/  
int main(int argc,char* argv[]) 
   { 
    int sockfd, connfd; 
    struct sockaddr_in servaddr, cli; 
    int PORT=8000;
    char IP[20]={0};

    //take 
    if(argc==4)
       {
        PORT=atoi(argv[2]);
        strcpy(IP,argv[1]);
        strcpy(name_file,argv[3]); 
       }
    else
       {
        printf("------------------GIVE PORT, MISS NAME_FILE AND IP ADRESS-----------------\n");
        exit(0);
       }

    // socket create and varification 
    sockfd = socket(AF_INET, SOCK_STREAM, 0); 
    if (sockfd == -1) 
       { 
        printf("-------------------SOCKET CREATION FILED-----------------\n"); 
        exit(0); 
       } 
    else
        printf("-------------------SOCKET CREATION SUCCESS---------------\n"); 
    bzero(&servaddr, sizeof(servaddr)); 
  
    // assign IP, PORT 
    servaddr.sin_family = AF_INET; 
    servaddr.sin_addr.s_addr = inet_addr(IP); 
    servaddr.sin_port = htons(PORT); 
  
    // connect the client socket to server socket 
    if (connect(sockfd, (SA*)&servaddr, sizeof(servaddr)) != 0) 
       { 
        printf("---------------------CONNECTION FAILED--------------------------\n"); 
        exit(0); 
       } 
    else
        printf("------------------CONNECTED TO THE SERVER--------------------\n"); 
  
    // function for chat 
    func(sockfd); 
  
    // close the socket 
    close(sockfd); 
   } 
