#ifndef RESEAUX_H
#define RESEAUX_H

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
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <arpa/inet.h>

#define MAXSTRING 100

// Modifier ici plutôt que dans properties.txt -> bug
#define PROPFILE "../Files/properties.txt"
#define IP "192.168.1.98"
#define PORT 5001
#define NBSERVER 1


#define DOC "DENY_OF_CONNEXION"
#define EOC "END_OF_CONNEXION"


typedef struct properties {
	char* machine = NULL;
	int port;
	int nbServer;
} properties;


class Socket {
	protected:

	public:
		int Create_Socket(int domain, int type, int protocol);
		sockaddr_in Infos_Host(properties prop);

		void Send_Message(int hSocketCible, const void* message, int flagUrg);
		bool Receive_Message(int hSocketSource, void* message, int flagUrgdest);

		properties Load_Properties(const char* fileName);
		char* Read_Line(int line, char* txt);
};

#endif