//gcc -std=c99 -Wall -W -pedantic server.c -o server -lcrypto

#include <stdio.h> 
#include <netdb.h> 
#include <netinet/in.h> 
#include <stdlib.h> 
#include <string.h> 
#include <strings.h> 
#include <sys/socket.h> 
#include <sys/types.h> 

#include <fcntl.h> // for open
#include <unistd.h> // for close
#include <arpa/inet.h>
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
/***************************************************************************************************************/
//variable for encrypt
char public_keys[MAX];//="TEST_KEYS\n";
char symetric_keys[MAX];
char data[MAX];
char name_file[MAX];//namefile
char private_keys[MAX];
char buff[MAX];
char tampon[MAX];
/**************************************************************************************************************/
//For generate RSA keys and to save the keys in private.txt for private keys and public.txt for public keys
 void generate_RSA_keys_256_octets()
      {
      //https://cboard.cprogramming.com/c-programming/172534-rsa-key-generate-then-encrypt-decrypt-error.html 
      //he is the author of this code
       
       char plain[] = "1234567890";
       char encrypted[NUM_BITS / 8];
       char plain2[sizeof plain];
       int len, rsa_len, out_len;
       

       RSA *rsa = RSA_new();
       BIGNUM *bn = BN_new();
       BN_set_word(bn, RSA_F4);

       // We are assuming the PRNG is automatically seeded
       // (should be the case if system has /dev/urandom)
       RSA_generate_key_ex(rsa, NUM_BITS, bn, NULL);
       BN_free(bn);
 
       len = strlen(plain);
       printf("Plain: %s\n", plain);
 
       rsa_len = RSA_public_encrypt(len, (uchar*)plain, (uchar*)encrypted, rsa, PADDING);
        printf("Encrypted: %d\n", rsa_len);
 
       out_len = RSA_private_decrypt(rsa_len, (uchar*)encrypted, (uchar*)plain2, rsa, PADDING);
       plain2[out_len] = '\0';

       if (strcmp(plain, plain2) != 0)
           printf( "RSA test failed\n");
       else 
          {
           FILE *f = fopen("private.txt", "w");
           PEM_write_RSAPrivateKey(f, rsa, NULL, NULL, 0, NULL, NULL);
           f = freopen("public.txt", "w", f);
           PEM_write_RSAPublicKey(f, rsa);
           fclose(f);
           }
        RSA_free(rsa);
      }
/**************************************************************************************************************/
RSA * createRSA(unsigned char * key,int public)
	{
     RSA *rsa= NULL;
     BIO *keybio ;
     keybio = BIO_new_mem_buf(key, -1);
     if (keybio==NULL)
    	{
         printf( "Failed to create key BIO");
         return 0;
    	}
     if(public)
    	{
         rsa = PEM_read_bio_RSA_PUBKEY(keybio, &rsa,NULL, NULL);
    	}
     else
    	{
         rsa = PEM_read_bio_RSAPrivateKey(keybio, &rsa,NULL, NULL);
    	}
 
     return rsa;
	}
/**************************************************************************************************************/
void downloads_data(char* data,char* name_file)
    {
     FILE* fichier=NULL;

     //create file 
     fichier=fopen(name_file,"r");

     if(fichier==NULL)
      {
        printf("--------------ERROR OPEN FILE-------------------\n");
        exit(EXIT_FAILURE);
      }

     char chaine[MAX];
     //save data in a file
     while(fgets(chaine,MAX,fichier)!=NULL)
      {
       strcat(data,chaine);
	   //bzero(chaine, MAX);        
      };

     //Close the file
     if(fclose(fichier)!=0)
      {
       printf("--------------ERROR CLOSE FILE-------------------\n");
       exit(EXIT_FAILURE); 
      }
    }
/**************************************************************************************************************/
void generate_key(char* public_keys,char* private_keys)
    {
     //generate RSA public and private keys
     generate_RSA_keys_256_octets();

     //downloads public keys
     downloads_data(public_keys,"public.txt");

     //downloads private keys
	 downloads_data(private_keys,"private.txt");     
    }
/**************************************************************************************************************/
char *encrypte_symetric_data(char *data,char *key)
	{
	 //int lengh_data=strlen(data);

     char *encrypte;
     encrypte=malloc(MAX*sizeof(char));
	 bzero(encrypte, MAX);

	 AES_KEY enc_key;
	 
	 AES_set_decrypt_key((uchar*)key,256,&enc_key);
     AES_decrypt((uchar*)data, (uchar*)encrypte, &enc_key); 

	 return encrypte;
	}
/**************************************************************************************************************/
char *decrypte_symetric_data(char *data,char *key)
	{
	 //int lengh_data=strlen(data);

     char *decrypte;
     decrypte=malloc(MAX*sizeof(char));
	 bzero(decrypte, MAX);

	 AES_KEY dec_key;
	 
	 AES_set_decrypt_key((uchar*)key,256,&dec_key);
     AES_decrypt((uchar*)data, (uchar*)decrypte, &dec_key); 

	 return decrypte;   
	}
/**************************************************************************************************************/
char *decrypte_asymetric_data(char *data, char *key)
	{
	 int lengh_data=strlen(data);

     char *decrypte;
     decrypte=malloc(MAX*sizeof(char));
	 bzero(decrypte, MAX); 

	 RSA * rsa = createRSA((uchar*)key,1);
     int result = RSA_public_decrypt(lengh_data,(uchar*) data,(uchar*)decrypte,rsa,PADDING);
    
     if(result==-1)
    	{
    	 printf("-------------------ERROR DECRYPT ASYMETRIC---------------------\n");
    	 exit(-1);
    	}

	 return decrypte;  
	}
