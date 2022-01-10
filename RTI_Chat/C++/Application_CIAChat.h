#ifndef CIACHAT_H
#define CIACHAT_H

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include <iostream>
using namespace std;

#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <netdb.h>
#include <errno.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <arpa/inet.h>

#include "socketClient.h"


#define LOGIN_GROUP "1"
#define LOGIN_C "C"
#define ERROR "ERROR"


void ConnectionTCP();

void Login();

sockaddr_in ConnectionUDP(char* add, char* port);

/*
void MainLoop();
*/

void CloseConnection();


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