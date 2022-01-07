#ifndef RESEAUX_CLIENT_H
#define RESEAUX_CLIENT_H

#include "socket.h"

class SocketClient: public Socket {
	public:
		void Connect_Client(int handleSocket, struct sockaddr *adress);
};

#endif