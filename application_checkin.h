#ifndef APPLICATION_CHECKIN_H
#define APPLICATION_CHECKIN_H

#include "socketClient.h"
#include "airport.h"


bool Login();
char* Ticket();
int Luggage();
bool Payment(int total, char* ticket);

void CloseConnection(int socket);
bool ShowMessage(char* msg);

class AppCheck {
	public:
		int hSocketClient;
        char msgClient[MAXSTRING], msgServer[MAXSTRING];
        char* ticket = NULL;
        int total;
};


SocketClient sock;
int hSocketClient;

#endif