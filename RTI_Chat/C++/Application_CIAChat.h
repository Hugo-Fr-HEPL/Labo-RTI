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
#include <netdb.h>
#include <errno.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <arpa/inet.h>


#define PORT 5000
#define MAXSTRING 100

#define DOC "DENY_OF_CONNEXION"
#define EOC "END_OF_CONNEXION"

#define LOGIN_GROUP = "1"
#define LOGIN_C = "C"
#define ERROR = "ERROR"

#endif