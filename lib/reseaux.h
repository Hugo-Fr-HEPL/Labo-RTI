#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include <iostream>
using namespace std;

#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <errno.h>

#define MAXSTRING 100

#define FILENAME "./lib/properties.txt"
#define PORT 50000
#define NBSERVER 5


#define DOC "DENY_OF_CONNEXION"
#define EOC "END_OF_CONNEXION"


#define LOGIN_OFFICER 1
#define LOGOUT_OFFICER 2
#define CHECK_TICKET 3
#define CHECK_LUGGAGE 4
#define PAYMENT_DONE 5


#define LOG "LOGIN"
#define PWD "PASSWORD"
#define EMPTY "EMPTY"
#define OK "OK"

#define TIC "TICKET"
#define NB "NB_LUGGAGE"


typedef struct properties {
	char* machine = NULL;
	int port;
	int nbServer;
} properties;


int Create_Socket(int domain, int type, int protocol);
sockaddr_in Infos_Host(properties prop);
void Bind_Socket(int handleSocket, const struct sockaddr *adress, int size);

void Listen_Server(int handleSocket, int nbConnection);
int Accept_Server(int handleSocket, struct sockaddr *adress, int size);

void Connect_Client(int handleSocket, struct sockaddr *adress, int size);

void Send_Message(int hSocketCible, const void* message, int sizeMsg, int flagUrg);
bool Receive_Message(int hSocketSource, void* message, int sizeMsg, int flagUrgdest);

properties Load_Properties(const char* fileName);
char* Read_Line(int line, char* txt);
char* Read_Line(int line, FILE* fp);