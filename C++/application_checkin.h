#ifndef APPLICATION_CHECKIN_H
#define APPLICATION_CHECKIN_H

#include "socketClient.h"
#include "airport.h"


void MainLoop();

bool Login();
bool Ticket();
void Luggage();
void Payment();

void CloseConnection();
bool ShowMessage(char* msg);


typedef struct AppCheck {
    int hSock;
    char msgCli[MAXSTRING], msgServ[MAXSTRING];
    char *ticket = NULL;
    int accomp, lug;
    int supp;
}AppCheck;


SocketClient sock;
AppCheck cli;

#endif