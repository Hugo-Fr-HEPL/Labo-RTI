#include "socketServer.h"

void SocketServer::Listen_Server(int handleSocket) {
	if(listen(handleSocket, SOMAXCONN) == -1) {
		cout << "Erreur sur l'ecoute " << errno << endl;
		close(handleSocket);
		exit(1);
	}
}

int SocketServer::Accept_Server(int handleSocket, struct sockaddr *adress) {
	int size = sizeof(struct sockaddr_in);
	int hSocketService = accept(handleSocket, adress, (unsigned int*)&size);
	if(hSocketService == -1) {
		cout << "Erreur sur l'acceptation " << errno << endl;
		close(handleSocket);
		exit(1);
	}
	return hSocketService;
}