/**************************************************************************************************************/
// Function designed for chat between client and server. 
void func(int sockfd) 
    {
     int flags=0;
     generate_key(public_keys,private_keys);
     int flagtampon=-1;  
     // infinite loop for chat 
     for (;;) 
       { 
        bzero(buff, MAX); 
  
        // read the message from client and copy it in buffer 
        read(sockfd, buff, sizeof(buff)); 
        printf("FROM CLIENT == %s\n", buff);//just for to see the receive packet
        flagtampon++;
        //the first connection
        if (strncmp(buff, "HELLO", 4) == 0 && flags==0 && flagtampon==0)
           {
            bzero(data, MAX);
            strcpy(data,"PUBLIC_KEY=");
            strcat(data,public_keys);
            write(sockfd, data, sizeof(data));
            flags=1;
            flagtampon++;
            continue;  
           }
        //if serveur receive the symetric keys

        if (flags==1 && flagtampon==1)
            {
             bzero(data, MAX); 
             strcpy(data,decrypte_asymetric_data(buff,private_keys));

            if((strncmp(data, "SYMETRIC_KEY=", 13)) == 0 )
                {
                 //decrypte symetric keys
                 strcpy(symetric_keys,data+13);
                 //using symetric keys for crypte DATA NAME
                 bzero(tampon, MAX);
                 bzero(buff, MAX);
                 strcpy(buff,"DATA_NAME=");
                 strcpy(tampon,encrypte_symetric_data(buff,symetric_keys));
                 write(sockfd, tampon, sizeof(tampon));
                 flags=2;
                 flagtampon++;
                 continue;  
                }
            else
                continue;
            }
        
        if(flags==2 && flagtampon==2)
            {
             bzero(data, MAX);
             strcpy(data,decrypte_symetric_data(buff,symetric_keys));
             if(strncmp(data, "DATA_NAME=", 10)==0 && flags==2)
                {
                 bzero(tampon, MAX); 

                 //decrypte symetric keys
                 strcpy(tampon,data+10);
                 bzero(data, MAX);
                 downloads_data(data,tampon);

                 bzero(buff, MAX);
                 strcpy(buff,"DATA=");
                 strcat(buff,encrypte_symetric_data(data,symetric_keys));
                 write(sockfd, buff, sizeof(buff));
                 flags=3;
                 flagtampon++;
                 continue;
                }
            }
        
        if(flags==3 && flagtampon==3)
           {
            bzero(data, MAX);
            strcpy(data,decrypte_symetric_data(buff,symetric_keys));
            if(strncmp(data, "RECEIVE_SUCCES=", 15)==0 && flags==3)
                {
                 printf("---------------------------CRYPTSEND IS OKAY----------------------------\n");
                 printf("---------------------------CONNECTION IS FINISH---------------------------\n"); 
                 break;
                }
            }
        
        bzero(tampon, MAX); 
        strcpy(tampon,"EXIT=");             
        printf("---------------------CONNECTION IS FINISH--------------------\n"); 
        write(sockfd, tampon, sizeof(tampon));
        break;
       }
    } 
/**************************************************************************************************************/ 
// Driver function 
int main(int argc,char* argv[]) 
{ 
    int sockfd, connfd;
    socklen_t len; 
    struct sockaddr_in servaddr, cli;
    int PORT=8000;
    char IP[20]={0}; 
  
    if(argc==3)
       {
        PORT=atoi(argv[2]);
        strcpy(IP,argv[1]);
       }
    else
       {
        printf("------------------GIVE PORT AND IP ADRESS-----------------\n");
        exit(0);
       }

    // socket create and verification 
    sockfd = socket(AF_INET, SOCK_STREAM, 0);

    if (sockfd == -1) 
       { 
        printf("-------------------SOCKET CREATION FAILED-----------------\n"); 
        exit(0); 
       } 
    else
        printf("-------------------SOCKET CREATION SUCCESS---------------\n"); 
    
    bzero(&servaddr, sizeof(servaddr)); 
  
    // assign IP, PORT 
    servaddr.sin_family = AF_INET; 
    servaddr.sin_addr.s_addr = inet_addr(IP);
    servaddr.sin_port = htons(PORT); 
  
    // Binding newly created socket to given IP and verification 
    if ((bind(sockfd, (SA*)&servaddr, sizeof(servaddr))) != 0) 
       { 
        printf("-------------------SOCKET BIND FAILED-----------------\n"); 
        exit(0); 
       } 
    else
        printf("-------------------SOCKET BIND SUCCES-----------------\n"); 
  
    while(1)
       {  
        // Now server is ready to listen and verification 
        if ((listen(sockfd, 5)) != 0) 
            { 
             printf("----------------------LISTEN FAILED-------------------\n"); 
             exit(0); 
            } 
        else
            printf("----------------------SERVER LISTENING----------------\n"); 
        len = sizeof(cli); 
  
        // Accept the data packet from client and verification 
        connfd = accept(sockfd, (SA*)&cli, &len); 
        if (connfd < 0) 
            { 
             printf("-----------------SERVER ACCEPT FAILED-----------------\n"); 
             exit(0); 
            } 
        else
            printf("--------------------SERVER ACCEPT --------------------\n"); 
  
        // Function for chatting between client and server 
         func(connfd);
        } 
  
        // After chatting close the socket 
    close(sockfd); 
} 
/**************************************************************************************************************/
