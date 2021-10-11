#ifndef APPLICATION_CHECKIN_H
#define APPLICATION_CHECKIN_H

#include "socketClient.h"
#include "airport.h"


bool Login(int socket);
char* Ticket(int socket);
int Luggage(int socket);
bool Payment(int socket, int total, char* ticket);

void CloseConnection(int socket);
bool ShowMessage(char* msg);


SocketClient sock;

#endif