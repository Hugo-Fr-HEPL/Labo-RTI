#ifndef RESEAUX_SERVER_H
#define RESEAUX_SERVER_H

#include "socket.h"

class SocketServer: public Socket {
	public:
		void Listen_Server(int handleSocket);
		int Accept_Server(int handleSocket, struct sockaddr *adress);
};

#endif